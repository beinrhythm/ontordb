package OWLTORS;

import java.util.List;

public class OntologyClass {

	private String name;
	private List<String> subClass;
	private List<String> superClass;
	private List<String> disjointClass;
	private List<String> equivalentClass;
	private List<ClassProperty> properties;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getSubClass() {
		return subClass;
	}
	public void setSubClass(List<String> subClass) {
		this.subClass = subClass;
	}
	public List<String> getSuperClass() {
		return superClass;
	}
	public void setSuperClass(List<String> superClass) {
		this.superClass = superClass;
	}
	public List<ClassProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<ClassProperty> properties) {
		this.properties = properties;
	}
	public List<String> getDisjointClass() {
		return disjointClass;
	}
	public void setDisjointClass(List<String> disjointClass) {
		this.disjointClass = disjointClass;
	}
	public List<String> getEquivalentClass() {
		return equivalentClass;
	}
	public void setEquivalentClass(List<String> equivalentClass) {
		this.equivalentClass = equivalentClass;
	}
	
}
