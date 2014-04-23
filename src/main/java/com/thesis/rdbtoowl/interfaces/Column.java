package com.thesis.rdbtoowl.interfaces;

public abstract interface Column {
    public abstract String scale();

    public abstract void setScale(String paramString);

    public abstract String length();

    public abstract void setLength(String paramString);

    public abstract String range();

    public abstract void setRange(String paramString);

    public abstract String table();

    public abstract void setTable(String paramString);

    public abstract String name();

    public abstract void setName(String paramString);

    public abstract String value();

    public abstract void setValue(String paramString);

    public abstract boolean isPrimaryKey();

    public abstract void setIsPrimaryKey(boolean paramBoolean);

    public abstract boolean isForeignKey();

    public abstract void setIsForeignKey(boolean paramBoolean);

    public abstract String references();

    public abstract void setReferences(String paramString);

    public abstract void setIsUniqueKey(boolean b);

    public abstract boolean isUniqueKey();

    public abstract void setIsNotNull(boolean b);

    public abstract boolean isNotNull();
}