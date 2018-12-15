package me.tylermoser.postmanrunner.service

import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import java.io.File

/**
 * An interface for all persistence between application executions
 */
interface AppPersistence {

    /**
     * Sets the directory that the application should look for test collections in
     */
    fun setPostmanTestCollectionDirectory(testCollectionDirectory: File)

    /**
     * Gets the directory that the application should look for test collections in
     */
    fun getPostmanTestCollectionDirectory(): File

    /**
     * Determines whether a directory has been selected for the application to look for test collections in
     */
    fun isPostmanTestCollectionDirectorySelected(): Boolean

    // =================================================================================================================

    /**
     * Sets the Postman environment file to be used by this application when executing test collections
     */
    fun setPostmanEnvironmentFile(environmentFile: File)

    /**
     * Gets the Postman environment file to be used by this application when executing test collections
     */
    fun getPostmanEnvironmentFile(): File

    /**
     * Determines whether a Postman environment file has been chosen to use when executing test collections
     */
    fun isPostmanEnvironmentFileSelected(): Boolean

    // =================================================================================================================

    /**
     * Sets the list of test collections that have been selected for execution
     */
    fun setPostmanTestCollectionsToExecute(testsSelectedForExecution: List<PostmanTest>)

    /**
     * Gets the list of test collections that have been selected for execution
     */
    fun getPostmanTestCollectionsToExecute(): List<PostmanTestFile>

    /**
     * Clears the list of test collections that have been selected for execution
     */
    fun clearPostmanTestCollectionsToExecute()

}
