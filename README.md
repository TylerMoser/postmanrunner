# Postman Test Collection Runner
### What is it?
An alternative UI for executing Postman test collections.

### Why use this utility?
* Easily execute a subset of many Postman test collections
* Quickly and easily switch in and out test collections from the set being executed
* Quickly and easily view results of test executions
* Faster and more responsive UI than the Postman application when executing lots of test collections
* No need to manage newman commands
* No need to reimport test collections into the Postman application as they change. Collection files are executed directly.

### Installation Instructions?
#### Install Node.js

Windows:
> https://nodejs.org/en/#download

Mac OSX:
> https://nodejs.org/en/download/

or
> brew install node

Debian-based Linux Distributions:
> sudo apt-get install nodejs

#### Install newman
> npm install -g newman

#### Install newman html reporter
> npm install -g newman-reporter-html

#### Install Java and JavaFX (Version 8 or newer)
> https://www.oracle.com/technetwork/java/javase/downloads/index.html

or, on Debian-based Linux Distributions: 
> sudo apt-get install default-jdk

#### Download the application
Download the postmanrunner.jar file in build/libs/ from this repository.
Run the Jar file.

> Note: This application saves user settings and test collection execution results in the folder where the Jar is located. You may want to place the jar in its own directory for that reason.

### How to use?
* Select the folder containing your Postman test collection json files by going to Preferences > Set Postman Test Collection Directory
* If needed, select a Postman environment file to use for your tests by going to Preferences > Set Postman Environment File
* Select the tests that you want to execute and click the 'Add Selected Tests To Execution List' button
* Click 'Execute Tests' to run each of the test collections in the table
* Double click on a test collection's entry in the table to open that collection's HTML output in your default web browser
* If desired, navigate to the newman directory in the folder containing this application's jar file to see the input, output, and error logs from each test execution along with the JSON and HTML outputed by the newman reporters for these file types.
