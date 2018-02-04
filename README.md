A HTTP server written in Kotlin.

It doesn't do anything interesting yet. It's just a chance to get familiar with how servers work.

# Running it in development

## Run via IntelliJ

Run the `Run Server` run config, which runs the server on `localhost:4444`.

## Run via Gradle

Run the `runServer` Gradle task, which runs the server on `localhost:4444`.

N.B.: When running the server via Gradle, killing the task does not kill the server.

# Running it for real

## Locally

* Run the `jar` Gradle task
* Run the JAR (e.g. `sudo java -jar example/build/libs/example.jar 80`)
* Connect from another machine on the same network using the server machine's 
  local IP address (and not the router's address!)

## Remotely

* Run the `jar` Gradle task
* Copy the JAR to the remote machine (e.g. 
  `scp example/build/libs/example.jar joeldudley@35.190.167.101:~`)
* Run the JAR (e.g. `sudo java -jar example.jar 80`)

Other stuff:

* add your ssh key to the server
* ssh into the machine (e.g. `ssh joeldudley@35.190.167.101`)
* install java (e.g. `sudo apt-get install openjdk-8-jre`)
* disable http firewall via google cloud platform interface

* need to spin server off into its own thread - but then how would I kill it?