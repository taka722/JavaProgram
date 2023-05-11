package edu.uob;

import java.util.*;
public class OXOModel {
    //declare the arraylist.
    //ArrayList<OXOPlayer> cells;
    //2d ArrayList
    private ArrayList<ArrayList<OXOPlayer>> cells;

    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        players = new OXOPlayer[2];
        cells = new ArrayList<>();
        for(int i = 0; i < numberOfRows; i++){
            cells.add(new ArrayList<>());
            for(int j = 0; j < numberOfColumns; j++){
                cells.get(i).add(null);
            }
        }
    }
    public int getNumberOfPlayers() {
        return players.length;
    }

    public void setNumberOfPlayers(int i){
        players = new OXOPlayer[i];
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {

        return players[number];
    }

    public OXOPlayer getWinner() {

        return winner;
    }

    public void setWinner(OXOPlayer player) {

        winner = player;
    }

    public int getCurrentPlayerNumber() {

        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {

        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {

        //return cells.length;
        //should I change something?
        return cells.size();
    }


    public int getNumberOfColumns() {
        //return cells[0].length;
        //should I change here as well?
        //I counted the number of columns through counting objects at Row(0).
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        //i feel need to change something
        //return cells[rowNumber][colNumber];
        //i will change from here
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        //cells[rowNumber][colNumber] = player;
        //I will edit from here// set means = //
        cells.get(rowNumber).set(colNumber, player);
    }

        public void setWinThreshold (int winThresh){

            winThreshold = winThresh;
        }

        public int getWinThreshold () {

            return winThreshold;
        }

        public void setGameDrawn () {

            gameDrawn = true;
        }

        public void resetGameDrawn(){
            gameDrawn = false;
        }


        public boolean isGameDrawn () {

            return gameDrawn;
        }

        public void addColumn () {
        //4. Add two methods to OXOModel called addColumn() and
            // addRow() that allow the board size to grow
        //use loop to add column
            for(int j = 0; j < getNumberOfRows(); j++) {
                cells.get(j).add(null);
            }
        }
        public void addRow () {
            cells.add(new ArrayList<>());
            for(int i = 0; i < getNumberOfColumns(); i++){
                //the index of row number will be counted from 0, so I need to subtract from the numberOfRows
                cells.get(getNumberOfRows()-1).add(null);
            }
        }
        public void removeColumn () {
        //5. Add two methods to OXOModel called removeColumn() and
            //removeRow() that allow the board size to shrink
            for(int j = 0; j < getNumberOfRows(); j++){
               if(getCellOwner(j, getNumberOfColumns()-1) == null) {
                   cells.get(j).remove(getNumberOfColumns() - 1);
               }
            }
        }
        public void removeRow () {
        cells.remove(getNumberOfRows()-1);
        }



}


