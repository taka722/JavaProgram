package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.GraphAttributes;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Id;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.PortNode;
import com.sun.java.accessibility.util.EventID;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;





/** This class implements the STAG server. */
public final class GameServer {

    private Map<String, Node> nodes = new HashMap<>();

    private List<Edge> edges = new ArrayList<>();
    private List<Graph> sections = new ArrayList<>();

    private HashMap<String,Location> locations = new HashMap<>();

    private List<Location> locationList = new ArrayList<>();
    private List<Artefact> artefacts = new ArrayList<>();
    private HashMap<String, Artefact> artefactHM = new HashMap<>();
    private List<Furniture> furnitures = new ArrayList<>();

    private HashMap<String, Furniture> furnitureHM= new HashMap<>();

    private List<Character> characters = new ArrayList<>();

    private HashMap<String, Character> characterHM = new HashMap<>();


    private HashMap<String, Player> players = new HashMap<>();


    private List<Path> paths = new ArrayList<>();

    //if I set the "verb" as string, I can get Action.
    private HashMap<String, HashSet<GameAction>> HS;
    //private HashMap<String,HashSet<GameAction>> actions;

    private GameState gameState;

    private int index = 0;




    private static final char END_OF_TRANSMISSION = 4;

    ArrayList<String> basicCommand = new ArrayList<>(Arrays.asList(
            "inventory","inv", "get", "look",
            "drop", "goto", "open","unlock",
            "chop","cut","cut down", "drink",
            "fight", "hit", "attack", "pay",
            "bridge", "dig", "blow"));

    ArrayList<String> subjects = new ArrayList<>(Arrays.asList("trapdoor", "potion", "tree", "elf", "key", "axe"
    ,"coin","shovel", "log", "river", "ground", "horn", "forest","hole", "gold","lumberjack", "cabin", "cellar", "clearing",
            "riverbank"));

    ArrayList<String> decorateWords = new ArrayList<>(Arrays.asList("with", "the", "please",
            "and", "i", "want", "at", "in", "use"));



    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        //parseEntities(entitiesFile);
        parseEntity(entitiesFile);
        parseAction(actionsFile);

        //testActionPrint();
        //setGameStateFirst();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here

