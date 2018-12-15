package me.tylermoser.postmanrunner.model

import java.io.File
import java.util.*

/**
 * Serves as a data structure for a Postman test collection file. This structure does not contain any execution status,
 * and is used only to refer to a Postman test collection file that exists on the file system. This file likely has the
 * potential to be executed, but is not set to be executed.
 */
open class PostmanTestFile(open val fullyQualifiedFileName: String = "") {

    @delegate:Transient
    val fileName by lazy {
        val beginningOfFileNameIndex = fullyQualifiedFileName.lastIndexOf(File.separatorChar) + 1
        fullyQualifiedFileName.substring(beginningOfFileNameIndex).removeSuffix(".postman_collection.json")
    }

    /**
     * Concerts this object to a [PostmanTest]. This is done before this test collection is executed so that it can
     * keep track of the test collection's execution status.
     */
    fun toPostmanTest(): PostmanTest {
        return PostmanTest(fullyQualifiedFileName)
    }

    /**
     * toString for an instance of this class returns only the fully qualified file name associated with that instance
     */
    override fun toString(): String {
        return fileName
    }

    override fun equals(other: Any?): Boolean {
        return (other is PostmanTestFile && other.fullyQualifiedFileName == this.fullyQualifiedFileName)
    }

    override fun hashCode(): Int {
        return Objects.hash(fullyQualifiedFileName)
    }

}