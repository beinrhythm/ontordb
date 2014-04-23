package com.thesis.rdbtoowl.mysql.impl;

import com.thesis.rdbtoowl.impl.ColumnImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.mysql.interfaces.MySQLColumn;

public class MySQLColumnImpl
        implements MySQLColumn {
    private Column c = new ColumnImpl();
    private String type = "";

    public MySQLColumnImpl() {
    }

    public MySQLColumnImpl(Column column) {
        this.c = column;
    }

    public String scale() {
        return this.c.scale();
    }

    public void setScale(String scale) {
        this.c.setScale(scale);
    }

    public String length() {
        String length = "";
        if ((this.c.length().length() > 0) && (new Integer(this.c.length()).intValue() > 2024)) {
            length = "2024";
        } else {
            length = this.c.length();
        }
        return length;
    }

    public void setLength(String length) {
        this.c.setLength(length);
    }

    public String range() {
        return this.c.range();
    }

    public void setRange(String range) {
        if (range.equals("int")) {
            this.c.setRange("integer");
        }
        if (range.equals("varchar")) {
            this.c.setRange("string");
        }
        if (range.equals("datetime")) {
            this.c.setRange("dateTime");
        }
        if (range.equals("decimal")) {
            this.c.setRange("decimal");
        }
        if (range.equals("date")) {
            this.c.setRange("date");
        }

        if (range.equals("tinyint")) {
            this.c.setRange("tinyint");
        }
        if (range.equals("smallint")) {
            this.c.setRange("smallint");
        }
        if (range.equals("mediumint")) {
            this.c.setRange("mediumint");
        }
        if (range.equals("bigint")) {
            this.c.setRange("bigint");
        }
        if (range.equals("real")) {
            this.c.setRange("real");
        }
        if (range.equals("float")) {
            this.c.setRange("float");
        }
        if (range.equals("double")) {
            this.c.setRange("double");
        }
        if (range.equals("numeric")) {
            this.c.setRange("numeric");
        }
        if (range.equals("time")) {
            this.c.setRange("time");
        }
        if (range.equals("timestamp")) {
            this.c.setRange("timestamp");
        }
        if (range.equals("char")) {
            this.c.setRange("char");
        }
        if (range.equals("tinyblob")) {
            this.c.setRange("tinyblob");
        }
        if (range.equals("mediumblob")) {
            this.c.setRange("mediumblob");
        }
        if (range.equals("longblob")) {
            this.c.setRange("longblob");
        }
        if (range.equals("tinytext")) {
            this.c.setRange("tinytext");
        }
        if (range.equals("text")) {
            this.c.setRange("text");
        }
        if (range.equals("mediumtext")) {
            this.c.setRange("mediumtext");
        }
        if (range.equals("longtext"))
            this.c.setRange("longtext");
    }

    public String table() {
        return this.c.table();
    }

    public void setTable(String tableName) {
        this.c.setTable(tableName);
    }

    public String getType() {
        if (range().equals("string")) {
            this.type = ("VARCHAR(" + length() + ")");
        }
        if (range().equals("integer")) {
            this.type = "INTEGER(11)";
        }
        if (range().equals("decimal")) {
            this.type = ("DECIMAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("dateTime")) {
            this.type = "DATETIME";
        }
        if (range().equals("date")) {
            this.type = "DATE";
        }

        if (range().equals("tinyint")) {
            this.type = "TINYINT(4)";
        }
        if (range().equals("smallint")) {
            this.type = "SMALLINT(6)";
        }
        if (range().equals("mediumint")) {
            this.type = "MEDIUMINT(8)";
        }
        if (range().equals("int")) {
            this.type = "INT(11)";
        }
        if (range().equals("bigint")) {
            this.type = "BIGINT(12)";
        }
        if (range().equals("real")) {
            this.type = ("REAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("float")) {
            this.type = ("FLOAT(" + length() + "," + scale() + ")");
        }
        if (range().equals("double")) {
            this.type = ("DOUBLE(" + length() + "," + scale() + ")");
        }
        if (range().equals("numeric")) {
            this.type = ("NUMERIC(" + length() + "," + scale() + ")");
        }
        if (range().equals("time")) {
            this.type = "TIME";
        }
        if (range().equals("timestamp")) {
            this.type = "TIMESTAMP";
        }
        if (range().equals("char")) {
            this.type = ("CHAR(" + length() + ")");
        }
        if (range().equals("tinyblob")) {
            this.type = "TINYBLOB";
        }
        if (range().equals("mediumblob")) {
            this.type = "MEDIUMBLOB";
        }
        if (range().equals("longblob")) {
            this.type = "LONGBLOB";
        }
        if (range().equals("tinytext")) {
            this.type = "TINYTEXT";
        }
        if (range().equals("text")) {
            this.type = "TEXT";
        }
        if (range().equals("mediumtext")) {
            this.type = "MEDIUMTEXT";
        }
        if (range().equals("longtext")) {
            this.type = "LONGTEXT";
        }

        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String name() {
        return this.c.name();
    }

    public void setName(String name) {
        this.c.setName(name);
    }

    public boolean isPrimaryKey() {
        return this.c.isPrimaryKey();
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.c.setIsPrimaryKey(isPrimaryKey);
    }

    public boolean isForeignKey() {
        return this.c.isForeignKey();
    }

    public void setIsForeignKey(boolean isForeignKey) {
        this.c.setIsForeignKey(isForeignKey);
    }

    public String value() {
        return this.c.value();
    }

    public void setValue(String value) {
        this.c.setValue(value);
    }

    public String references() {
        return c.references();
    }

    public void setReferences(String columnReferenced) {
        c.setReferences(columnReferenced);
    }

    public void setIsUniqueKey(boolean b) {
        this.c.setIsUniqueKey(b);
    }

    public boolean isUniqueKey() {
       return this.c.isUniqueKey();
    }

    public void setIsNotNull(boolean b) {
        this.c.setIsNotNull(b);
    }

    public boolean isNotNull() {
        return this.c.isNotNull();
    }
}