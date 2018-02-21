A HTTP server written in Kotlin.

It doesn't do anything interesting yet. It's just a chance to get familiar with how servers work.

# Running it in development

## Via IntelliJ

* Run the `Run Server` run config, which runs the server on `localhost:4444`.

## Via Gradle

* Run the `runServer` Gradle task, which runs the server on `localhost:4444`.
    * When running the server via Gradle, killing the task does not kill the server.

## Run via the command line

* Run the `jar` Gradle task from the root of the project to create the JAR
* Run the JAR (e.g. `sudo java -jar example/build/libs/example.jar 80`)
    * `sudo` is required to run the server on a port as low as 80
* Connect from another machine on the same network using the server machine's local IP address
    * Get the local IP address using `ipconfig getifaddr en0`
    * Do not use the router's IP address (the one you see when you Google your IP address)

# Running it for real

## Prepping a Google Cloud Platform server

* Add your SSH key to the server
* SSH into the machine (e.g. `ssh joeldudley@35.190.167.101`)
* Install java (e.g. `sudo apt-get install openjdk-8-jre`)
* Disable HTTP firewall via google cloud platform interface

## Copying over the JAR

* Run the `jar` Gradle task from the root of the project to create the JAR
* Copy the JAR to the remote machine (e.g. `scp example/build/libs/example.jar joeldudley@35.190.167.101:~`)

## Running the server

* Run the JAR (e.g. `sudo java -jar example.jar 80`)
    * `sudo` is required to run the server on a port as low as 80
    * Need to spin server off into its own thread - but then how would I kill it?
* Leave the machine using `exit`