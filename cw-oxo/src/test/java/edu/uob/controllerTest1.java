package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


//This is the case when I added the player number from two to three
class controllerTest1 {
    private OXOModel model;
    private OXOController controller;

    // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
    // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.setNumberOfPlayers(3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        model.addPlayer(new OXOPlayer('Y'));
        //model.addPlayer(new OXOPlayer('Y'));
        controller = new OXOController(model);
    }

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    // Test out basic win detection
    @Test
    void testThreeBasicWin() throws OXOMoveException {

        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a bunch of moves for the two players

        //case 1:
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("b2"); // third
        sendCommandToController("a2"); // first
        sendCommandToController("c1"); // sec
        sendCommandToController("c2"); // third
        sendCommandToController("a3"); // first


        // a1, a2, a3 should be a win for the first player (since players alternate between moves)
        // Let's check to see whether the first moving player is indeed the winner
        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    }

    // Test simple move taking and cell claiming functionality
    @Test
    void testBasicMoveTaking() throws OXOMoveException {
        // Find out which player is going to make the first move
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
        OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+2);
        System.out.println(firstMovingPlayer);
        System.out.println(secondMovingPlayer);
        System.out.println(thirdMovingPlayer);
        // Make a move
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("a3");
        // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
        String failedTestComment = "Cell a1 wasn't claimed by the first player";
        assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
        assertEquals(secondMovingPlayer, controller.gameModel.getCellOwner(0, 1), failedTestComment);
        assertEquals(thirdMovingPlayer, controller.gameModel.getCellOwner(0, 2), failedTestComment);
    }



    @Test
    void testHorizentalWin() throws OXOMoveException{
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

        //OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
        //model.setCurrentPlayerNumber(0);
        //when the player 1 win
        //Case1: Horizental
        sendCommandToController("a1"); // First
        sendCommandToController("b1"); // Second
        sendCommandToController("c1"); // third
        sendCommandToController("a2"); // first
        sendCommandToController("b2"); // second
        sendCommandToController("c2"); // third
        sendCommandToController("a3"); //first

        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testVerticalWin() throws OXOMoveException{
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        //when the player 1 win

        sendCommandToController("a1"); // First player
        sendCommandToController("a2"); // Second player
        sendCommandToController("a3"); // third
        sendCommandToController("b1"); // first
        sendCommandToController("b2"); // second
        sendCommandToController("b3"); //third
        sendCommandToController("c1"); //first


        String failedTestComment3 = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment3);



    }


    @Test
    void testVerticalWin2() throws OXOMoveException{
        //OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

        OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+1);
        //model.setCurrentPlayerNumber(0);
        //when the player 2 win

        sendCommandToController("a1"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("c1"); // third
        sendCommandToController("b1"); // first
        sendCommandToController("c2"); // second
        sendCommandToController("b3"); // third
        sendCommandToController("a3");   //first
        sendCommandToController("a2"); //second



        String failedTestComment = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment);
    }

    //i will check when third player win just in case.

    @Test
    void testThirdWin() throws OXOMoveException{
        OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber()+2);
        controller.addColumn();
        controller.addRow();

        //4*4 for make space
        //win - horizental
        sendCommandToController("a1"); //first
        sendCommandToController("b1"); //second
        sendCommandToController("c1"); //third
        sendCommandToController("d1"); //first
        sendCommandToController("a2"); //second
        sendCommandToController("c2"); //third
        sendCommandToController("a3"); //first
        sendCommandToController("b2"); //second
        sendCommandToController("c3"); //third

        String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);


        controller.reset();
        //still 4*4, back to first moving
        //win-vertical

        sendCommandToController("a1"); //first
        sendCommandToController("a2"); //second
        sendCommandToController("a3"); //third
        sendCommandToController("a4"); //first
        sendCommandToController("b1"); //second
        sendCommandToController("b3"); //third
        sendCommandToController("b2"); //first
        sendCommandToController("b4"); //second
        sendCommandToController("c3"); //third

        assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);


        //diagonal case
        controller.reset();
        //a1 b2 c3

        sendCommandToController("b1"); //first
        sendCommandToController("c1"); //second
        sendCommandToController("c3"); //third
        sendCommandToController("a3"); //first
        sendCommandToController("c2"); //second
        sendCommandToController("b2"); //third
        sendCommandToController("a2"); //first
        sendCommandToController("d1"); //second
        sendCommandToController("a1"); //third
        assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
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
    void testReset()throws OXOMoveException{

        sendCommandToController("a1"); // First player
        sendCommandToController("a2"); // Second player
        sendCommandToController("b1"); // third
        sendCommandToController("b2"); // first
        sendCommandToController("c1"); // second
        controller.reset();
        String failedTestComment10 = "Every column and row should be " + null + " but wasn't";
        String failedTestComment11 = "The winner should be " + null + " but wasn't";
        String failedTestComment12 = "The win threshold should be " + 3 + " but wasn't";
        String failedTestComment13 = "the new game is start from first player ";
        String failedTestComment14 = "the number of player should not change.";


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
        assertEquals(model.getWinThreshold(),3, failedTestComment12);
        assertEquals(model.getCurrentPlayerNumber(), 0, failedTestComment13);
        //number of player should not change
        assertEquals(model.getNumberOfPlayers(), 3, failedTestComment14);

    }
    @Test
    void testDraw()throws OXOMoveException{

        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("c1"); // third
        sendCommandToController("c3"); // first
        sendCommandToController("b2"); // sec
        sendCommandToController("c2"); // third
        sendCommandToController("a2"); // first
        sendCommandToController("a3"); // second
        sendCommandToController("b3"); // third

        String failedTestComment = "This game should be draw";
        assertTrue(model.isGameDrawn(), failedTestComment);
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


}

