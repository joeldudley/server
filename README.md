A HTTP server written in Kotlin.

It doesn't do anything interesting yet. It's just a chance to get familiar with how servers work.

# Running it in development

## Run via IntelliJ

Run the `Run Server` run config, which runs the server on `localhost:4444`.

## Run via Gradle

Run the `runServer` Gradle task, which runs the server on `localhost:4444`.

N.B.: When running the server via Gradle, killing the task does not kill the server.

# Running it for real

* Run the `jar` Gradle task
* Run `java -jar example/build/libs/example.jar [PORT_NUMBER]`