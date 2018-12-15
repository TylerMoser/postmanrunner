package me.tylermoser.postmanrunner.view

import javafx.beans.binding.Bindings
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import me.tylermoser.postmanrunner.controller.MainController
import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import me.tylermoser.postmanrunner.model.PostmanTestStatus
import me.tylermoser.postmanrunner.style.AppStylesheet
import tornadofx.*

/**
 * The main UI for this Application.
 *
 * This UI is implemented using the builder structure supported by TornadoFX to provide a wholly programmatic UI
 * specification
 */
class MainView : View("Postman Test Runner") {

    // UI element references. Only keep references if absolutely necessary.
    private var listview: ListView<PostmanTestFile> by singleAssign()
    private var tableview: TableView<PostmanTest> by singleAssign()

    // TornadoFX constructs for binding UI elements to logic
    private class PostmanTestFileItemViewModel : ItemViewModel<PostmanTestFile>()
    private class PostmanTestItemViewModel : ItemViewModel<PostmanTest>()
    private val lastSelectedPostmanTestFileInList = PostmanTestFileItemViewModel()
    private val lastSelectedPostmanTestInTable = PostmanTestItemViewModel()

    // The view should only depend on controllers to serve as intermediaries between UI and logic
    private val mainController: MainController by inject()

    override val root = vbox {
        setPrefSize(700.0, 650.0)
        menubar {
            menu("File") {
                item("Refresh").action { mainController.refresh() }
                separator()
                item("Exit").action { System.exit(0) }
            }
            menu("Preferences") {
                item("Set Postman Test Collection Directory").action {
                    mainController.setPostmanTestCollectionDirectory()
                }
                item("Set Postman Environment File").action {
                    mainController.setPostmanEnvironmentFile()
                }
            }
        }
        splitpane {
            listview = listview(mainController.postmanTestCollectionsInChosenDirectory) {
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                bindSelected(lastSelectedPostmanTestFileInList)
            }
            tableview = tableview(mainController.postmanTestCollectionsSelectedForExecution) {
                readonlyColumn("Test Collection Name", PostmanTest::fileName).apply {
                    sortableProperty().bind(!mainController.areTestCollectionsExecutingProperty)
                }
                column("Test Collection Execution Status", PostmanTest::statusProperty).apply {
                    sortableProperty().bind(!mainController.areTestCollectionsExecutingProperty)
                }.cellFormat {
                    text = it.toString() // need to manually set text value when overriding cell formatting
                    this.tableRow?.toggleClass(AppStylesheet.postmanTestPass, it == PostmanTestStatus.PASS)
                    this.tableRow?.toggleClass(AppStylesheet.postmanTestFail, it == PostmanTestStatus.FAIL)
                }

                contextmenu {
                    item("Open Test Result in Browser").action {
                        selectedItem?.let { mainController.openHtmlTestResultFileInBrowser(selectedItem!!) }
                    }
                }
                onUserSelect(/* double click */2) { mainController.openHtmlTestResultFileInBrowser(it) }
                columnResizePolicy = SmartResize.POLICY
                vgrow = Priority.ALWAYS
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                bindSelected(lastSelectedPostmanTestInTable)
            }

            orientation = Orientation.VERTICAL
            setDividerPositions(0.5)
        }
        borderpane {
            left {
                button("Add Selected Tests To Execution List") {
                    disableProperty().bind(Bindings.or(
                            lastSelectedPostmanTestFileInList.empty,
                            mainController.areTestCollectionsExecutingProperty
                    ))
                    prefWidth = 320.0
                    action {
                        val selectedItemsInListView = listview.selectionModel.selectedItems
                        mainController.addTestsToExecutionList(selectedItemsInListView)
                    }
                }
            }
            right {
                button("Remove Selected Tests From Execution List") {
                    disableProperty().bind(Bindings.or(
                            lastSelectedPostmanTestInTable.empty,
                            mainController.areTestCollectionsExecutingProperty
                    ))
                    prefWidth = 320.0
                    action {
                        val selectedItemsInTableView = tableview.selectionModel.selectedItems
                        mainController.removeTestsFromExecutionList(selectedItemsInTableView)
                        tableview.refresh()
                    }
                }
            }
        }
        hbox {
            alignment = Pos.CENTER
            paddingAll = 15.0
            button("Execute Tests") {
                disableProperty().bind(Bindings.or(
                        mainController.postmanTestCollectionsSelectedForExecution.sizeProperty.eq(0),
                        mainController.areTestCollectionsExecutingProperty
                ))
                action { mainController.executeTests() }
                style { fontWeight = FontWeight.BOLD }
            }
        }
    }

}
