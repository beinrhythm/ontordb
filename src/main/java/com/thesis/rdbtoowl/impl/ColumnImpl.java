package com.thesis.rdbtoowl.impl;

import com.thesis.rdbtoowl.interfaces.Column;

public class ColumnImpl
        implements Column {
    private String name = "";
    private String type = "";
    private String tableName = "";
    private String range = "";
    private String length = "";
    private String scale = "";
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private boolean isUniqueKey;
    private boolean isNotNull;
    private String value = "";
    private String columnReferenced = "";


    public ColumnImpl() {
    }

    public ColumnImpl(String name) {
        this.name = name;
    }

    public String scale() {
        return this.scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String length() {
        return this.length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String range() {
        return this.range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String table() {
        return this.tableName;
    }

    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public String value() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String createSQL() {
        return "";
    }

    public boolean isForeignKey() {
        return this.isForeignKey;
    }

    public void setIsForeignKey(boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }

    public String references() {
        return this.columnReferenced;
    }

    public void setReferences(String columnReferenced) {
        this.columnReferenced = columnReferenced;
    }

	public boolean isUniqueKey() {
		return isUniqueKey;
	}

	public void setIsUniqueKey(boolean isUniqueKey) {
		this.isUniqueKey = isUniqueKey;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public void setIsNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}


}