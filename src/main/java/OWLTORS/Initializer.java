package OWLTORS;

import java.util.HashMap;

public class Initializer {
	
	protected static final String inputFileName = System.getProperty("user.dir")+"/src/main/resources/ontologies/camera.owl";
	protected static CreateRelationalSchema createRelationalSchema = new CreateRelationalSchema();
	protected static final HashMap<String, String> dtc = new DataTypeConverter().getDataTypeMapper();
	protected static final DBUtils db = new DBUtils();


}
