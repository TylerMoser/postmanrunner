package me.tylermoser.postmanrunner.service

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.tylermoser.postmanrunner.model.PostmanTest
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
        executeTestCollectionsSequentially(testCollectionsToExecute)
    }

    /**
     * Takes a list of [PostmanTest]s and executes them one after another. Under this scheme, only one Postman test
     * collection will be executing at a time, and then next collection will not start executing until the previous
     * one has completed.
     */
    private fun executeTestCollectionsSequentially(testCollectionsToExecute: MutableList<PostmanTest>) {
        areTestsExecuting = true
        launch {
            for (testCollection in testCollectionsToExecute) {
                async(CommonPool) {
                    collectionExecutor.executeTestCollection(testCollection)
                }.await()
            }
            Platform.runLater { areTestsExecuting = false } // Run on JavaFX thread
        }
    }

    /**
     * Executes a list of [PostmanTest]s simultaneously
     *
     * !!!!! UNTESTED !!!!!
     * TODO: Test This!
     */
    private fun executeTestCollectionsConcurrently(testCollectionsToExecute: MutableList<PostmanTest>) {
        areTestsExecuting = true
        launch {
            val jobs = arrayListOf<Deferred<Any>>()
            for (testCollection in testCollectionsToExecute) jobs += async(CommonPool) {
                collectionExecutor.executeTestCollection(testCollection)
            }
            jobs.forEach { it.await() }
            Platform.runLater { areTestsExecuting = false }
        }
    }

}
