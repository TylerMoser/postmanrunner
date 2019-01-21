package me.tylermoser.postmanrunner.view

import javafx.beans.binding.Bindings
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import me.tylermoser.postmanrunner.controller.FileSystemController
import me.tylermoser.postmanrunner.controller.TestCollectionController
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
    private val testCollectionController: TestCollectionController by inject()
    private val fileSystemController: FileSystemController by inject()

    override val root = borderpane {
        setPrefSize(700.0, 650.0)
        top = menubar {
            menu("File") {
                item("Refresh").action { testCollectionController.refresh() }
                separator()
                item("Exit").action { System.exit(0) }
            }
            menu("Preferences") {
                item("Set Postman Test Collection Directory").action {
                    fileSystemController.setPostmanTestCollectionDirectory()
                }
                item("Set Postman Environment File").action {
                    fileSystemController.setPostmanEnvironmentFile()
                }
            }
        }
        center = splitpane {
            listview = listview(testCollectionController.postmanTestCollectionsInChosenDirectory) {
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                bindSelected(lastSelectedPostmanTestFileInList)
            }
            tableview = tableview(testCollectionController.postmanTestCollectionsSelectedForExecution) {
                readonlyColumn("Test Collection Name", PostmanTest::fileName).apply {
                    sortableProperty().bind(!testCollectionController.areTestCollectionsExecutingProperty)
                }
                column("Test Collection Execution Status", PostmanTest::statusProperty).apply {
                    sortableProperty().bind(!testCollectionController.areTestCollectionsExecutingProperty)
                }.cellFormat {
                    text = it.toString() // need to manually set text value when overriding cell formatting
                    this.tableRow?.toggleClass(AppStylesheet.postmanTestPass, it == PostmanTestStatus.PASS)
                    this.tableRow?.toggleClass(AppStylesheet.postmanTestFail, it == PostmanTestStatus.FAIL)
                }

                contextmenu {
                    item("Open Log File Directory in File Browser").action {
                        fileSystemController.openOutputLogDirectory()
                    }
                    item("Open HTML Test Result in Web Browser").action {
                        val selectedTests = selectionModel.selectedItems.filter { test -> test != null }
                        fileSystemController.openHtmlTestResultFilesInBrowser(selectedTests)
                    }
                    item("Open JSON Test Result in Text Editor").action {
                        val selectedTests = selectionModel.selectedItems.filter { test -> test != null }
                        fileSystemController.openJsonTestResultsInTextEditor(selectedTests)
                    }
                    item("Open Error Stream Log in Text Editor").action {
                        val selectedTests = selectionModel.selectedItems.filter { test -> test != null }
                        fileSystemController.openErrorStreamLogsInTextEditor(selectedTests)
                    }
                    item("Open Input Stream Log in Text Editor").action {
                        val selectedTests = selectionModel.selectedItems.filter { test -> test != null }
                        fileSystemController.openInputStreamLogsInTextEditor(selectedTests)
                    }
                    item("Open Output Stream Log in Text Editor").action {
                        val selectedTests = selectionModel.selectedItems.filter { test -> test != null }
                        fileSystemController.openOutputStreamLogsInTextEditor(selectedTests)
                    }
                }
                onUserSelect(/* double click */2) { fileSystemController.openHtmlTestResultFileInBrowser(it) }
                columnResizePolicy = SmartResize.POLICY
                vgrow = Priority.ALWAYS
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                bindSelected(lastSelectedPostmanTestInTable)
            }

            orientation = Orientation.VERTICAL
            setDividerPositions(0.5)
        }
        bottom = vbox {
            borderpane {
                left {
                    button("Add Selected Tests To Execution List") {
                        disableProperty().bind(Bindings.or(
                                lastSelectedPostmanTestFileInList.empty,
                                testCollectionController.areTestCollectionsExecutingProperty
                        ))
                        prefWidth = 320.0
                        action {
                            val selectedItemsInListView = listview.selectionModel.selectedItems
                            testCollectionController.addTestsToExecutionList(selectedItemsInListView)
                        }
                    }
                }
                right {
                    button("Remove Selected Tests From Execution List") {
                        disableProperty().bind(Bindings.or(
                                lastSelectedPostmanTestInTable.empty,
                                testCollectionController.areTestCollectionsExecutingProperty
                        ))
                        prefWidth = 320.0
                        action {
                            val selectedItemsInTableView = tableview.selectionModel.selectedItems
                            testCollectionController.removeTestsFromExecutionList(selectedItemsInTableView)
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
                            testCollectionController.postmanTestCollectionsSelectedForExecution.sizeProperty.eq(0),
                            testCollectionController.areTestCollectionsExecutingProperty
                    ))
                    action { testCollectionController.executeTests() }
                    style { fontWeight = FontWeight.BOLD }
                }
            }
        }
    }

}
