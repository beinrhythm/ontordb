/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OWLTORS;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author shekhar2010us
 */
public class XMLModel {
  
  private String relation;
  private LinkedHashMap<String, String> attributes;
  private List<String> primaryKeys;
  private List<ForeignKeys> foreignKeys;
  
  public static class ForeignKeys {
    private String parentAttribute;
    private String childAttribute;
    private String parentTable;
    
    public void setParentAttribute(String parentAttribute) {
      this.parentAttribute = parentAttribute;
    }
    
    public String getParentAttribute() {
      return parentAttribute;
    }
    
    public void setChildAttribute(String childAttribute) {
      this.childAttribute = childAttribute;
    }
    
    public String getChildAttribute() {
      return childAttribute;
    }
    
    public void setParentTable(String parentTable) {
      this.parentTable = parentTable;
    }
    
    public String getParentTable() {
      return parentTable;
    }
    
    
    
  }
  
  public void setRelation(String relation) {
    this.relation = relation;
  }
  
  public String getRelation() {
    return relation;
  }
  
  public void setAttributes(LinkedHashMap<String, String> attributes) {
    this.attributes = attributes;
  }
  
  public LinkedHashMap<String, String> getAttributes() {
    return attributes;
  }
  
  
  public void setPrimary(List<String> primaryKeys) {
    this.primaryKeys = primaryKeys;
  }
  
  public List<String> getPrimary() {
    return primaryKeys;
  }
  
  public List<ForeignKeys> getForeignKeys() {
    return foreignKeys;
  }
  
  public void setForeignKeys( List<ForeignKeys> foreignKeys ) {
    this.foreignKeys = foreignKeys;
  }
  
  
}
