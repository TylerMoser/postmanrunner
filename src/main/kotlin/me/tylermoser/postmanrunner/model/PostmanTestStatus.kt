package me.tylermoser.postmanrunner.model

/**
 * An enum representing the different status's that a Postman test collection can have.
 */
enum class PostmanTestStatus {

    TEST_EXECUTION_NOT_STARTED{
        override fun toString(): String {
            return "Test Not Started"
        }
    },

    TEST_EXECUTING{
        override fun toString(): String {
            return "Test Execution In Progress"
        }
    },

    PASS{
        override fun toString(): String {
            return "Pass"
        }
    },

    FAIL{
        override fun toString(): String {
            return "Fail"
        }
    }

}
