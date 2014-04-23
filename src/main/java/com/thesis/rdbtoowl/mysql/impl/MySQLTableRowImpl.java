package com.thesis.rdbtoowl.mysql.impl;

import com.thesis.rdbtoowl.impl.TableRowImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.TableRow;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLColumn;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLTableRow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MySQLTableRowImpl
  implements MySQLTableRow
{
  private TableRow tableRow = new TableRowImpl();

  public MySQLTableRowImpl()
  {
  }

  public MySQLTableRowImpl(TableRow tableRow)
  {
    this.tableRow = tableRow;
  }

  public void addColumn(Column column)
  {
    this.tableRow.addColumn(column);
  }

  public String table()
  {
    return this.tableRow.table();
  }

  public void setTable(String name)
  {
    this.tableRow.setTable(name);
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
    String sql = "INSERT INTO " + database() + "." + table();
    String columnsSQL = " (";
    String valuesSQL = "VALUES (";

    Iterator iter = columns().iterator();
    while (iter.hasNext()) {
      Column c = (Column)iter.next();
      columnsSQL = columnsSQL + c.name() + ",";

      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "\\'");
          token = token.replace("\"", "\"\"");
        }
        value = value + token;
      }
      valuesSQL = valuesSQL + "'" + value + "',";
    }

    valuesSQL = valuesSQL.substring(0, valuesSQL.length() - 1) + ")";

    columnsSQL = columnsSQL.substring(0, columnsSQL.length() - 1) + ")";
    sql = sql + columnsSQL + " " + valuesSQL;

    return sql;
  }

  public String checkIfExistsSQL()
  {
    String sql = "SELECT COUNT(*) FROM " + database().trim() + "." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = columns().iterator();
    while (iter.hasNext()) {
      MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "\\'");
          token = token.replace("\"", "\"\"");
        }
        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")) || (c.range().equals("timestamp")) || (c.range().equals("char"))) {
        columnValue = "'" + value + "'";
      }
      else {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String checkIfExistsSQL(ArrayList columns)
  {
    String sql = "SELECT COUNT(*) FROM " + database().trim() + "." + table().trim() + " WHERE ";
    String columnName = "";
    String columnValue = "";

    Iterator iter = columns.iterator();
    while (iter.hasNext()) {
      MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "\\'");
          token = token.replace("\"", "\"\"");
        }
        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")) || (c.range().equals("timestamp")) || (c.range().equals("char"))) {
        columnValue = "'" + value + "'";
      }
      else {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String database()
  {
    return this.tableRow.database();
  }

  public void setDatabase(String name)
  {
    this.tableRow.setDatabase(name);
  }

  public String deleteSQL() {
    String sql = "DELETE FROM " + database().trim() + "." + table().trim() + " WHERE ";

    String columnName = "";
    String columnValue = "";

    Iterator iter = columns().iterator();
    while (iter.hasNext()) {
      MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "\\'");
          token = token.replace("\"", "\"\"");
        }
        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")) || (c.range().equals("timestamp")) || (c.range().equals("char"))) {
        columnValue = "'" + value + "'";
      }
      else {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }

  public String deleteSQL(ArrayList columns)
  {
    String sql = "DELETE FROM " + database().trim() + "." + table().trim() + " WHERE ";

    String columnName = "";
    String columnValue = "";

    Iterator iter = columns.iterator();
    while (iter.hasNext()) {
      MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
      columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
      String value = "";
      StringTokenizer valueTokenizer = new StringTokenizer(c.value());
      while (valueTokenizer.hasMoreTokens()) {
        String token = valueTokenizer.nextToken();
        if (((token.indexOf("'") != -1) && (token.indexOf("\"") == -1)) || (
          (token.indexOf("'") == -1) && (token.indexOf("\"") != -1))) {
          token = token.replace("'", "\\'");
          token = token.replace("\"", "\"\"");
        }
        value = value + token;
      }
      if ((c.range().equals("string")) || (c.range().equals("date")) || (c.range().equals("dateTime")) || (c.range().equals("timestamp")) || (c.range().equals("char"))) {
        columnValue = "'" + value + "'";
      }
      else {
        columnValue = value;
      }
      sql = sql + columnName + " = " + columnValue + " AND ";
    }

    sql = sql.substring(0, sql.length() - 4);
    return sql;
  }
}