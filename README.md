# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

Link to my Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAEYAdAAZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATG43N0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4qXap1JqtAzqBJoIei0azF5vDgHYsgwr5y7Drco67F8H5LCBALnMCspqig5QIAePKwvuh6ouisTYgmhgumGbqlOSVI0q+KAmkS4YWhy3K8gagrCjAxGhmRbqdlh5Q0dojrOgSuEsh6uqZLG-rTiGrrSpGHLRjAgnxnK2HJlBJYoTy2a5pg-4gjBJRXAM5bAeOrZfNOs7Nvpi7thphTZD2MD9oOvQ6SOowLpOfRGY2JkTn0S6cKu3h+IEXgoOge4Hr4zDHukmSYFZF5FNQJYVNIACiu5JfUSXNC0D6qE+3RuXO5lsup5TTtASAAF7xIk5QADz5eg+RqQpUCaeCMAIfYYXIaFvpoRimGyRqPGkgRgkBkGxloKRTLMeJXI8jGQa0WE9VoIxM1ia18pSUtHFYUNTG8RwKDcAJQbjXW7lTetZoRoUlrSCdFKGNJ2hCiKunivtSZ-s15QoWFKkIHmzUsVeaY9N+4OdjFYB9gOQ7eSunh+RukK2ru0IwAA4qOrIRae0XnswmkJdjaWZfYo55RNV2FfJQIlqVUAVVVaC1atjXqVtcHtdCyHQn1GGcYm3GHSNFJjat023RR828q98jvTAq03eRP1xU622K8AIvYWLG2ksgsS46Mqi0p9JFq7N92UQtOOjst9GW9bm2XrB5SmygMmwZ2xV7oLaiqepYPxRDfRU2b4yVP0kcoAAktI0dOL2ADMAAsTwnpkBoVhMXw6AgoANrnwH508ccAHKjvneyNFDYcw8T8N2bHePRxUbejInydp5nUzZ-qlsrAXRcgCXw-l1MVc130Xn12YPko+ugTYD4UDYNw8D8YYXspJFZ45CT7taZUtQNJT1PBLTc5DjPowN8cDOpiVQZlZVBDVTAdU3w1Q4R6OauTl-hNUZi1E+bVPR6lhHAHeQssR6wOobd0o1zrS1duaOaVFFpxjenRVWolME83KDrRBOFxbuigZkL2sJ75W0IXddkVobQO1GA6Bhocta8y9j7Lhfs-qwK9JkIGIMwGcIAgA7uSdygpwzo-DsGtYalFsv-OOPcZF9yRr5FeARLAnQQskGAAApCAPJWGGACIXYuRMj5sh-OUaolI7wtDjjTS6t9eib2AHoqAcAIAISgLMNR0h5G-jOGA1+fp35sw5r-NA+R-5eJ8X4gJKwADqLB47pRaAAIV3AoOAABpL4wTe5yNAamYhMAABWpi0CwhMcpFAaJ+pkINrdfCks0FxJlurW28scHBjwStOJGCIxVNId9dp4ZOlgBocE3pNsmHYwpOYp2wSxmcLajwvag1yHINKH4LQ1DRy0NHInRZYl+mUmwMc3ejsOIcI1qxNZuzfYa2Ko0tAIiKkFmeeDUokN6anCUSo3oWjl7+QCF4bxXYvSwGANgTehA2b70JrDOxALKjJVSulTKxh6a-Qib8ygVSQDcDwLCAa7zpl4XJfCqlmy5qPVOi9XaSs6IMW+s-YE5REDwp+SHf5YcrihKbkfFuiNF4riAA

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
