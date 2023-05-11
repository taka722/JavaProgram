package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String description;

    private String name;
    private int health;
    private Location currentLocation;
    private List<Artefact> inventory;




    public Player(String name, Location firstLocation) {
        this.name = name;
        this.description = "player";
        this.health = 3;
        this.currentLocation = firstLocation;

        inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public boolean checkDead() {
        System.out.println("now health"+ getHealth());
        if(getHealth()>0){
            return false;
        }else {
            return true;
        }
    }
    public void increaseHealth(){
        //System.out.println("Now player health "+getHealth());
        if(this.health == 3){
            this.health = 3;
        } else{
            this.health++;
        }
    }
    public void decreaseHealth(){
        health = health - 1;
        if(health==0){
            setDeath();
        }
    }
    public void setDeath(){
         dropArtefact();
         this.inventory.clear();
         //this.health = 3;
    }

    public void dropArtefact(){
        for(Artefact inventory : inventory){
            this.getCurrentLocation().addItemtoLocation(inventory);
        }
    }



    public Location getCurrentLocation() {
        return currentLocation;
    }
    public String getCurrentLocationName() {
        return currentLocation.getName();
    }
    public String getLocaitonDescription(){
        return currentLocation.getDescription();

    }

    public List<Artefact> getcurrentArtefact(){
        return this.currentLocation.getArtefacts();
    }


    public List<Artefact> getPlayerArtefact(){
        return this.inventory;
    }

    public Artefact getPlayerArtefactByName(String artefactName){
        Artefact queryArtifact = null;
        for(Artefact artefact: this.inventory) {
            if((artefact.getArtefactName()).equals(artefactName)){
                queryArtifact = artefact;
                break;
            }
        }
        return queryArtifact;
    }


    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    public List<Furniture> getCurrentFurniture(){
        return this.currentLocation.getFurnitures();
    }
    public String getnextPath(){
        List<String> destinations= new ArrayList<>();
        for(Path path: currentLocation.getPaths()){
            String destination = path.getDirection();
            destinations.add(destination);
        }
        return destinations.toString();
    }


    public void addItemToInventory(Artefact artefact) {
        this.inventory.add(artefact);
    }

    public List<Artefact> getItemInventory(){
        return this.inventory;
    }

    public void consumeItemFromInventory(Artefact artefact) {
        //System.out.print("check consume artefact Name: "+artefact.getArtefactName());
        this.inventory.remove(artefact);
    }
}
