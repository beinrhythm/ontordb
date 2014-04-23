package OWLTORS;

import java.util.HashMap;

public class Initilizer {
	protected static final String inputFileName = "/Users/abhi.pandey/Example_Vehicle_Ontology.owl";
	protected static CreateRelationalSchema createRelationalSchema = new CreateRelationalSchema();
	protected static final HashMap<String, String> dtc = new DataTypeConverter().getDataTypeMapper();
	protected static final DBUtils db = new DBUtils();

}
