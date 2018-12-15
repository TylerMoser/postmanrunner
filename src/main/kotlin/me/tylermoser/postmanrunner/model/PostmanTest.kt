package me.tylermoser.postmanrunner.model

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.util.*

/**
 * Serves as a data structure for a Postman test collection that may be executed. This model differs from
 * [PostmanTestFile] in that it is aware of the execution state of this test collection.
 */
class PostmanTest(
        override val fullyQualifiedFileName: String = "",
        status: PostmanTestStatus = PostmanTestStatus.TEST_EXECUTION_NOT_STARTED)
    : PostmanTestFile(fullyQualifiedFileName)
{

    // JavaFX properties using TornadoFX implementation
    // These properties update the UI to automatically reflect their values
    private val statusProperty = SimpleObjectProperty(status)
    fun statusProperty() = statusProperty
    var status: PostmanTestStatus by statusProperty

    /**
     * Converts this object to a [PostmanTestFile]
     */
    fun toPostmanTestFile(): PostmanTestFile {
        return PostmanTestFile(fullyQualifiedFileName)
    }

    override fun equals(other: Any?): Boolean {
        return (other is PostmanTestFile && other.fullyQualifiedFileName == fullyQualifiedFileName)
    }

    override fun hashCode(): Int {
        return Objects.hash(fullyQualifiedFileName)
    }

}
