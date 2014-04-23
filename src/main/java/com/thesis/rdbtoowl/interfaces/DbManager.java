package com.thesis.rdbtoowl.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public abstract interface DbManager {
    public abstract Connection getConnection();

    public abstract Connection getConnection(String paramString);

    public abstract ArrayList getDatabases();

    public abstract Database getDatabase(String paramString);

    public abstract ArrayList getDatabaseList();

    public abstract ArrayList<Table> getTables(String paramString);

    public abstract void setTables(ArrayList<Table> tables);

    public abstract ArrayList getTableList(String paramString);

    public abstract ArrayList getColumns(String paramString1, String paramString2);

    public  abstract  void setColumns(String table, ArrayList<Column> columns);

    public abstract ResultSet getData(String paramString)
            throws Exception;

    public abstract void dropForeignKeys(String paramString1, String paramString2);
}