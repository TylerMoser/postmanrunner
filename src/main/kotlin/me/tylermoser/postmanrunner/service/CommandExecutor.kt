package me.tylermoser.postmanrunner.service

import tornadofx.*
import java.io.File

/**
 * A helper class for executing a system command independently of the JVM's host operating system.
 */
class CommandExecutor: Component(), ScopedInstance {

    /**
     * Executes the command passed in as a [String] and waits for the command to complete. This method is operating
     * system independent and includes separate logic for Windows and Unix-based operating systems. This method blocks
     * while the command is executing, and opens a new window to display the command's execution.
     *
     * @param logFolderPath The path to the folder to store the log files in
     * @param logFilePrefix A unique identifier for this command. Used for determining the correct log file.
     * @param commandToExecute The OS-independent command to be executed as a string.
     * @param commandArguments The arguments for the command
     *
     * @return True if the command executed successfully. False otherwise.
     */
    fun executeCommand(logFolderPath: String, logFilePrefix: String, commandToExecute: String, commandArguments: List<String>): Boolean {
        return with(ProcessBuilder()) {
            command(commandToExecute, *commandArguments.toTypedArray())
            createFiles(logFolderPath, logFilePrefix)
            printCommand(commandToExecute, commandArguments)
            start()
        }.waitFor() == 0
    }

    /**
     * Creates the output directory structure and log files for the execution, and redirects the process output streams
     * to these log files.
     *
     * @param logFolderPath The path to the folder to store the log files in
     * @param logFilePrefix A unique identifier for this command. Used for determining the correct log file.
     */
    private fun ProcessBuilder.createFiles(logFolderPath: String, logFilePrefix: String) {
        File(logFolderPath).mkdirs()
        val inF = File(logFolderPath + File.separator + logFilePrefix + "_in.log")
        val errF = File(logFolderPath + File.separator + logFilePrefix + "_err.log")
        val outF = File(logFolderPath + File.separator + logFilePrefix + "_out.log")
        inF.createNewFile()
        errF.createNewFile()
        outF.createNewFile()
        this.redirectInput(inF)
        this.redirectError(errF)
        this.redirectOutput(outF)
    }

    /**
     * Prints a string of the command being executed to STDOUT
     *
     * @param commandToExecute The command that is being executed
     * @param commandArguments The arguments being passed to the command
     */
    private fun printCommand(commandToExecute: String, commandArguments: List<String>) {
        print("$commandToExecute ")
        for (arg in commandArguments) print("$arg ")
        println()
    }

}
