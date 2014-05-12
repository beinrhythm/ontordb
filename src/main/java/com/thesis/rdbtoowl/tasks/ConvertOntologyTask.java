package com.thesis.rdbtoowl.tasks;

import com.thesis.rdbtoowl.DatabaseManagerFactory;
import com.thesis.rdbtoowl.Trigger;
import com.thesis.rdbtoowl.interfaces.DbManager;
import com.thesis.rdbtoowl.interfaces.Table;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by abhi.pandey on 4/18/14.
 */
public class ConvertOntologyTask {
    private DbManager dbManager;
    private String schema;
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private String statMessage;
    private OWLOntologyManager manager;
    private Trigger ro;
    private JTextArea console;
    private ArrayList<Table> tables;


    private static final Logger log = Logger
            .getLogger(ConvertSchemaTask.class);
    private OWLOntology ontology;

    public ConvertOntologyTask(Connection connection, String driver, OWLOntology mergedOntology, OWLOntologyManager manager, Trigger ro, JTextArea console) {
        this.ontology = mergedOntology;
        this.manager = manager;
        this.ro = ro;
        this.console = console;
        
        try {
            this.dbManager = DatabaseManagerFactory.getDbManagerInstance(
                    connection, driver);
            tables =  new CreateSchema(console).getTables();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void go() {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                statMessage = null;
                dbManager.setTables(tables);
                done = true;
                return "Done";

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


}
