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
