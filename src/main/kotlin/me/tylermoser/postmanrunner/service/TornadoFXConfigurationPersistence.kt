package me.tylermoser.postmanrunner.service

import com.google.gson.Gson
import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import tornadofx.*
import java.io.File

/**
 * An implementation of persistence for this application that uses the TornadoFX 'Config Settings and State' feature
 * to store persistent data in a properties file found in the application's conf folder.
 */
class TornadoFXConfigurationPersistence: Component(), ScopedInstance, AppPersistence {

    private val testCollectionDirectoryPathKey = "testCollectionDirectory"
    private val postmanEnvironmentFilePathKey = "environmentFile"
    private val testsSelectedForExecutionKey = "postmanTestCollectionsSelectedForExecution"

    private val gson = Gson()

    // =================================================================================================================

    override fun setPostmanTestCollectionDirectory(testCollectionDirectory: File) {
        with(config) {
            set(testCollectionDirectoryPathKey, testCollectionDirectory.toString())
            save()
        }
    }

    override fun getPostmanTestCollectionDirectory(): File {
        return File(config[testCollectionDirectoryPathKey].toString())
    }

    override fun isPostmanTestCollectionDirectorySelected(): Boolean {
        return config[testCollectionDirectoryPathKey] != null
    }

    // =================================================================================================================

    override fun setPostmanEnvironmentFile(environmentFile: File) {
        with(config) {
            set(postmanEnvironmentFilePathKey, environmentFile.toString())
            save()
        }
    }

    override fun getPostmanEnvironmentFile(): File {
        return File(config[postmanEnvironmentFilePathKey].toString())
    }

    override fun isPostmanEnvironmentFileSelected(): Boolean {
        return config[postmanEnvironmentFilePathKey] != null
    }

    // =================================================================================================================

    override fun setPostmanTestCollectionsToExecute(testsSelectedForExecution: List<PostmanTest>) {
        val testFilesToPersist = testsSelectedForExecution.map {
            it.toPostmanTestFile()
        }

        with(config) {
            set(testsSelectedForExecutionKey to gson.toJson(testFilesToPersist))
            save()
        }
    }

    override fun getPostmanTestCollectionsToExecute(): List<PostmanTestFile> {
        val listAsJsonString: String? = config[testsSelectedForExecutionKey]?.toString()
        return gson.fromJson(listAsJsonString, Array<PostmanTestFile>::class.java)?.toList() ?: emptyList()
    }

    override fun clearPostmanTestCollectionsToExecute() {
        with(config) {
            remove(testsSelectedForExecutionKey)
            save()
        }
    }

}
