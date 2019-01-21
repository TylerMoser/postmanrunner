package me.tylermoser.postmanrunner.controller

import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.service.FileSystem
import me.tylermoser.postmanrunner.view.MainView
import tornadofx.*

/**
 * The controller responsible for actions requiring interaction with the underlying file system.
 *
 * This controller acts as the intermediary between [MainView] and the relevant
 * services. It does this by containing methods that are called directly from the view layer, and that call the
 * services.
 *
 * As an additional goal, this controller should remain as stateless as possible to make it easier to change UI
 * frameworks later if desired.
 */
class FileSystemController: Component(), ScopedInstance {

    private val fileSystemService: FileSystem by inject()
    private val testCollectionController: TestCollectionController by inject()

    /**
     * Changes the directory that this application searches for Postman test collections in.
     */
    fun setPostmanTestCollectionDirectory() {
        fileSystemService.choosePostmanTestCollectionDirectory()
        testCollectionController.refresh()
    }

    /**
     * Changes the Postman environment file used by this application when executing tests
     */
    fun setPostmanEnvironmentFile() {
        fileSystemService.choosePostmanEnvironmentFile()
    }

    /**
     * Opens the folder containing the test execution output files in the default file viewer
     */
    fun openOutputLogDirectory() {
        fileSystemService.openOutputLogDirectory()
    }

    /**
     * Opens the HTML output of a single test execution in a web browser.
     */
    fun openHtmlTestResultFileInBrowser(testCollectionSelected: PostmanTest) {
        fileSystemService.openHtmlTestResultInBrowser(testCollectionSelected)
    }

    /**
     * Opens the HTML output of the given test executions in a web browser.
     */
    fun openHtmlTestResultFilesInBrowser(testCollectionsSelected: List<PostmanTest>) {
        testCollectionsSelected.forEach {
            fileSystemService.openHtmlTestResultInBrowser(it)
        }
    }

    /**
     * Opens the JSON output of the given test executions in the default text editor
     */
    fun openJsonTestResultsInTextEditor(testCollectionsSelected: List<PostmanTest>) {
        testCollectionsSelected.forEach {
            fileSystemService.openJsonTestResultInTextEditor(it)
        }
    }

    /**
     * Opens the error stream log for the given tests in the default text editor
     */
    fun openErrorStreamLogsInTextEditor(testCollectionsSelected: List<PostmanTest>) {
        testCollectionsSelected.forEach {
            fileSystemService.openErrorStreamLogInTextEditor(it)
        }
    }

    /**
     * Opens the input stream log for the given tests in the default text editor
     */
    fun openInputStreamLogsInTextEditor(testCollectionsSelected: List<PostmanTest>) {
        testCollectionsSelected.forEach {
            fileSystemService.openInputStreamLogInTextEditor(it)
        }
    }

    /**
     * Opens the output stream log for the given tests in the default text editor
     */
    fun openOutputStreamLogsInTextEditor(testCollectionsSelected: List<PostmanTest>) {
        testCollectionsSelected.forEach {
            fileSystemService.openOutputStreamLogInTextEditor(it)
        }
    }

}
