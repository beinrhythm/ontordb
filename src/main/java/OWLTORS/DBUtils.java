package OWLTORS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "smallwonder";
	private String dbName;

	public Connection createDB(){

		Statement statement = null;
		String createDB ="CREATE DATABASE "+ getDbName();
		String showExistingDB = "SHOW DATABASES LIKE '" + getDbName() + "'";
		Connection connection = null;
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating database...");
			statement = connection.createStatement();

			int result;
			result = statement.executeUpdate(showExistingDB);
			if(result==0)
				result = statement.executeUpdate(createDB);
			statement.close();
			System.out.println("Database created successfully...");
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{

			try{
				if(statement!=null)
					statement.close();
				
			}
			catch(SQLException se2){
				se2.printStackTrace();
			}
		}
		System.out.println("DB created and connection obtained");
		return connection;

	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}


}
