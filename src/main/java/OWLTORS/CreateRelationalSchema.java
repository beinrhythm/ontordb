package OWLTORS;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CreateRelationalSchema extends Initilizer {

	private List<OntologyClass> ontologyClasses;
	private Connection connection ;
	private Statement statement;
	private String schemaName;

	public void create(List<OntologyClass> ontologyClasses){
		this.ontologyClasses = ontologyClasses;
		try{
			connection = db.createDB();
			statement = connection.createStatement();
			schemaName = db.getDbName();
			createTables();
			System.out.println("Tables created!!!");
			createColumnConstraints();
			System.out.println("Constraints created!!!");

		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(connection!=null)
					connection.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}


	private void createTables(){
		System.out.println("Creating tables- ");
		String createTableQuery = "";
		Table table;

		for(OntologyClass ontologyClass : ontologyClasses){
			table = new Table();
			table.setTableName(ontologyClass.getName());
			List<TableColumn> tableColumn = new ArrayList<TableColumn>();
			List<ForeignKey> foreignKey = new ArrayList<ForeignKey>();
			TableColumn tc;
			for(ClassProperty property : ontologyClass.getProperties()){
				ForeignKey fk;
				tc = new TableColumn();

				if(property.isFunctional()){
					table.setPrimarykey(property.getName());
					tc.setPrimaryKey(true);
				}
				tc.setCoulmnName(property.getName());
				if(property.isDataTypeProperty())
					tc.setDataType(dtc.get(property.getRange().get(0)));


				if(property.isObjectProperty()){
					fk = new ForeignKey();
					tc.setForeignKey(true);
					fk.setName(property.getName());
					if(property.getDomain().size()==1)
						fk.setFromTableName(property.getDomain().get(0));
					if(property.getRange().size()==1)
						fk.setToTableName(property.getRange().get(0));
					fk.setToTableColumnName("");

					foreignKey.add(fk);
				}
				tableColumn.add(tc);
			}
			table.setColumnName(tableColumn);
			table.setForeignKey(foreignKey);
			createTableQuery = createTableQuery + getCreateTableQuery(table);
			createTableQuery = createTableQuery +" \n);\n";

		}
		try {
			statement.executeUpdate(createTableQuery);
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}		

	}

	private void createColumnConstraints(){
		System.out.println("Creating column constraints");
	}

	private String getCreateTableQuery(Table table){

		String query = "CREATE TABLE '" +schemaName+ "'.'"+ table.getTableName()+ "' (\n";

		for(TableColumn column : table.getColumnName()){
			if(table.getPrimarykey().equals(column.getCoulmnName()) && column.isPrimaryKey()){
				System.out.println("Column '" + column.getCoulmnName()+ "' is a primary key");
				query = query + "'"+column.getCoulmnName() + "' "+ column.getDataType()+ " not NULL,  PRIMARY KEY ('" +column.getCoulmnName()+"')\n";

			}else{
			
				query = query + column.getCoulmnName() + " " + column.getDataType()+" NULL ,\n";
			}
		}


		return query;

	}
}
