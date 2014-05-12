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
import java.util.*;

/**
 *
 */
public class CreateSchema {

    private static final Logger log = Logger
            .getLogger(CreateSchema.class);
    private final String inputFileName;
    private static final HashMap<String, String> OWLtoRDBDataTypeMapper = new HashMap<String, String>();
    private final HashMap<String, PrimaryKey> tablePK = new HashMap<String, PrimaryKey>();
    private OntModel ontologyModel;
    private ArrayList<Table> tables = new ArrayList<Table>();
    private JTextArea console;
    private HashSet<OntClass> equivalentClasses = new HashSet<OntClass>();
    private HashMap<String, String> unprocessedTablesMap = new HashMap<String, String>();
    private HashSet<OntProperty> equivalentProperties = new HashSet<OntProperty>();
    private HashMap<OntProperty, OntProperty> equivalentPropertyMap = new HashMap<OntProperty, OntProperty>();
    private boolean isEquivalentPropertyAdded = false;

    public ArrayList<Table> getTables() {
        return tables;
    }

    public CreateSchema(JTextArea console) {
        this.console = console;
        this.inputFileName = CreateOntology.ontologyFile + "Union.owl";
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
                Iterator<OntClass> eqClass = ontClass.listEquivalentClasses();
                if (eqClass.hasNext()) {
                    while (eqClass.hasNext()) {
                        OntClass e = eqClass.next();
                        equivalentClasses.add(e);
                        if (equivalentClasses.contains(ontClass)) {
                            log.info(ontClass.getLocalName() + " class is already processed");

                        } else {
                            table = new TableImpl(ontClass.getLocalName());
                            table.setDatabase("MERGED");
                            log.info("Creating table - " + table.name());
                            console.append("Creating table - " + table.name());
                            ArrayList<Column> columns = createColumns(table, ontClass, e);
                            table.setColumns(columns);
                            tables.add(table);
                            tablePK.put(table.name(), table.primaryKey());
                            unprocessedTablesMap.put(e.getLocalName(), table.name());
                        }
                    }
                } else {
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
        }
        addRangeToObjectProperty();
     //   processEquivalentProperties();

        log.info("Tables created");
    }

    private void processEquivalentProperties() {
        Iterator<Map.Entry<OntProperty, OntProperty>> epm = equivalentPropertyMap.entrySet().iterator();
        while (epm.hasNext()) {
            Map.Entry<OntProperty, OntProperty> mapEntry = epm.next();
            String fromTable = mapEntry.getKey().getDomain().getLocalName();
            String toTable = mapEntry.getValue().getDomain().getLocalName();
            String fromColumn = mapEntry.getKey().getLocalName();
            String toColumn = mapEntry.getValue().getLocalName();
            Iterator<Table> tableIterator = tables.iterator();
            Table ft = null, tt = null;
            Column fc = null, tc = null;
            while (tableIterator.hasNext()) {
                Table t = tableIterator.next();
                if (t.name().equals(fromTable)) {
                    for (Column c : t.columns()) {
                        if (c.name().equals(fromColumn)) {
                            ft = t;
                            fc = c;
                            break;
                        }
                    }
                }
                if (t.name().equals(toTable)) {
                    for (Column c : t.columns()) {
                        if (c.name().equals(toColumn)) {
                            tt = t;
                            tc = c;
                            break;
                        }
                    }
                }
                if (ft != null && tt != null && fc != null && tc != null) {
                    fc.setReferences(tt.name() + "." + tc.name());
                    fc.setIsForeignKey(true);
                    tc.setRange(fc.range());
                    log.info("Mapping created between table " + ft.name() + " column " + fc.name() + " to table " + tt.name() + " column " + tc.name());
                    break;
                }
            }
        }
    }

