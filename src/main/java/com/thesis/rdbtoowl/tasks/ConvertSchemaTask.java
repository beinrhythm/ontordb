package com.thesis.rdbtoowl.tasks;

import com.thesis.rdbtoowl.DatabaseManagerFactory;
import com.thesis.rdbtoowl.Trigger;
import com.thesis.rdbtoowl.interfaces.DbManager;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.swing.*;

public class ConvertSchemaTask {
	private DbManager dbManager;
	private String schema;
	private int lengthOfTask;
	private int current = 0;
	private boolean done = false;
	private String statMessage;
	private OWLOntologyManager manager;
	private Trigger ro;
    private JTextArea console;

	private static final Logger log = Logger
			.getLogger(ConvertSchemaTask.class);
	private OWLOntology ontology;

	public ConvertSchemaTask(Connection connection, String driver, String schema, OWLOntologyManager manager, Trigger ro, JTextArea console) {
		this.schema = schema;
		this.manager = manager;
		this.ro = ro;
        this.console = console;
		try {
			this.dbManager = DatabaseManagerFactory.getDbManagerInstance(
					connection, driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.lengthOfTask = this.dbManager.getTables(schema).size();
	}



	public void go() {
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				current = 0;
				done = false;
				statMessage = null;
				CreateOntology createOwl = new CreateOntology(schema,dbManager,manager,ro,console);
				if(createOwl.isDone()) done = true;
				setOntology(createOwl.getOntology());
				return  createOwl.getOntology();

			}
		};
		worker.start();
	}

	public int getLengthOfTask() {
		return lengthOfTask;
	}

	public int getCurrent() {
		return current;
	}

	public void stop() {
		statMessage = null;
	}

	public boolean isDone() {
		return done;
	}

	public String getMessage() {
		return statMessage;
	}



	public OWLOntology getOntology() {
		return ontology;
	}



	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

}