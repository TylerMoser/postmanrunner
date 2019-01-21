package me.tylermoser.postmanrunner.service

import javafx.stage.FileChooser
import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.io.FileFilter

/**
 * An abstraction for file system operations that are not explicitly persistence. However, the logic in this service
 * often asks as a helper for persistence-related logic.
 */
class FileSystem: Component(), ScopedInstance {

    private val persistence: TornadoFXConfigurationPersistence by inject()

    val desktop by lazy { Desktop.getDesktop() }

    /**
     * Uses a file chooser dialog to allow the user to set the directory that this application searches for Postman test
     * collections in.
     */
    fun choosePostmanTestCollectionDirectory() {
        val testCollectionDirectory = chooseDirectory("Choose Directory Containing Postman Collection JSON Files")

        if (testCollectionDirectory != null) {
            persistence.setPostmanTestCollectionDirectory(testCollectionDirectory)
            persistence.clearPostmanTestCollectionsToExecute()
        }
    }

    /**
     * Uses a file chooser dialog to allow the user to set the Postman environment file used by this application when
     * executing tests.
     */
    fun choosePostmanEnvironmentFile() {
        val environmentFile = chooseFile(
                "Select Postman Environment File For Use in Executing Tests",
                arrayOf(FileChooser.ExtensionFilter("Postman Environment File", "*.postman_environment.json")))

        if (environmentFile.isNotEmpty()) {
            persistence.setPostmanEnvironmentFile(environmentFile[0])
        }
    }

    /**
     * Gets a list of [PostmanTestFile]s that exist in the directory that the application searches for test collections
     * in.
     */
    fun getAllPostmanCollectionsInChosenDirectory(): List<PostmanTestFile> {
        val postmanCollectionDirectory = persistence.getPostmanTestCollectionDirectory()
        val arrayOfFileObjectsContainingFullPaths =  postmanCollectionDirectory.listFiles(FileFilter {
            it.name.endsWith(".postman_collection.json")
        })

        return arrayOfFileObjectsContainingFullPaths.map {
            PostmanTestFile(it.toString())
        }
    }

    /**
     * Opens the folder containing the test execution output files in the default file viewer
     */
    fun openOutputLogDirectory() {
        val outputLogDir = File("newman" + File.separator)
        desktop.open(outputLogDir)
    }

    /**
     * Opens the HTML result file produced by newman for a specific Postman test collection in the user's selected
     * default web browser.
     */
    fun openHtmlTestResultInBrowser(testCollectionDoubleClicked: PostmanTest) {
        val htmlFile = File("newman" + File.separator + testCollectionDoubleClicked.fileName + ".html")
        if (htmlFile.exists()) desktop.browse(htmlFile.toURI())
    }

    /**
     * Opens the JSON test output in the default text editor
     */
    fun openJsonTestResultInTextEditor(testCollectionDoubleClicked: PostmanTest) {
        val jsonFile = File("newman" + File.separator + testCollectionDoubleClicked.fileName + ".json")
        if (jsonFile.exists()) desktop.open(jsonFile)
    }

    /**
     * Opens the error stream log for this test in the default text editor
     */
    fun openErrorStreamLogInTextEditor(testCollectionDoubleClicked: PostmanTest) {
        val errorLogFile = File("newman" + File.separator + testCollectionDoubleClicked.fileName + "_err.log")
        if (errorLogFile.exists()) desktop.open(errorLogFile)
    }

    /**
     * Opens the input stream log for this test in the default text editor
     */
    fun openInputStreamLogInTextEditor(testCollectionDoubleClicked: PostmanTest) {
        val inputLogFile = File("newman" + File.separator + testCollectionDoubleClicked.fileName + "_in.log")
        if (inputLogFile.exists()) desktop.open(inputLogFile)
    }

    /**
     * Opens the output stream log for this test in the default text editor
     */
    fun openOutputStreamLogInTextEditor(testCollectionDoubleClicked: PostmanTest) {
        val outputLogFile = File("newman" + File.separator + testCollectionDoubleClicked.fileName + "_out.log")
        if (outputLogFile.exists()) desktop.open(outputLogFile)
    }

}
