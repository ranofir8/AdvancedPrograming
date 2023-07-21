# Book Scrabble
Advanced Programming
Bar-Ilan 83-677 Course, 2023



## About the project

This project is a Book Scrabble game that contains some features:


A desktop game application in 2 modes: host and guest (JavaFX application). see more details about this application [here](https://shorturl.at/qxNVZ).

A server for Book queries (Java application).

A server for saving and restoring games from our DB (RESTful API, MYSQL using Hibernate and TomCat services).

A smartphone application (Android and iOS) to view details of a saved game.



# How To Build:

## Prerequisites for playing as guest/host:

Open JDK - version 11+

JavaFX JDK - javafx.fxml, javafx.controls, javafx.media

## Additional prerequisites for running the GameServer (3rd milestone):
TomCat v9


## Follow the steps below to build the project:

Open your preferred IDE (Eclipse, IntelliJ, Visual Studio Code, etc.).

Go to Files -> New -> Import Project.

Import the project from this repository.

### Running the servers
To start the BookServer, run

    src\main\java\ap\ex2\BookScrabbleServer\MainScrabbleServer.java
To start the GameServer, run

    src\main\java\ap\ex3\ with TomCat (package to WAR)
    
and MySQL with the correct tabels and [user: JavaUser ; password: eli]
### Playing the game
To host or join a game, run 

    src\main\java\ap\ex2\bookscrabble\ScrabbleGame.java
    
or (if the above will not work - missing JavaFX components)
    
    src\main\java\ap\ex2\bookscrabble\JarMain.java

# Watch the Demos:

1. Full demo of the game + how to build it (Up to the second milestone):
   
   [![here](https://img.youtube.com/vi/N-JbX_DCnog/mqdefault.jpg)](https://youtu.be/N-JbX_DCnog)
3. See our presentation, including design, project architecture and insights (milestones three and four):
   [![here](https://img.youtube.com/vi/AeEG5uR77gM/mqdefault.jpg)](https://youtu.be/AeEG5uR77gM)


# The Team:

Gilad Savoray

Ran Ofir

Ofir Yehezkel

Moriya Weitzman


