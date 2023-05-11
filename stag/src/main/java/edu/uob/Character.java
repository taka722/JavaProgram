package edu.uob;

public class Character extends GameEntity {
    private int health;
    private String name;

    private String description;
    public Character(String name, String description) {
        super(name, description);
        //this.name = name;
        //this.description = description;



    }



    public boolean stayAlive(){
        return this.health > 0;
    }
}
