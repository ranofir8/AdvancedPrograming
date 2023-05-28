# Advanced Programing
Bar-Ilan 83-677 Course, 2023

This project is a full Book Scrabble game in a desktop application. The application is a JavaFX project and includes the following components:

A server for Book queries.
A desktop game application with two modes:
Host mode: Allows users to connect to the server and invite friends to play.
Guest mode: Enables users to join an existing game using the host's properties (IP and port).

The Books the server uses as dictionart:
A lice in wonderland
Frank Herbert - Dune
Harry Potter
mobydick
scrubble-sowpods
shakespeare
The Matrix

# How To Build:

## Prerequisites:

Open JDK
JavaFX JDK

Follow the steps below to build the project:

Open your preferred IDE (Eclipse, IntelliJ, Visual Studio Code, etc.).
Go to Files -> New -> Import Project.
Import the project from this repository.
To start the server, run src\main\java\ap\ex2\BookScrabbleServer\MainScrabbleServer.java.
To start or join a game, run src\main\java\ap\ex2\bookscrabble\ScrabbleGame.java.

# Watch the Demo:

# The Team:

Gilad Savoray
Ran Ofir
Ofir Yehezkel
Moriya Weitzman

# Next Steps:

Implement HTTP communication between the model and the server in REST style.
Implement saving games in a database using an ORM layer.
Develop an Android application for guest players.

#
