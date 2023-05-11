package edu.uob;


import java.util.HashMap;


public class Index {

    //private int index_value;
    //private String tablename;
    private HashMap<String, Integer> list_index;
    public Index() {
        list_index = new HashMap<String, Integer>();
        //tablename = name;
    }

    public void addIndexFirst(String tablename,int currentIndex) {
        list_index.put(tablename,currentIndex);
    }
    public void setIndex(String tablename) {
        list_index.put(tablename, getIndex(tablename)+1);
    }

    public int getIndex(String tablename) {
        int index = list_index.get(tablename);
        return index;
    }

}