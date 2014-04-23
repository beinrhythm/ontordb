package com.thesis.rdbtoowl.mysql.impl;

import com.thesis.rdbtoowl.impl.DatabaseImpl;
import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.Table;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLDatabase;
import java.util.ArrayList;

public class MySQLDatabaseImpl
  implements MySQLDatabase
{
  private Database d = new DatabaseImpl();

  public MySQLDatabaseImpl()
  {
  }

  public MySQLDatabaseImpl(Database database)
  {
    this.d = database;
  }

  public void addTable(Table t)
  {
    this.d.addTable(t);
  }

  public String name()
  {
    return this.d.name();
  }

  public Table getTable(String name)
  {
    return this.d.getTable(name);
  }

  public void setName(String name)
  {
    this.d.setName(name);
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
    String sql = "CREATE DATABASE IF NOT EXISTS " + this.d.name();

    return sql;
  }

  public String checkIfExistsSQL()
  {
    return "SHOW DATABASES LIKE '" + this.d.name() + "'";
  }

  public String checkConstraintsSQL(boolean check)
  {
    String sql = "";
    if (check) sql = "SET FOREIGN_KEY_CHECKS = 1"; else {
      sql = "SET FOREIGN_KEY_CHECKS = 0";
    }
    return sql;
  }
}