package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private String currentLocation;
    private List<Furniture> furnitureStates;
    private Artefact artefactStates;
    private Map<String, Location> locations;

    private Map<String, Player> players;

    private Location location;



    public GameState(Location startingLocation) {
        this.location = startingLocation;
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
    }


}
