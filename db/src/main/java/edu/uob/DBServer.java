package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.*;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final String storageFolderPath;

    //my code
    private final HashMap<String, DBTable> tableshm = new HashMap<>();
    private final HashMap<String, Database> dbs = new HashMap<>();  //lots of databases
    private DBTable table;
    private Database database; //one of the database from hashmap above.
    private Index indexcol;
    private String dbPath;
    private String dbName;
    private final String sep = File.separator;


    private static final boolean True = false;
    //private Map<String,DBTable> tables;

    //2D arraylist for storing file.
    //private ArrayList<ArrayList<String>> Data1;



    public static void main(String[] args) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        //storageFolderPath should store every database.
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) throws IOException {
        // TODO implement your server logic here

        // Remove any whitespace at the beginning and end of the query
        command = command.trim();


        // This is "blind" replacement - replacing if they exist, doing nothing if they don't
        while (command.contains("  ")) command = command.replaceAll("  ", " ");
        //String s = "dsjjfjnkboksmb dsf df;";
        char lastChar = command.charAt(command.length()-1);
        String s1 = Character.toString(lastChar);


        String result = "";
        try{
            if(!s1.equals(";")){
                throw new IOException("the end of query is supposed to be ; ");
            } else {
                //sdfgh;jhgfd;
                command = command.substring(0, command.length()-1);


                String[] command1 = command.split(" "); // to just check the first keywords.
                //creating a database or table that already exists
                if((command1[0]).equalsIgnoreCase("CREATE")) {
                    //create = create tabla/database

                    String[] create = command.split(" ");
                    //CREATE DATABASE " + randomName + ";"
                    if (create[1].equalsIgnoreCase("DATABASE")) {
                        try{
                            boolean value = checkDatabse(create[2]);
                            if(value){
                                throw new IOException("this database is already exit. ");
                            }else{
                                createDatabase(create[2]);
                                result = "[OK]".concat(" ");
                            }
                        }catch (IOException e) {
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    } else if (create[1].equalsIgnoreCase("TABLE")) {
                        String colN = "";
                        for(int i = 3; i<create.length; i++){
                            colN = colN.concat(create[i]);
                        }
                        try{

                            boolean value1 = checkTable(create[2]);

                            if(value1){
                                throw new IOException("this table is already exit. ");
                            }else{
                                createTable(create[2],manageLine(colN));
                                boolean value2 = hasDuplicates(manageLine(colN));

                                if(value2) {
                                    throw new IOException("column name is duplicated.");
                                }else {
                                    saveToDatabase(tableshm.get(create[2]),create[2]);
                                    result = "[OK]".concat(" ");
                                }
                            }
                        } catch (IOException e) {
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    }
                } else if(command1[0].equalsIgnoreCase("USE")){
                    String[] use = command.split(" ");
                    useDatabase(use[1]);
                    result = "[OK]".concat(" ");
                } else if(command1[0].equalsIgnoreCase("DROP")){
                    String[] drop = command.split(" ");
                    if(drop[1].equalsIgnoreCase("DATABASE")){
                        dropDatabase(drop[2]);
                        //saveToDatabase(tableshm.get(drop));
                        result = "[OK]".concat(" ");
                    } else if(drop[1].equalsIgnoreCase("TABLE")){
                        dropTable(drop[2]);
                        result = "[OK]".concat(" ");
                    }

                } else if(command1[0].equalsIgnoreCase("ALTER")){

                    String[] Alter = command.split(" ");
                    //alterDrop(String tblName,ArrayList<String> colName)
                    String colN1 = "";
                    for(int i = 5; i<Alter.length; i++){
                        colN1 = colN1.concat(Alter[i]);
                    }
                    if(Alter[3].equalsIgnoreCase("DROP")){
                        //ALTER TABLE table_name DROP COLUMN column_name;  'a', 'b'
                        try{
                            boolean value5 = checkdropcolumn(Alter[2],manageLine(colN1));
                            if(value5){
                                //saveToDatabase(DBTable table,String tableName){
                                //saveToDatabase(tableshm.get(create[2]),create[2]);
                                alterDrop(Alter[2],manageLine(colN1));
                                saveToDatabase(tableshm.get(Alter[2]),Alter[2]);
                                result = "[OK]".concat(" ").concat(result);
                            } else{
                                throw new IOException("can not delete id column or colum name does not exit.");
                            }
                        } catch (IOException E){
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    } else if(Alter[3].equalsIgnoreCase("ADD")){
                        try{
                            boolean value6 = checkduplicateName(Alter[2], Alter[4]);
                            if(value6){
                                addColumnAlter(Alter[2], Alter[4]);
                                saveToDatabase(tableshm.get(Alter[2]),Alter[2]);
                                result = "[OK]".concat(" ").concat(result);
                            } else{
                                throw new IOException("table has same column name, it shuld be changed.");
                            }
                        } catch (IOException e){
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    } else if(Alter[6].equalsIgnoreCase("TO")){
                        try{
                            boolean value7 = checkduplicateName(Alter[2], Alter[4]);
                            if(value7){
                                renameColumnAlter(Alter[2],Alter[5], Alter[7]);
                                saveToDatabase(tableshm.get(Alter[2]),Alter[2]);
                                result = "[OK]".concat(" ").concat(result);
                            } else{
                                throw new IOException("table has same column name, it shuld be changed.");
                            }
                        } catch(IOException e){
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    }

                    //checkAlteradd(String tablename, String colna
                    //addColumnAlter(String tblName, String newcolna)

                    result = "[OK]".concat(" ").concat(result);

                } else if(command1[0].equalsIgnoreCase("INSERT")){
                    // INSERT INTO TABLE NAME value('at' , 'to, 'yo);
                    String[] Insert = command.split(" ");
                    String[] colnum = command.toUpperCase().split("VALUES");
                    String col1 = colnum[1];
                    String[] elements = col1.substring(1, col1.length()-1).split(",");
                    //number of elements for INSERT statement.
                    int numElements = elements.length;
                    //number of columns except id.
                    DBTable currentTable = tableshm.get(Insert[2]);
                    int x = currentTable.getColumns().size()-1;

                    String colN = "";
                    for(int i =4; i< Insert.length; i++){
                        colN = colN.concat(Insert[i]);
                    }
                    //inserting too many (or too few) values into a table
                    try {
                        if(x == numElements){
                            insert(Insert[2], manageLine(colN));
                            result = "[OK]".concat(" ");
                            //saveToDatabase(currentTable, Insert[2]);
                        } else {
                            throw new IOException("number of elements is not equal to number of columns.");
                        }
                    } catch (IOException e) {
                        result = "[ERROR]".concat(result);
                        System.err.println("[ERROR]");

                    }
                } else if(command1[0].equalsIgnoreCase("SELECT")){
                    String[] select1 = command.split(" ");
                    String tblname = select1[3].replace(";","");
                    //tableName = tblname;
                    //result = "brabraaa";
                    // checkTable(select1[3]);
                    //shortest query for select statement
                    //SELECT * FROM marks;
                    if(select1.length == 4){
                        try{
                            boolean value4 = checkTable(tblname);
                            if(value4){
                                result = selectAll(select1[3]);
                                result = "[OK]".concat(" ").concat(result);

                            } else {
                                throw new IOException("There is no such table.");
                            }
                        } catch(IOException e) {
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                    } else if(select1.length > 4 && select1[4].equals("WHERE")){
                        String colN = " ";
                        for(int i = 5; i< select1.length; i++){
                            colN = colN.concat(select1[i]).replace("'","");
                        }
                        try{
                            boolean value4 = checkTable(select1[3]);
                            if(value4){
                                result = selectF(select1[3],select1[1], colN);
                                //saveToDatabase(tableshm.get(select1[3]),select1[3]);
                                result = "[OK]".concat(" ").concat(result);

                            } else {
                                throw new IOException("There is no such table.");
                            }
                        } catch (IOException e){
                            result = "[ERROR]".concat(result);
                            System.err.println("[ERROR]");
                        }
                        //result = selectF(select1[3],select1[1], colN);
                    }else {
                        result = "[ERROR]".concat(result);
                    }//else if(select1.length > 4 && select1[1].equals())
                }else if(command1[0].equalsIgnoreCase("UPDATE")){
                    //UPDATE marks SET mark = 38 WHERE name == 'Clive';
                    //update(String tableName,String newValue,String condition,String columnCondition,String columnTaget)
                    String[] Update = command.split(" ");
                    update(Update[1],Update[5],Update[9],Update[7],Update[3]);
                    saveToDatabase(tableshm.get(Update[1]),Update[1]);
                    result = "[OK]".concat(" ").concat(result);

                }else if(command1[0].equalsIgnoreCase("DELETE")){
                    //ArrayList<String> s = new ArrayList<>(Arrays.asList("==",">", "<", ">=", "<=", "!=" ,"LIKE"));
                    //delete("Name","NewChris","people2");
                    //delete tb where a = 'v'
                    //DELETE FROM marks WHERE name == 'Dave';
                    //delete(String conditionColumn,String conditionValue,String tablename)
                    String[] Delete = command.split(" ");
                    //String[] Delete1 = command.split("");
                    String colN = "";
                    for(int i = 4; i< Delete.length; i++) {
                        colN = colN.concat(" " + Delete[i]).replace("'", "");
                    }
                    delete(Delete[2],colN);
                    saveToDatabase(tableshm.get(Delete[2]), Delete[2]);
                    result = "[OK]".concat(" ");

                }else if(command1[0].equalsIgnoreCase("JOIN")){
                    String[] Join = command.split(" ");
                    result = join(Join[1],Join[3],Join[5],Join[7]);
                    //saveToDatabase(tableshm.get(Join[1]), Join[1]);
                    result = "[OK]".concat(" ").concat(result);
                }
            }
        } catch (IOException e){
            result = "[ERROR]".concat(result);
            System.err.println("[ERROR]");
        }

        return result;
    }




    public void alterDrop(String tblName,ArrayList<String> colName) {
        DBTable table = tableshm.get(tblName);
        ArrayList<Integer> indCol = new ArrayList<>();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (colName.contains(table.getColumns().get(i).getData().get(0))) {
                indCol.add(i);
            }
        }
        for (int i = 0; i < indCol.size(); i++) {
            //for(int j = 0;j< table.getColumns().get(0).getData().size();j++){
            table.getRows().remove(i);
        }
        ArrayList<Integer> ind = new ArrayList<>();
        for (int i = 0; i < table.getRows().get(0).getValues().size(); i++) {
            if (colName.contains(table.getRows().get(0).getValues().get(i))){
                ind.add(i);
            }
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            for(int j = 0;j<ind.size();j++){
                table.getRows().get(i).getValues().remove(j);
            }
        }
    }

    public boolean checkdropcolumn(String tablename,ArrayList<String> colna){
        DBTable currenttable = tableshm.get(tablename);
        ArrayList<String> currentColNames = new ArrayList<>();

        // Get the current column names from the DBTable object
        for (DBTColumn column : currenttable.getColumns()) {
            currentColNames.add(column.getData().get(0));
        }
        boolean result;

        for (int i = 0; i < colna.size(); i++) {
            if (colna.get(i).equals("id")) {
                result = false; // If the input column name is "id", return false
            } else if (!currentColNames.contains(colna.get(i))) {
                result = false; // If the input column name is not found in the table, return false
            }
        }
        result = true;


        // If all input column names are found in the table and "id" column is not found, return true
        return result;
    }
    public boolean checkduplicateName(String tablename, String colna){
        DBTable currenttable = tableshm.get(tablename);
        ArrayList<String> currentColNames = new ArrayList<>();

        // Get the current column names from the DBTable object
        for (DBTColumn column : currenttable.getColumns()) {
            currentColNames.add(column.getData().get(0));
        }
        boolean result;

        if(currentColNames.contains(colna)) {
            result = false; // If the input column name is "id", return false
        } else{
            result = true;
        }

        return result;

    }

    public void addColumnAlter(String tblName, String newcolna){
        DBTable table = tableshm.get(tblName);
        //declare the new column
        DBTColumn column = new DBTColumn(newcolna);
        //fill first row as column name.
        table.getRows().get(0).getValues().add(newcolna);
        for(int i = 1; i < table.getRowCount(); i++){
            table.getRows().get(i).getValues().add(null);
        }

        column.addData(newcolna);
        for(int i = 1; i <table.getColumns().get(0).getData().size(); i++){
            //column.addData(null);
            column.addData(null);
        }
        table.addColumn(column);
        //table.getRows().get(0).getValues().add(newcolna);
    }
    public void renameColumnAlter(String tblName, String precolna, String newcolna){
        DBTable table = tableshm.get(tblName);
        //ALTER TABLE table_name
        //RENAME COLUMN old_name to new_name;
        //row
        for(int i = 0; i < table.getRows().get(0).getValues().size(); i++){
            if(table.getRows().get(0).getValues().get(i).equals(precolna)){
                table.getRows().get(0).setValue(i ,newcolna);

            }
        }
        //column
        for(int i = 0; i < table.getColumnCount(); i++){
            if(table.getColumns().get(i).getData().get(0).equals(precolna)){
                table.getColumns().get(i).setData(0, newcolna);
                break;
            }
        }
    }



    //Select * from tableName where ()id	name	mark	pass 1	Steve	65	TRUE
    // (name == 'Dave',mark == 65,pass ==TRUE)
    public String selectF(String tableName,String columnsList,String conditionString){
        ArrayList<String> columnTarget = new ArrayList<>();
        ArrayList<String> columnCondition = new ArrayList<>();

        conditionString.replace("(", "").replace(")", "");
        conditionString.replace("'", "");
        DBTable CurrentTable = tableshm.get(tableName);

        if(columnsList.equals("*")){
            String s = CurrentTable.getRows().get(0).getValues().toString();
            columnTarget = manageLine(s);
        }else{
            columnTarget = (manageLine(columnsList));

        }

        ArrayList<ArrayList<String>> cdstring = rex.method2(conditionString);

        //Tochec
        for(int i = 0; i < cdstring.size();i++){
            columnCondition.add(cdstring.get(i).get(0));
        }

        ArrayList<ArrayList<String>> temp_view  = new ArrayList<>();
        for(int i =0 ; i< CurrentTable.getRows().size(); i++){
            temp_view.add(new ArrayList<>());
            for(int j =0 ; j< CurrentTable.getRows().get(0).rowCount(); j++){
                temp_view.get(i).add(j,CurrentTable.getRows().get(i).getValue(j));
            }
        }
        //Collect index which match to condition
        ArrayList<Integer> keepIndexColumn  = new ArrayList<Integer>();
        for(int i =0 ; i< temp_view.get(0).size(); i++){
            //for(int j =0 ; j< temp_view.get(0).size(); j++)
            if(columnTarget.contains(temp_view.get(0).get(i))){
                keepIndexColumn.add(i);
            }
        }

        List<Integer> conditionIndex = new ArrayList<>();


        //conditionIndex2 -test
        for(int i =0 ; i< cdstring.size(); i++){
            for(int j =0 ; j< temp_view.get(0).size(); j++)
                if( (cdstring.get(i).get(0)).equals(temp_view.get(0).get(j))){

                    conditionIndex.add(j);
                }
        }

        //conditionString = "mark == 65 OR pass == TRUE";
        //cdstring = "mark == 65, pass == TRUE";
        List<Integer> conditionRowsIndex = new ArrayList<>();
        List<String> andOrList = rex.method3(conditionString);
        List<String> uppercaseWords = new ArrayList<>();
        for (String word : andOrList) {
            uppercaseWords.add(word.toUpperCase());
        }

        //[Comparator]  ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
        int k =0;
        //List<String> Comparator = Arrays.asList("==", ">", ">" , "<", ">=" , "<=" , "!=", "LIKE");
        for(int i = 1;i<temp_view.size();i++){
            List<Boolean> conditions = new ArrayList<>();

            for(int j = 0;j<conditionIndex.size();j++){
                String operator = cdstring.get(j).get(1);
                switch(operator.toUpperCase()) {
                    case "==":
                        if(temp_view.get(i).get(conditionIndex.get(j)).equals(cdstring.get(j).get(2))){
                            conditions.add(true);
                        }else {
                            conditions.add(false);
                        }

                        break;
                    case ">":
                        if(Integer.parseInt(temp_view.get(i).get(conditionIndex.get(j))) > Integer.parseInt(cdstring.get(j).get(2))){
                            conditions.add(true);
                        }else {conditions.add(false);
                        }
                        break;
                    case ">=":
                        if(Integer.parseInt(temp_view.get(i).get(conditionIndex.get(j))) >= Integer.parseInt(cdstring.get(j).get(2))){
                            conditions.add(true);
                        }else {
                            conditions.add(false);
                        }
                        break;
                    case "<":
                        if(Integer.parseInt(temp_view.get(i).get(conditionIndex.get(j))) < Integer.parseInt(cdstring.get(j).get(2))){
                            conditions.add(true);
                        }else {
                            conditions.add(false);
                        }
                        break;
                    case "<=":
                        if(Integer.parseInt(temp_view.get(i).get(conditionIndex.get(j))) <= Integer.parseInt(cdstring.get(j).get(2))) {
                            conditions.add(true);
                        }else {
                            conditions.add(false);
                        }
                        break;
                    case "!=":
                        if(!temp_view.get(i).get(conditionIndex.get(j)).equals(cdstring.get(j).get(2))){
                            conditions.add(true);
                        }else {
                            conditions.add(false);
                        }
                        break;
                    case "LIKE":
                        if(temp_view.get(i).get(conditionIndex.get(j)).contains(cdstring.get(j).get(2).replace("'",""))){
                            conditions.add(true);
                        }
                        else{
                            conditions.add(false);}
                        break;
                    // add more cases for other operators as needed
                    default:
                        System.out.println("[ERROR]");
                        break;
                }
                k=j;
            }

            boolean b = rex.evaluateConditions(conditions, andOrList);

            if(b){conditionRowsIndex.add(i);
                //System.out.println("ddd" + i);
            }else {
                System.out.println("ERROR");
            }
        }

        ArrayList<String> new_temp_view  = new ArrayList<>();
        //Add value to new arrayList
        //temp_view.size() = number of row
        for(int i =0 ; i< temp_view.size(); i++){
            for(int j =0 ; j< temp_view.get(0).size(); j++){
                if(conditionRowsIndex.contains(i) && keepIndexColumn.contains(j)){
                    //new_temp_view.add(temp_view.get(0).get(j));
                    new_temp_view.add(temp_view.get(i).get(j));

                }
            }
        }

        // Converting the ArrayList to a space-separated String
        StringBuilder sb = new StringBuilder();
        for (String str : new_temp_view) {
            sb.append(str).append(" ");
        }
        String result = sb.toString().trim();

        printTable(new_temp_view,columnTarget);

        return result;
    }

    public void dropTable(String tblName){
        //ok
        String path = dbPath+sep+tblName;
        File file = new File(path);
        ///System.out.println(path);
        if (file.delete()) {
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("Failed to delete the file.");
        }
    }
    public void dropDatabase(String dbName){
        String path = dbPath;//+ sep + dbName it used dbpath only


        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folder.delete();
        }

        Database database = dbs.get(dbName);
        for(int i=0; i< database.tableCount();i++){
            System.out.println("0.8");
            tableshm.remove(database.getTables().get(i).getName());
            database.removeTable(i);
        }

        dbs.remove(dbName);


    }



    public String selectAll(String tblName){

        tblName = tblName.replace(";", ""); // assign the result back to tblName
        DBTable currentTable = tableshm.get(tblName);
        String table = currentTable.toString();
        return table;
        //printTable(ArrayList<String> TempTable,ArrayList<String> columnTarget
    }


    public boolean checkDatabse(String dbName){
        //To check the whether the database is exit or not.
        File folder = new File(storageFolderPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> dbNames = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                dbNames.add(file.getName());
            }
        }
        boolean result;
        // extract all folder's name and add to ArrayList dbNames
        if(dbNames.contains(dbName)){
            // return true
            result = true;
        }else {
            result = false;
        }
        return result;
    }


    public void createDatabase(String dbName){

        database = new Database(dbName);
        dbs.put(dbName, database);
        dbPath = storageFolderPath + sep + dbName;

        File folder = new File(dbPath);
        boolean created = folder.mkdir();
        if (created) {
            System.out.println("Folder was created successfully.");
        } else {
            System.out.println("Failed to create the folder.");
        }
    }


    public void useDatabase(String dbNameIp){

        dbName = dbNameIp;
        database = new Database(dbName);
        dbs.put(dbName,database);

        dbPath = storageFolderPath +sep+ dbNameIp;


        //callTable(String filePath)
        //Extract every files and keep it in table
        //add table to tableshm
        File folder = new File(dbPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String tblPath = dbPath+ sep + file.getName();
                //System.out.println("tblPath"+tblPath);
                try {
                    callTable(tblPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    //JOIN coursework AND marks ON submission AND id;
    public String join(String firstTable,String secondTable,String firstKey,String SecKey) {


        DBTable firstOri = tableshm.get(firstTable);
        DBTable secondOri  = tableshm.get(secondTable);

        //;
        ArrayList<String> colList1  = new ArrayList<>(firstOri.getRows().get(0).getValues());
        //colList1.add(SecKey)
        ArrayList<String> colList2 = new ArrayList<>(secondOri.getRows().get(0).getValues());

        colList1.remove(0);colList2.remove(0);

        createTable("currentTable1",colList1);
        createTable("currentTable2",colList2);
        //here
        //colList1.add(0,"id"); colList1.add(0,"id");

        DBTable currentTable1 = tableshm.get("currentTable1");
        DBTable currentTable2 = tableshm.get("currentTable2");

        for(int i=1;i< firstOri.getRowCount();i++){
            insert("currentTable1", firstOri.getRows().get(i).getValues());
            currentTable1.getRows().get(i).removeValue(0);

        }
        for(int i=1;i< secondOri.getRowCount();i++){
            insert("currentTable2", secondOri.getRows().get(i).getValues());
            currentTable2.getRows().get(i).removeValue(0);

        }

        int table1Id = 0;
        for(int i = 0;i<currentTable1.getColumnCount();i++){
            currentTable1.getColumns().get(i).setData(0,currentTable1.getName()+"."+currentTable1.getColumns().get(i).getData().get(0));

            if((currentTable1.getColumns().get(i).getName()).equals(firstKey)){
                table1Id = i;
            }
        }
        int table2Id = 0;
        for(int i = 0;i<currentTable2.getColumnCount();i++ ){
            currentTable2.getColumns().get(i).setData(0,currentTable2.getName()+"."+currentTable2.getColumns().get(i).getData().get(0));
            if((currentTable2.getColumns().get(i).getName()).equals(SecKey)){
                table2Id = i;

            }
        }

        // Loop for matching
        for(int i = 0;i<currentTable1.getRowCount();i++){
            for(int j = 0;j<currentTable2.getRowCount();j++){
                if(i==0 && j==0){
                    for(int k = 0;k<currentTable1.getRows().get(0).rowCount();k++){
                        currentTable1.getRows().get(0).getValues().set(k,firstTable+"."+currentTable1.getRows().get(0).getValue(k));
                    }
                    for(int l = 0;l<currentTable2.getRows().get(0).rowCount();l++){
                        currentTable2.getRows().get(0).getValues().set(l,secondTable+"."+currentTable2.getRows().get(0).getValue(l));
                    }
                    currentTable1.getRows().get(0).getValues().addAll(currentTable2.getRows().get(0).getValues());

                }else
                if( (currentTable1.getRows().get(i).getValue(table1Id)).equals(currentTable2.getRows().get(j).getValue(table2Id))){
                    currentTable1.getRows().get(i).getValues().addAll(currentTable2.getRows().get(j).getValues());
                }
            }
        }

        for(int i = 0; i <currentTable1.getRowCount();i++){
            if(currentTable1.getRows().get(i).getValues().size()<currentTable1.getRows().get(0).getValues().size()){
                currentTable1.getRows().remove(i);
            }
        }


        ArrayList<String> list = new ArrayList<String>();
        list.add(firstKey);list.add(SecKey); list.add("id");
        ArrayList<Integer> indexKeep = new ArrayList<Integer>();
        for(int j = 0; j <currentTable1.getRows().get(0).getValues().size();j++){
            if (list.contains(currentTable1.getRows().get(0).getValue(j).split("\\.")[1])){
                indexKeep.add(j);
            }
        }

        for(int i = 0; i <currentTable1.getRowCount();i++){
            for(int j = 0; j<currentTable1.getRows().get(0).getValues().size();j++){
                if (indexKeep.contains(j)){
                    currentTable1.getRows().get(i).setValue(j, null);}
            }
        }
        for(int i = 0; i <currentTable1.getRowCount();i++){
            currentTable1.getRows().get(i).getValues().removeIf(str -> str == null);
        }
        int colNum = currentTable1.getColumnCount();
        for (int i = 0; i <  currentTable1.getRows().get(0).getValues().size(); i++){
            DBTColumn column = new DBTColumn("");
            column.addData(currentTable1.getRows().get(0).getValues().get(i));
            for (int j = 1; j < currentTable1.getRowCount(); j++) {
                column.addData(currentTable1.getRows().get(j).getValues().get(i));
            }

            currentTable1.addColumn(column);

        }
        for (int i = 0; i < colNum; i++){
            currentTable1.getColumns().remove(0);
        }

        DBTColumn col = new DBTColumn("index");
        col.addData("id");
        // "Here"
        //for (int i = 1; i <  currentTable1.getColumns().get(0).getData().size(); i++){
        for (int i = 1; i <  currentTable1.getColumns().get(0).columnCount(); i++){
            col.addData(Integer.toString(i));

        }
        currentTable1.getColumns().add(0,col);


        currentTable1.getRows().get(0).getValues().add(0,"id");
        for (int i = 1; i <currentTable1.getRowCount(); i++){
            currentTable1.getRows().get(i).getValues().add(0,Integer.toString(i));

        }
        for (int i = 1; i <currentTable1.getRowCount(); i++){
            currentTable1.getRows().get(i).getValues().add(0,Integer.toString(i));

        }


        String result =  printFromColumn(currentTable1);
        dropTable("currentTable1");
        dropTable("currentTable2");
        return result;

    }

    public String printFromColumn(DBTable table) {
        //String result = "hello";
        ArrayList<ArrayList<String>> input= new ArrayList<>();
        // table.getColumns().get(END_OF_TRANSMISSION);
        for(int i=0;i<table.getColumnCount();i++){
            input.add(table.getColumns().get(i).getData());
        }


        ArrayList<String> header = new ArrayList<>();
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        // Create header
        for (ArrayList<String> row : input) {
            header.add(row.get(0));
        }

        // Create data
        for (int i = 1; i < input.get(0).size(); i++) {
            ArrayList<String> newRow = new ArrayList<>();
            for (int j = 0; j < input.size(); j++) {
                newRow.add(input.get(j).get(i));
            }
            data.add(newRow);
        }

        // Create new ArrayList
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        output.add(header);
        output.addAll(data);
        String s2 ="";
        String s = "";

        for(int i=0;i<output.size();i++){
            ArrayList<String> string = output.get(i);
            s = string.toString().replace("]", "").replace("[", "");
            String[] parts = s.split(",");
            for (String part : parts) {
                System.out.print(part.trim() + " ");
                s2 = s2.concat(part);
            }
            s2 = s2.concat(" ");
            System.out.println();
        }

        return s2;

    }
    public ArrayList<String> manageLine(String line) {
        ArrayList<String> finalString = new ArrayList<>();

        line = line.replaceAll("[/\\\\\"'()\\[\\];]", "");
        String[] words = line.split(",\\s*"); // split by comma with optional whitespace
        finalString = new ArrayList<>(Arrays.asList(words));
        return finalString;
    }





    //CREATE TABLE marks (name, mark, pass);
    public void createTable(String tableName,ArrayList<String> columnName) {

        DBTable currentTable = new DBTable(tableName);
        indexcol = new Index();
        indexcol.addIndexFirst(tableName, 0);
        currentTable.addIndex(indexcol);

        // Add index column for command
        DBTColumn col1 = new DBTColumn("id");
        col1.addData("id");
        currentTable.addColumn(col1);

        // Add column from Tab file
        for(int i = 0; i< columnName.size();i++){
            DBTColumn column = new DBTColumn(columnName.get(i));
            //for(int j = 0; j<columnName.size();j++){
            column.addData(columnName.get(i));
            // }
            currentTable.addColumn(column);
        }

        // Add rows from command
        DBTRow row = new DBTRow();
        row.addValue("id");
        for(int j = 0; j<columnName.size();j++){
            row.addValue(columnName.get(j));
        }
        currentTable.addRow(row);
        tableshm.put(tableName,currentTable);
        database.addTable(currentTable);


    }

    //DELETE FROM marks WHERE name == 'Dave';
    //delete("Name","NewChris","people2");
    public void delete(String tablename,String conditionString) {
        //int whereIndexColumn = 0;
        //int targetIndexColumn = 0;
        try {

            List<String> andOrList = rex.method3(conditionString);
            ArrayList<String> uppercaseWords = new ArrayList<>();
            for (String word : andOrList) {
                uppercaseWords.add(word.toUpperCase());
            }

            DBTable currenttable = tableshm.get(tablename);// = new DBTable("new");
            ArrayList<ArrayList<String>> cdstring = rex.method2(conditionString);
            conditionString.replace("(", "").replace(")", "");
            conditionString.replace("'", "");
            //Remove row
            ArrayList<ArrayList<Boolean>> bool = new ArrayList<>();
            ArrayList<Integer> resultInd = new ArrayList<>();
            ArrayList<Integer> indRow = new ArrayList<>();
            for (int i = 0; i < currenttable.getRows().get(0).getValues().size(); i++) {
                for (int j = 0; j < cdstring.size(); j++) {
                    if (currenttable.getRows().get(0).getValues().get(i).equals(cdstring.get(j).get(0))) {
                        indRow.add(i);
//
                    }
                }
            }

            for (int i = 1; i < currenttable.getRows().size(); i++) {
                ArrayList<Boolean> bool2 = new ArrayList<>();
                for (int j = 0; j < indRow.size(); j++) {
                    for (int k = 0; k < cdstring.size(); k++) {
                        if (currenttable.getRows().get(0).getValues().get(indRow.get(j)).equals(cdstring.get(k).get(0))) {
                            switch (cdstring.get(k).get(1)) {

                                case "==":
                                    if (currenttable.getRows().get(i).getValues().get(indRow.get(j)).equals(cdstring.get(k).get(2))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);

                                    }
                                    break;
                                case ">":
                                    if (Integer.parseInt(currenttable.getRows().get(i).getValues().get(indRow.get(j))) > Integer.parseInt(cdstring.get(k).get(2))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                case ">=":
                                    if (Integer.parseInt(currenttable.getRows().get(i).getValues().get(indRow.get(j))) >= Integer.parseInt(cdstring.get(k).get(2))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                case "<":
                                    if (Integer.parseInt(currenttable.getRows().get(i).getValues().get(indRow.get(j))) < Integer.parseInt(cdstring.get(k).get(2))) {
                                        bool2.add(true);

                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                case "<=":
                                    if (Integer.parseInt(currenttable.getRows().get(i).getValues().get(indRow.get(j))) <= Integer.parseInt(cdstring.get(k).get(2))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                case "!=":
                                    if (!currenttable.getRows().get(i).getValues().get(indRow.get(j)).equals(cdstring.get(k).get(2))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                case "LIKE":
                                    if (currenttable.getRows().get(i).getValues().get(indRow.get(j)).contains(cdstring.get(k).get(2).replace("'", ""))) {
                                        bool2.add(true);
                                    } else {
                                        bool2.add(false);
                                    }
                                    break;
                                // add more cases for other operators as needed
                                default:
                                    throw new IOException("error");
                            }
                        }
                    }
                }

                bool.add(bool2);
            }


            if (bool.get(0).size() == 2) {
                for (int i = 0; i < bool.size(); i++) {
                    if (uppercaseWords.get(0).equals("AND")) {
                        if (bool.get(i).get(0) == true && bool.get(i).get(1) == true) {
                            resultInd.add(i+1);
                        }
                    } else if (uppercaseWords.get(0).equals("OR")) {
                        if (!(bool.get(i).get(0) == false && bool.get(i).get(1) == false)) {
                            resultInd.add(i+1);
                        }
                    }
                }
            } else {
                for (int i = 0; i < bool.size(); i++) {
                    if (bool.get(i).get(0) == true) {
                        resultInd.add(i+1);


                    }
                }
            }




            for (int i = currenttable.getRowCount() - 1; i >= 0; i--) {
                if (resultInd.contains(i)) {
                    currenttable.getRows().remove(i);
                }
            }

            //Remove column
            //currenttable.dropColumn(whereIndexColumnC);
            ArrayList<ArrayList<Boolean>> list = new ArrayList<>();

            for (int i = 0; i < currenttable.getColumnCount(); i++) {
                ArrayList<Boolean> list2 = new ArrayList<>();
                for (int j = 1; j < currenttable.getColumns().get(0).getData().size(); j++) {
                    for (int k = 0; k < cdstring.size(); k++) {

                        if (currenttable.getColumns().get(i).getData().get(0).equals(cdstring.get(k).get(0))) {

                            switch (cdstring.get(k).get(1).toUpperCase()) {
                                case "==":
                                    if (currenttable.getColumns().get(i).getData().get(j).equals(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case ">":
                                    if (Integer.parseInt(currenttable.getColumns().get(i).getData().get(j)) > Integer.parseInt(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case ">=":
                                    if (Integer.parseInt(currenttable.getColumns().get(i).getData().get(j)) >= Integer.parseInt(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case "<":
                                    if (Integer.parseInt(currenttable.getColumns().get(i).getData().get(j)) < Integer.parseInt(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case "<=":
                                    if (Integer.parseInt(currenttable.getColumns().get(i).getData().get(j)) <= Integer.parseInt(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case "!=":
                                    if (!currenttable.getColumns().get(i).getData().get(j).equals(cdstring.get(k).get(2))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                case "LIKE":
                                    if (currenttable.getColumns().get(i).getData().get(j).contains(cdstring.get(k).get(2).replace("'", ""))) {
                                        list2.add(true);
                                    } else {
                                        list2.add(false);
                                    }
                                    break;
                                // add more cases for other operators as needed
                                default:
                                    throw new IOException("error");
                            }
                        }
                    }
                }
                list.add(list2);
            }

            list.removeAll(Collections.singleton(new ArrayList<Boolean>()));


            ArrayList<ArrayList<Boolean>> list_tp = new ArrayList<ArrayList<Boolean>>();
            for (int i = 0; i < list.get(0).size(); i++) {
                ArrayList<Boolean> row = new ArrayList<Boolean>();
                for (int j = 0; j < list.size(); j++) {
                    row.add(list.get(j).get(i));
                }
                list_tp.add(row);
            }


            ArrayList<Integer> resultList = new ArrayList<>();


            if (list_tp.get(0).size() == 2) {
                for (int i = 0; i < list_tp.size(); i++) {
                    if (uppercaseWords.get(0).equals("AND")) {
                        if (list_tp.get(i).get(0) == true && list_tp.get(i).get(1) == true) {
                            resultList.add(i+1);
                        }
                    } else if (uppercaseWords.get(0).equals("OR")) {
                        if (!(list_tp.get(i).get(0) == false && list_tp.get(i).get(1) == false)) {
                            resultList.add(i+1);
                        }
                    }
                }
            } else {
                for (int i = 0; i < bool.size(); i++) {
                    if (list_tp.get(i).get(0) == true) {
                        resultList.add(i+1);

                    }
                }
            }


            for (int i = 0; i < currenttable.getColumnCount(); i++) {
                for (int j = 0; j < resultList.size(); j++) {
                    currenttable.getColumns().get(i).getData().remove(resultList.get(j));
                }
            }

            //System.out.println(currenttable);
            saveToDatabase(currenttable,tablename);


        }  catch (IOException e){
            System.err.println("[ERROR]");
        }


    }


    public DBTable callTable(String filePath) throws IOException {
        // it used to be  String name =".."+sep+filePath
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        // Get table name from file name if not provided in file
        String fileName = file.getName();
        String tableName = null;
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            tableName = fileName.substring(0, dotIndex);
        }else{
            //keep the file info even after reconnect the database server.
            tableName = fileName;
        }

        // create a Scanner object to read the contents of the file
        Scanner scanner = new Scanner(file);
        // read the first line of the file, which contains the column names
        //String columnLine = scanner.nextLine();
        table = new DBTable(tableName);
        ArrayList<ArrayList<String>> temp_table = new ArrayList<ArrayList<String>>();
        ArrayList<String[]> words  = new ArrayList<String[]>();
        String[] word2;
        int cntRow = 0;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            word2 = line.split("\\s+");
            words.add(word2);
            cntRow++;
        }


        indexcol = new Index();
        indexcol.addIndexFirst(tableName, cntRow-1);
        table.addIndex(indexcol);

        // Add column from Tab file
        for(int i = 0; i<words.get(0).length;i++){
            DBTColumn column = new DBTColumn(words.get(0)[i]);

            for(int j = 0; j<words.size();j++){
                column.addData(words.get(j)[i]);
            }
            table.addColumn(column);
        }
        for(int i = 0; i<words.size();i++){
            DBTRow row = new DBTRow();

            for(int j = 0; j<words.get(0).length;j++){
                row.addValue(words.get(i)[j]);
            }
            table.addRow(row);
        }
        tableshm.put(tableName,table);

        scanner.close();
        br.close();
        return table;
    }

    public void saveToDatabase(DBTable table,String tableName){
        //dbPath = storageFolderPath+"/"+dbName;
        String filePath = dbPath + sep + tableName;
        File file = new File(filePath);
        //"/Users/takashiichinose/IdeaProjects/Assignment2/Weekly
        // Workbooks/07 Briefing on DB assignment/resources/cw-db/databases\\fsrkpayikq;/marks"
        File parentDir = file.getParentFile();

        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Failed to create directory: " + parentDir.getAbsolutePath());
                return;
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write data rows
            for (DBTRow row : table.getRows()) {
                writer.write(String.join("\t", row.getValues()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save table to file: " + e.getMessage());
        }
    }

    public boolean checkTable(String tableName) {
        File file = new File(dbPath+sep+tableName);

        boolean result;
        if (file.exists()) {
            System.out.println("File exists!");
            result = true;
        } else {
            System.out.println("File does not exist.");
            result = false;
        }
        return result;
    }

    public boolean hasDuplicates(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String current = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                if (current.equals(list.get(j))) {
                    return true; // found a duplicate
                }
            }
        }
        return false; // no duplicates found
    }


    //UPDATE marks SET mark = 38 WHERE name == 'Clive';
//marks = tableName // mark = columnTaget // 38 == newValue // columnCondition == name //condition == Clive
    public void update(String tableName,String newValue,String condition,String columnCondition,String columnTaget) {

        int whereIndexColumn = 0;
        int targetIndexColumn = 0;
        DBTable currenttable = tableshm.get(tableName);
        for(int i = 0; i<currenttable.getRows().get(0).rowCount();i++){
            if((currenttable.getRows().get(0).getValue(i)).equals(columnCondition)){
                whereIndexColumn = i;
            }else
            if((currenttable.getRows().get(0).getValue(i)).equals(columnTaget)){
                targetIndexColumn = i;
            }
        }
        int targetIndexRow = 0;
        for(int i = 0; i<currenttable.getRows().size();i++){
            if((currenttable.getRows().get(i).getValue(whereIndexColumn)).equals(condition.replace("'",""))){
                targetIndexRow = i;
            }
        }
        currenttable.getRows().get(targetIndexRow).setValue(targetIndexColumn,newValue);
        //return currenttable.toString();
    }

    public void insert(String tablename,ArrayList<String> words) {
        //1.add index
        DBTable currentTable = tableshm.get(tablename);

        currentTable.getNextId().get(0).setIndex(tablename);
        String index_string = Integer.toString(currentTable.getNextId().get(0).getIndex(tablename));
        currentTable.getColumns().get(0).addData(index_string);

        for(int i = 1;i<currentTable.getColumnCount();i++){
            //for(int j = 0;j<words.size();j++){
            currentTable.getColumns().get(i).addData(words.get(i-1));
            //}
        }
        //3.add to each row
        DBTRow row = new DBTRow();
        row.addValue(index_string);
        for(int i = 0; i<words.size();i++){
            row.addValue(words.get(i));
        }
        currentTable.addRow(row);
        saveToDatabase(currentTable, tablename);
    }





    public void printTable(ArrayList<String> TempTable,ArrayList<String> columnTarget) {

        for (String value : columnTarget) {
            System.out.print(value + "\t");
        }
        System.out.println();
        int count = 0;
        for (String word : TempTable) {
            if (count == 0) {
                System.out.print(word + "\t");
                count++;
            } else if (count == columnTarget.size() - 1) {
                System.out.println(word);
                count = 0;
            } else {
                System.out.print(word + "\t");
                count++;
            }
        }
        // Print a new line if there are still words left to print
        if (count > 0) {
            System.out.println();
        }
    }






    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