    private ArrayList<Column> createColumns(Table table, OntClass ontClass, OntClass equivalentClass) {

        ArrayList<Column> nativeColumns = createColumns(table);
        Table eqTable = new TableImpl(equivalentClass.getLocalName());
        ArrayList<Column> columnsFromEqTable = createColumns(eqTable);

        ExtendedIterator<OntProperty> propertyIterator = ontClass.listDeclaredProperties(false);
        while (propertyIterator.hasNext()) {
            OntProperty property = propertyIterator.next();

            if (property.listEquivalentProperties().toList().size() > 0) {
                ExtendedIterator<? extends OntProperty> listEquivalentProperties = property.listEquivalentProperties();
                while (listEquivalentProperties.hasNext()) {
                    OntProperty equivalentProperty = listEquivalentProperties.next();
                    Iterator<Column> columnIterator = columnsFromEqTable.listIterator();
                    boolean flag = false;
                    while (columnIterator.hasNext()) {
                        Column c = columnIterator.next();
                        String cName = c.name();

                        if (cName.equals(equivalentProperty.getLocalName())) {
                            if (equivalentProperty.getDomain().getLocalName().equals(equivalentClass.getLocalName())) {
                                log.info(equivalentProperty.getLocalName() + " from " + equivalentProperty.getDomain().getLocalName() + " is equivalent property to " + property.getLocalName() + " from " + table.name());
                                columnIterator.remove();
                                flag = true;
                                equivalentProperties.remove(equivalentProperty);
                            } else {
                                if (!equivalentPropertyMap.containsKey(property)) {
                                    log.info("Adding " + equivalentProperty.getLocalName() + " for later processing");
                                    equivalentPropertyMap.put(property, equivalentProperty);
                                }
                            }
                            break;
                        }
                    }
                    if (flag == false && !equivalentPropertyMap.containsKey(property)) {
                        log.info("Adding " + equivalentProperty.getLocalName() + " for later processing");
                        equivalentPropertyMap.put(property, equivalentProperty);
                    }
                }
            }
        }

        ExtendedIterator<OntProperty> equivalentClassPropertyIterator = equivalentClass.listDeclaredProperties(false);
        while (equivalentClassPropertyIterator.hasNext()) {
            OntProperty property = equivalentClassPropertyIterator.next();

            if (property.listEquivalentProperties().toList().size() > 0) {
                ExtendedIterator<? extends OntProperty> listEquivalentProperties = property.listEquivalentProperties();
                while (listEquivalentProperties.hasNext()) {
                    OntProperty equivalentProperty = listEquivalentProperties.next();
                    equivalentProperties.remove(equivalentProperty);
                    Iterator<Column> columnIterator = columnsFromEqTable.listIterator();

                    while (columnIterator.hasNext()) {
                        Column c = columnIterator.next();
                        String cName = c.name();

                        if (cName.equals(property.getLocalName())) {
                            if (equivalentProperty.getDomain().getLocalName().equals(table.name())) {
                                log.info(equivalentProperty.getLocalName() + " from " + table.name() + " is equivalent property to " + property.getLocalName() + " from " + property.getDomain().getLocalName());
                                columnIterator.remove();

                                equivalentProperties.remove(property);
                            }
                            break;
                        }

                    }
                }
            }
        }

        for (Column c : columnsFromEqTable) {
            if (c.isPrimaryKey()) {
                table.primaryKey().addColumn(c);
            }
        }
        nativeColumns.addAll(columnsFromEqTable);
        return nativeColumns;
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

            if (property.listEquivalentProperties().hasNext()) {
                if (!equivalentProperties.contains(property) && !isEquivalentPropertyAdded) {
                    log.info("Need to address " + property.getLocalName());
                    equivalentProperties.add(property);
                }
            }

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
            if (property.listEquivalentProperties().hasNext()) {
                if (!equivalentProperties.contains(property) && !isEquivalentPropertyAdded) {
                    log.info("Need to address " + property.getLocalName());
                    equivalentProperties.add(property);
                }
            }
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
        isEquivalentPropertyAdded = true;
        return columns;
    }

    /**
     *
     */
    private void addRangeToObjectProperty() {
        for (Table table : tables) {
          //  System.out.println("Processing table: " + table.name());
            for (Column cl : table.columns()) {
         //       System.out.println("\t Column: " + cl.name());
                String referenceTable = null;
                String referenceColumn = null;
                if (cl.isForeignKey()) {
                    String[] references = cl.references().split("\\.");
                    referenceTable = references[0];
                    referenceColumn = references[1];
                    Iterator<String> keys = unprocessedTablesMap.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (referenceTable.equals(key)) {
                            referenceTable = unprocessedTablesMap.get(key);
                            cl.setReferences(referenceTable + "." + referenceColumn);
                        }
                    }
                }
                if (cl.range().equals("")) {
      //              System.out.println("\t \t" + cl.range());

                    for (Column pk : tablePK.get(referenceTable).getColumns()) {
                        if (referenceColumn.equals(pk.name())) {
                            cl.setRange(pk.range());
       //                     System.out.println("\t \t Range " + cl.range());
                        }
                    }
                }
            }
        }
    }
}


