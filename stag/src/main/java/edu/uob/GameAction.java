package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class GameAction {
    private List<String> verbPhrases;
    private List<String> subjectPhrases;

    private List<String> usedEntities;

    private List<String> produceEntities;

    private String narration;

    public GameAction(List<String> verbPhrases,List<String> subjectPhrases,
                      List<String> usedEntities, List<String> produceEntities, String narration){
        this.verbPhrases = verbPhrases;
        this.subjectPhrases = subjectPhrases;
        this.usedEntities = usedEntities;
        this.produceEntities = produceEntities;
        this.narration = narration;
    }

    public List<String> getVerbPhrases() {
        return verbPhrases;
    }
    public List<String> getSubjectPhrases(){
        return subjectPhrases;
    }
    public List<String> getUsedEntities(){
        return usedEntities;
    }
    public List<String> getProduceEntities(){
        return produceEntities;
    }
    public String getNarration(){
        return narration;
    }
    public void setNarration(String narration) {
        this.narration = narration;
    }



}
