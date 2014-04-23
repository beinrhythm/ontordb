package com.thesis.rdbtoowl.mysql.impl;

import com.thesis.rdbtoowl.impl.TableImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import com.thesis.rdbtoowl.interfaces.Table;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLColumn;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MySQLTableImpl
        implements MySQLTable {
    public Table t = new TableImpl();
    public String databaseName = "";
    public String type = "";
    private static int globalFKCount =0;

    public MySQLTableImpl() {
    }

    public MySQLTableImpl(Table table) {
        this.t = table;
    }

    public void addColumn(Column column) {
        this.t.addColumn(column);
    }

    public MySQLColumnImpl getColumn(String name) {
        return new MySQLColumnImpl(this.t.getColumn(name));
    }

    public ArrayList columns() {
        return this.t.columns();
    }

    public void setColumns(ArrayList columns) {
        this.t.setColumns(columns);
    }

    public String name() {
        return this.t.name();
    }

    public PrimaryKey primaryKey() {
        return this.t.primaryKey();
    }

    public void setName(String name) {
        this.t.setName(name);
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.t.setPrimaryKey(primaryKey);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        setType(type);
    }

    public String createSQL() {
        String sql = "CREATE TABLE IF NOT EXISTS " + database() + "." + this.t.name() + " ( \r\n";
        String indexSQL = "";
        MySQLColumn c = null;
        Iterator<Column> iter = this.t.columns().iterator();

        while (iter.hasNext()) {

            c = new MySQLColumnImpl( iter.next());
            sql = sql + "\t" + c.name() + " " + c.range();

            if (c.isPrimaryKey() || c.isNotNull()){
                sql = sql + " NOT NULL ";
            }else sql = sql + " NULL ";

            sql = sql + ",\r\n";
        }
        sql = sql.substring(0, sql.length() - 3);

        iter = this.t.primaryKey().getColumns().iterator();

        if (iter.hasNext()) {

            sql = sql + ",\r\n\tPRIMARY KEY (";

            while (iter.hasNext()) {

                c = new MySQLColumnImpl( iter.next());

                StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
                String name = "";
                while (tokenizer.hasMoreTokens()) {
                    name = tokenizer.nextToken();
                }
                sql = sql + name + ",";
                indexSQL = indexSQL + "\r\n\tINDEX (" + name + "),";
            }

            sql = sql.substring(0, sql.length() - 1);

            sql = sql + "),";

            sql = sql + indexSQL;
        } else {
            sql = sql + ")";
        }

        sql = sql.substring(0, sql.length() - 1);

        sql = sql + ") CHARSET=utf8 \r\n";

        return sql;
    }

    public String createForeignKeySQL() {
        String sql = "ALTER TABLE " + database() + "." + this.t.name();
        String foreignKeySQL = "";
        MySQLColumn c = null;
        int localFKCount = 0;
        Iterator<Column> iter = this.t.columns().iterator();
        while (iter.hasNext()) {
            c = new MySQLColumnImpl( iter.next());
            if (c.isForeignKey()) {
                globalFKCount++;
                localFKCount++;
                StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
                String fkName = "";
                while (tokenizer.hasMoreTokens()) {
                    fkName = tokenizer.nextToken();
                }
                foreignKeySQL = "\nADD CONSTRAINT "+fkName+"_FK"+ globalFKCount +"\r\n\t FOREIGN KEY (";
                foreignKeySQL = foreignKeySQL + fkName + ") REFERENCES ";
                foreignKeySQL = foreignKeySQL +database() + "." + c.references().substring(0, c.references().indexOf(".")) + "(";
                foreignKeySQL = foreignKeySQL + c.references().substring(c.references().indexOf(".") + 1, c.references().length());
                sql = sql + foreignKeySQL + "),";
            }
        }

        if (localFKCount > 0) {
            sql = sql.substring(0, sql.length() - 1);
        } else sql = "";

        return sql;
    }

    public String checkIfExistsForeignkeySQL() {
        String sql = "";
        return sql;
    }

    public String database() {
        return this.t.database();
    }

    public void setDatabase(String databaseName) {
        this.t.setDatabase(databaseName);
    }

    public String createDatabase() {
        return "CREATE DATABASE IF NOT EXISTS '" + database() + "'";
//                "USE '"+database()+"'";
    }

    public String checkIfExistsSQL() {
        return "SHOW TABLES FROM " + database() + " LIKE '" + name() + "'";
    }

    public String getAllDataSQL() {
        return "SELECT * FROM " + database() + "." + name();
    }

    public String deleteAllDataSQL() {
        return "DELETE FROM " + database() + "." + name();
    }

    public String dropTableSQL() {
        return "DROP TABLE " + database() + "." + name();
    }
}