        String response = "";
        //it should set the initial gamestate when the player play this game for the first time.
        try {

            if (index == 0) {
                setGameStateFirst();
            }
            index++;

            // Normalize and tokenize the input command
            String normalizeCommand = normalizeInput(command);
            List<String> tokens = tokenizeInput(normalizeCommand);

            // Remove decorative words
            List<String> essentialTokens = removeDecorativeWords(tokens);


            List<String> verbPhrases = new ArrayList<>();
            List<String> subjectPhrases = new ArrayList<>();


            for (String essentialToken : essentialTokens) {
                if (basicCommand.contains(essentialToken)) {
                    verbPhrases.add(essentialToken);
                } else if (subjects.contains(essentialToken)) {
                    subjectPhrases.add(essentialToken);
                }
            }
            String playerName = essentialTokens.get(0);

            Player playermain = null;//= setPlayerFirst("simon");
            for (Player player : players.values()) {
                if (player.getName().equals(playerName)) {
                    playermain = players.get(playerName);
                  //  System.out.println("check essential token " + playermain);
                    break;
                }
            }
            if (playermain == null) {
                playermain = setPlayerFirst(playerName);
               ////System.out.println("check essential token " + playermain);
            }


//            for (String verb1 : verbPhrases) {
//                //System.out.print("verb phrases  " + verb1);
//            }
//            for (String subject1 : subjectPhrases) {
//                //System.out.print("subject phrases  " + subject1);
//            }
            List<String> keysList = new ArrayList<>(HS.keySet());
            for (String verb : verbPhrases) {
                if (!(checkNumKeyVerbs(verbPhrases))) {
                    if (verb.equals("look")) {
                        //System.out.println("Check Point : look ");
                        response = commandLook(playermain);
                        break;
                    } else if ((verb.equals("inv")) || verb.equals("inventory")) {
                        //System.out.println("this inv loop");
                        response = commandInv(playermain);
                        break;
                    } else {
                        //System.out.println();
                        for (String subject : subjectPhrases) {
                            // exam: subject = "potion", "place"
                            if (verb.equals("get")) {
                                if (isArtefact(subject, playermain)) {
                                    response = commandGet(playermain, subject);
                                } else {
                                    response = " No artefact that you can collect.";
                                }
                            } else if (verb.equals("goto")) {

                                commandGoto(playermain, subject);

                                response = " ";
                            } else if (keysList.contains(verb)) {

                                HashMap<String, String> resource = new HashMap<>();
                                HashMap<String, String> resource1 = new HashMap<>();

                                HashSet<GameAction> actions_ = HS.get(verb);
                                //System.out.println("checkCurrentInv " + verb); //fine still "hit"
                                for (GameAction action1 : actions_) {
                                    List<String> subjects = action1.getSubjectPhrases();
                                    for (String sj : subjects) {
                                        resource.put(sj, checkEntityType(sj));
                                    }

                                    if (checkCurrentInv(playermain, resource)) {
                                        String res = updateGameState(playermain, action1);
                                        response = printNarration(verb, subject) + "\n" + res;
                                    } else {
                                        //System.out.print("error");
                                        response = "Did you forget to collect artefact?";
                                    }
                                }
                            } else {
                                response = "Invalid command";
                            }
                        }
                    }
                }else {
                    response = "Ambiguous input. Try again";
                }
            }

        } catch (Exception e){
            e.printStackTrace(); // Log the exception (optional)
            //System.err.println("An error occurred: " + e.getMessage());
            response = "Invalid input. Please try again.";
        }
        return response;
    }




    //command handler.
    public String normalizeInput(String command) {
        //String[] words = command.trim().split("\\s");
        return command.toLowerCase().replaceAll("\\p{Punct}", "").trim();
    }
    //command handler 2
    private List<String> tokenizeInput(String input) {
        return new ArrayList<>(Arrays.asList(input.split("\\s+")));
    }
    private List<String> removeDecorativeWords(List<String> tokens) {
        List<String> essentialTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!decorateWords.contains(token)) {
                essentialTokens.add(token);
            }
        }
        return essentialTokens;
    }

    private boolean checkNumKeyVerbs(List<String> essentialTokens){
        boolean num = false;
        int index = 0;
        for(String token: essentialTokens){
            for(String verb : basicCommand){
                if(token.equals(verb)){
                    index++;
                    if(index >=2){
                        num = true;
                        break;

                    }
                }
            }
        }
        return num;
    }

    private void setGameStateFirst() {
        String firstPlace = null;
        for (Location location : locationList) {
            if (location.getIndex() == 1) {
                firstPlace = location.getName();
            }
        }
        Location firstLocation = locations.get(firstPlace);
        gameState = new GameState(firstLocation);
    }

    private Player setPlayerFirst(String name) {
        //current location
        //artefacr that they can correct.
        //name.
        String firstPlace = null;
        for (Location location : locationList) {
            if (location.getIndex() == 1) {
                firstPlace = location.getName();
            }
        }
        Location firstLocation = locations.get(firstPlace);
        //gameState = new GameState(firstLocation);


        Player player = new Player(name,firstLocation);
        players.put(name, player);

        return player;

    }



    private String printNarration(String keyAction, String keysubject){

        HashSet<GameAction> actions = HS.get(keyAction);
        String narration = null;

        for(GameAction gameAction : actions){
            for(String subject : subjects){
                if(subject.equals(keysubject)){
                    narration = gameAction.getNarration();
                    break;
                }
            }
        }
        return narration;

    }

    private String checkEntityType(String keyaction){
        // element = axe / tree
        String entityType = null;

                // Check furniture
                for (Furniture furniture : furnitures) {
                    if (furniture.getName().equals(keyaction)) {
                        entityType = "furniture";
                        break;
                    }
                }
                // Check artefacts
                for (Artefact artefact : artefacts) {
                    if (artefact.getName().equals(keyaction)) {
                        entityType = "artefact";
                        break;
                    }
                }
                // Check characters
                for (Character character : characters) {
                    if (character.getName().equals(keyaction)) {
                        entityType = "character";
                        break;
                    }
                }
                for(Location location : locationList){
                    if(location.getName().equals(keyaction)){
                        entityType = "location";
                        break;
                    }
                }
                if(keyaction.equals("health")){
                    entityType = "health" ;
                }
        return entityType;
    }




    private boolean checkCurrentInv(Player player, HashMap<String, String> resource) {
        //cut = action
        //action1 = axe/tree
//        HashMap<String, String> resource = new HashMap<>();
        boolean haveArtefact = false;
        // choose only artefact type
        for (Map.Entry<String, String> entry : resource.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(value.equals("artefact")){
                for(Artefact artefact : player.getItemInventory()){
                    if(key.equals(artefact.getName())){
                        haveArtefact = true;
                        break;
                    }
                }
            }
            if(value.equals("character")) {
                haveArtefact = true;
            }
        }
        return haveArtefact;
    }

    //this is method to handle the command Actoin which contains in action xml.
    private String updateGameState(Player player, GameAction action){
        //remove tree (furniture) in current location
        //take log out from stoor room
        //add to current location as artefact
        //this is method to handle the command Actoin which contains in action xml.
        String response = "";
        Location currentLocation = player.getCurrentLocation();
        Artefact artefactsToremove = null;

        List<String> consumes = action.getUsedEntities();
        List<String> keyPhrases = action.getVerbPhrases();
        List<String> produces = action.getProduceEntities();

            //null check
        if(keyPhrases == null){
            keyPhrases = new ArrayList<>();
        }
        if(consumes == null){
            consumes = new ArrayList<>();
        }
        if(produces == null){
            produces = new ArrayList<>();
        }
        for (String consume : consumes) {
                //tree
            if(!consume.equals("health")){
                //response = "consume 01 " +consume;
                if(checkEntityType(consume).equals("furniture")){
                        //furnitureHM.put(locationName,furniture);
                    //response = "consume 011 " + consume;
                    currentLocation.removeFurnitureByName(consume);


                } else if (checkEntityType(consume).equals("artefact")){
                    player.consumeItemFromInventory(player.getPlayerArtefactByName(consume));
                }
            }
            else {
                for (String keyPhrase : keyPhrases) {
                    //response = keyPhrase;
                    //response.append("KEY PHRASE ").append(keyPhrase);
                    if (keyPhrase.equals("drink")) {
                        artefactsToremove = player.getPlayerArtefactByName(consume);
                        player.consumeItemFromInventory(artefactsToremove);
                        player.increaseHealth();
                        break;
                    } else {
                        //decrease health
                        //check death
                        player.decreaseHealth();
                        if(player.checkDead()){
                            //true dead
                            for(Artefact artefact: player.getItemInventory()){
                                currentLocation.addItemtoLocation(artefact);
                            }
                            player.setDeath();
                            player.setCurrentLocation(locationList.get(0));
                            response = "you died and lost all of your items, you must return to the start of the game";
                        }else {
                            //player.decreaseHealth();
                            response = "decrease health , player health : "+player.getHealth();;
                            break;
                        }
                    }
                }
            }
        }

        Location storeRoom = locations.get("storeroom");
        //check produce
        for(String produce : produces){
            if(produce.equals("health")) {
                player.increaseHealth();
                response = "Your current health is now "+player.getHealth()+ " .";
                break;
            } else{
                String entType = checkEntityType(produce);
                if(entType.equals("location")){
                    Path path = new Path(player.getCurrentLocationName(), produce);
                    currentLocation.addPath(path);
                    //response = "you died and7";
                     //currentLocation.addPath(path);
                } else if(entType.equals("artefact")){
                    //response = "you died and8";
                    Iterator<Artefact> iterator = storeRoom.getArtefacts().iterator();
                    while (iterator.hasNext()) {
                        Artefact artefact = iterator.next();
                        if (artefact.getName().equals(produce)) {
                            currentLocation.addItemtoLocation(artefact);
                            iterator.remove(); // Safely remove the artefact from the storeRoom's artefacts
                           // response = "you died and9";
                        }
                    }
                } else if(entType.equals("furniture")) {
                    //response = "you died and10";
                    Iterator<Furniture> iterator = storeRoom.getFurnitures().iterator();
                    while (iterator.hasNext()) {
                        Furniture furniture = iterator.next();
                        if (furniture.getName().equals(produce)) {
                            currentLocation.addFurniture(furniture);
                            iterator.remove(); // Safely remove the furniture from the storeRoom's furnitures
                            //response = "you died and11";
                        }
                    }
                } else if(entType.equals("character")){
                    currentLocation.setCharacter(storeRoom.getCharacterObject());
                    storeRoom.removeCharacterObject();
                    //response = "you died and12";
                }
            }
        }
        return response;
    }


    private void commandGoto(Player player, String path){
        Location location = player.getCurrentLocation();

        for (Path destination : location.getPaths()){
            //System.out.println("destination: "+ destination);
            if(destination.getDirection().equals(path)){
                Location nextLocation = locations.get(path);
                player.setCurrentLocation(nextLocation);
            } else {
                continue;
            }
        }
    }

    private String commandGet(Player player, String artefact){
        StringBuilder response = new StringBuilder();
        Location location = player.getCurrentLocation();
        Artefact artefactsToGet = null;
        for(Artefact artefact1 : player.getCurrentLocation().getArtefacts()){
            if(artefact1.getName().equals(artefact)){
                artefactsToGet = artefact1;
                break;
            }
        }
        if(artefactsToGet != null){
            // Add the artefact to the player's inventory
            player.addItemToInventory(artefactsToGet);
            // Remove the artefact from the location
            location.removeItemfromlocation(artefactsToGet);
            response.append("You have taken the ").append(artefact).append(".");
        } else {
            response.append("There is no such item in the current location.");
        }
        return response.toString();
    }

    private String commandInv(Player player){
        StringBuilder response = new StringBuilder();
        for(Artefact artefact1 : player.getPlayerArtefact()){
            response.append(artefact1.getDescription()).append("\n");
        }
        return response.toString();
    }

    private boolean isArtefact(String itemName,Player player){
     //to check things are artefact or not.
        return player.getCurrentLocation().IsArtefactsByName(itemName);
    }



    //look: description+ "You can see "artefact" + "furniture" + "You can access from here:" + nextLocation
    private String commandLook(Player player){
        StringBuilder response = new StringBuilder();

        Location currentLocation = player.getCurrentLocation();
        response.append(player.getLocaitonDescription()).append(" You can see:\n");

        for (Artefact art : player.getcurrentArtefact()) {
            response.append(art.getDescription()).append("\n");
        }
        for (Furniture fur : player.getCurrentFurniture()) {
            response.append(fur.getDescription()).append("\n");
        }
        if(!(currentLocation.getCharacter() == null)){
            response.append(currentLocation.getCharacter()).append("\n");
        }
        response.append("You can access from here: ").append(player.getnextPath()).append("\n");
        for (Player otherPlayer : players.values()) {
            if (otherPlayer.getCurrentLocation().equals(player.getCurrentLocation()) && !otherPlayer.getName().equals(player.getName())) {
                response.append("player name | ").append(otherPlayer.getName()).append("\n");
            }
        }
        return response.toString();
    }



    public void parseEntity(File entity){
        try{
            Parser parser = new Parser();
            FileReader reader = null;
            reader = new FileReader(entity);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            int index = 0;
            for(Graph section : sections){
                String sectionName = section.getId().getId();
                if("locations".equals(sectionName)){
                    ArrayList<Graph> locationGraphs = section.getSubgraphs();

                    for(Graph locationGraph : locationGraphs){
                        ArrayList<Graph> components = locationGraph.getSubgraphs();
                        Node locationDetails = locationGraph.getNodes(false).get(0);
                        String locationName = locationDetails.getId().getId();
                        String locationDetail = locationDetails.getAttribute("description");

                        index++;
                        //create new location
                        // = new Location(locationName, locationDetail, paths, characters, artefacts, furnitures);
                        Character character = null;
                        //to store each location.
                        List<Furniture> furIndi = new ArrayList<>();
                        List<Artefact> artefactIndi = new ArrayList<>();
                        List<Character> charIndi = new ArrayList<>();
                        //locations.add(location);
                        for(Graph component : components){
                            String name = component.getId().getId();//components for real.¥
                            ArrayList<Node> nodes = component.getNodes(false);
                            // ArrayList<Node> nodes = component.getNodes(false);
                            //System.out.println("lllllaaaa   " + name);
                            //
                            for(Node node : nodes){
                                //System.out.println("flag00001");
                                int index_ = 0;
                                if("artefacts".equals(name) && !node.getId().getId().isEmpty() ) {
                                    index_++;
                                    String entityName1 = node.getId().getId();
                                    String description1 = node.getAttribute("description");
                                    //System.out.println("Entity1: " + entityName1 + " | Description1: " + description1 + " | Component: " + name);
                                    Artefact artefact = new Artefact(entityName1, description1);
                                    artefacts.add(artefact);
                                    artefactIndi.add(artefact);
                                    artefactHM.put(locationName,artefact);
                                    //System.out.println("arts: "+ artefacts.get(0).getName());
                                    //System.out.println("Ind: "+ artefactIndi.get(0).getName());
                                    //System.out.println("HM: " + artefactHM.get(locationName));
                                } else if("furniture".equals(name)&& !node.getId().getId().isEmpty()){
                                    String entityName2 = node.getId().getId();
                                    String description2 = node.getAttribute("description");
                                    //System.out.println("Entity2: " + entityName2 + " | Description2: " + description2 + " | Component: " + name);
                                    Furniture furniture = new Furniture(entityName2, description2);
                                    furnitures.add(furniture);
                                    furIndi.add(furniture);
                                    furnitureHM.put(locationName,furniture);
                                } else if("characters".equals(name)&& !node.getId().getId().isEmpty()){
                                    String entityName3 = node.getId().getId();
                                    String description3 = node.getAttribute("description");
                                   // System.out.println("Entity3: " + entityName3 + " | Description3: " + description3 + " | Component: " + name);
                                    character = new Character(entityName3, description3);
                                    characters.add(character);
                                    charIndi.add(character);
                                    characterHM.put(locationName, character);
                                }
                            }
                        }
                        Location location = new Location(locationName, locationDetail, artefactIndi, furIndi, index);
                        if(character != null){
                            //System.out.println("character:" + character);
                            location.setCharacter(character);
                            // System.out.println("character1:" + location);
                        }else{
                            character = new Character(null, "There is no character here");
                            location.setCharacter(character);
                        }
                        locationList.add(location);
                        locations.put(locationName,location);
                    }
                } else if("paths".equals(sectionName)){
                    ArrayList<Path> extractedPaths = new ArrayList<>();
                    ArrayList<Edge> edges = section.getEdges();
                    for (Edge edge : edges){
                        Node fromLocation = edge.getSource().getNode();
                        String fromName = fromLocation.getId().getId();
                        Node toLocation = edge.getTarget().getNode();
                        String toName = toLocation.getId().getId();
                        Path path = new Path(fromName, toName);
                        paths.add(path);
                    }
                    for(int i = 0; i < locationList.size(); i++){ //4
                        for(int j = 0; j < paths.size(); j++){ //3
                            //System.out.println("check "+ locationList.get(i).getName());
                            if(locationList.get(i).getName().equals(paths.get(j).getFromLocation())){
                                //System.out.println("check "+paths.get(j).getFromLocation());
                                locationList.get(i).addPath(paths.get(j));
                                //System.out.println("from |" + locationList.get(i).getName() + " | to |" + paths.get(j).getDirection() + " | to |" + paths.get(j).getFromLocation());
                            }
                            else{
                                //System.out.println("from |" + locationList.get(i).getName() + " | to |" + paths.get(j).getFromLocation());
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found: " + entity.getAbsolutePath());
        } catch (ParseException pe) {
            System.err.println("Error while parsing the file: " + entity.getAbsolutePath());
            pe.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("NullPointerException encountered while parsing the file: " + entity.getAbsolutePath());
            npe.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while parsing the file: " + entity.getAbsolutePath());
            e.printStackTrace();
        }
    }



    public void parseAction(File actionEntity){
        try {
            HS = new HashMap<>();
            List<String> tempName = new ArrayList<>();

            // Create a DocumentBuilder to parse the XML file
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionEntity);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            NodeList action = root.getElementsByTagName("action");


            for(int i = 0; i < action.getLength(); i++){
                Element Action = (Element)action.item(i);

                NodeList triggers = Action.getElementsByTagName("triggers").item(0).getChildNodes();
                NodeList subjects = Action.getElementsByTagName("subjects").item(0).getChildNodes();
                NodeList consumed = Action.getElementsByTagName("consumed").item(0).getChildNodes();
                NodeList produced = Action.getElementsByTagName("produced").item(0).getChildNodes();
                String narration = Action.getElementsByTagName("narration").item(0).getTextContent();

                //Node.ELEMENT_NODE
                //System.out.println("Action " + (i + 1) + ":");
                //trigger phrasers verb
                List<String> triggerList = new ArrayList<>();
                NodeList triggerNodes = Action.getElementsByTagName("triggers").item(0).getChildNodes();
                //System.out.print("Triggers: ");
                for (int j = 0; j < triggers.getLength(); j++) {
                    //Node triggerNode = (Node) triggerNodes.item(j);
                    if (triggers.item(j).getNodeName().equals("keyphrase")) {
                        //System.out.print(triggers.item(j).getTextContent() + " ");
                        triggerList.add(triggers.item(j).getTextContent());
                    }
                }
//                for (String trigger : triggerList) {
//                   // System.out.print(trigger + " ");
//                }
//                System.out.println();

                List<String> subjectList = new ArrayList<>();

                for (int j = 0; j < subjects.getLength(); j++) {
                    if (subjects.item(j).getNodeName().equals("entity")) {
                        //System.out.print(subjects.item(j).getTextContent() + " ");
                        subjectList.add(subjects.item(j).getTextContent());
                    }
                }
//                for (String subject : subjectList) {
//                    System.out.print(subject + " ");
//                }
//                System.out.println();

                List<String> consumedList = new ArrayList<>();
                //System.out.print("Consumed: ");
                for (int j = 0; j < consumed.getLength(); j++) {
                    if (consumed.item(j).getNodeName().equals("entity")) {
                        consumedList.add(consumed.item(j).getTextContent());
                    }
                }
                List<String> producedList = new ArrayList<>();
                //System.out.print("Produced: ");
                for (int j = 0; j < produced.getLength(); j++) {
                    if (produced.item(j).getNodeName().equals("entity")) {
                        producedList.add(produced.item(j).getTextContent());
                    }
                }
                for (String trigger : triggerList) {
                    GameAction gameaction = new GameAction(triggerList,subjectList, consumedList, producedList, narration);
                    HashSet<GameAction> actionSet = new HashSet<>();
                    //adding game-action to my Hashset
                    if(tempName.contains(trigger) ) {
                        HS.get(trigger).add(gameaction);
                    }else{
                        actionSet.add(gameaction);
                        HS.put(trigger,actionSet);
                        tempName.add(trigger);
                    }
                }
            }
        } catch (ParserConfigurationException pce) {
            System.err.println("ParserConfigurationException was thrown when attempting to read basic actions file: " + actionEntity.getAbsolutePath());
            pce.printStackTrace();
        } catch (SAXException saxe) {
            System.err.println("SAXException was thrown when attempting to read basic actions file: " + actionEntity.getAbsolutePath());
            saxe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IOException was thrown when attempting to read basic actions file: " + actionEntity.getAbsolutePath());
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while parsing the basic actions file: " + actionEntity.getAbsolutePath());
            e.printStackTrace();
        }
    }



    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}

