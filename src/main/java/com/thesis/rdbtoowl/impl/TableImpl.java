package com.thesis.rdbtoowl.impl;

import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import com.thesis.rdbtoowl.interfaces.Table;
import java.util.ArrayList;
import java.util.Iterator;

public class TableImpl implements Table {
	private String name = "";
	private String databaseName = "";
	private ArrayList<Column> columns = new ArrayList<Column>();
	private PrimaryKey primaryKey = new PrimaryKeyImpl();

	public TableImpl() {
	}

	public TableImpl(String name) {
		this.name = name;
	}

	public TableImpl(Table table) {
		this.name = table.name();
		this.databaseName = table.database();
		this.columns = table.columns();
		this.primaryKey = table.primaryKey();
	}

	public String name() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Column getColumn(String name) {
		boolean blnColExists = false;
		Column c = null;
		Iterator<Column> iter = this.columns.listIterator();

		while (iter.hasNext()) {
			c = iter.next();
			if (c.name().equals(name)) {
				blnColExists = true;
				break;
			}

		}

		return c;
	}

	public void addColumn(Column c) {
		if (!this.columns.contains(c))
			this.columns.add(c);
	}

	public PrimaryKey primaryKey() {
		return this.primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;

		/*Iterator<Column> iter = primaryKey.getColumns().iterator();
		Iterator<Column> iter2;
		for (; iter.hasNext(); iter2.hasNext()) {
			Column pkColumn = iter.next();
			iter2 = columns().iterator();
			Column column = iter2.next();
			if (column.name().equals(pkColumn.name()))
				column.setIsPrimaryKey(true);
		}*/
	}

    public ArrayList<Column> columns() {
		return this.columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public String createSQL() {
		return "";
	}

	public String checkIfExistsSQL() {
		return "";
	}

	public String createForeignKeySQL() {
		return "";
	}

	public String checkIfExistsForeignkeySQL() {
		return "";
	}

	public String database() {
		return this.databaseName;
	}

	public void setDatabase(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getAllDataSQL() {
		return "SELECT * FROM \"" + database() + "\"." + name();
	}

	public String deleteAllDataSQL() {
		return null;
	}

	public String dropTableSQL() {
		return null;
	}

    public String createDatabase() {
        return "";
    }
}