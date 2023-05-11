package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;



class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    System.out.println(firstMovingPlayer);
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  void testBasicWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    //case 1:
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player


    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
  @Test
  void testHorizentalWin1() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    //OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 1 win
    //Case1: Horizental
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);

    controller.reset();
    OXOPlayer firstMovingPlayer1 = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    sendCommandToController("b2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("a3"); // Sebcond player
    sendCommandToController("b1"); // First player

    String failedTestComment11 = "Winner was expected to be " + firstMovingPlayer1.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer1, model.getWinner(), failedTestComment11);

    controller.reset();
    OXOPlayer firstMovingPlayer2 = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    sendCommandToController("c2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c3"); // First player

    String failedTestComment12 = "Winner was expected to be " + firstMovingPlayer2.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer2, model.getWinner(), failedTestComment12);

    controller.reset();
    OXOPlayer firstMovingPlayer3 = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    //longer version use every cell
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player


    String failedTestComment13 = "Winner was expected to be " + firstMovingPlayer2.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer3, model.getWinner(), failedTestComment13);


  }

  //Test horizental way of winning
  @Test
  void testHorizentalWin2() throws OXOMoveException{
    //OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 2 win
    //Case1: Horizental
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("b3"); // First player

    String failedTestComment2 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment2);

    controller.reset();

    sendCommandToController("b2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("a2"); // Second player

    String failedTestComment21 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment21);

    controller.reset();

    sendCommandToController("b2"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("c1"); // Second player


    String failedTestComment22 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment22);

  }


  @Test
  void testVerticalWin1() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    //when the player 1 win

    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player

    String failedTestComment3 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment3);


  }



  @Test
  void testVerticalWin2() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    //when the player 1 win

    sendCommandToController("b2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c2"); // First player

    //error here
    String failedTestComment31 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment31);


  }

