package OWLTORS;

public class TableColumn {

	private String coulmnName;
	private String dataType;
	private boolean isPrimaryKey;
	private boolean isForeignKey;
	
	public String getCoulmnName() {
		return coulmnName;
	}
	public void setCoulmnName(String coulmnName) {
		this.coulmnName = coulmnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public boolean isForeignKey() {
		return isForeignKey;
	}
	public void setForeignKey(boolean isForeignKey) {
		this.isForeignKey = isForeignKey;
	}
	

}
