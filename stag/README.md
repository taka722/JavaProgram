# Simple Text Adventure Game

## Overview
This game engine server is designed to communicate with game clients to play any text-based adventure game, provided the game conforms to certain rules. 
The server listens for incoming client connections, processes commands, manages game state, and sends responses back to the clients.

## Project File Description  
- **GameClient.java** - Allows users to input game commands and communicates with a game server on localhost at port 8888, displaying server responses to the user.
  
- **GameServer.java** - Managing a game's state, processing commands from players, and interacting with game configurations(Graphviz  and XML formats).
  
- **GameAction.java** - It represents an action within a game, encompassing verb phrases, subject phrases, entities used and produced by the action, and a narration describing the action's outcome.
  
- **GameEntity.java** - 
- **Artefact.java**
- **Character.java**
- **GameState.java**
- **Location.java**
- **Path.java**
- **Player.java**
