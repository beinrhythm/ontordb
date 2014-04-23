package com.thesis.rdbtoowl.db2.impl;

import com.thesis.rdbtoowl.db2.interfaces.DB2Column;
import com.thesis.rdbtoowl.db2.interfaces.DB2Table;
import com.thesis.rdbtoowl.impl.TableImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import com.thesis.rdbtoowl.interfaces.Table;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class DB2TableImpl
        implements DB2Table {
    private Table t = new TableImpl();
    private String type = "";

    public DB2TableImpl() {
    }

    public DB2TableImpl(Table table) {
        this.t = table;
    }

    public String name() {
        return this.t.name();
    }

    public void setName(String name) {
        this.t.setName(name);
    }

    public String database() {
        return this.t.database();
    }

    public void setDatabase(String name) {
        this.t.setDatabase(name);
    }

    public DB2Column getColumn(String name) {
        return new DB2ColumnImpl(this.t.getColumn(name));
    }

    public void addColumn(Column column) {
        this.t.addColumn(column);
    }

    public PrimaryKey primaryKey() {
        return this.t.primaryKey();
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.t.setPrimaryKey(primaryKey);
    }

    public ArrayList columns() {
        return this.t.columns();
    }

    public void setColumns(ArrayList columns) {
        this.t.setColumns(columns);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        setType(type);
    }

    public String createSQL() {
        String sql = "CREATE TABLE \"" + database() + "\"." + this.t.name() + " ( \r\n";
        DB2ColumnImpl c = null;
        Iterator iter = this.t.columns().iterator();

        while (iter.hasNext()) {
            c = new DB2ColumnImpl((Column) iter.next());
            StringTokenizer columnTokens = new StringTokenizer(c.name(), ".");
            String columnName = c.name();
            while (columnTokens.hasMoreTokens()) {
                columnName = columnTokens.nextToken();
            }
            sql = sql + "\t" + columnName + " " + c.getType();
            if (c.isPrimaryKey())
                sql = sql + " NOT NULL ";
            sql = sql + ", \r\n";
        }
        if (this.t.primaryKey().getColumns().size() == 0) {
            sql = sql.substring(0, sql.lastIndexOf(","));
        }

        iter = this.t.primaryKey().getColumns().iterator();
        if (iter.hasNext()) {
            sql = sql + "\tPRIMARY KEY (";
            while (iter.hasNext()) {
                c = new DB2ColumnImpl((Column) iter.next());

                StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
                String name = "";
                while (tokenizer.hasMoreTokens()) {
                    name = tokenizer.nextToken();
                }
                sql = sql + name + ",";
            }
            sql = sql.substring(0, sql.lastIndexOf(","));
            sql = sql + ")";
        }

        sql = sql + " \r\n)";

        return sql;
    }

    public String checkIfExistsSQL() {
        String sql = "SELECT COUNT(*) FROM SYSIBM.SYSTABLES WHERE CREATOR = '" + database().trim() + "'  AND NAME = '" + name().trim() + "'";
        return sql;
    }

    public String createForeignKeySQL() {
        String sql = "ALTER TABLE \"" + database() + "\"." + this.t.name();
        String foreignKeySQL = "";
        String referencesSQL = "";
        String separateFKSql = "";
        boolean fkExist = false;
        ArrayList fkColumns = new ArrayList();
        ArrayList refColumns = new ArrayList();
        Hashtable fkValues = new Hashtable();
        Hashtable fkHash = new Hashtable();
        DB2ColumnImpl c = null;

        Iterator iter = this.t.columns().iterator();
        while (iter.hasNext()) {
            c = new DB2ColumnImpl((Column) iter.next());
            if (c.isForeignKey()) {
                String tableReferenced = c.references().substring(0, c.references().indexOf("."));
                String columnReferenced = c.references().substring(c.references().indexOf(".") + 1, c.references().length());

                StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
                String fkColumn = "";
                while (tokenizer.hasMoreTokens()) {
                    fkColumn = tokenizer.nextToken();
                }

                if (fkHash.containsKey(tableReferenced)) {
                    fkValues = (Hashtable) fkHash.get(tableReferenced);
                    fkColumns = (ArrayList) fkValues.get("fkColumns");
                    fkColumns.add(fkColumn);
                    fkValues.put("fkColumns", fkColumns);
                    refColumns = (ArrayList) fkValues.get("refColumns");
                    refColumns.add(columnReferenced);
                    fkValues.put("refColumns", refColumns);
                    fkHash.remove(tableReferenced);
                    fkHash.put(tableReferenced, fkValues);
                } else {
                    fkValues = new Hashtable();
                    fkColumns = new ArrayList();
                    refColumns = new ArrayList();
                    fkColumns.add(fkColumn);
                    fkValues.put("fkColumns", fkColumns);
                    refColumns.add(columnReferenced);
                    fkValues.put("refColumns", refColumns);
                    fkHash.put(tableReferenced, fkValues);
                }
            }
        }

        Enumeration fkElements = fkHash.keys();
        while (fkElements.hasMoreElements()) {
            String tableReferenced = fkElements.nextElement().toString();
            fkValues = (Hashtable) fkHash.get(tableReferenced);
            foreignKeySQL = "\r\n\tADD FOREIGN KEY (";
            referencesSQL = " REFERENCES \"" + database() + "\"." + tableReferenced + " (";
            separateFKSql = "";

            fkColumns = (ArrayList) fkValues.get("fkColumns");
            iter = fkColumns.iterator();
            while (iter.hasNext()) {
                foreignKeySQL = foreignKeySQL + iter.next().toString() + ",";
                fkExist = true;
            }
            foreignKeySQL = foreignKeySQL.substring(0, foreignKeySQL.length() - 1) + ")";

            refColumns = (ArrayList) fkValues.get("refColumns");
            int refColumnId = 0;
            iter = ((ArrayList) fkValues.get("refColumns")).iterator();
            while (iter.hasNext()) {
                String columnReferenced = iter.next().toString();

                if (refColumns.indexOf(columnReferenced) != refColumns.lastIndexOf(columnReferenced)) {
                    separateFKSql = separateFKSql + "\r\n\tADD FOREIGN KEY (" + fkColumns.get(refColumnId) + ")";
                    separateFKSql = separateFKSql + " REFERENCES \"" + database() + "\"." + tableReferenced + " (" + columnReferenced + ")";
                    refColumnId++;
                    fkExist = true;
                } else {
                    referencesSQL = referencesSQL + columnReferenced + ",";
                }
            }
            referencesSQL = referencesSQL.substring(0, referencesSQL.length() - 1) + ")";

            if (referencesSQL.endsWith(" )")) {
                sql = sql + separateFKSql;
            } else {
                sql = sql + foreignKeySQL + referencesSQL + separateFKSql;
            }
        }
        if (!fkExist) {
            sql = "";
        }
        return sql;
    }

    public String checkIfExistsForeignkeySQL() {
        String sql = "SELECT COUNT(*) FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database().trim() + "'  AND TBNAME = '" + name().trim() + "'";
        return sql;
    }

    public String getAllDataSQL() {
        return "SELECT * FROM \"" + database() + "\"." + name();
    }

    public String deleteAllDataSQL() {
        return "DELETE FROM \"" + database() + "\"." + name();
    }

    public String dropTableSQL() {
        return "DROP TABLE \"" + database() + "\"." + name();
    }

    public String createDatabase() {
        return null;
    }
}