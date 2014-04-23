/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OWLTORS;

import OWLTORS.XMLModel.ForeignKeys;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apande3
 *
 */
public class ParseFile {
  
  private static BufferedReader br;
 // private static List<String> files;
  
  ParseFile( ) { }
  
  public List<XMLModel> getXMLModel( String location ) {
    List<String> files = getAllFilesInPath(location);
    List<XMLModel> models = new ArrayList<XMLModel>();
    for (String file : files) {
      String content = readFileToString(file);
      XMLModel model = getFileToModel(content);
      models.add(model);
    }
    return models;
  }
  
  public List<XMLModel> getXMLModel( List<String> files ) {
    List<XMLModel> models = new ArrayList<XMLModel>();
    for (String file : files) {
      String content = readFileToString(file);
      System.out.println("File: \n" + content + "\n");
      XMLModel model = getFileToModel(content);
      models.add(model);
    }
    return models;
  }
  
  public String readFileToString( String file ) {
    
    StringBuilder builder = new StringBuilder();
    try {
      br = new BufferedReader(new FileReader(file) );
      String line = null;
      try {
        while ( (line = br.readLine()) != null ) {
          line = line.trim();
          if ( !( line.isEmpty()) ) {
            builder.append(line.trim()).append("\n");
          }
        }
      } catch (IOException ex) {
        Logger.getLogger(ParseFile.class.getName()).log(Level.SEVERE, null, ex);
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ParseFile.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return builder.toString();
    
  }
  
  public List<String> getAllFilesInPath(String path) {
    List<String> allFiles = new ArrayList<String>();
    String files;
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        files = listOfFiles[i].getName();
        if (files.endsWith(".txt") || files.endsWith(".TXT")) {
          allFiles.add(path+"/"+files);
        }
      }
    }
    return allFiles;
  }
  
  private XMLModel getFileToModel(String content) {
    
    XMLModel model = new XMLModel();
    
    System.out.println(content);
    
    String relationName = null;
    LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
    List<String> primaryKeys = new ArrayList<String>();
    List<ForeignKeys> foreignkeys = new ArrayList<ForeignKeys>();
    
    String[] lines = content.split("\n");
    
    for (String line : lines ) {
      if ( (line.trim()).startsWith("@RELATION") ) {
        relationName = ( line.split("( )+")[1] ).trim();
      }
      if ( (line.trim()).startsWith("@ATTRIBUTE") ) {
        attributes.put( (line.split("( )+")[1].trim()) , (line.split("( )+")[2].trim())  );
      }
    }
    
    Pattern pattern = Pattern.compile("@primary\\s*((.|\\s)*?)\\s*@foreign");
    Matcher matcher = pattern.matcher(content);
    if (matcher.find() ) {
      String match = matcher.group(0);
      match = match.replaceAll("@primary", "").replaceAll("@foreign", "").trim();
      String[] keys = match.split("\n");
      for ( String key : keys) {
        if ( key.trim().length() > 0 )
          primaryKeys.add( key.trim() );
      }
    }
    
    
    pattern = Pattern.compile("@foreign((\\s).*)*");
    matcher = pattern.matcher(content);
    if ( matcher.find() ) {
      String match = matcher.group(0);
      match = match.replaceAll("@foreign", "").trim();
      String[] keys = match.split("\n");
      for ( String key : keys) {
        if ( key.trim().length() > 0 ) {
          String parentAttribute = key.split(" ")[0].trim();
          String childAttribute = key.split(" ")[1].trim();
          String parentTable = key.split(" ")[2].trim();
          ForeignKeys fKey = new ForeignKeys();
          fKey.setChildAttribute(childAttribute);
          fKey.setParentAttribute(parentAttribute);
          fKey.setParentTable(parentTable);
          foreignkeys.add(fKey);
        }
      }
    }
    
    model.setRelation(relationName);
    model.setAttributes(attributes);
    model.setPrimary(primaryKeys);
    model.setForeignKeys(foreignkeys);
    
    return model;
    
  }
  
}
