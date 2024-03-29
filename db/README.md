# Relational Database Server from Scratch
This project entails the construction of a relational database server from the ground up. It offers hands-on experience with Java programming, query language operations, and database persistence.

## Project File Description 
- DBSlient.java - It interacts with a local database server on port 8888, sending SQL commands and displaying server responses.
- DBServer.java - It handles basic database commands and operations.
- DBTColumn.java - It represents a database table column with a name and data, providing methods to manage and access its contents.
- DBTRow.java - It represents a database table row with an array of values, offering methods for value management and retrieval.
- Database.java - It represents a database with a name and a list of tables, providing methods to manage these tables.
- Index.java - It manages a mapping of table names to integer indices using a hashmap. 
- rex.java - It provides utility methods for parsing and evaluating SQL-like conditional statements using regular expressions. (No Need)
- ExampleDBTest.java - testing this system. 


## Features:
1. Database Management: Allows creating, using, and deleting databases.
2. Table Management: Supports creating, altering, and dropping tables.
3. Data Manipulation: Enables inserting, updating, and deleting data in tables.
4. Data Retrieval: Handles selecting data with basic filtering options.
5. Join Operation: Supports joining tables based on specified conditions.

## Getting Started
You need to prepare two separate terminals to execute this program. 

You are able to start the game by typing the following commands in the command line:
```
cw-db $ ./mvnw clean compile
```
```
cw-db $ ./mvnw exec:java@server
```

Move to another terminal: 
```
cw-db $ ./mvnw exec:java@client
```
Now, You can see this screen in the following: 
```
SQL:> 
```
You need to set up the database and table. Here is an example.  
```
SQL:>  CREATE DATABASE name of database;
```
```
SQL:>  USE name of database;
```
```
SQL:>  CREATE TABLE name of table (components);
```
```
SQL:>  INSERT INTO name of table VALUES (actual values for components);
```
```
SQL:> SELECT * FROM name of table;
```
```
SQL:> JOIN table1 AND table2 ON ref1 AND ref2;
```
```
SQL:> UPDATE table1 SET component1 = value WHERE component2 == ' ';
```
```
SQL:> DELETE FROM table WHERE component == 'value'; 
```
- USE: changes the database against which the following queries will be run
- CREATE: constructs a new database or table (depending on the provided parameters)
- INSERT: adds a new record (row) to an existing table
- SELECT: searches for records that match the given condition
- UPDATE: changes the existing data contained within a table
- ALTER: changes the structure (columns) of an existing table
- DELETE: removes records that match the given condition from an existing table
- DROP: removes a specified table from a database, or removes the entire database
- JOIN: performs an inner join on two tables (returning all permutations of all matching records)

These are the example SQL commands that you can try in this system. 

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.


