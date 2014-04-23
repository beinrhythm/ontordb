package com.thesis.rdbtoowl.db2.impl;

import com.thesis.rdbtoowl.db2.interfaces.DB2Database;
import com.thesis.rdbtoowl.impl.DatabaseImpl;
import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.Table;
import java.util.ArrayList;

public class DB2DatabaseImpl
  implements DB2Database
{
  private Database d = new DatabaseImpl();

  public DB2DatabaseImpl()
  {
  }

  public DB2DatabaseImpl(Database database)
  {
    this.d = database;
  }

  public String name()
  {
    return this.d.name();
  }

  public void setName(String name)
  {
    this.d.setName(name);
  }

  public Table getTable(String name)
  {
    return this.d.getTable(name);
  }

  public void addTable(Table table)
  {
    this.d.addTable(table);
  }

  public ArrayList getTables()
  {
    return this.d.getTables();
  }

  public void setTables(ArrayList tables)
  {
    this.d.setTables(tables);
  }

  public String createSQL()
  {
    String sql = "CREATE SCHEMA " + this.d.name();
    return sql;
  }

  public String checkIfExistsSQL()
  {
    String sql = "SELECT COUNT(*) FROM SYSIBM.SYSSCHEMATA WHERE NAME = '" + this.d.name() + "'";
    return sql;
  }

  public String checkConstraintsSQL(boolean check)
  {
    String sql = "";
    return sql;
  }
}