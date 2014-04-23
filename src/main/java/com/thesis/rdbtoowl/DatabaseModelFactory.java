package com.thesis.rdbtoowl;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DatabaseModelFactory
{
  public static final String RELATIONAL_OWL = "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#";

  public static OntModel createEmptyModel()
  {
    return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
  }

  public static OntModel createDefaultModel()
    throws Exception
  {
    OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    try {
      URL url = new URL("http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#");
      m.read(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"), "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#");
      m.setNsPrefix("dbs", "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }

    return m;
  }

  public static OntModel createModelSchema(String path)
    throws Exception
  {
    OntModel model = createDefaultModel();
    try {
      String baseURI = "file:///" + path;
      model.read(new FileReader(path), baseURI);
    } catch (FileNotFoundException e) {
      throw new Exception(e.getMessage());
    }

    return model;
  }

  public static OntModel createModelData(String path)
  {
    OntModel m = createEmptyModel();
    InputStream in = FileManager.get().open(path);
    m.read(new InputStreamReader(in), "");
    return m;
  }
}