@Test
  void testVerticalWin3() throws OXOMoveException{

    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c3"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a3"); // First player


    String failedTestComment32 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment32);

  }



  @Test
  void testVerticalWin4() throws OXOMoveException{
    //OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 2 win

    sendCommandToController("a1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("c3"); // First player
    sendCommandToController("a2"); // First player


    String failedTestComment4 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment4);

    controller.reset();

    sendCommandToController("a2"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("c1"); // Second player


    String failedTestComment41 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment41);

    controller.reset();

    sendCommandToController("b2"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("a1"); // First player
    sendCommandToController("b3"); // Second player
    String failedTestComment42 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment42);


  }




  @Test
  void testDiagonalWin1() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    //OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 1 win

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("c3"); // First player

    String failedTestComment5 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment5);
  }


  @Test
  void testDiagonalWin2() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a3"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player


    String failedTestComment51 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment51);

  }

  @Test
  void testDiagonalWin3()throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("b2"); // First player

    String failedTestComment52 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment52);



  }
  @Test
  void testDiagonalWin4() throws OXOMoveException{
    //OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 1 win

    sendCommandToController("b1"); // First player
    sendCommandToController("a1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("c3"); // second player
    String failedTestComment6 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment6);

    controller.reset();

    sendCommandToController("a1"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c3"); // First player
    sendCommandToController("c1"); // Second player

    String failedTestComment61 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment61);


  }

  @Test
  void testAfterWin() throws OXOMoveException{

    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);

    String failedTestComment1 = "After winning somebody, we cannot continue without using add row column or etc..";
    sendCommandToController("c3");
    assertNull(model.getCellOwner(2,2),failedTestComment1);

  }
  @Test
  void testExpandRow() throws OXOMoveException {
    int firstSize = model.getNumberOfRows();
    controller.addRow();
    int secSize = model.getNumberOfRows();

    String failedTestComment7 = "Size of Row is expected to " + secSize + " but wasn't";
    assertEquals(firstSize,secSize-1, failedTestComment7);
  }

  @Test
  void testMaximumRow() throws OXOMoveException{
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();

    int firstSize = model.getNumberOfRows();
    //it should be 9 right now
    //if I add row next, it should not add row, because maximum size is 9.
    controller.addRow();
    int SecSize = model.getNumberOfRows();

    String failedTestComment = "Size of Row is expected to " + firstSize + " but wasn't";
    assertEquals(SecSize, firstSize, failedTestComment);
  }


  @Test
  void testAddColumn() throws OXOMoveException{
    int firstSize = model.getNumberOfColumns();
    controller.addColumn();
    int secSize = model.getNumberOfColumns();

    String failedTestComment8 = "Size of Column is expected to " + secSize + " but wasn't";
    assertEquals(firstSize,secSize-1, failedTestComment8);
  }

  @Test
  void testMaximumColumn() throws OXOMoveException{
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();

    int firstSize = model.getNumberOfColumns();
    //it should be 9 right now
    //if I add row next, it should not add row, because maximum size is 9.
    controller.addColumn();
    int SecSize = model.getNumberOfColumns();

    String failedTestComment = "Size of Column is expected to " + firstSize + " but wasn't";
    assertEquals(SecSize, firstSize, failedTestComment);
  }
  @Test
  void testRemoveRow() throws OXOMoveException {
    int firstSize = model.getNumberOfRows();
    controller.removeRow();
    int secSize = model.getNumberOfRows();

    String failedTestComment9 = "Size of Row is expected to " + secSize + " but wasn't";
    assertEquals(firstSize,secSize+1, failedTestComment9);
  }

  @Test
  void testMinimumRow() throws OXOMoveException{
    //3*3
    controller.removeRow();
    controller.removeRow();
    int firstSize = model.getNumberOfRows();
    //it should be 9 right now
    //if I add row next, it should not add row, because maximum size is 9.
    controller.removeRow();
    //the minimum should be 1, so it should not remove
    int SecSize = model.getNumberOfRows();


    //it should be equal between first one and second.
    String failedTestComment = "Size of Row is expected to " + SecSize + " but wasn't";
    assertEquals(SecSize,firstSize, failedTestComment);
  }
  @Test
  void testRemoveColumn() throws OXOMoveException{
    int firstSize = model.getNumberOfColumns();
    controller.removeColumn();
    int secSize = model.getNumberOfColumns();

    String failedTestComment10 = "Size of Column is expected to " + secSize + " but wasn't";
    assertEquals(firstSize,secSize+1, failedTestComment10);
  }

  @Test
  void testMinimumColumn() throws OXOMoveException{
    //3*3
    controller.removeColumn();
    controller.removeColumn();
    int firstSize = model.getNumberOfColumns();
    //it should be 9 right now
    //if I add row next, it should not add column, because maximum size is 9.
    controller.removeColumn();
    //the minimum should be 1, so it should not remove
    int SecSize = model.getNumberOfColumns();


    //it should be equal between first one and second.
    String failedTestComment = "Size of Column is expected to " + SecSize + " but wasn't";
    assertEquals(SecSize,firstSize, failedTestComment);
  }

  @Test
  void testReset()throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player

    controller.reset();
    String failedTestComment10 = "Every column and row should be " + null + " but wasn't";
    String failedTestComment11 = "The winner should be " + null + " but wasn't";
    String failedTestComment12 = "The win threshold should be " + 3 + " but wasn't";
    String failedTestComment13 = "The game drawn should be " + false + " but wasn't";
    String failedTestComment14 = "the new game is start from first player ";

    assertNull(model.getCellOwner(0, 0), failedTestComment10);
    assertNull(model.getCellOwner(0, 1), failedTestComment10);
    assertNull(model.getCellOwner(0, 2), failedTestComment10);
    assertNull(model.getCellOwner(1, 0), failedTestComment10);
    assertNull(model.getCellOwner(1, 1), failedTestComment10);
    assertNull(model.getCellOwner(1, 2), failedTestComment10);
    assertNull(model.getCellOwner(2, 0), failedTestComment10);
    assertNull(model.getCellOwner(2, 1), failedTestComment10);
    assertNull(model.getCellOwner(2, 2), failedTestComment10);
    assertNull(model.getWinner(),failedTestComment11);
    assertFalse(model.isGameDrawn(), failedTestComment13);
    //how can I check win-thresh hold is not changed in reset?
    assertEquals(model.getWinThreshold(),3, failedTestComment12);
    assertEquals(model.getCurrentPlayerNumber(), 0, failedTestComment14);
  }

  @Test
  void testReset1()throws OXOMoveException{
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b3"); // First player

    controller.reset();
    String failedTestComment14 = "Every column and row should be " + null + " but wasn't";
    String failedTestComment15 = "The winner should be " + null + " but wasn't";
    String failedTestComment16 = "The win threshold should be " + 3 + " but wasn't";
    String failedTestComment17 = "The game drawn should be " + false + " but wasn't";
    String failedTestComment18 = "the new game is start from first player ";

    assertNull(model.getCellOwner(0, 0), failedTestComment14);
    assertNull(model.getCellOwner(0, 1), failedTestComment14);
    assertNull(model.getCellOwner(0, 2), failedTestComment14);
    assertNull(model.getCellOwner(1, 0), failedTestComment14);
    assertNull(model.getCellOwner(1, 1), failedTestComment14);
    assertNull(model.getCellOwner(1, 2), failedTestComment14);
    assertNull(model.getCellOwner(2, 0), failedTestComment14);
    assertNull(model.getCellOwner(2, 1), failedTestComment14);
    assertNull(model.getCellOwner(2, 2), failedTestComment14);
    assertNull(model.getWinner(),failedTestComment15);
    assertFalse(model.isGameDrawn(), failedTestComment16);
    assertEquals(model.getWinThreshold(),3, failedTestComment17);
    assertEquals(model.getCurrentPlayerNumber(), 0, failedTestComment18);
  }

  @Test
  void testReset2() throws OXOMoveException{
    //testing that reset cannot change win thresh holds.
    sendCommandToController("a1");
    sendCommandToController("b1");
    sendCommandToController("c1");
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();
    //now game is 4*4 & win-thresh hold = 4
    sendCommandToController("c4");
    sendCommandToController("d1");
    sendCommandToController("b2");
    sendCommandToController("a2");
    sendCommandToController("b3");
    sendCommandToController("a3");
    sendCommandToController("b4");

    controller.reset();

    String failedTestComment = "Every column and row should be " + null + " but wasn't";
    String failedTestComment1 = "The winner should be " + null + " but wasn't";
    String failedTestComment2 = "The win threshold should be reset but wasn't";
    String failedTestComment3 = "The game drawn should be " + false + " but wasn't";
    String failedTestComment4 = "the new game is start from first player ";
    String failedTestComment5 = "the reset cannot reset the size of Row and Column. ";

    assertNull(model.getCellOwner(0, 0), failedTestComment);
    assertNull(model.getCellOwner(0, 1), failedTestComment);
    assertNull(model.getCellOwner(0, 2), failedTestComment);
    assertNull(model.getCellOwner(0, 3), failedTestComment);
    assertNull(model.getCellOwner(1, 0), failedTestComment);
    assertNull(model.getCellOwner(1, 1), failedTestComment);
    assertNull(model.getCellOwner(1, 2), failedTestComment);
    assertNull(model.getCellOwner(1, 3), failedTestComment);
    assertNull(model.getCellOwner(2, 0), failedTestComment);
    assertNull(model.getCellOwner(2, 1), failedTestComment);
    assertNull(model.getCellOwner(2, 2), failedTestComment);
    assertNull(model.getCellOwner(2, 3), failedTestComment);
    assertNull(model.getCellOwner(3, 0), failedTestComment);
    assertNull(model.getCellOwner(3, 1), failedTestComment);
    assertNull(model.getCellOwner(3, 2), failedTestComment);
    assertNull(model.getCellOwner(3, 3), failedTestComment);

    //it should start from first player
    assertNull(model.getWinner(),failedTestComment1);
    assertFalse(model.isGameDrawn(), failedTestComment2);
    assertEquals(model.getWinThreshold(),4, failedTestComment3);
    assertEquals(model.getCurrentPlayerNumber(), 0, failedTestComment4);
    assertEquals(model.getNumberOfRows(),4, failedTestComment5);
    assertEquals(model.getNumberOfRows(), 4, failedTestComment5);


  }


  @Test
  void testDraw()throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b3"); // First player


    String failedTestComment = "This game should be draw";
    assertTrue(model.isGameDrawn(), failedTestComment);


  }
  @Test
  void testDrawWin()throws OXOMoveException{
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b3"); // First player


    String failedTestComment = "This game should be draw";
    assertTrue(model.isGameDrawn(), failedTestComment);

    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();

    //4*4 and win thresh holds == 4

    sendCommandToController("d1"); // sec
    sendCommandToController("b4"); // first
    sendCommandToController("d2"); // sec
    sendCommandToController("a4"); // fir
    sendCommandToController("d3"); // sec
    sendCommandToController("c4"); // fir
    sendCommandToController("d4"); // sec


    String failedTestComment1 = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment1);

  }

  @Test
  void testDraw2()throws OXOMoveException{
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b3"); // First player


    String failedTestComment = "This game should be draw";
    assertTrue(model.isGameDrawn(), failedTestComment);

    controller.addRow();
    controller.addColumn();
    controller.addRow();
    controller.addColumn();

    controller.removeColumn();
    controller.removeColumn();
    //5*3 and win thresh holds == 3
    //test that cells which is occupied by player and the player change their mind and remove the column. if we remove
    //column to 3, they should not remove the cells which is occupied, I had a problem, so I will check.
    //3*3 should be occupied.

    String failedTestComment1 = "it should not be null.";
    assertNotNull(model.getCellOwner(0, 0), failedTestComment1);
    assertNotNull(model.getCellOwner(0, 1), failedTestComment1);
    assertNotNull(model.getCellOwner(0, 2), failedTestComment1);
    assertNotNull(model.getCellOwner(1, 0), failedTestComment1);
    assertNotNull(model.getCellOwner(1, 1), failedTestComment1);
    assertNotNull(model.getCellOwner(1, 2), failedTestComment1);
    assertNotNull(model.getCellOwner(2, 0), failedTestComment1);
    assertNotNull(model.getCellOwner(2, 1), failedTestComment1);
    assertNotNull(model.getCellOwner(2, 2), failedTestComment1);

  }

  @Test
  void testIncreaseWinthresh()throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.increaseWinThreshold();
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    controller.addRow();
    controller.addColumn();
    sendCommandToController("b3"); //second
    sendCommandToController("d1"); //first


    String failedTestComment13 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    String failedTestComment14 = "the win threshold should be " + 4 + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment13);
    assertEquals(model.getWinThreshold(), 4, failedTestComment14);

    //sendCommandToController("-");
  }

  @Test
  void testValidWinthresh() throws OXOMoveException{
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.decreaseWinThreshold();
    String failedTestComment = "the win threshold should be " + 4 + " but wasn't";
    assertEquals(model.getWinThreshold(), 4, failedTestComment);
  }

  @Test
  void testDecreaseInvalidWinthresh()throws OXOMoveException{
    // here is win-thresh hold is 3, we cannot decrease the win-thresh hold.
    controller.decreaseWinThreshold();
    String failedTestComment16 = "the win threshold should be " + 3 + " but wasn't";
    assertEquals(model.getWinThreshold(), 3, failedTestComment16);

  }



  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidLengthIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("aaa"), failedTestComment);

  }

  @Test
  void testInvalidCharacterIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for this command";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("ba"), failedTestComment);
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("11"), failedTestComment);
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("1a"), failedTestComment);
  }

  @Test
  void testInvalidOutsideIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierOutsideException for this command";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("b4"), failedTestComment);
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("d1"), failedTestComment);
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("e1"), failedTestComment);
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("c5"), failedTestComment);
  }

 @Test
  void testInvalidRemoveColumnRow() throws OXOMoveException {
    //Test for remove row or column, when one of each is occupied by player. they should not remove them.
   OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

   sendCommandToController("a3"); //first
   sendCommandToController("c1"); //second

   controller.removeRow();
   controller.removeColumn();

   String failedTestComment = "the the size of row should be " + 3 + " but wasn't";
   String failedTestComment1 = "the size of column should be " + 3 + " but wasn't";


   assertEquals(model.getNumberOfRows(), 3, failedTestComment);
   assertEquals(model.getNumberOfColumns(), 3, failedTestComment);

  }

  @Test
  void testInvalidAlreadyTakenIdentifierException() throws OXOMoveException {
     //Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierOutsideException for this command";
     //The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);


    model.setCellOwner(0, 0,model.getPlayerByNumber(model.getCurrentPlayerNumber()));
    model.setCellOwner(0,1, model.getPlayerByNumber(model.getCurrentPlayerNumber()));
    model.setCellOwner(1,0, model.getPlayerByNumber(model.getCurrentPlayerNumber()));
    model.setCellOwner(0,2, model.getPlayerByNumber(model.getCurrentPlayerNumber()));
    model.setCellOwner(1,1, model.getPlayerByNumber(model.getCurrentPlayerNumber()));
    model.setCellOwner(1,2, model.getPlayerByNumber(model.getCurrentPlayerNumber()));


    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a1"), failedTestComment);
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a2"), failedTestComment);
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("b1"), failedTestComment);

    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a3"), failedTestComment);
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("b2"), failedTestComment);
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("b3"), failedTestComment);
  }



  @Test
  void testValiddecreaseWinthreshhold() throws OXOMoveException{
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.addColumn();
    controller.addColumn();
    controller.addRow();
    controller.addRow();
    //win-thresh holds == 5; 5*5
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); //sec
    sendCommandToController("a4"); //first
    sendCommandToController("b4"); //sec
    sendCommandToController("a5");

    String failedTestComment = "Every column and row should be " + null + " but wasn't";
    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);

    //we can decrease the win-thresh hold after win.
    controller.decreaseWinThreshold();
    controller.decreaseWinThreshold();

    controller.reset();

    //and I also check reset methods.
    assertNull(model.getCellOwner(0, 0), failedTestComment);
    assertNull(model.getCellOwner(0, 1), failedTestComment);
    assertNull(model.getCellOwner(0, 2), failedTestComment);
    assertNull(model.getCellOwner(0, 3), failedTestComment);
    assertNull(model.getCellOwner(0, 4), failedTestComment);
    assertNull(model.getCellOwner(1, 0), failedTestComment);
    assertNull(model.getCellOwner(1, 1), failedTestComment);
    assertNull(model.getCellOwner(1, 2), failedTestComment);
    assertNull(model.getCellOwner(1, 3), failedTestComment);
    assertNull(model.getCellOwner(1, 4), failedTestComment);
    assertNull(model.getCellOwner(2, 0), failedTestComment);
    assertNull(model.getCellOwner(2, 1), failedTestComment);
    assertNull(model.getCellOwner(2, 2), failedTestComment);
    assertNull(model.getCellOwner(2, 3), failedTestComment);
    assertNull(model.getCellOwner(2, 4), failedTestComment);
    assertNull(model.getCellOwner(3, 0), failedTestComment);
    assertNull(model.getCellOwner(3, 1), failedTestComment);
    assertNull(model.getCellOwner(3, 2), failedTestComment);
    assertNull(model.getCellOwner(3, 3), failedTestComment);
    assertNull(model.getCellOwner(3, 4), failedTestComment);
    assertNull(model.getCellOwner(4, 0), failedTestComment);
    assertNull(model.getCellOwner(4, 1), failedTestComment);
    assertNull(model.getCellOwner(4, 2), failedTestComment);
    assertNull(model.getCellOwner(4, 3), failedTestComment);
    assertNull(model.getCellOwner(4, 4), failedTestComment);

    String failedTestComment2 = "The win-thresh hold should be" + 3 + " but wasn't";
    assertEquals(model.getWinThreshold(), 3, failedTestComment2);

  }

  @Test
  void testWinAndContinue() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player


    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);


    controller.addRow();
    controller.addColumn();
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();

    sendCommandToController("b3"); //sec
    sendCommandToController("a4"); //first
    sendCommandToController("b4"); //sec
    sendCommandToController("a5"); //first

    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);
  }

  //increaseing win-thresh holds and win
  @Test
  void testaddWinthreshWin() throws OXOMoveException{
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

    //OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
    //model.setCurrentPlayerNumber(0);
    //when the player 1 win
    //Case1: Horizental
    controller.addColumn();
    controller.addRow();
    controller.increaseWinThreshold();

    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3");
    sendCommandToController("a4");

    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);

  }
  @Test
  void testExtremecaseWin() throws OXOMoveException{

    //vertical
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.removeColumn();
    controller.removeColumn();

    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    //9*1
    sendCommandToController("d1");//first
    sendCommandToController("A1");//sec
    sendCommandToController("e1");//first
    sendCommandToController("b1");//sec
    sendCommandToController("I1");//first
    sendCommandToController("h1");//sec
    sendCommandToController("f1");//first

    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);



  }
  @Test
  void testExtremecaseWin2() throws OXOMoveException{
    //horizental just in case
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.removeRow();
    controller.removeRow();

    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    //1*9

    sendCommandToController("a4"); //first
    sendCommandToController("a9"); //sec
    sendCommandToController("a6"); //first
    sendCommandToController("a1"); //sec
    sendCommandToController("a5"); //first

    String failedTestComment1 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment1);


  }







}

