package com.thesis.rdbtoowl.tasks;

import java.io.File;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.thesis.rdbtoowl.Trigger;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

public class MergeOntology {

	private static final Logger log = Logger.getLogger(MergeOntology.class);
	private Trigger ro;
	private OWLOntology mergedOntology;

	public void merge(OWLOntologyManager manager, Trigger ro) {
		try { 
			this.ro= ro;
			Iterator<OWLOntology> iter = manager.getOntologies().iterator();

			while(iter.hasNext()){
				OWLOntology o = iter.next();
				log.info("Merging ontology- "+ o);
			}

			OWLOntologyMerger merger = new OWLOntologyMerger(manager);
			IRI mergedOntologyIRI = IRI.create("MergedOntology");
			OWLOntology merged = merger.createMergedOntology(manager, mergedOntologyIRI);	
			
			File file = new File(CreateOntology.ontologyFile+"MergedOntology.owl");

			if (!file.exists()) {
				file.createNewFile();
			}
			
			//manager.saveOntology(merged, new StreamDocumentTarget(System.out));
			manager.saveOntology(merged, IRI.create(file.toURI()));

			log.info("Ontolgies are merged");
			setMergedOntology(merged);

		}catch (OWLOntologyCreationException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			showExceptionDialog(e.getMessage());
			e.printStackTrace();
		}

	}
	private void showExceptionDialog(String message) {
		JOptionPane.showMessageDialog(
				ro, message, "Exception message", 0);
		
	}
	public OWLOntology getMergedOntology() {
		return mergedOntology;
	}
	public void setMergedOntology(OWLOntology mergedOntology) {
		this.mergedOntology = mergedOntology;
	}
}

