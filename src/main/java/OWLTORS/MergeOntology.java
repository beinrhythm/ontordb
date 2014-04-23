package OWLTORS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

public class MergeOntology {

	private static final String FILE_LOCATION = "/Users/apande3/Documents/ontologies/ontologiesToBeMerged";
	private OWLOntologyManager manager;
	private IRI mergedOntologyIRI;

	public MergeOntology(){
		manager = OWLManager.createOWLOntologyManager();
		mergedOntologyIRI = IRI.create("http://www.semanticweb.com/mymergedont");
	}

	private List<File> ontologiesToBeMerged() throws OWLOntologyCreationException {

		File folder = new File(FILE_LOCATION);
		File[] files = folder.listFiles(); 
		List<File> owlFiles = new ArrayList<File>();

		for (File file : files) {
			if (file.isFile() && (file.getName().endsWith(".owl")) || file.getName().endsWith(".OWL") ) {
				owlFiles.add(file);
			}

		}
		return owlFiles;


	}

	public OWLOntology mergeOntologies(){
		OWLOntology merged = null;
	
		try{

			List<File>ontologiesToBeMerged = ontologiesToBeMerged();
			File o1= ontologiesToBeMerged.get(0);
			if(ontologiesToBeMerged.size()>1){
				for(int i=1 ; i<ontologiesToBeMerged.size();i++){

					merged = mergeTwoOntologies(o1, ontologiesToBeMerged.get(i));
				}
			}

			// Print out the axioms in the merged ontology.
			for (OWLAxiom ax : merged.getAxioms()) {
				System.out.println(ax);
			}
			// Now save a copy to another location in OWL/XML format (i.e. disregard
	        // the format that the ontology was loaded in).
	        File f = File.createTempFile("owlapiexample", "example1.xml");
	        IRI documentIRI2 = IRI.create(f);
	        manager.saveOntology(merged, new OWLXMLOntologyFormat(), documentIRI2);
	        // Remove the ontology from the manager
	        manager.removeOntology(merged);
	        f.delete();
		
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return merged;

	}

	private OWLOntology mergeTwoOntologies(File f1, File f2){

		OWLOntology o1o2 = null;

		try{
			OWLOntology o1 = manager.loadOntologyFromOntologyDocument(f1);
			OWLOntology o2 = manager.loadOntologyFromOntologyDocument(f2);
			System.out.println("Ontologies to be merged: O1- "+ o1 + ", O2- "+o2 );
			OWLOntologyMerger merger = new OWLOntologyMerger(manager);

			o1o2 = merger.createMergedOntology(manager, mergedOntologyIRI);
			manager.removeOntology(o1);
			manager.removeOntology(o2);
			
		}

		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return o1o2;

	}
}
