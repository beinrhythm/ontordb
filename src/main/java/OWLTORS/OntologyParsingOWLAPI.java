package OWLTORS;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyParsingOWLAPI {

	private static final String FILE_LOCATION = System.getProperty("user.dir")+"/src/main/resources/ontologies/ontologiesToBeMerged/COMPANY.owl";
	private OWLOntologyManager manager;
	private OWLDataFactory factory;
	private OWLOntology ontology;
	public OntologyParsingOWLAPI(){

		try {
			setUp();
			this.setOntology(loadLocalOntology());
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setUp(){
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
	}

	/** Load Ontology
	 * @return ontology
	 * 
	 * @throws OWLOntologyCreationException */
	public OWLOntology loadLocalOntology() throws OWLOntologyCreationException {

		File file = new File(FILE_LOCATION);
		// Now load the local copy
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		System.out.println("Loaded ontology: " + ontology);

		return ontology;


	}
	
	public static void main(String args[]){
		try {
			new OntologyParsingOWLAPI().loadLocalOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getOntologyClasses(){

		Set<OWLClass> classes= getOntology().getClassesInSignature();
		OWLClass owlClass;
		for(Iterator<OWLClass> iter = classes.iterator(); iter.hasNext();){
			owlClass = iter.next();
		
			System.out.println("Class- " +owlClass.toString());
			System.out.println("Object props-"+owlClass.getObjectPropertiesInSignature());

		}
	}



	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

}
