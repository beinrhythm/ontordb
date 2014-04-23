package com.thesis.rdbtoowl.interfaces;

import java.util.ArrayList;

public abstract interface Database
{
  public abstract String name();

  public abstract void setName(String paramString);

  public abstract Table getTable(String paramString);

  public abstract void addTable(Table paramTable);

  public abstract ArrayList getTables();

  public abstract void setTables(ArrayList paramArrayList);

  public abstract String createSQL();

  public abstract String checkIfExistsSQL();

  public abstract String checkConstraintsSQL(boolean paramBoolean);
}