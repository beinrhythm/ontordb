package com.thesis.rdbtoowl.db2.impl;

import com.thesis.rdbtoowl.db2.interfaces.DB2Column;
import com.thesis.rdbtoowl.db2.interfaces.DB2TableRow;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.TableRow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class DB2TableRowImpl
  implements DB2TableRow
{
  private TableRow tableRow;

  public DB2TableRowImpl()
  {
  }

  public DB2TableRowImpl(TableRow tableRow)
  {
    this.tableRow = tableRow;
  }

  public String database()
  {
    return this.tableRow.database();
  }

  public void setDatabase(String name)
  {
    this.tableRow.setDatabase(name);
  }

  public String table()
  {
    return this.tableRow.table();
  }

  public void setTable(String name)
  {
    this.tableRow.setTable(name);
  }

  public void addColumn(Column column)
  {
    this.tableRow.addColumn(column);
  }

  public ArrayList columns()
  {
    return this.tableRow.columns();
  }

  public void setColumns(ArrayList columns)
  {
    this.tableRow.setColumns(columns);
  }

  public String createSQL()
  {
    String sql = "INSERT INTO \"" + database() + "\"." + table() + " ";
    String columnStmt = "(";
    String valuesStmt = " VALUES (";

    Iterator iter = columns().iterator();
    while (iter.hasNext()) {
      DB2Column c = new DB2ColumnImpl((Column)iter.next());
      columnStmt = columnStmt + c.name().substring(c.name().indexOf(".") + 1, c.name().length()) + ",";
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "''");
        }

        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")))
      {
        valuesStmt = valuesStmt + "'" + value + "',";
      }
      else
      {
        valuesStmt = valuesStmt + value + ",";
      }
    }
    columnStmt = columnStmt.substring(0, columnStmt.length() - 1) + ")";
    valuesStmt = valuesStmt.substring(0, valuesStmt.length() - 1) + ")";
    sql = sql + columnStmt + valuesStmt;

    return sql;
  }

  public String checkIfExistsSQL()
  {
    String sql = "SELECT COUNT(*) FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = columns().iterator();
    while (iter.hasNext()) {
      DB2Column c = new DB2ColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "''");
        }

        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")))
      {
        columnValue = "'" + value + "'";
      }
      else
      {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String checkIfExistsSQL(ArrayList columns)
  {
    String sql = "SELECT COUNT(*) FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = columns.iterator();
    while (iter.hasNext()) {
      DB2Column c = new DB2ColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "''");
        }

        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")))
      {
        columnValue = "'" + value + "'";
      }
      else
      {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String deleteSQL() {
    String sql = "DELETE FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = this.tableRow.columns().iterator();
    while (iter.hasNext()) {
      DB2Column c = new DB2ColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "''");
        }

        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")))
      {
        columnValue = "'" + value + "'";
      }
      else
      {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String deleteSQL(ArrayList columns) {
    String sql = "DELETE FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = columns.iterator();
    while (iter.hasNext()) {
      DB2Column c = new DB2ColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "''");
        }

        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")))
      {
        columnValue = "'" + value + "'";
      }
      else
      {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }
}