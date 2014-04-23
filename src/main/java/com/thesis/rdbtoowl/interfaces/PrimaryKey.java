package com.thesis.rdbtoowl.interfaces;

import java.util.ArrayList;

public abstract interface PrimaryKey
{
  public abstract void  addColumn(Column paramColumn);

  public abstract ArrayList<Column> getColumns();

  public abstract void setColumns(ArrayList<Column> paramArrayList);
}