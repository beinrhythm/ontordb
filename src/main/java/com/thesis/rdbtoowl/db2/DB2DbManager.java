package com.thesis.rdbtoowl.db2;

import com.thesis.rdbtoowl.db2.impl.DB2ColumnImpl;
import com.thesis.rdbtoowl.db2.impl.DB2DatabaseImpl;
import com.thesis.rdbtoowl.db2.impl.DB2TableImpl;
import com.thesis.rdbtoowl.db2.interfaces.DB2Column;
import com.thesis.rdbtoowl.db2.interfaces.DB2Database;
import com.thesis.rdbtoowl.db2.interfaces.DB2Table;
import com.thesis.rdbtoowl.impl.ColumnImpl;
import com.thesis.rdbtoowl.impl.DatabaseImpl;
import com.thesis.rdbtoowl.impl.TableImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.Database;
import com.thesis.rdbtoowl.interfaces.DbManager;
import com.thesis.rdbtoowl.interfaces.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class DB2DbManager
implements DbManager
{
	private String driver = "com.ibm.db2.jcc.DB2Driver";
	private Connection connection;
	private String sql;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;

	public DB2DbManager(String dbUrl, String dbUser, String dbPassword)
	{
		try
		{
			Class.forName(this.driver).newInstance();
			this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			this.dbUrl = dbUrl;
			this.dbUser = dbUser;
			this.dbPassword = dbPassword;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public DB2DbManager(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception {
		this.driver = driver;
		try {
			Class.forName(driver).newInstance();
			this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			this.dbUrl = dbUrl;
			this.dbUser = dbUser;
			this.dbPassword = dbPassword;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public DB2DbManager(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection()
	{
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
				this.connection = DriverManager.getConnection(this.dbUrl + "/" + dbName, this.dbUser, this.dbPassword);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return this.connection;
	}

	public ArrayList getDatabases() {
		ArrayList databases = new ArrayList();
		this.sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		try
		{
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next()) {
				DB2Database database = new DB2DatabaseImpl(new DatabaseImpl(result.getString(1)));
				database.setTables(getTables(database.name()));
				databases.add(database);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return databases;
	}

	public Database getDatabase(String databaseName)
	{
		this.sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		try
		{
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next()) {
				DB2Database database = new DB2DatabaseImpl(new DatabaseImpl(result.getString(1)));
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

	public ArrayList getDatabaseList() {
		ArrayList databaseList = new ArrayList();
		this.sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		try
		{
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next())
				databaseList.add(result.getString(1));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return databaseList;
	}

	public ArrayList getTables(String database) {
		ArrayList tables = new ArrayList();
		this.sql = ("SELECT * FROM SYSIBM.SYSTABLES WHERE CREATOR ='" + database.trim() + "'");
		try
		{
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next()) {
				DB2Table table = new DB2TableImpl(new TableImpl(result.getString(1)));
				table.setDatabase(database);
				table.setColumns(getColumns(database, table.name()));
				tables.add(table);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return tables;
	}

   
    public void setTables(ArrayList<Table> tables) {

    }

    public ArrayList getTableList(String database) {
		ArrayList tableList = new ArrayList();
		this.sql = ("SELECT * FROM SYSIBM.SYSTABLES WHERE CREATOR ='" + database.trim() + "'");
		try
		{
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next())
				tableList.add(result.getString(1));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return tableList;
	}

	public ArrayList getColumns(String database, String table) {
		ArrayList columns = new ArrayList();
		ArrayList pkColumns = getPrimaryKeys(database, table);
		ArrayList fKeys = getForeignKeys(database, table);
		this.sql = ("SELECT * FROM SYSIBM.COLUMNS WHERE TABLE_SCHEMA = '" + database.trim() + "' AND TABLE_NAME = '" + table.trim() + "'");
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);

			while (result.next()) {
				DB2Column column = new DB2ColumnImpl(new ColumnImpl(result.getString(4)));
				String columnName = column.name();
				column.setType(result.getString("DATA_TYPE"));
				column.setRange(result.getString("DATA_TYPE"));

				if (result.getString("CHARACTER_MAXIMUM_LENGTH") != null) {
					column.setLength(result.getString("CHARACTER_MAXIMUM_LENGTH"));
				}

				if (result.getString("NUMERIC_PRECISION") != null) {
					column.setLength(result.getString("NUMERIC_PRECISION"));
				}

				if (result.getString("NUMERIC_SCALE") != null) {
					column.setScale(result.getString("NUMERIC_SCALE"));
				}
				else {
					column.setScale("0");
				}

				if (pkColumns.contains(columnName)) {
					column.setIsPrimaryKey(true);
				}

				Iterator iter = fKeys.iterator();
				while (iter.hasNext()) {
					Hashtable fKey = (Hashtable)iter.next();
					ArrayList fkColumns = new ArrayList((Collection)fKey.keySet());
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

    public ResultSet getData(String sql) throws Exception {
		ResultSet result = null;
		try
		{
			Statement stmt = getConnection().createStatement();
			result = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	private ArrayList getPrimaryKeys(String database, String table) {
		ArrayList primaryKeys = new ArrayList();
		this.sql = ("SELECT *  FROM SYSIBM.SYSINDEXES WHERE TBCREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'");
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);
			StringTokenizer tokenizer;
			for (; result.next(); 
					tokenizer.hasMoreTokens())
			{
				String pkColumns = result.getString("COLNAMES");
				tokenizer = new StringTokenizer(pkColumns, "+");

				primaryKeys.add(tokenizer.nextToken());
			}

			result.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return primaryKeys;
	}

	private ArrayList getForeignKeys(String database, String table) {
		ArrayList foreignKeys = new ArrayList();
		Hashtable fKey = new Hashtable();
		this.sql = ("SELECT * FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'");
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(this.sql);
			StringTokenizer tokenizer;
			StringTokenizer tokenizer2;
			result.next();

			String fkColumns = result.getString("FKCOLNAMES");
			String fkReferencedColumns = result.getString("PKCOLNAMES");
			String refTable = result.getString("REFTBNAME");

			tokenizer = new StringTokenizer(fkColumns, " ");
			tokenizer2 = new StringTokenizer(fkReferencedColumns, " ");
			while( (tokenizer.hasMoreTokens()) && (tokenizer2.hasMoreTokens())){
				String fkColumn = tokenizer.nextToken();
				String referencedColumn = refTable + "." + tokenizer2.nextToken();
				fKey.put(fkColumn, referencedColumn);
				foreignKeys.add(fKey);
			}

			result.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return foreignKeys;
	}

	public void dropForeignKeys(String database, String table) {
		String sql = "SELECT COUNT(*) FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sql);

			if (result.next())
			{
				int fkeysCount = result.getInt(1);

				for (int i = 0; i < fkeysCount; i++) {
					sql = "SELECT * FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";
					result = stmt.executeQuery(sql);
					if (result.next()) {
						String foreignKey = result.getString("RELNAME");
						sql = "ALTER TABLE \"" + database.trim() + "\".\"" + table.trim() + "\" DROP FOREIGN KEY \"" + foreignKey + "\"";
						stmt.executeUpdate(sql);
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}