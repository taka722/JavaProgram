package edu.uob;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;

public class Location extends GameEntity {
    private List<Path> paths;
    private List<Character> characters;
    private Character character;
    private List<Artefact> artefacts;

    private List<Furniture> furnitures;

    private int Index;
    public Location(String name, String description,List<Artefact> artefacts, List<Furniture> furnitures, int Index) {
        super(name, description);
        this.paths = new ArrayList<Path>();
        //this.character = new Character();
        this.artefacts = artefacts;
        this.furnitures = furnitures;
        this.Index = Index;

    }
    public List<Path> getPaths() {
        return paths;
    }

    public int getIndex(){
        return Index;
    }
    public void setCharacter(Character character){
        this.character = character;
    }
    public void addCharacter(Character character){

        this.character = character;
    }

    public String getCharacter(){
        return character.getName();
    }
    public Character getCharacterObject(){
        return this.character;
    }
    public void removeCharacterObject(){
         character = null;
    }
    public List<Furniture> getFurnitures(){
        return furnitures;
    }


//to remove the artefact in the current location when the player collect it
    public void removeItemfromlocation(Artefact artefact){
        this.artefacts.remove(artefact);
    }

    public void addItemtoLocation(Artefact artefact){
        this.artefacts.add(artefact);
    }
    public List<Artefact> getArtefacts() {
        return this.artefacts;
    }

    public boolean IsArtefactsByName(String name) {
        boolean checkAr = false;
        for(Artefact art: artefacts){
            if(art.getName().equals(name)){
                checkAr = true;
                break;
            }
            checkAr = false;
        }
        return checkAr;
    }

    public void addPath(Path path){
        //to elimintate to add duplicate paths to location. 1:01
        for (Path existingPath : paths) {
            if (existingPath.getDirection().equals(path.getDirection())) {
                return; // Path already exists, don't add it
            }
        }
        this.paths.add(path);
    }
    public void addFurniture(Furniture furniture){
        this.furnitures.add(furniture);
    }


//    public void removeFurniture(Furniture furniture){
//        this.furnitures.remove(furniture);
//    }


    public void removeFurnitureByName(String furnitureName){
        Iterator<Furniture> iterator = this.furnitures.iterator();
        while (iterator.hasNext()) {
            Furniture furniture = iterator.next();
            if (furniture.getName().equals(furnitureName)) {
                iterator.remove(); // Safely remove the furniture from the furnitures list
            }
        }
    }
//    public boolean checkPath(Path path){
//        for(Path path1 : this.paths){
//            if(path1.getDirection().equals(path.getDirection())){
//                return false;
//            } else {
//                return true;
//            }
//        }
//
//        return false;
//    }


}
