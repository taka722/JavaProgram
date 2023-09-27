# Tic Tac Toe (OXO) Game 
## Introduction
This project is a Java-based GUI implementation of the classic Tic-Tac-Toe (OXO) game using Java's AWT library. It incorporates functionalities such as dynamic resizing of the game board and modifying the win threshold.

## Default OXO Game
### Description:
The default game is a 3x3 grid where two players, O and X, take turns to mark a cell. The first player to get 3 of their marks in a row (horizontally, vertically, or diagonally) wins.

### How to Play:
1. Start the game.
2. Player X goes first, followed by Player O.
3. Input the cell identifier (e.g., A1, B2) where you want to place your mark.
4. The game ends when one player has 3 marks in a row or all cells are filled (a draw).

## Enhanced OXO Game
### Description:
The enhanced version builds upon the default game by adding error handling, customizable win thresholds, support for more players, and other advanced features.

### New Features:
#### 1. Error Handling:
・ Invalid Identifier Length: The cell identifier is not two characters.

・ Invalid Identifier Character: The row isn't a letter or the column isn't a digit.

・ Outside Range: The identifiers are valid, but they're out of range.

・ Already Taken: The cell is already claimed by a player.

#### 2. Win Threshold Customization:
・ Press + to increase the win threshold.

・ Press - to decrease the win threshold.

### 3. Dynamic Board Resizing: 
・ Adjust the board's rows and columns using mouse actions.

#### 4. Support for Multiple Players:
・ The game can now support more than two players. Each player will have a unique character to mark their cells.

###  How to Play:
#### Execute this program 
```
cw-oxo % ./mvnw clean compile
```
```
cw-oxo % ./mvnw exec:java
```

<p align="center">
  <a href="https://github.com/taka722/JavaProgram/assets/98585910/2ae098c9-24d8-4e4b-9f8e-7715a7d9c4aa">
    <img src="https://github.com/taka722/JavaProgram/assets/98585910/2ae098c9-24d8-4e4b-9f8e-7715a7d9c4aa" alt="IMAGE ALT TEXT">
  </a>
</p>


https://github.com/taka722/JavaProgram/assets/98585910/2ae098c9-24d8-4e4b-9f8e-7715a7d9c4aa




1. Start the game.
2. Players take turns in sequence.
3. Input the cell identifier (e.g., A1, B2) where you want to place your mark.
4. Use the + and - keys during the game to adjust the win threshold as desired.
5. Use the cursor　to add or remove a column or row.
6. The game ends when one player meets the current win threshold or all cells are filled (a draw).
### Testing:
Automated tests have been provided to validate the functionality of both versions of the game. These tests check for basic gameplay, error handling, and other game rules.

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.
