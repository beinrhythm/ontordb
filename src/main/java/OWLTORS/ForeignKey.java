package OWLTORS;

public class ForeignKey {

	private String name;
	private String fromTableName;
	private String toTableName;
	private String toTableColumnName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFromTableName() {
		return fromTableName;
	}
	public void setFromTableName(String fromTableName) {
		this.fromTableName = fromTableName;
	}
	public String getToTableName() {
		return toTableName;
	}
	public void setToTableName(String toTableName) {
		this.toTableName = toTableName;
	}
	public String getToTableColumnName() {
		return toTableColumnName;
	}
	public void setToTableColumnName(String toTableColumnName) {
		this.toTableColumnName = toTableColumnName;
	}
}
