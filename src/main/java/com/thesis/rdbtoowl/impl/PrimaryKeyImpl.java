package com.thesis.rdbtoowl.impl;

import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import java.util.ArrayList;

public class PrimaryKeyImpl
  implements PrimaryKey
{
  private ArrayList columns = new ArrayList();

  public void addColumn(Column c)
  {
    this.columns.add(c);
  }

  public ArrayList getColumns()
  {
    return this.columns;
  }

  public void setColumns(ArrayList columns)
  {
    this.columns = columns;
  }
}
