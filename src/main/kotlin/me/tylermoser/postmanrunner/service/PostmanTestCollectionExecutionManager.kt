package me.tylermoser.postmanrunner.service

//import javafx.application.Platform
//import javafx.beans.property.SimpleBooleanProperty
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestStatus
import tornadofx.*

/**
 * Provides logic for managing the execution of multiple Postman test collections
 */
class PostmanTestCollectionExecutionManager: Component(), ScopedInstance {

    private val collectionExecutor: PostmanTestCollectionExecutor by inject()

    // JavaFX property for if test collections are currently running.
    // All buttons and TableView sorting should be disabled while test collections are executing.
    private val areTestsExecutingProperty = SimpleBooleanProperty(false)
    fun areTestsExecutingProperty() = areTestsExecutingProperty
    var areTestsExecuting: Boolean by areTestsExecutingProperty

    /**
     * Executes a list of [PostmanTest]s.
     */
    fun executeTestCollections(testCollectionsToExecute: MutableList<PostmanTest>) {
        resetAllTestCollections(testCollectionsToExecute)
        executeTestCollectionsSequentially(testCollectionsToExecute)
    }

    /**
     * Takes a list of [PostmanTest]s and executes them one after another. Under this scheme, only one Postman test
     * collection will be executing at a time, and then next collection will not start executing until the previous
     * one has completed.
     */
    private fun executeTestCollectionsSequentially(testCollectionsToExecute: MutableList<PostmanTest>) {
        areTestsExecuting = true
        GlobalScope.launch {
            for (testCollection in testCollectionsToExecute) {
                withContext(Default) {
                    collectionExecutor.executeTestCollection(testCollection)
                }
            }
            Platform.runLater { areTestsExecuting = false } // Run on JavaFX thread
        }
    }

    /**
     * Executes a list of [PostmanTest]s simultaneously
     *
     * TODO: Test This!
     */
    private fun executeTestCollectionsConcurrently(testCollectionsToExecute: MutableList<PostmanTest>) {
        areTestsExecuting = true
        GlobalScope.launch {
            val jobs = arrayListOf<Deferred<Any>>()
            for (testCollection in testCollectionsToExecute) jobs += async {
                collectionExecutor.executeTestCollection(testCollection)
            }
            jobs.forEach { it.await() }
            Platform.runLater { areTestsExecuting = false }
        }
    }

    /**
     * Resets the status of each test selected to be executed to "test execution not started"
     */
    private fun resetAllTestCollections(testCollectionsToExecute: MutableList<PostmanTest>) {
        for (collection in testCollectionsToExecute) {
            collection.status = PostmanTestStatus.TEST_EXECUTION_NOT_STARTED
        }
    }

}
