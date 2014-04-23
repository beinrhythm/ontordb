package com.thesis.rdbtoowl.mysql;

import com.thesis.rdbtoowl.impl.ColumnImpl;
import com.thesis.rdbtoowl.impl.DatabaseImpl;
import com.thesis.rdbtoowl.impl.TableImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.DbManager;
import com.thesis.rdbtoowl.interfaces.Table;
import com.thesis.rdbtoowl.mysql.impl.MySQLColumnImpl;
import com.thesis.rdbtoowl.mysql.impl.MySQLDatabaseImpl;
import com.thesis.rdbtoowl.mysql.impl.MySQLTableImpl;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLColumn;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLDatabase;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLTable;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class MySQLDbManager
        implements DbManager {
    private static final Logger log = Logger
            .getLogger(MySQLDbManager.class);
    private String driver = "com.mysql.jdbc.Driver";
    private Connection connection;
    private String sql;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public MySQLDbManager(String dbUrl, String dbUser, String dbPassword) {
        try {
            Class.forName(this.driver).newInstance();
            this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            this.dbUrl = dbUrl;
            this.dbUser = dbUser;
            this.dbPassword = dbPassword;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public MySQLDbManager(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception {
        this.driver = driver;
        try {
            Class.forName(driver).newInstance();
            this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            this.dbUrl = dbUrl;
            this.dbUser = dbUser;
            this.dbPassword = dbPassword;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public MySQLDbManager(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Connection getConnection(String dbName) {
        StringTokenizer connTokens = new StringTokenizer(this.dbUrl, "/");
        String connToken = null;
        while (connTokens.hasMoreTokens()) {
            connToken = connTokens.nextToken();
        }
        if ((connToken != null) && (!connToken.equals(dbName))) {
            try {
                if (!this.dbUrl.substring(this.dbUrl.length() - 1, this.dbUrl.length()).equals("/"))
                    this.dbUrl += "/";
                this.connection = DriverManager.getConnection(this.dbUrl + dbName, this.dbUser, this.dbPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }

    public ArrayList getDatabases() {
        ArrayList databases = new ArrayList();
        this.sql = "SHOW DATABASES";
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next()) {
                MySQLDatabase database = new MySQLDatabaseImpl(new DatabaseImpl(result.getString(1)));
                database.setTables(getTables(database.name()));
                databases.add(database);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return databases;
    }

    public Database getDatabase(String databaseName) {
        this.sql = "SHOW DATABASES";
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next()) {
                MySQLDatabase database = new MySQLDatabaseImpl(new DatabaseImpl(result.getString(1)));
                if (database.name().equals(databaseName)) {
                    database.setTables(getTables(database.name()));

                    return database;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getDatabaseList() {
        ArrayList<String> databaseList = new ArrayList<String>();
        this.sql = "SHOW DATABASES";
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next())
                databaseList.add(result.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return databaseList;
    }

    public ArrayList getTables(String database) {
        ArrayList tables = new ArrayList();
        this.sql = ("SHOW TABLES FROM " + database.trim());
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next()) {
                MySQLTable table = new MySQLTableImpl(new TableImpl(result.getString(1)));
                table.setDatabase(database);
                table.setColumns(getColumns(database, table.name()));
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        try {

            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String dropDB = "DROP DATABASE MERGED";

            String showExistingDB = "SHOW DATABASES LIKE 'MERGED'";

            if (stmt.executeUpdate(showExistingDB) != 0) {
                stmt.executeUpdate(dropDB);
            }

            for (Iterator<Table> iterator = tables.iterator(); iterator.hasNext(); ) {
                Table next = iterator.next();

                MySQLTable table = new MySQLTableImpl(next);
                String createDB = "CREATE DATABASE " + table.database();
               // String showExistingDB = "SHOW DATABASES LIKE '" + table.database() + "'";

                if (stmt.executeUpdate(showExistingDB) == 0) {
                    stmt.executeUpdate(createDB);
                }

                String check = table.checkIfExistsSQL();
                ResultSet result = stmt.executeQuery(check);

                String create = table.createSQL();

                if ((result.next()) && (result.getObject(1).toString().equals("0"))) {
                    stmt.executeUpdate(create);
                } else {
                    result = stmt.executeQuery(table.checkIfExistsSQL());
                    if (!result.next()) {
                        stmt.executeUpdate(create);
                    }
                }
                log.info("Table " + table.name() + " created!");

            }

            for (Iterator<Table> iterator = tables.iterator(); iterator.hasNext(); ) {
                Table next = iterator.next();

                MySQLTable table = new MySQLTableImpl(next);
                String createFK = table.createForeignKeySQL();

                if (createFK.length() > 0) {
                    stmt.executeUpdate(createFK);
                }
            }
            log.info("Foreign Keys created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ArrayList getTableList(String database) {
        ArrayList tableList = new ArrayList();
        this.sql = ("SHOW TABLES FROM " + database.trim());
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next())
                tableList.add(result.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableList;
    }

    public ArrayList getColumns(String database, String table) {
        ArrayList columns = new ArrayList();
        ArrayList fKeys = getForeignKeys(database, table);
        this.sql = ("SHOW COLUMNS FROM " + database + "." + table);
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);

            while (result.next()) {
                MySQLColumn column = new MySQLColumnImpl(new ColumnImpl(result.getString(1)));

                String columnName = column.name();
                column.setType(result.getString(2));

                String type = result.getString(2);
                if (type.indexOf(")") != -1) {
                    type = type.substring(0, type.lastIndexOf(")") + 1);
                }
                if (type.endsWith(")")) {
                    StringTokenizer typeTokenizer = new StringTokenizer(type, "(");
                    String range = typeTokenizer.nextToken();
                    column.setRange(range);

                    String length = typeTokenizer.nextToken();
                    typeTokenizer = new StringTokenizer(length, ",");
                    if (typeTokenizer.countTokens() > 1) {
                        length = typeTokenizer.nextToken();
                        column.setLength(length);
                        String scale = typeTokenizer.nextToken();
                        column.setScale(scale.substring(0, scale.length() - 1));
                    } else {
                        column.setLength(length.substring(0, length.length() - 1));
                        column.setScale("0");
                    }
                } else {
                    column.setRange(type);
                    column.setScale("0");
                }
                if (result.getString(3).toUpperCase().equals("NO")) {
                    column.setIsNotNull(true);

                } else if (result.getString(3).toUpperCase().equals("YES")) {
                    column.setIsNotNull(false);
                }

                if (result.getString(4).toUpperCase().equals("PRI")) {
                    column.setIsPrimaryKey(true);
                }

                if (result.getString(4).toUpperCase().equals("UNI")) {
                    column.setIsUniqueKey(true);
                }

                Iterator iter = fKeys.iterator();
                while (iter.hasNext()) {
                    Hashtable fKey = (Hashtable) iter.next();
                    ArrayList fkColumns = new ArrayList((Collection) fKey.keySet());
                    if (fkColumns.contains(columnName)) {
                        column.setIsForeignKey(true);
                        column.setReferences(fKey.get(columnName).toString());
                    }
                }
                columns.add(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    public void setColumns(String table, ArrayList<Column> columns) {

    }

    private ArrayList getForeignKeys(String database, String table) {
        ArrayList<Hashtable> foreignKeys = new ArrayList<Hashtable>();
        Hashtable fKey = new Hashtable();
        this.sql = ("SHOW CREATE TABLE " + database.trim() + "." + table.trim());

        try {
            Statement stmt = getConnection().createStatement();
            ResultSet result = stmt.executeQuery(this.sql);
            result.next();
            String createStmt = result.getString(2);
            String fkColumn = "";
            String referencedColumn = "";
            String refTable = "";

            while (createStmt.indexOf("FOREIGN KEY") > -1) {
                createStmt = createStmt.substring(createStmt.indexOf("FOREIGN KEY") + 12, createStmt.length());
                String fkStmt = createStmt;
                fkColumn = fkStmt.substring(fkStmt.indexOf("(`") + 2, fkStmt.indexOf("`)"));

                fkStmt = fkStmt.substring(fkStmt.indexOf("`)") + 2, fkStmt.length());
                refTable = fkStmt.substring(fkStmt.indexOf("`") + 1, fkStmt.indexOf("(`") - 2);
                referencedColumn = fkStmt.substring(fkStmt.indexOf("(`") + 2, fkStmt.indexOf("`)"));
                referencedColumn = refTable + "." + referencedColumn;
                if (!fKey.containsValue(referencedColumn)) {
                    fKey.put(fkColumn, referencedColumn);
                    foreignKeys.add(fKey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foreignKeys;
    }

    public ResultSet getData(String sql) throws Exception {
        ResultSet result = null;
        try {
            Statement stmt = getConnection().createStatement();
            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public void dropForeignKeys(String database, String table) {
    }

}