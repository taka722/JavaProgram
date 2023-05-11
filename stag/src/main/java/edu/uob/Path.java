package edu.uob;

import com.alexmerz.graphviz.objects.Edge;

public class Path {
    //private String id;
    private String fromLocation;
    private String toLocation;
    private String direction;


    public Path(String fromLocation, String toLocation){
        //pathId, sourceLocationName, "", targetLocationName, "", pathDirection);
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }





    public String getDirection(){
        return this.toLocation;
    }

    public String getFromLocation(){
        return this.fromLocation;
    }

}
