package com.thesis.rdbtoowl.db2.interfaces;

import com.thesis.rdbtoowl.interfaces.Column;



public abstract interface DB2Column extends Column
{
  public abstract String getType();

  public abstract void setType(String paramString);
}