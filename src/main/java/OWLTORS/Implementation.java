/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OWLTORS;

import java.util.List;

import OWLTORS.XMLModel.ForeignKeys;


/**
 * @author apande3
 *
 */
public class Implementation {
  
  private final static String location = "/Users/apande3/Dropbox/Abhishek/sample";
  private static ParseFile parsefile;
  
  Implementation() {
    Initializer();
  }
  
  private void Initializer() {
    parsefile = new ParseFile();
  }
  
  public static void main( String[] args) {
	    new Implementation();
	    List<XMLModel> models = parsefile.getXMLModel(location);
	    
	    print(models);
	    
	  }
	  
	  private static void print(List<XMLModel> models) {
	    int count = 0;
	    for (XMLModel model : models) {
	      count++;
	      System.out.println("\n************** TABLE " + count + " **************");
	      System.out.println( "Relation: " + model.getRelation() + "\nAttributes: " + model.getAttributes() + "\nPrimary Keys: " + model.getPrimary() + "\nForeign Keys: " );
	      List<ForeignKeys> fKeys = model.getForeignKeys();
	      for ( ForeignKeys fKey : fKeys ) {
	        System.out.println( fKey.getParentAttribute() + ", " + fKey.getChildAttribute() + ", " + fKey.getParentTable() );
	      }
	      System.out.println();
	    }
	    
	  }
	  
	  
	}
