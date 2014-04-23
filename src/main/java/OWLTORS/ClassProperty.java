package OWLTORS;

import java.util.List;

public class ClassProperty {

	private boolean isObjectProperty;
	private boolean isDataTypeProperty;
	private String name;
	private List<String> range;
	private List<String> domain;
	private boolean isFunctional;
	private boolean isTransitive;
	private boolean isSymmetric;
	private boolean isInverseFunctionalProperty;
	private boolean isSingleValued;
	private boolean isRequired;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getRange() {
		return range;
	}
	public void setRange(List<String> range) {
		this.range = range;
	}
	public List<String> getDomain() {
		return domain;
	}
	public void setDomain(List<String> domain) {
		this.domain = domain;
	}
	public boolean isFunctional() {
		return isFunctional;
	}
	public void setFunctional(boolean isFunctional) {
		this.isFunctional = isFunctional;
	}
	public boolean isTransitive() {
		return isTransitive;
	}
	public void setTransitive(boolean isTransitive) {
		this.isTransitive = isTransitive;
	}
	public boolean isSymmetric() {
		return isSymmetric;
	}
	public void setSymmetric(boolean isSymmetric) {
		this.isSymmetric = isSymmetric;
	}
	public boolean isInverseFunctionalProperty() {
		return isInverseFunctionalProperty;
	}
	public void setInverseFunctionalProperty(boolean isInverseFunctionalProperty) {
		this.isInverseFunctionalProperty = isInverseFunctionalProperty;
	}
	public boolean isObjectProperty() {
		return isObjectProperty;
	}
	public void setObjectProperty(boolean isObjectProperty) {
		this.isObjectProperty = isObjectProperty;
	}
	public boolean isDataTypeProperty() {
		return isDataTypeProperty;
	}
	public void setDataTypeProperty(boolean isDataTypeProperty) {
		this.isDataTypeProperty = isDataTypeProperty;
	}
	public boolean isSingleValued() {
		return isSingleValued;
	}
	public void setSingleValued(boolean isSingleValued) {
		this.isSingleValued = isSingleValued;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	
}
