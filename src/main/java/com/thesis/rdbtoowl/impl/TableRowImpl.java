package com.thesis.rdbtoowl.impl;

import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.TableRow;
import java.util.ArrayList;

public class TableRowImpl
  implements TableRow
{
  private String tableName = "";
  private String databaseName = "";
  private ArrayList columns = new ArrayList();

  public TableRowImpl() {
  }

  public TableRowImpl(String tableName) {
    this.tableName = tableName;
  }

  public TableRowImpl(String tableName, ArrayList columns) {
    this.tableName = "";
    this.columns = columns;
  }

  public void addColumn(Column c) {
    this.columns.add(c);
  }

  public String table() {
    return this.tableName;
  }

  public void setTable(String tableName) {
    this.tableName = tableName;
  }

  public ArrayList columns() {
    return this.columns;
  }

  public void setColumns(ArrayList columns)
  {
    this.columns = columns;
  }

  public String createSQL() {
    return "";
  }

  public String checkIfExistsSQL() {
    return "";
  }

  public String checkIfExistsSQL(ArrayList columns) {
    return "";
  }

  public String database() {
    return this.databaseName;
  }

  public void setDatabase(String databaseName) {
    this.databaseName = databaseName;
  }

  public String deleteSQL() {
    return "";
  }

  public String deleteSQL(ArrayList columns) {
    return "";
  }
}