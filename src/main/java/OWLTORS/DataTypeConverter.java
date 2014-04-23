package OWLTORS;

import java.util.HashMap;

public class DataTypeConverter {

	private static final HashMap<String, String> DataTypeMapper = new HashMap<String, String>();

	public DataTypeConverter(){
	
		if(DataTypeMapper.isEmpty())
			this.createDataTypeConverter();
	}

	public HashMap<String, String> getDataTypeMapper() {
		return DataTypeMapper;
	}

	private void createDataTypeConverter(){
		DataTypeMapper.put("short", "SMALLINT");
		DataTypeMapper.put("unsignedShort", "SMALLINT");
		DataTypeMapper.put("integer", "INTEGER(20)");
		DataTypeMapper.put("positiveInteger", "INTEGER(20)");
		DataTypeMapper.put("negativeInteger", "INTEGER(20)");
		DataTypeMapper.put("positiveInteger", "INTEGER(20)");
		DataTypeMapper.put("nonPositiveInteger", "INTEGER(20)");
		DataTypeMapper.put("nonNegativeInteger", "INTEGER(20)");
		DataTypeMapper.put("int", "INTEGER(20)");
		DataTypeMapper.put("unsignedInt", "INTEGER(20)");
		DataTypeMapper.put("long", "INTEGER(20)");
		DataTypeMapper.put("unsignedLong", "INTEGER(20)");
		DataTypeMapper.put("decimal", "DECIMAL");
		DataTypeMapper.put("float", "FLOAT");
		DataTypeMapper.put("double", "DOUBLE PRECISION");
		DataTypeMapper.put("string", "VARCHAR(256)");
		DataTypeMapper.put("normalizedString", "VARCHAR(256)");
		DataTypeMapper.put("token", "VARCHAR(256)");
		DataTypeMapper.put("language", "VARCHAR(256)");
		DataTypeMapper.put("NMTOKEN", "VARCHAR(256)");
		DataTypeMapper.put("Name", "VARCHAR(256)");
		DataTypeMapper.put("NCName", "VARCHAR(256)");
		DataTypeMapper.put("hexBinary", "VARCHAR(256)");
		DataTypeMapper.put("anyURI", "VARCHAR(256)");
		DataTypeMapper.put("time", "TIME");
		DataTypeMapper.put("datetime", "TIMESTAMP");
		DataTypeMapper.put("date", "DATE");
		DataTypeMapper.put("gYearMonth", "DATE");
		DataTypeMapper.put("gMonthDay", "DATE");
		DataTypeMapper.put("gDay", "DATE");
		DataTypeMapper.put("gMonth", "DATE");
		DataTypeMapper.put("boolean", "BIT");
		DataTypeMapper.put("byte", "BIT VARYING");
		DataTypeMapper.put("unsignedByte", "BIT VARYING");

	}
}
