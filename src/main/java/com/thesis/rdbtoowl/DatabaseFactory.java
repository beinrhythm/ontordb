package com.thesis.rdbtoowl;

import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import com.thesis.rdbtoowl.interfaces.Table;
import com.thesis.rdbtoowl.interfaces.TableRow;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class DatabaseFactory
{
  private static final String PROPS_PATH = "rdbms.properties";
  private static Document RDBMSDoc;

  private static void initializeProperties()
  {
    try
    {
      SAXBuilder builder = new SAXBuilder();
      RDBMSDoc = builder.build(new File("rdbms.properties"));
    }
    catch (JDOMException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Element getDriverElement(String driver) {
    initializeProperties();
    Element root = RDBMSDoc.getRootElement();

    Iterator iter = root.getChildren().iterator();
    while (iter.hasNext()) {
      Element jdbcDriver = (Element)iter.next();
      if (jdbcDriver.getAttributeValue("driverName").equals(driver)) {
        return jdbcDriver;
      }
    }
    return null;
  }

  public static Database createDatabaseInstance(String driver, Database database) {
    initializeProperties();
    Element jdbcDriver = getDriverElement(driver);
    String databaseImpl = jdbcDriver.getChild("databaseImpl").getValue();

    Class[] databaseArgsClass = { Database.class };
    Object[] databaseArgs = { database };
    try
    {
      Class databaseClassDefinition = Class.forName(databaseImpl);
      Constructor databaseArgsConstructor = 
        databaseClassDefinition.getConstructor(databaseArgsClass);

      return (Database)databaseArgsConstructor.newInstance(databaseArgs);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static Table createTableInstance(String driver, Table table) {
    initializeProperties();
    Element jdbcDriver = getDriverElement(driver);
    String tableImpl = jdbcDriver.getChild("tableImpl").getValue();

    Class[] tableArgsClass = { Table.class };
    Object[] tableArgs = { table };
    try
    {
      Class tableClassDefinition = Class.forName(tableImpl);
      Constructor tableArgsConstructor = 
        tableClassDefinition.getConstructor(tableArgsClass);

      return (Table)tableArgsConstructor.newInstance(tableArgs);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static Column createColumnInstance(String driver, Column column) {
    initializeProperties();
    Element jdbcDriver = getDriverElement(driver);
    String columnImpl = jdbcDriver.getChild("columnImpl").getValue();

    Class[] columnArgsClass = { Column.class };
    Object[] columnArgs = { column };
    try
    {
      Class columnClassDefinition = Class.forName(columnImpl);
      Constructor columnArgsConstructor = 
        columnClassDefinition.getConstructor(columnArgsClass);

      return (Column)columnArgsConstructor.newInstance(columnArgs);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static PrimaryKey createPrimaryKeyInstance(String driver, PrimaryKey primaryKey) {
    initializeProperties();
    Element jdbcDriver = getDriverElement(driver);
    String primaryKeyImpl = jdbcDriver.getChild("primaryKeyImpl").getValue();

    Class[] primaryKeyArgsClass = { PrimaryKey.class };
    Object[] primaryKeyArgs = { primaryKey };
    try
    {
      Class primaryKeyClassDefinition = Class.forName(primaryKeyImpl);
      Constructor primaryKeyArgsConstructor = 
        primaryKeyClassDefinition.getConstructor(primaryKeyArgsClass);

      return (PrimaryKey)primaryKeyArgsConstructor.newInstance(primaryKeyArgs);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static TableRow createTableRowInstance(String driver, TableRow tableRow) {
    initializeProperties();
    Element jdbcDriver = getDriverElement(driver);
    String tableRowImpl = jdbcDriver.getChild("tableRowImpl").getValue();

    Class[] tableRowArgsClass = { TableRow.class };
    Object[] tableRowArgs = { tableRow };
    try
    {
      Class tableRowClassDefinition = Class.forName(tableRowImpl);
      Constructor tableRowArgsConstructor = 
        tableRowClassDefinition.getConstructor(tableRowArgsClass);

      return (TableRow)tableRowArgsConstructor.newInstance(tableRowArgs);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }

    return null;
  }
}