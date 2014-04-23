package com.thesis.rdbtoowl.interfaces;

import java.util.ArrayList;

public abstract interface Table {
    public abstract String name();

    public abstract void setName(String paramString);

    public abstract String database();

    public abstract void setDatabase(String paramString);

    public abstract Column getColumn(String paramString);

    public abstract void addColumn(Column paramColumn);

    public abstract PrimaryKey primaryKey();

    public abstract void setPrimaryKey(PrimaryKey paramPrimaryKey);

    public abstract ArrayList<Column> columns();

    public abstract void setColumns(ArrayList<Column> paramArrayList);

    public abstract String createSQL();

    public abstract String checkIfExistsSQL();

    public abstract String checkIfExistsForeignkeySQL();

    public abstract String createForeignKeySQL();

    public abstract String getAllDataSQL();

    public abstract String deleteAllDataSQL();

    public abstract String dropTableSQL();

    public abstract  String createDatabase();
}