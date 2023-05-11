package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

class ExampleSTAGTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
      //pass this one
    String response = sendCommandToServer("simon: look");
    response = response.toLowerCase();
//      response = "A log cabin in the woods You can see:\n" +
//              "potion\n" +
//              "trapdoor\n" +
//              "You can access from here: [forest]\n";
    System.out.println("res  " + response);
    assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    assertTrue(response.contains("forest"), "Did not see available paths in response to look");
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      //pass this one
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");

      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }
    // Add more unit tests or integration tests here.

  @Test
  void testGetKeyPotion() {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");

      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");

      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");

      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion")&& response.contains("key"), "Did not see the potion in the inventory after an attempt was made to get it");

  }


  @Test
  void testUseArtefact(){
      //check use potion.
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");

      sendCommandToServer("simon: drink potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "You still have potion in your inventory?");


  }

  @Test
  void testOpenkey(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");

      sendCommandToServer("simon: open key");
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cellar"), "Failed to add new path");


        //check consume key
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "it supposed to not to have key");



      //and check goto cellar//

      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"), "Failed attempt to use 'goto' command to move to the cellar - there is no key in the current location");

  }
  @Test
  void testAddhealth(){
       String response;
       sendCommandToServer("simon: get potion");
       response = sendCommandToServer("simon: inv");
       response = response.toLowerCase();
       assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
       sendCommandToServer("simon: goto forest");
       sendCommandToServer("simon: get key");
       sendCommandToServer("simon: goto cabin");
       sendCommandToServer("simon: open key");
       sendCommandToServer("simon: goto cellar");


        //check health is decreasing or not.

      response = sendCommandToServer("simon: hit elf");
      response = response.toLowerCase();
      assertTrue(response.contains("health : 2"), "Failed attempt to use fight and manage health");
      //check again with flexible command
      response = sendCommandToServer("simon: fight with elf");
      response = response.toLowerCase();
      assertTrue(response.contains("health : 1"), "Failed attempt to use fight and manage health");


      //check drink potion affect health
      response = sendCommandToServer("simon: drink potion");
      response = response.toLowerCase();
      assertTrue(response.contains("2"), "Failed attempt to drink potion and restore the health");

      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Failed attempt to consume potion");
  }
  @Test
  void testDead(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: open key");
      sendCommandToServer("simon: goto cellar");



        //death case
      sendCommandToServer("simon: hit elf");
      sendCommandToServer("simon: fight elf");
      response = sendCommandToServer("simon: attack elf");
      assertTrue(response.contains("died"), "it does not work die case");


        //after die
      //check whether the player go back to initaial place.
      response = sendCommandToServer("simon: look");

      //it supposed to keep this path
      assertTrue(response.contains("cellar"), "it does not work die case");
      assertTrue(response.contains("forest"), "it does not work die case");
      //u cannot see potion in initial location
      assertFalse(response.contains("potion"), "it does not work die case");

      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("potion"), "it does not work die case");

      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: look");
      // i need to drop all the things in current location where player die
      assertTrue(response.contains("potion"), "it does not work die case");





  }





}
