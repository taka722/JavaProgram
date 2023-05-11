package edu.uob;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
public class AdditionalSTAGTest {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will time out if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testLook() {
        //pass this one
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();


        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");

        assertTrue(response.contains("sharp axe"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("silver coin"), "Did not see a description of artifacts in response to look");

    }
    @Test
    void testGet()
    {
        //pass this one
        String response;
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");

        response = response.toLowerCase();

        assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
        assertTrue(response.contains("coin"), "Did not see the coin in the inventory after an attempt was made to get it");
        assertTrue(response.contains("axe"), "Did not see the potion in the inventory after an attempt was made to get it");

        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
        assertFalse(response.contains("coin"), "Potion is still present in the room after an attempt was made to get it");
        assertFalse(response.contains("axe"), "Potion is still present in the room after an attempt was made to get it");



    }
    @Test
    void testGoto()
    {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Failed attempt to use 'goto' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'goto' command");


        sendCommandToServer("simon: goto riverbank");
        String response1 = sendCommandToServer("simon: look");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("horn"), "Failed attempt to use 'goto' command");
        assertTrue(response1.contains("river"), "Failed attempt to use 'goto' command");


    }
    @Test
    void testCutTree(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        sendCommandToServer("simon: cut down tree");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("log"), "Failed attempt to use 'cut' command");


        //check collectable or not
        sendCommandToServer("simon: get log");
        String response1 = sendCommandToServer("simon: inv");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("log"), "Failed attempt to use 'cut' command");

    }

    @Test
    void testBridge(){


        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");

        sendCommandToServer("simon: cut tree");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("log"), "Failed attempt to use 'cut' command");


        //check collectable or not
        sendCommandToServer("simon: get log");
        String response1 = sendCommandToServer("simon: inv");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("log"), "Failed attempt to use 'cut' command");

        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: bridge riverbank");
        String response2 = sendCommandToServer("simon: look");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("clearing"), "Failed attempt to use 'bridge' command ");

        //check consume stuff
        String response3 = sendCommandToServer("simon: inv");
        response3 = response3.toLowerCase();
        assertFalse(response3.contains("log"), "Failed attempt to use 'bridge' command ");

    }

    @Test
    void testPay(){
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get potion");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");


        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"), "Failed attempt to use 'open' command ");

        //check consume key
        String response1 = sendCommandToServer("simon: inventory");
        response1 = response1.toLowerCase();
        assertFalse(response1.contains("key"), "Failed attempt to use 'open' command ");

        //check current location
        sendCommandToServer("simon: goto cellar");
        String response2 = sendCommandToServer("simon: look");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("elf"), "Failed attempt to use 'open' and 'goto' command ");

        //check pay and produce shovel
        String response3 = sendCommandToServer("simon: pay coin");
        response3 = response3.toLowerCase();
        assertTrue(response3.contains("shovel"), "Failed attempt to use 'pay' command");

        //check produce in current location
        String response4 = sendCommandToServer("simon: look");
        response4 = response4.toLowerCase();
        assertTrue(response4.contains("shovel"), "Failed attempt to use 'pay' command ");

        //check collectable or not
        sendCommandToServer("simon: get shovel");
        String response5 = sendCommandToServer("simon: inv");
        response5 = response5.toLowerCase();
        assertTrue(response5.contains("shovel"), "Failed attempt to use 'get' command ");

        //player is going to die and new player comming up case.
        String response6 = sendCommandToServer("simon: hit elf");
        response6 = response6.toLowerCase();
        assertTrue(response6.contains("health : 2"), "Failed attempt to use 'hit' command & consume health ");

        sendCommandToServer("simon: hit elf");
        String response7 = sendCommandToServer("simon: hit elf");
        response7 = response7.toLowerCase();
        assertTrue(response7.contains("died"), "Failed attempt to use 'hit' command & dead case ");


        //check multiple player.
        String response8 = sendCommandToServer("Takashi: look");
        //it supposed to be at inital place(cabin)
        response8 = response8.toLowerCase();
        assertTrue(response8.contains("simon"), "Failed attempt to use 'look' command &  implement multiple player");
        assertTrue(response8.contains("trapdoor"), "Failed attempt to use 'look' command &  implement multiple player");


        sendCommandToServer("Takashi: goto cellar");
        String response9 = sendCommandToServer("Takashi: look");
        //it needs to appear the artefact when one player died and drop all artefact that this player have.
        response9 = response9.toLowerCase();
        assertTrue(response9.contains("potion"), "Failed attempt to use 'look' command &  implement multiple player");
        assertTrue(response9.contains("shovel"), "Failed attempt to use 'look' command &  implement multiple player");
        assertTrue(response9.contains("elf"), "Failed attempt to use 'look' command &  implement multiple player");


        //when one player died, this player needs to locate the
        String response10 = sendCommandToServer("simon: look");
        response10 = response10.toLowerCase();
        assertTrue(response10.contains("trapdoor"), "Failed attempt to use 'look' command &  implement multiple player");

        //check player who are dead do not have artefact that he collected before he died.
        String response11 = sendCommandToServer("simon: inv");
        response11 = response11.toLowerCase();
        assertFalse(response11.contains("potion"), "Failed attempt to use 'hit' command and die case");
        assertFalse(response11.contains("shovel"), "Failed attempt to use 'hit' command and die case");
    }


    @Test
    void testDig(){
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get axe");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: cut tree");
        sendCommandToServer("simon: get log");

        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open key");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: pay coin");
        sendCommandToServer("simon: get shovel");

        sendCommandToServer("simon: goto cabin");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: bridge river");
        sendCommandToServer("simon: goto clearing");

        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("soil"), "Failed attempt to use 'open' command ");


        String response1 = sendCommandToServer("simon: dig ground");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("gold"), "Failed attempt to use 'dig' command ");

        sendCommandToServer("simon: get gold");
        String response2 = sendCommandToServer("simon: inv");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("gold"), "Failed attempt to use 'get' command ");


    }
    //riverbank horn
    @Test
    void testBlow(){
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get axe");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: cut tree");
        sendCommandToServer("simon: get log");

        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open key");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: pay coin");
        sendCommandToServer("simon: get shovel");

        sendCommandToServer("simon: goto cabin");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");

        sendCommandToServer("simon: get horn");
        sendCommandToServer("simon: goto riverbank");

        String response = sendCommandToServer("simon: blow horn");
        response = response.toLowerCase();
        assertTrue(response.contains("lumberjack"), "Failed attempt to use 'blow' command ");

        String response1 = sendCommandToServer("simon: look");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("lumberjack"), "Failed attempt to use 'look' command and put the produces in current location");


    }




}
