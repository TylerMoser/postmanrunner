package me.tylermoser.postmanrunner.service

import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestFile
import tornadofx.*

/**
 * A service for managing the Postman test collections that this application is aware of. This mostly consists of
 * keeping lists of which test collections the user has designated to be executed and which collections they have not.
 */
class TestCollectionManager: Component(), ScopedInstance {

    val postmanTestCollectionsInChosenDirectory = mutableListOf<PostmanTestFile>().observable()
    val postmanTestCollectionsSelectedForExecution = mutableListOf<PostmanTest>().observable()

    private val persistence: TornadoFXConfigurationPersistence by inject()
    private val fileSystemService: FileSystem by inject()

    /**
     * Takes a subset of the tests on [postmanTestCollectionsInChosenDirectory] and adds those tests to
     * [postmanTestCollectionsSelectedForExecution].
     */
    fun addTestsToExecutionList(testsToAddToExecutionList: List<PostmanTestFile>) {
        postmanTestCollectionsSelectedForExecution.addAll(testsToAddToExecutionList.map {
            it.toPostmanTest()
        })
        postmanTestCollectionsInChosenDirectory.removeAll(testsToAddToExecutionList)
        persistence.setPostmanTestCollectionsToExecute(postmanTestCollectionsSelectedForExecution)
    }

    /**
     * Takes a subset of the tests on [postmanTestCollectionsSelectedForExecution] and removes them, returning them to
     * [postmanTestCollectionsInChosenDirectory]
     */
    fun removeTestsFromExecutionList(testsToRemoveFromExecutionList: List<PostmanTest>) {
        postmanTestCollectionsInChosenDirectory.addAll(testsToRemoveFromExecutionList.map {
            it.toPostmanTestFile()
        })
        postmanTestCollectionsSelectedForExecution.removeAll(testsToRemoveFromExecutionList)
        persistence.setPostmanTestCollectionsToExecute(postmanTestCollectionsSelectedForExecution)
    }

    /**
     * Updates both lists, adding new test collection files added to the chosen directory and removing those that have
     * been deleted.
     */
    fun refreshTestCollections() {
        if (persistence.isPostmanTestCollectionDirectorySelected()) {
            val collectionFilesInDirectory = fileSystemService.getAllPostmanCollectionsInChosenDirectory()
            addAllToCollectionList(collectionFilesInDirectory)
            removeAllMissingFromCollectionList(collectionFilesInDirectory)

            val persistedCollectionsSelectedForExecution = persistence.getPostmanTestCollectionsToExecute()
            postmanTestCollectionsInChosenDirectory.removeAll(persistedCollectionsSelectedForExecution)

            addAllForExecution(persistedCollectionsSelectedForExecution)
            removeAllMissingForExecution(collectionFilesInDirectory)
            persistence.setPostmanTestCollectionsToExecute(postmanTestCollectionsSelectedForExecution)
        }
    }

    /**
     * Adds each element of [listOfElementsToAddIfMissing] to [postmanTestCollectionsInChosenDirectory] if
     * [postmanTestCollectionsInChosenDirectory] does not already contain the element.
     */
    private fun addAllToCollectionList(listOfElementsToAddIfMissing: List<PostmanTestFile>) {
        for (postmanTestFile in listOfElementsToAddIfMissing) {
            if (!postmanTestCollectionsInChosenDirectory.contains(postmanTestFile)) {
                postmanTestCollectionsInChosenDirectory.add(postmanTestFile)
            }
        }
    }

    /**
     * Removes each element of [postmanTestCollectionsInChosenDirectory] that is not also in [listOfElementsToNotRemove]
     */
    private fun removeAllMissingFromCollectionList(listOfElementsToNotRemove: List<PostmanTestFile>) {
        postmanTestCollectionsInChosenDirectory.removeIf {
            !listOfElementsToNotRemove.contains(it)
        }
    }

    /**
     * Adds each element of [listOfElementsToAddIfMissing] to [postmanTestCollectionsSelectedForExecution] if
     * [postmanTestCollectionsSelectedForExecution] does not already contain the element.
     */
    private fun addAllForExecution(listOfElementsToAddIfMissing: List<PostmanTestFile>) {
        for (postmanTestFile in listOfElementsToAddIfMissing) {
            if (!postmanTestCollectionsSelectedForExecution.contains(postmanTestFile)) {
                postmanTestCollectionsSelectedForExecution.add(postmanTestFile.toPostmanTest())
            }
        }
    }

    /**
     * Removes each element of [postmanTestCollectionsSelectedForExecution] that is not also in
     * [listOfElementsToNotRemove]
     */
    private fun removeAllMissingForExecution(listOfElementsToNotRemove: List<PostmanTestFile>) {
        postmanTestCollectionsSelectedForExecution.removeIf {
            !listOfElementsToNotRemove.contains(it.toPostmanTestFile())
        }
    }

}
