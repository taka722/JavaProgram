# Simple Text Adventure Game

## Overview
This game engine server is designed to communicate with game clients to play any text-based adventure game, provided the game conforms to certain rules. 
The server listens for incoming client connections, processes commands, manages game state, and sends responses back to the clients.

## Project File Description  
- **GameClient.java** - Allows users to input game commands and communicates with a game server on localhost at port 8888, displaying server responses to the user.
  
- **GameServer.java** - Managing a game's state, processing commands from players, and interacting with game configurations(Graphviz  and XML formats).
  
- **GameAction.java** - It represents an action within a game, encompassing verb phrases, subject phrases, entities used and produced by the action, and a narration describing the action's outcome.

- **GameState.java** - Maintains the state of a game, tracking the current location, furniture, artefacts, players, and all game locations using various collections.

- **Path.java** - It represents a directional pathway between two game locations, encapsulating information about the starting location (fromLocation) and the destination (toLocation).

- **Player.java** -  Models a game player with attributes like name, health, and current location. Players have an inventory to hold artefacts, can move between locations, modify their health, and interact with artefacts in their current location or in their inventory.
  
- **GameEntity.java** - It represents a foundational game element with a name and description, serving as a base for more specialized game entities like locations, characters, and artifacts.

- **Artefact.java** - Extending GameEntity.java, represents a specific game item with a name and description, and provides a method to retrieve its name.

- **Character.java** - Extending GameEntity, represents a game character with health and provides a method to check if the character is still alive.

- **Location.java** -  Extending GameEntity, models a location within the game, consisting of available paths, characters, artefacts, and furniture, alongside functionalities to manage these entities within the location.

## Getting Started
You are able to start the game by typing the following commands in the command line:
```
stag $ ./mvnw clean compile
```
```
stag $ ./mvnw exec:java@server
```
```
stag $ ./mvnw exec:java@client -Dexec.args= "your name"
```

## How to Play
You are able to use the following commands: 

- "inventory" (or "inv" for short): lists all of the artefacts currently being carried by the player
- "get": picks up a specified artefact from the current location and adds it into player's inventory
- "drop": puts down an artefact from player's inventory and places it into the current location
- "goto": moves the player to the specified location (if there is a path to that location)
- "look": prints names and descriptions of entities in the current location and lists paths to other locations
