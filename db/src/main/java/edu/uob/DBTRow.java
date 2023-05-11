package edu.uob;
import java.util.ArrayList;



public class DBTRow {
    private int id;
    private ArrayList<String> values;

    public DBTRow() {
        //this.id = id;
        this.values = new ArrayList<String>();
    }



    public void addValue(String value) {
        this.values.add(value);
    }

    public void setValue(int index, String value) {
        values.set(index, value);
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getValue(int index) {
        return values.get(index);
    }

    public void removeValue(int index) {
        values.remove(index);
    }

    public int rowCount() {
        return this.values.size();
    }

    //@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        for (String value : values) {
            sb.append(value).append("\t");
        }
        return sb.toString().trim();
    }
}
