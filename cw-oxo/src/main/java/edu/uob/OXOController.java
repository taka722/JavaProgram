package edu.uob;

import edu.uob.OXOMoveException.*;
import java.util.ArrayList;

public class OXOController {
    OXOModel gameModel;
    //this i is to keep updating about the current state.
    private int i = 0; //counting how many times change the player//
    private int current = 0; //index about which player did finish the turn. 0 or 1

    private ArrayList<Integer> playerOrder;

    public OXOController(OXOModel model) {
        gameModel = model;

    }

    //OXOController (via a call to the handleIncomingCommand method).
    // Your task is to interpret this incoming command and update the game state accordingly.
    public void handleIncomingCommand(String command) throws OXOMoveException {


        int com = command.length();
        int rows;
        int columns;
        int cnt = 1;//counting same Object "X" or "O"

        char s; // character 'a' 'b' 'c'
        char s1;

        if(com != 2){
            throw new OXOMoveException.InvalidIdentifierLengthException(com);
        }
        //change to the lower case when user input the capital letter, such as 'A', 'B'.
        command = command.toLowerCase();
        //get the first index.
        s = command.charAt(0);
        //change first index(alphabet) to the integer.
        rows = s - 'a';
        //get the second index as a char type
        s1 = command.charAt(1);
        //change data type from char to int. (-1: we want to start from 0.)
        columns = s1 - '1';
        //I have to make it char
        if(!(checkRowLetter(command))){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW,s);
        }
        else if(!(checkColumnLetter(command))){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN,s1);
        }
        else if(checkRowCell(rows)){
            throw new OutsideCellRangeException(RowOrColumn.ROW,rows);
        }
        else if(checkColumnCell(columns)){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN,columns);
        }
        //Already Taken: The specified cell exists, but it has already been claimed by a player
        else if(gameModel.getCellOwner(rows, columns) != null){
            throw new CellAlreadyTakenException(rows,columns);
        }

        if (gameModel.getWinner() == null) {

            //flexible number of player,I use arraylist for storing the size of player to make it flexible.
            int player = gameModel.getNumberOfPlayers();
            playerOrder = new ArrayList<>(player);
            for(int i = 0; i < player; i++) {
                playerOrder.add(i);
            }
            int fleeter = playerOrder.get(0);
            playerOrder.remove(0);
            playerOrder.add(fleeter);

            for(int p =0;p < player; p++) {
                if (i % player == p) {
                    int n = gameModel.getCurrentPlayerNumber();
                    gameModel.setCellOwner(rows, columns, gameModel.getPlayerByNumber(n));
                    current = p;
                    gameModel.setCurrentPlayerNumber(playerOrder.get(p));
                }
            }
            i = i + 1;

            //win detection
            int valueH = HorizontalCheck(rows, columns, cnt);
            int valueV = VerticalCheck(rows, columns, cnt);
            int valueD1 = DiagonalCheck1(rows, columns, cnt);
            int valueD2 = DiagonalCheck2(rows, columns, cnt);
            //probably I have to call the draw case.
            if (checkFillboard() == 0) {
                decreaseWinThreshold();
            }
            else if (valueH == gameModel.getWinThreshold()) {
                gameModel.setWinner(gameModel.getPlayerByNumber(current));
            }
            else if (valueV == gameModel.getWinThreshold()) {
                gameModel.setWinner(gameModel.getPlayerByNumber(current));
            }
            else if (valueD1 == gameModel.getWinThreshold()) {
                gameModel.setWinner(gameModel.getPlayerByNumber(current));
            }
            else if(valueD2 == gameModel.getWinThreshold()){
                gameModel.setWinner(gameModel.getPlayerByNumber(current));
            }
            else if(checkFillboard() == gameModel.getNumberOfRows() * gameModel.getNumberOfColumns()){
                gameModel.setGameDrawn();
            }
        }
        //because it should stop after win
        else{

        }
    }

    private int checkFillboard(){
        int cnt = 0;
        for(int i = 0; i < gameModel.getNumberOfRows(); i++){
            for(int j = 0; j < gameModel.getNumberOfColumns(); j++){
                if(gameModel.getCellOwner(i, j) != null){
                    cnt++;

                }
            }
        }
        return cnt;
    }


    private boolean checkRowLetter(String command){
        char s; // character 'a' 'b' 'c'
        command = command.toLowerCase();

        s = command.charAt(0); // it should be a, b, c, d, e, f, g, h, i
        //s1 = command.charAt(1);// it should be 1, 2, 3, 4, 5, 6, 7, 8, 9
        if(s >= 'a' && s <= 'i'){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean checkColumnLetter(String command){
        char s1;
        command = command.toLowerCase();
        s1 = command.charAt(1); // it should be 1, 2, 3, 4, 5, 6, 7, 8, 9
        if(s1 >= '1' && s1 <= '9'){
            return true;
        }
        else{
            return false;
        }

    }


    private boolean checkRowCell(int rows){
        if(rows < 0 || rows >= gameModel.getNumberOfRows()){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean checkColumnCell(int columns){
        if(columns < 0 || columns >= gameModel.getNumberOfColumns()){
            return true;
        }
        else{
            return false;
        }
    }



    //To check when the player(cell) is outside the row and column.
    private boolean checkExistCell(int rows, int columns){
        if(rows < 0 || rows >= gameModel.getNumberOfRows() || columns < 0 || columns >= gameModel.getNumberOfColumns()){
            return true;
        }
        else{
            return false;
        }
    }



    private int HorizontalCheck(int rows, int columns, int cnt) {
        int win = gameModel.getWinThreshold();
        while (cnt < win) {
            //check right side
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows, columns + i + 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows, columns + i + 1)) {
                    cnt = cnt + 1;
                }
                else{
                    break;
                }
            }
            //check left side
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows, columns - i - 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows, columns - i - 1)) {
                    cnt = cnt + 1;
                }
                else {
                    break;
                }

            }
            break; //to run away from the while loop
        }
        return cnt;
    }


    private int VerticalCheck(int rows, int columns, int cnt){
        int win = gameModel.getWinThreshold();
        while (cnt < win) {
            //check upside
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows + i + 1, columns)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows + i + 1, columns)) {
                    cnt = cnt + 1;
                }
                else{
                    break;
                }
            }
            //check downside
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows - i - 1, columns)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows - i - 1, columns)) {
                    cnt = cnt + 1;
                }
                else {
                    break;
                }

            }
            break; //to run away from the while loop
        }
        return cnt;

    }
    private int DiagonalCheck1(int rows, int columns, int cnt){
        int win = gameModel.getWinThreshold();
        while (cnt < win) {
            //check left upside
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows -i - 1, columns - i - 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows - i -1, columns - i - 1)) {
                    cnt = cnt + 1;
                }
                else{
                    break;
                }
            }
            //check right downside
            for (int i = 0; i < win; i++) {
                if (checkExistCell(rows + i + 1, columns + i + 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows + i + 1, columns+ i + 1)) {
                    cnt = cnt + 1;
                }
                else {
                    break;
                }
            }
            break; //to run away from the while loop
        }
        return cnt;

    }

    private int DiagonalCheck2(int rows, int columns, int cnt){
        int win = gameModel.getWinThreshold();
        while(cnt < win){
            for (int i = 0; i < win; i++) {
                //check right upside
                if (checkExistCell(rows - i - 1, columns + i + 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows - i - 1, columns + i + 1)) {
                    cnt = cnt + 1;
                }
                else {
                    break;
                }

            }
            for (int i = 0; i < win; i++) {
                //check left downside
                if (checkExistCell(rows + i + 1, columns - i - 1)) {
                    break;
                }
                else if (gameModel.getCellOwner(rows, columns) == gameModel.getCellOwner(rows + i + 1, columns - i - 1)) {
                    cnt = cnt + 1;
                }
                else {
                    break;
                }
            }
            break; //to run away from the while loop
        }
        return cnt;
    }




    public void addRow() {
        //set maximum row
        if(gameModel.getNumberOfRows() < 9 && gameModel.getWinner() != null){
            gameModel.addRow();
            gameModel.setWinner(null);
        }
        else if(gameModel.getNumberOfRows() < 9 && gameModel.isGameDrawn()){
            gameModel.addRow();
            gameModel.resetGameDrawn();
        }

        else if(gameModel.getNumberOfRows() < 9) {
            gameModel.addRow();
        }

    }
    public void removeRow() {
        //if there is no null cell in the row, it should not do that.
        int cnt = 0;
        for(int i = 0; i < gameModel.getNumberOfColumns(); i++){
            if(gameModel.getCellOwner(gameModel.getNumberOfRows()-1, i) == null){
                cnt++;
            }
        }
        if(cnt == gameModel.getNumberOfColumns()&& gameModel.getNumberOfRows() > 1){
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        //set maximum 10
        if(gameModel.getNumberOfColumns() < 9 && gameModel.getWinner() != null){
            gameModel.addColumn();
            gameModel.setWinner(null);
        }
        else if(gameModel.getNumberOfColumns() < 9 && gameModel.isGameDrawn()){
            gameModel.addColumn();
            gameModel.resetGameDrawn();
        }
        else if(gameModel.getNumberOfColumns() < 9) {
            gameModel.addColumn();
        }


    }
    public void removeColumn() {
        //if there is no null cell in the row, it should not do that.
        int cnt = 0;
        for(int i = 0; i < gameModel.getNumberOfRows(); i++){
            if(gameModel.getCellOwner(i, gameModel.getNumberOfColumns()-1) == null){
                cnt++;
            }
        }
        if(cnt == gameModel.getNumberOfRows() && gameModel.getNumberOfColumns() > 1){
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold()+1);
    }
    public void decreaseWinThreshold() {
        //I cannot manipulate after beginning the game
        if(i == 0 && gameModel.getWinThreshold() > 3){
            gameModel.setWinThreshold(gameModel.getWinThreshold()-1);
        }
        //still manipulate after win.
        else if(gameModel.getWinner() != null && gameModel.getWinThreshold() > 3){
            gameModel.setWinThreshold(gameModel.getWinThreshold()-1);
        }
        else{
            gameModel.setWinThreshold(gameModel.getWinThreshold());
        }
        //gameModel.setWinThreshold(gameModel.getWinThreshold()-1);
    }
    public void reset() {
        //When user want to reset the game, they can press esc button to reset the game.
        //clear the board and reinitialise the game state to the original settings.
        for(int i = 0; i < gameModel.getNumberOfRows(); i++){
            for(int k = 0; k < gameModel.getNumberOfColumns(); k++){
                gameModel.setCellOwner(i, k, null);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
        gameModel.setWinner(null);
        gameModel.resetGameDrawn();
        i = 0;

        //if I try to reset, do not change player num, only cells should be clear.

        //minimum size of the board would be 1X1
        //minumum size of the winthresh should be 3
        //test reset.



//        1. reset functions should not reset the numberOfPlayers.
//        2. addPlayer in Model can only be called in the setup method in test.
//        3. cannot remove cell that is occupied.
//        4. remove row/column should not lead to draw.
//        5. allow to add/remove cell after the game has been won as long as the cell has not been occupied.
//        6. allow to add/remove winThreshold after the game has been won.
//        7. Reset doesn’t reset the winThreshold
//        8. Minimum winThreshold should be 3(following the poll result, not the limitation written on the workbook
    }
}
