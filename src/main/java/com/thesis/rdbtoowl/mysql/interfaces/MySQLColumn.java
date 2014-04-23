package com.thesis.rdbtoowl.mysql.interfaces;

import com.thesis.rdbtoowl.interfaces.Column;

public abstract interface MySQLColumn extends Column
{
  public abstract String getType();

  public abstract void setType(String paramString);
}