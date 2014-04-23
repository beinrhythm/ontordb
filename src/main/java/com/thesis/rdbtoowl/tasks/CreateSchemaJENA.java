package com.thesis.rdbtoowl.tasks;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.thesis.rdbtoowl.impl.ColumnImpl;
import com.thesis.rdbtoowl.impl.PrimaryKeyImpl;
import com.thesis.rdbtoowl.impl.TableImpl;
import com.thesis.rdbtoowl.interfaces.Column;
import com.thesis.rdbtoowl.interfaces.PrimaryKey;
import com.thesis.rdbtoowl.interfaces.Table;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class CreateSchemaJENA {

    private static final Logger log = Logger
            .getLogger(CreateSchemaJENA.class);
    private final String inputFileName;
    private static final HashMap<String, String> OWLtoRDBDataTypeMapper = new HashMap<String, String>();
    private final HashMap<String, PrimaryKey> tablePK = new HashMap<String, PrimaryKey>();
    private OntModel ontologyModel;
    private ArrayList<Table> tables = new ArrayList<Table>();
    private JTextArea console;

    public ArrayList<Table> getTables() {
        return tables;
    }

    public CreateSchemaJENA(JTextArea console) {
        this.console = console;
        this.inputFileName = CreateOntology.ontologyFile + "MergedOntology.owl";
        ontologyModel = ModelFactory.createOntologyModel();
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }
        ontologyModel.read(in, "");
        createDataTypeConverter();
        create();
    }

    /**
     *
     */
    private void createDataTypeConverter() {
        OWLtoRDBDataTypeMapper.put("short", "SMALLINT");
        OWLtoRDBDataTypeMapper.put("unsignedShort", "SMALLINT");
        OWLtoRDBDataTypeMapper.put("integer", "INT(11)");
        OWLtoRDBDataTypeMapper.put("positiveInteger", "INT(20)");
        OWLtoRDBDataTypeMapper.put("negativeInteger", "INT(20)");
        OWLtoRDBDataTypeMapper.put("positiveInteger", "INT(20)");
        OWLtoRDBDataTypeMapper.put("nonPositiveInteger", "INT(20)");
        OWLtoRDBDataTypeMapper.put("nonNegativeInteger", "INT(20)");
        OWLtoRDBDataTypeMapper.put("int", "INT(20)");
        OWLtoRDBDataTypeMapper.put("unsignedInt", "INT(20)");
        OWLtoRDBDataTypeMapper.put("long", "INT(20)");
        OWLtoRDBDataTypeMapper.put("unsignedLong", "INT(20)");
        OWLtoRDBDataTypeMapper.put("decimal", "DECIMAL");
        OWLtoRDBDataTypeMapper.put("float", "FLOAT");
        OWLtoRDBDataTypeMapper.put("double", "DOUBLE PRECISION");
        OWLtoRDBDataTypeMapper.put("string", "VARCHAR(45)");
        OWLtoRDBDataTypeMapper.put("normalizedString", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("token", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("language", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("NMTOKEN", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("Name", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("NCName", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("hexBinary", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("anyURI", "VARCHAR(256)");
        OWLtoRDBDataTypeMapper.put("time", "TIME");
        OWLtoRDBDataTypeMapper.put("dateTime", "DATE");
        OWLtoRDBDataTypeMapper.put("date", "DATE");
        OWLtoRDBDataTypeMapper.put("gYearMonth", "DATE");
        OWLtoRDBDataTypeMapper.put("gMonthDay", "DATE");
        OWLtoRDBDataTypeMapper.put("gDay", "DATE");
        OWLtoRDBDataTypeMapper.put("gMonth", "DATE");
        OWLtoRDBDataTypeMapper.put("boolean", "BIT");
        OWLtoRDBDataTypeMapper.put("byte", "BIT VARYING");
        OWLtoRDBDataTypeMapper.put("unsignedByte", "BIT VARYING");

    }

    /**
     *
     */
    private void create() {

        OntClass ontClass;
        Table table;

        for (Iterator<OntClass> classes = ontologyModel.listClasses(); classes.hasNext(); ) {
            ontClass = classes.next();

            if (ontClass.getLocalName() != null) {
                table = new TableImpl(ontClass.getLocalName());
                table.setDatabase("MERGED");
                log.info("Creating table - " + table.name());
                console.append("Creating table - " + table.name());
                ArrayList<Column> columns = createColumns(table);
                table.setColumns(columns);
                tables.add(table);
                tablePK.put(table.name(), table.primaryKey());

            }
        }
        log.info("Tables created");
        addRangeToObjectProperty();
    }

    /**
     * @param table
     * @return
     */
    private ArrayList<Column> createColumns(Table table) {
        ArrayList<Column> columns = new ArrayList<Column>();
        Column column;
        PrimaryKey pk = new PrimaryKeyImpl();
        int number = 0;

        ExtendedIterator<ObjectProperty> objectProperties = ontologyModel.listObjectProperties();
        while (objectProperties.hasNext()) {

            OntProperty property = objectProperties.next();

            ExtendedIterator<? extends OntResource> domains = property.listDomain();
            while (domains.hasNext()) {
                OntResource domain = domains.next();

                if (table.name().equals(domain.getLocalName())) {

                    column = new ColumnImpl(property.getLocalName());
                    log.info("Creating column- " + column.name());

                    column.setTable(table.name());

                    if (property.isFunctionalProperty()) {
                        column.setIsPrimaryKey(true);
                        log.info("Column " + column.name() + " is primary key for table " + column.table());
                    }

                    if (property.isInverseFunctionalProperty()) {
                        column.setIsUniqueKey(true);
                        log.info("Column " + column.name() + " is unique key for table " + column.table());
                    }

                    Iterator<Restriction> i = property.listReferringRestrictions();
                    while (i.hasNext()) {
                        Restriction r = i.next();
                        if (r.isMinCardinalityRestriction()) {
                            column.setIsNotNull(true);
                            log.info("Column " + column.name() + " is not null for table " + column.table());
                        }

                    }

                    String referenceTable, referenceColumn;
                    referenceTable = property.getRange().getLocalName();
                    referenceColumn = property.getComment(null);
                    log.info("Column " + column.name() + " is foreign key referring to " + referenceTable + "." + referenceColumn);
                    column.setReferences(referenceTable + "." + referenceColumn);
                    column.setIsForeignKey(true);

                    number++;
                    columns.add(column);
                    if (column.isPrimaryKey()) {
                        pk.addColumn(column);
                    }
                    break;
                }

            }

        }
        ExtendedIterator<DatatypeProperty> dataProperties = ontologyModel.listDatatypeProperties();

        while (dataProperties.hasNext()) {
            OntProperty property = dataProperties.next();
            ExtendedIterator<? extends OntResource> domains = property.listDomain();

            while (domains.hasNext()) {
                OntResource domain = domains.next();

                if (table.name().equals(domain.getLocalName())) {
                    column = new ColumnImpl(property.getLocalName());
                    log.info("Creating column- " + column.name());
                    column.setTable(table.name());

                    if (property.isFunctionalProperty()) {
                        Iterator<RDFNode> e = property.listComments(null);
                        while (e.hasNext()) {
                            RDFNode comment = e.next();

                            if (table.name().equals(comment.toString().split("\\^")[0])) {
                                column.setIsPrimaryKey(true);
                                log.info("Column " + column.name() + " is primary key for table " + column.table());
                            }
                        }
                    }

                    if (property.isInverseFunctionalProperty()) {
                        column.setIsUniqueKey(true);
                        log.info("Column " + column.name() + " is unique key for table " + column.table());
                    }

                    Iterator<Restriction> i = property.listReferringRestrictions();
                    while (i.hasNext()) {
                        Restriction r = i.next();
                        if (r.isMinCardinalityRestriction()) {
                            column.setIsNotNull(true);
                            log.info("Column " + column.name() + " is not null for table " + column.table());
                        }
                    }

                    if (property.getRange() != null) {
                        log.info("Column " + column.name() + " has range " + OWLtoRDBDataTypeMapper.get(property.getRange().getLocalName()));
                        column.setRange(OWLtoRDBDataTypeMapper.get(property.getRange().getLocalName()));
                    }

                    number++;
                    columns.add(column);
                    if (column.isPrimaryKey()) {
                        pk.addColumn(column);
                    }

                    break;
                }
            }
        }
        log.info("Total properties- " + number);
        table.setPrimaryKey(pk);
        return columns;
    }

    /**
     *
     */
    private void addRangeToObjectProperty() {
        for (Table table : tables) {
            for (Column cl : table.columns()) {
                if (cl.range().equals("")) {
                    String[] references = cl.references().split("\\.");
                    String referenceTable = references[0];
                    String referenceColumn = references[1];
                    for (Column pk : tablePK.get(referenceTable).getColumns()) {
                        if (referenceColumn.equals(pk.name())) {
                            cl.setRange(pk.range());
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Column> createColumn(OntClass ontClass, Table table) {

        ArrayList<Column> columns = new ArrayList<Column>();
        Column c;
        PrimaryKey pk = new PrimaryKeyImpl();
        OntProperty property;

        if (ontClass.listProperties().hasNext()) {
            int number = 0;
            for (Iterator<OntProperty> properties = ontClass.listDeclaredProperties(false); properties.hasNext(); ) {
                number++;
                property = properties.next();
                c = new ColumnImpl(property.getLocalName());
                log.info("Creating column- " + c.name());

                c.setTable(ontClass.getLocalName());
                log.info("Column " + c.name() + " is created for table " + c.table());
                if (property.isFunctionalProperty()) {
                    c.setIsPrimaryKey(true);
                    log.info("Column " + c.name() + " is primary key for table " + c.table());
                }

                if (property.isInverseFunctionalProperty()) {
                    c.setIsUniqueKey(true);
                    log.info("Column " + c.name() + " is unique key for table " + c.table());
                }

                Iterator<Restriction> i = property.listReferringRestrictions();
                while (i.hasNext()) {
                    Restriction r = i.next();
                    if (r.isMinCardinalityRestriction()) {
                        c.setIsNotNull(true);
                        log.info("Column " + c.name() + " is not null for table " + c.table());
                    }

                }

                if (property.isDatatypeProperty()) {
                    if (property.getRange() != null) {
                        log.info("Column " + c.name() + " has range " + OWLtoRDBDataTypeMapper.get(property.getRange().getLocalName()));
                        c.setRange(OWLtoRDBDataTypeMapper.get(property.getRange().getLocalName()));
                    }
                }

                if (property.isObjectProperty()) {
                    if (property.getRange() != null) {
                        String referenceTable, referenceColumn;
                        referenceTable = property.getRange().getLocalName();
                        referenceColumn = property.getComment(null);
                        log.info("Column " + c.name() + " is foreign key referring to " + referenceTable + "." + referenceColumn);
                        c.setReferences(referenceTable + "." + referenceColumn);
                        c.setIsForeignKey(true);

                    }
                }

                columns.add(c);

                if (c.isPrimaryKey()) {
                    pk.addColumn(c);
                }
            }
            log.info("Total properties- " + number);
        }
        table.setPrimaryKey(pk);
        return columns;
    }
}


