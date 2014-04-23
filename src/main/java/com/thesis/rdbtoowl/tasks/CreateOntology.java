package com.thesis.rdbtoowl.tasks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;

import com.thesis.rdbtoowl.Trigger;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.DbManager;
import com.thesis.rdbtoowl.interfaces.Table;

public class CreateOntology {

	private static final Logger log = Logger
			.getLogger(CreateOntology.class);

	private static final HashMap<String, OWLDatatype> RDBtoOWLDataTypeMapper = new HashMap<String, OWLDatatype>();
	public static final String ontologyFile= System.getProperty("user.dir")+"/src/main/resources/ontologies/ontologiesToBeMerged/";

	private OWLOntology ontology;
	private String schema;
	private DbManager dbManager;
	private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;
	private boolean done;
	private Trigger ro;
    private JTextArea console;

	public CreateOntology(String schema, DbManager dbManager, OWLOntologyManager manager, Trigger ro, JTextArea console){
		this.schema = schema;
		this.dbManager = dbManager;
		this.manager = manager;
		this.ro = ro;
        this.console = console;
		dataFactory = this.manager.getOWLDataFactory();
		if(RDBtoOWLDataTypeMapper.isEmpty()){
			this.createDataTypeConverter();
		}

		createOntology();
	}

	private void createDataTypeConverter() {
		RDBtoOWLDataTypeMapper.put("integer", dataFactory.getIntegerOWLDatatype());
		RDBtoOWLDataTypeMapper.put("string",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
		RDBtoOWLDataTypeMapper.put("dateTime",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()));
		RDBtoOWLDataTypeMapper.put("decimal",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));
		RDBtoOWLDataTypeMapper.put("date",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()));
		RDBtoOWLDataTypeMapper.put("float",  dataFactory.getFloatOWLDatatype());
		RDBtoOWLDataTypeMapper.put("tinyint",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI()));
		RDBtoOWLDataTypeMapper.put("smallint",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI()));
		RDBtoOWLDataTypeMapper.put("mediumint",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI()));
		RDBtoOWLDataTypeMapper.put("bigint",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI()));
		RDBtoOWLDataTypeMapper.put("real",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));
		RDBtoOWLDataTypeMapper.put("double",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));
		RDBtoOWLDataTypeMapper.put("numeric",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI()));	
		RDBtoOWLDataTypeMapper.put("time",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()));
		RDBtoOWLDataTypeMapper.put("timestamp",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()));	
		RDBtoOWLDataTypeMapper.put("char",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
		RDBtoOWLDataTypeMapper.put("tinytext",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
		RDBtoOWLDataTypeMapper.put("text",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
		RDBtoOWLDataTypeMapper.put("mediumtext",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));
		RDBtoOWLDataTypeMapper.put("longtext",  dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()));	
		RDBtoOWLDataTypeMapper.put("BIT",  dataFactory.getBooleanOWLDatatype());
		RDBtoOWLDataTypeMapper.put("boolean",  dataFactory.getBooleanOWLDatatype());

	}

	private void createOntology(){

		IRI ontologyIRI = IRI.create(schema);

		try {
			ontology = manager.createOntology(ontologyIRI);
			log.info("Creating ontology: " + ontology);
            console.append("Creating ontology: " + ontology+"\n");
            createOntologyClasses();
            createOntologyProperties();
            saveOntology();
			log.info(schema+ " ontology created");
            console.append(schema+ " ontology created"+"\n");
			done = true;
			
		}catch (OWLOntologyCreationException e) {
			showException(e);
		}catch (OWLOntologyStorageException e) {
			showException(e);
		}catch (IOException e) {
			showException(e);
		}catch (Exception e) {
			showException(e);
		}

		setOntology(ontology);
	}



    private void saveOntology() throws IOException, OWLOntologyStorageException {
        File file = new File(ontologyFile+schema+".owl");
        if (!file.exists()) {
            file.createNewFile();
        }

        manager.saveOntology(ontology, IRI.create(file.toURI()));
        //manager.saveOntology(ontology, new StreamDocumentTarget(System.out));
    }

    private void createOntologyProperties() {
        Set<OWLAxiom> owlProperties = new HashSet<OWLAxiom>();
        Set<OWLAxiom> owlSubclasses = new HashSet<OWLAxiom>();
        for(Table table : dbManager.getTables(schema)){
            String range = null, referenceTable, referenceColumn;
            String[] references;
            OWLClass domainClass = null;
            Iterator<OWLClass> iter = ontology.getClassesInSignature().iterator();
            OWLClass owlClass;
            while(iter.hasNext()){
                owlClass = iter.next();

                if(table.name().equals(owlClass.toString().substring(1, owlClass.toString().length()-1))){
                    domainClass = owlClass;
                    break;
                }
            }
            for(Column column : table.columns()){

                IRI prop = IRI.create(column.name());

                if(column.isForeignKey()){

                    OWLObjectProperty objectProperty = dataFactory.getOWLObjectProperty(prop);
                    references = column.references().split("\\.");
                    referenceTable = references[0];
                    referenceColumn = references[1];

                    iter = ontology.getClassesInSignature().iterator();
                    OWLClass rangeClass  = null;
                    while(iter.hasNext()){
                        owlClass = iter.next();

                        if(referenceTable.equals(owlClass.toString().substring(1, owlClass.toString().length()-1))){
                            rangeClass = owlClass;
                            break;
                        }
                    }

                    if(column.isPrimaryKey()){
                        log.info(column.name() + " is primary key for table- "+ table.name());
                        console.append(column.name() + " is primary key for table- "+ table.name()+"\n");

                        OWLFunctionalObjectPropertyAxiom funcObjectProperty = dataFactory.getOWLFunctionalObjectPropertyAxiom(objectProperty);
                        owlProperties.add(funcObjectProperty);

                        log.info(domainClass+ " is sub-class of " + rangeClass);
                        console.append(domainClass+ " is sub-class of " + rangeClass+"\n");

                        owlSubclasses.add(dataFactory.getOWLSubClassOfAxiom(domainClass, rangeClass));

                    }else if(column.isNotNull()){
                        log.info(column.name() + " is not null in table- "+ table.name());
                        console.append(column.name() + " is not null in table- "+ table.name()+"\n");
                        OWLObjectMinCardinality m = dataFactory.getOWLObjectMinCardinality(1,objectProperty);
                        owlSubclasses.add(dataFactory.getOWLSubClassOfAxiom(domainClass,m));

                    }

                    log.info(column.name() + " is foreign key from table- "+ domainClass+ " to-"+ rangeClass);
                    console.append(column.name() + " is foreign key from table- "+ domainClass+ " to-"+ rangeClass+"\n");

                    if(column.isUniqueKey()){
                        log.info(column.name() + " is unique key for table- "+ table.name());
                        console.append(column.name() + " is unique key for table- "+ table.name()+"\n");
                        OWLInverseFunctionalObjectPropertyAxiom invFuncObjectProperty = dataFactory.getOWLInverseFunctionalObjectPropertyAxiom(objectProperty);
                        owlProperties.add(invFuncObjectProperty);


                    }

                    OWLAnnotation comment = dataFactory.getOWLAnnotation(dataFactory.getRDFSComment(),
                            dataFactory.getOWLLiteral(referenceColumn));

                    owlProperties.add(dataFactory.getOWLAnnotationAssertionAxiom(objectProperty.getIRI(),comment));

                    OWLObjectPropertyDomainAxiom domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, domainClass);
                    OWLObjectPropertyRangeAxiom   rangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom( objectProperty, rangeClass);
                    owlProperties.add(domainAxiom);
                    owlProperties.add(rangeAxiom);

                }else{
                    range = column.range();
                    OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(prop);

                    if(column.isPrimaryKey()){
                        log.info(column.name() + " is primary key for table- "+ table.name());
                        console.append(column.name() + " is primary key for table- "+ table.name()+"\n");
                        OWLFunctionalDataPropertyAxiom funcDataProperty = dataFactory.getOWLFunctionalDataPropertyAxiom(dataProperty);
                        owlProperties.add(funcDataProperty);
                        OWLAnnotation comment = dataFactory.getOWLAnnotation(dataFactory.getRDFSComment(),
                                dataFactory.getOWLLiteral(table.name()));

                        owlProperties.add(dataFactory.getOWLAnnotationAssertionAxiom(dataProperty.getIRI(),comment));

                    }else if(column.isNotNull()){
                        log.info(column.name() + " is not null in table- "+ table.name());
                        console.append(column.name() + " is not null in table- "+ table.name()+"\n");
                        OWLDataMinCardinality m = dataFactory.getOWLDataMinCardinality(1, dataProperty);
                        owlSubclasses.add(dataFactory.getOWLSubClassOfAxiom(domainClass,m));

                    }

                    OWLDataPropertyDomainAxiom domainAxiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, domainClass);
                    OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, RDBtoOWLDataTypeMapper.get(range));
                    owlProperties.add(domainAxiom);
                    owlProperties.add(rangeAxiom);
                }
            }
        }
        manager.addAxioms(ontology, owlProperties);
        manager.addAxioms(ontology, owlSubclasses);
    }

    private void createOntologyClasses() {
        int tableProcessedCount = 0;
        Set<OWLAxiom> owlClasses = new HashSet<OWLAxiom>();

        for(Table table : dbManager.getTables(schema)){
            OWLClass owlClass = dataFactory.getOWLClass( IRI.create(table.name()));
            OWLAxiom owlAxiom = dataFactory.getOWLDeclarationAxiom(owlClass);
            owlClasses.add(owlAxiom);
            tableProcessedCount++;

        }

        manager.addAxioms(ontology, owlClasses);
        log.info("Total tables converted into owl class = "+ tableProcessedCount);
        console.append("Total tables converted into owl class = "+ tableProcessedCount+"\n");
    }

    private void showException(Exception e) {
		showExceptionDialog(e.getMessage());
		e.printStackTrace();
		done = true;
	}

	private void showExceptionDialog(String message) {
		JOptionPane.showMessageDialog(ro, message, "Exception message", 0);

	}

	public OWLOntology getOntology() {
		return ontology;
	}
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
    public boolean isDone() {
		return done;
	}

}
