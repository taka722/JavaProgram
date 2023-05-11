package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.EventObject;
import java.util.Map;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class EntitiesFileTests {

  // Test to make sure that the basic entities file is
  private GameServer gameServer;



  @Test
  void testBasicEntitiesFileIsReadable() {
      try {
          Parser parser = new Parser();
          FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
          parser.parse(reader);
          Graph wholeDocument = parser.getGraphs().get(0);
          ArrayList<Graph> sections = wholeDocument.getSubgraphs();
//          for (Graph section : sections) {
//              String sectionName = section.getId().getId();
//              System.out.println("000 "+ sectionName);
//          }
          for(Graph section : sections){
              String sectionName = section.getId().getId();
              System.out.println("000 "+ sectionName);
              if("locations".equals(sectionName)){
                  ArrayList<Graph> locationGraphs = section.getSubgraphs();
                  for(Graph locationGraph : locationGraphs){
                      ArrayList<Graph> components = locationGraph.getSubgraphs();
                      for(Graph component : components){
                          String name = component.getId().getId();//components for real.¥
                          ArrayList<Node> nodes = component.getNodes(false);
                         // ArrayList<Node> nodes = component.getNodes(false);
                          for(Node node : nodes){
                              if("artefacts".equals(name)) {
                                  String entityName1 = node.getId().getId();
                                  String description1 = node.getAttribute("description");
                                  System.out.println("Entity1: " + entityName1 + " | Description1: " + description1 + " | Component: " + name);
                              } else if("furniture".equals(name)){
                                  String entityName2 = node.getId().getId();
                                  String description2 = node.getAttribute("description");
                                  System.out.println("Entity2: " + entityName2 + " | Description2: " + description2 + " | Component: " + name);

                              } else if("characters".equals(name)){
                                  String entityName3 = node.getId().getId();
                                  String description3 = node.getAttribute("description");
                                  System.out.println("Entity3: " + entityName3 + " | Description3: " + description3 + " | Component: " + name);
                              }
                          }
                      }
                  }
              } else if("paths".equals(sectionName)){
                  ArrayList<Path> extractedPaths = new ArrayList<>();
                  ArrayList<Edge> edges = section.getEdges();
                  for (Edge edge : edges){
                      Node fromLocation = edge.getSource().getNode();
                      String fromName = fromLocation.getId().getId();
                      Node toLocation = edge.getTarget().getNode();
                      String toName = toLocation.getId().getId();

                      System.out.println("from |" + fromName + "|  TO |" + toName);
                  }
              }
          }



          //String sectionName = sections.g;
          //System.out.println("000 "+ sectionName);

          // The locations will always be in the first subgraph
          ArrayList<Graph> locations = sections.get(0).getSubgraphs();
          Graph firstLocation = locations.get(0);
          Node locationDetails = firstLocation.getNodes(false).get(0);
          // Yes, you do need to get the ID twice !
          String locationName = locationDetails.getId().getId();
          assertEquals("cabin", locationName, "First location should have been 'cabin'");


          //get name and description about location
          for(int i = 0; i < locations.size(); i++){
              Graph allLocation = locations.get(i);
              Node locationDetails01 = allLocation.getNodes(false).get(0);
              String locationName01 = locationDetails01.getId().getId();
              System.out.println("011 "+ locationName01 );
              System.out.println("location detail: " + locationDetails01.getAttribute("description"));
          }

          //get artefact
          ArrayList<Graph> artefacts = sections.get(0).getSubgraphs();

          Graph firstartefact = artefacts.get(0).getSubgraphs().get(0);
          Node artefactdetail = firstartefact.getNodes(false).get(0);
          String artefactName = artefactdetail.getId().getId();
          System.out.println("012 "+ artefactName);
          System.out.println("013 "+ artefactdetail.getAttribute("description"));

          Graph secartefact = artefacts.get(1).getSubgraphs().get(0);
          Node artefactdetail1 = secartefact.getNodes(false).get(0);
          String artefactName1 = artefactdetail1.getId().getId();
          System.out.println("014 "+ artefactName1);
          System.out.println("015 "+ artefactdetail1.getAttribute("description"));



          //get character and description
          ArrayList<Graph> characters = sections.get(0).getSubgraphs();
          Graph firstcharacter = characters.get(2).getSubgraphs().get(0);
          Node characterdetail = firstcharacter.getNodes(false).get(0);
          String characterName = characterdetail.getId().getId();
          System.out.println("016 "+ characterName);
          System.out.println("017 "+ characterdetail.getAttribute("description"));


          //get furnitures and description.
          ArrayList<Graph> furnitures = sections.get(0).getSubgraphs();
          Graph firstfurniture = furnitures.get(0).getSubgraphs().get(1);
          Node furnituredetail = firstfurniture.getNodes(false).get(0);
          String furnitureName = furnituredetail.getId().getId();
          System.out.println("018 "+ furnitureName);
          System.out.println("019 "+ furnituredetail.getAttribute("description"));

          Graph firstfurniture1 = furnitures.get(1).getSubgraphs().get(1);
          Node furnituredetail1 = firstfurniture1.getNodes(false).get(0);
          String furnitureName1 = furnituredetail1.getId().getId();
          System.out.println("018 "+ furnitureName1);
          System.out.println("019 "+ furnituredetail1.getAttribute("description"));


          // The paths will always be in the second subgraph
          ArrayList<Edge> paths = sections.get(1).getEdges();
          Edge firstPath = paths.get(0);
          Edge secondPath = paths.get(1);
          Edge thirdPath = paths.get(2);

          //cabin -> forest;
          Node fromLocation = firstPath.getSource().getNode();
          String fromName = fromLocation.getId().getId();
          Node toLocation = firstPath.getTarget().getNode();
          String toName = toLocation.getId().getId();

          //forest -> cabin;
          Node fromLocation1 = secondPath.getSource().getNode();
          String fromName1 = fromLocation1.getId().getId();
          Node toLocation1 = secondPath.getTarget().getNode();
          String toName1 = toLocation1.getId().getId();


          //cellar -> cabin;
          Node fromLocation2 = thirdPath.getSource().getNode();
          String fromName2 = fromLocation2.getId().getId();
          Node toLocation2 = thirdPath.getTarget().getNode();
          String toName2 = toLocation2.getId().getId();

          System.out.println("020 "+ fromName);
          System.out.println("021 "+ toName);

          System.out.println("022 "+ fromName1);
          System.out.println("023 "+ toName1);

          System.out.println("024 "+ fromName2);
          System.out.println("025 "+ toName2);

          assertEquals("cabin", fromName, "First path should have been from 'cabin'");
          assertEquals("forest", toName, "First path should have been to 'forest'");



      } catch (FileNotFoundException fnfe) {
          fail("FileNotFoundException was thrown when attempting to read basic entities file");
      } catch (ParseException pe) {
          fail("ParseException was thrown when attempting to read basic entities file");
      }
  }




}
