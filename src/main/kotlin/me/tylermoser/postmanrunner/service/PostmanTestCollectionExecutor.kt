package me.tylermoser.postmanrunner.service

import me.tylermoser.postmanrunner.model.PostmanTest
import me.tylermoser.postmanrunner.model.PostmanTestStatus
import org.apache.commons.lang3.SystemUtils
import tornadofx.*
import java.io.File

/**
 * Executes a single Postman test collection.
 *
 * This is done by using Newman to execute the test collection on a command line.
 */
class PostmanTestCollectionExecutor: Component(), ScopedInstance {

    private val commandExecutor: CommandExecutor by inject()
    private val persistence: TornadoFXConfigurationPersistence by inject()

    /**
     * Executes a single Postman test collection
     */
    fun executeTestCollection(test: PostmanTest) {
        test.status = PostmanTestStatus.TEST_EXECUTING
        val result = commandExecutor.executeCommand(
                logFolderPath = "newman",
                logFilePrefix = test.fileName,
                // On Windows the command should read "powershell newman args".
                // Newman can be executed directly on other platforms.
                commandToExecute = if (SystemUtils.IS_OS_WINDOWS) "powershell" else "newman",
                commandArguments = getArguments(test))
        test.status = if (result) PostmanTestStatus.PASS else PostmanTestStatus.FAIL
    }

    /**
     * Sets the arguments for the newman command execution
     */
    private fun getArguments(test: PostmanTest): List<String> {
        val arguments = mutableListOf<String>()
        if (SystemUtils.IS_OS_WINDOWS) arguments.add("newman")
        arguments.add("run")
        if (persistence.isPostmanEnvironmentFileSelected()) {
            arguments.add("--environment")
            arguments.add("'${persistence.getPostmanEnvironmentFile()}'")
        }
        arguments.add("--reporters")
        arguments.add("json,html")
        arguments.add("--reporter-json-export")
        arguments.add("'newman" + File.separator + test.fileName + ".json'")
        arguments.add("--reporter-html-template")
        arguments.add("templates" + File.separator + "htmlreqres.hbs")
        arguments.add("--reporter-html-export")
        arguments.add("'newman" + File.separator + test.fileName + ".html'")
        arguments.add("'${test.fullyQualifiedFileName}'")
        return arguments
    }

}
