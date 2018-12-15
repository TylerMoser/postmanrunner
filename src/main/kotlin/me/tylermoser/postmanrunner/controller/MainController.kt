package me.tylermoser.postmanrunner.controller

import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import me.tylermoser.postmanrunner.service.FileSystem
import me.tylermoser.postmanrunner.service.PostmanTestCollectionExecutionManager
import me.tylermoser.postmanrunner.service.TestCollectionManager
import me.tylermoser.postmanrunner.view.MainView
import tornadofx.*

/**
 * The controller for the main UI window. This controller acts as the intermediary between [MainView] and the relevant
 * services. It does this by containing methods that are called directly from the view layer, and that call the
 * services.
 *
 * As an additional goal, this controller should remain as stateless as possible to make it easier to change UI
 * frameworks later if desired.
 */
class MainController: Component(), ScopedInstance {

    private val testCollectionManager: TestCollectionManager by inject()
    private val fileSystemService: FileSystem by inject()
    private val collectionExecutor: PostmanTestCollectionExecutionManager by inject()

    val postmanTestCollectionsInChosenDirectory = testCollectionManager.postmanTestCollectionsInChosenDirectory
    val postmanTestCollectionsSelectedForExecution = testCollectionManager.postmanTestCollectionsSelectedForExecution
    val areTestCollectionsExecutingProperty = collectionExecutor.areTestsExecutingProperty()

    init {
        // Populate the initial state of the two main lists when this singleton is created
        refresh()
    }

    /**
     * Changes the directory that this application searches for Postman test collections in.
     */
    fun setPostmanTestCollectionDirectory() {
        fileSystemService.choosePostmanTestCollectionDirectory()
        refresh()
    }

    /**
     * Changes the Postman environment file used by this application when executing tests
     */
    fun setPostmanEnvironmentFile() {
        fileSystemService.choosePostmanEnvironmentFile()
    }

    /**
     * Updates the UI to reflect data changes
     */
    fun refresh() {
        testCollectionManager.refreshTestCollections()
    }

    /**
     * Takes subset of the test collections in the chosen directory and adds it to the list of collections that gets
     * executed
     */
    fun addTestsToExecutionList(testsToAddToExecutionList: List<PostmanTestFile>) {
        testCollectionManager.addTestsToExecutionList(testsToAddToExecutionList)
    }

    /**
     * Removes a subset of the test collection that are selected to be executed from that list
     */
    fun removeTestsFromExecutionList(testsToRemoveFromExecutionList: List<PostmanTest>) {
        testCollectionManager.removeTestsFromExecutionList(testsToRemoveFromExecutionList)
    }

    /**
     * Executes the test collections that have been selected to be executed
     */
    fun executeTests() {
        collectionExecutor.executeTestCollections(postmanTestCollectionsSelectedForExecution)
    }

    /**
     * Opens the HTML output of a test execution in a web browser.
     */
    fun openHtmlTestResultFileInBrowser(testCollectionSelected: PostmanTest) {
        fileSystemService.openHtmlTestResultInBrowser(testCollectionSelected)
    }

}
