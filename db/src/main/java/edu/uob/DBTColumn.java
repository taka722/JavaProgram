package edu.uob;
import java.util.ArrayList;



public class DBTColumn {
    private String name;
    private String dataType;
    private ArrayList<String> data;

    public DBTColumn(String name) {
        this.name = name;
        //this.dataType = dataType;
        this.data = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public ArrayList<String> getData() {
        return data;
    }
    public String getDataInColumn(int index) {
        return data.get(index);
    }

    public void addData(String data) {
        this.data.add(data);
    }
    public int columnCount() {
        return this.data.size();
    }

    public void addDataWithIndex(String data,int index) {
        this.data.add(index,data);
    }

    public void setData(int index, String data) {
        this.data.set(index, data);
    }

    public void removeData(int index) {
        this.data.remove(index);
    }


}
