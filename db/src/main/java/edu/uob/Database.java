package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;


public class Database {
    private String name;
    private ArrayList<DBTable> tables;

    public Database(String name) {
        this.name = name;
        tables= new ArrayList<>();

    }

    public String getName() {
        return name;
    }
    public ArrayList<DBTable> getTables() {
        return tables;
    }
    public void addTable(DBTable table) {
        tables.add(table);
    }

    public void removeTable(int index) {
        tables.remove(index);
    }

    public int tableCount() {
        return tables.size();
    }


}