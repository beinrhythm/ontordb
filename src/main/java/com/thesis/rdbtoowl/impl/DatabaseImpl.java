package com.thesis.rdbtoowl.impl;

import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.Table;
import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseImpl
  implements Database
{
  private String name = "";
  private ArrayList tables = new ArrayList();

  public DatabaseImpl() {
  }

  public DatabaseImpl(String name) {
    this.name = name;
  }

  public String name()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Table getTable(String name)
  {
    boolean blnTableExists = false;
    Table t = new TableImpl();
    Iterator iter = this.tables.iterator();

    while (iter.hasNext()) {
      t = (Table)iter.next();
      if (t.name().equals(name)) {
        blnTableExists = true;
        break;
      }

    }

    return t;
  }

  public void addTable(Table t)
  {
    this.tables.add(t);
  }

  public ArrayList getTables() {
    return this.tables;
  }

  public void setTables(ArrayList tables) {
    this.tables = tables;
  }

  public String createSQL() {
    return "";
  }

  public String checkIfExistsSQL() {
    return "";
  }

  public String checkConstraintsSQL(boolean check) {
    return "";
  }
}