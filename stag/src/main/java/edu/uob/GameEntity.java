package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;



import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.GraphAttributes;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Id;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.PortNode;



public abstract class GameEntity
{
    private String name;
    private String description;

    //to store the information, such as location, artefact....
    //private List<GameEntity> entities;



    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
        //

    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
//    public List<GameEntity> getEntities(){
//        return entities;
//    }



}


// I will combine with gameaction and game entity.

//location
//path to other location
//cgaracters that are currently present in location
//artefacts that are currently present in loca
//furniture that belongs in location

//JPGD  parser for dot file
//JAXP  parser for xml file
//functionality
//test coverage
//

