package com.thesis.rdbtoowl.interfaces;

import java.sql.Driver;

public abstract interface DatabaseRepresentation
{
  public abstract boolean connect(String paramString1, String paramString2, String paramString3, Driver paramDriver);

  public abstract String getSchema();

  public abstract String getData();

  public abstract String getTable(String paramString);
}