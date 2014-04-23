package com.thesis.rdbtoowl.interfaces;

import java.util.ArrayList;

public abstract interface TableRow
{
  public abstract void addColumn(Column paramColumn);

  public abstract String table();

  public abstract void setTable(String paramString);

  public abstract String database();

  public abstract void setDatabase(String paramString);

  public abstract ArrayList columns();

  public abstract void setColumns(ArrayList paramArrayList);

  public abstract String createSQL();

  public abstract String checkIfExistsSQL();

  public abstract String checkIfExistsSQL(ArrayList paramArrayList);

  public abstract String deleteSQL();

  public abstract String deleteSQL(ArrayList paramArrayList);
}