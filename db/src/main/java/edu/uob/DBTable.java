package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;


public class DBTable {
    private String name;
    private ArrayList<DBTColumn> columns;
    private ArrayList<DBTRow> rows;
    private ArrayList<Index> indexs;

    public DBTable(String name) {
        this.name = name;
        columns = new ArrayList<DBTColumn>();
        rows = new ArrayList<DBTRow>();
        indexs = new ArrayList<Index>();
    }

    public String getName() {
        return name;
    }



    public ArrayList<DBTColumn> getColumns() {
        return columns;
    }

    public  ArrayList<DBTRow> getRows() {
        return rows;
    }

    public ArrayList<Index> getNextId() {
        return indexs;
    }


    public void addColumn(DBTColumn column) {
        columns.add(column);
    }

    public void addIndex(Index index) {
        indexs.add(index);
    }

    public void addRow(DBTRow row) {
        //row.setId(nextId++);
        rows.add(row);
        //System.out.println(row);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append(name + "\n");
        for (DBTColumn column : columns) {
            sb.append(column.getName() + "\t");
        }
        sb.append("\n");
        for (DBTRow row : rows) {
            sb.append(row.toString() + "\n");
        }
        return sb.toString();
    }
    public int getColumnCount() {
        return columns.size();
    }
    public int getRowCount() {
        return rows.size();
    }


}
