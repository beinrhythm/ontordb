package OWLTORS;

import java.util.List;

public class Table {

	private String tableName;
	private List<TableColumn> column;
	private String primarykey;
	private List<ForeignKey> foreignKey;

	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<TableColumn> getColumnName() {
		return column;
	}
	public void setColumnName(List<TableColumn> columnName) {
		this.column = columnName;
	}
	public String getPrimarykey() {
		return primarykey;
	}
	public void setPrimarykey(String primarykey) {
		this.primarykey = primarykey;
	}
	public List<ForeignKey> getForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(List<ForeignKey> foreignKey) {
		this.foreignKey = foreignKey;
	}
}
