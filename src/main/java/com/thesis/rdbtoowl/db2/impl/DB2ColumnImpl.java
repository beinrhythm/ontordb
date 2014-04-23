package com.thesis.rdbtoowl.db2.impl;

import com.thesis.rdbtoowl.db2.interfaces.DB2Column;
import com.thesis.rdbtoowl.impl.ColumnImpl;
import com.thesis.rdbtoowl.interfaces.Column;

public class DB2ColumnImpl
        implements DB2Column {
    private Column c = new ColumnImpl();
    private String type = "";

    public DB2ColumnImpl() {
    }

    public DB2ColumnImpl(Column column) {
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
        if (range.toUpperCase().equals("INTEGER")) {
            this.c.setRange("integer");
        }
        if (range.toUpperCase().equals("VARCHAR")) {
            this.c.setRange("string");
        }
        if (range.toUpperCase().equals("CHARACTER VARYING")) {
            this.c.setRange("string");
        }
        if (range.toUpperCase().equals("CHARACTER")) {
            this.c.setRange("string");
        }
        if (range.toUpperCase().equals("TIME")) {
            this.c.setRange("dateTime");
        }
        if (range.toUpperCase().equals("TIMESTAMP")) {
            this.c.setRange("dateTime");
        }
        if (range.toUpperCase().equals("DECIMAL")) {
            this.c.setRange("decimal");
        }
        if (range.toUpperCase().equals("DATE")) {
            this.c.setRange("date");
        }

        if (range.toUpperCase().equals("BIGINT")) {
            this.c.setRange("integer");
        }
        if (range.toUpperCase().equals("BLOB")) {
            this.c.setRange("blob");
        }
        if (range.toUpperCase().equals("BOOLEAN")) {
            this.c.setRange("boolean");
        }
        if (range.toUpperCase().equals("DATALINK")) {
            this.c.setRange("datalink");
        }
        if (range.toUpperCase().equals("DOUBLE")) {
            this.c.setRange("double");
        }
        if (range.toUpperCase().equals("LONG VARCHAR")) {
            this.c.setRange("string");
        }
        if (range.toUpperCase().equals("REAL")) {
            this.c.setRange("real");
        }
        if (range.toUpperCase().equals("REFERENCE")) {
            this.c.setRange("reference");
        }
        if (range.toUpperCase().equals("SMALLINT"))
            this.c.setRange("smallint");
    }

    public String table() {
        return this.c.table();
    }

    public void setTable(String name) {
        this.c.setTable(name);
    }

    public String getType() {
        if (range().equals("string")) {
            this.type = ("VARCHAR(" + length() + ")");
        }
        if (range().equals("integer")) {
            this.type = "INTEGER";
        }
        if (range().equals("decimal")) {
            this.type = ("DECIMAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("dateTime")) {
            this.type = "TIMESTAMP";
        }
        if (range().equals("date")) {
            this.type = "DATE";
        }

        if (range().equals("tinyint")) {
            this.type = "SMALLINT";
        }
        if (range().equals("smallint")) {
            this.type = "SMALLINT";
        }
        if (range().equals("mediumint")) {
            this.type = "INTEGER";
        }
        if (range().equals("int")) {
            this.type = "INTEGER";
        }
        if (range().equals("bigint")) {
            this.type = "BIGINT";
        }
        if (range().equals("real")) {
            this.type = ("REAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("float")) {
            this.type = ("DECIMAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("double")) {
            this.type = ("DOUBLE(" + length() + "," + scale() + ")");
        }
        if (range().equals("numeric")) {
            this.type = ("DECIMAL(" + length() + "," + scale() + ")");
        }
        if (range().equals("time")) {
            this.type = "TIME";
        }
        if (range().equals("timestamp")) {
            this.type = "TIMESTAMP";
        }
        if (range().equals("char")) {
            this.type = ("CHARACTER(" + length() + ")");
        }
        if (range().equals("tinyblob")) {
            this.type = "BLOB";
        }
        if (range().equals("mediumblob")) {
            this.type = "BLOB";
        }
        if (range().equals("longblob")) {
            this.type = "BLOB";
        }
        if (range().equals("tinytext")) {
            this.type = "VARCHAR(32)";
        }
        if (range().equals("text")) {
            this.type = "VARCHAR(64)";
        }
        if (range().equals("mediumtext")) {
            this.type = "VARCHAR(128)";
        }
        if (range().equals("longtext")) {
            this.type = "VARCHAR(255)";
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
        return this.c.references();
    }

    public void setReferences(String columnReferenced) {
        this.c.setReferences(columnReferenced);
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