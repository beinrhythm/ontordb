package com.thesis.rdbtoowl;

import com.thesis.rdbtoowl.interfaces.*;
import com.thesis.rdbtoowl.interfaces.constants.*;
import com.thesis.rdbtoowl.tasks.*;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author abhi.pandey
 */
public class Trigger extends JPanel
        implements ActionListener, TreeSelectionListener, CommandConstants, ButtonConstants {
    private static final String CONNECTION_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/main/resources/connections.properties";
    private JTextField textJDBCDriver;
    private JTextField textDBUrl;
    private JTextField textDBUser;
    private JPasswordField textDBPassword;
    private JTextField textDB;
    private JTextField textExportSchemaName;
    private JTextField textConnectionName;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JLabel lblExportSchema;
    private JLabel lblExportSQL;
    JPanel panelSchemaInputData;
    JPanel panelSchemaButtons;
    JPanel schemaPanelRight;
    JPanel panelSQLInputData;
    JPanel sqlPanelRight;
    GridBagConstraints cSchema;
    GridBagConstraints cSQL;
    private JButton btAddSchema;
    private JButton btRemoveSchema;
    private JButton btAddSQL;
    private JButton btRemoveSQL;
    private JButton btConvertSchema;
    private JButton btMergeSchema;
    private JButton btMergeSQL;
    private JButton btConvertOntology;
    private JButton btConvertOntologyFromSQL;
    private JButton btSaveSettings;
    private JButton btReloadConnection;
    private JButton btConvertSQL;
    private JButton btAddConnection;
    private JButton btDeleteConnection;
    private JPanel panelConfig;
    private JPanel panelConsole;
    private JTree treeExport;
    private DefaultTreeModel exportTreeModel;
    private JTextArea textArea;
    private JTextArea  console;
    private JComboBox<String> connectionList;
    private DefaultMutableTreeNode topExport;
    private RelationalOWLProperties props;
    private JProgressBar sqlConvert;
    private JProgressBar SchemaConvert;
    private Timer timerConvertToOntology;
    private Timer timerConvertToRDB;
    private ConvertSchemaTask taskCS;
    private ConvertOntologyTask taskCO;

    DbManager dbManager;
    Integer schemaIndexer = 1;
    Integer sqlIndexer = 1;
    ArrayList<JTextField> listOfSchema = new ArrayList<JTextField>();
    HashMap<JTextField, JLabel> mapOfSchemaLabel = new HashMap<JTextField, JLabel>();
    ArrayList<JTextArea> listOfSQL = new ArrayList<JTextArea>();
    HashMap<JTextArea, JLabel> mapOfSQLLabel = new HashMap<JTextArea, JLabel>();
    ArrayList<OWLOntology> listofOntologies = new ArrayList<OWLOntology>();
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private MergeOntology mo;

    private OWLOntology mergedOntology;


    public Trigger() {
        super(new BorderLayout());
        new JFileChooser();
        props = new RelationalOWLProperties(CONNECTION_PROPERTIES_PATH);
        label8 = new JLabel("JDBC-Connections");
        label9 = new JLabel("JDBC Driver");
        textJDBCDriver = new JTextField(40);
        textJDBCDriver.addActionListener(this);
        label10 = new JLabel("Database URL");
        textDBUrl = new JTextField(40);
        textDBUrl.addActionListener(this);
        label11 = new JLabel("Userid");
        textDBUser = new JTextField(40);
        textDBUser.addActionListener(this);
        textDBUser.setText("root");
        label12 = new JLabel("Password");
        textDBPassword = new JPasswordField(40);
        textDBPassword.addActionListener(this);
        new JLabel("Database");
        textDB = new JTextField(40);
        textDB.addActionListener(this);

        lblExportSchema = new JLabel("Schema : " + schemaIndexer);
        textExportSchemaName = new JTextField(20);
        textExportSchemaName.addActionListener(this);
        textExportSchemaName.setText("");
        listOfSchema.add(textExportSchemaName);
        mapOfSchemaLabel.put(textExportSchemaName, lblExportSchema);

        lblExportSQL = new JLabel("SQL : " + sqlIndexer);
        textArea = new JTextArea(2, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        listOfSQL.add(textArea);
        mapOfSQLLabel.put(textArea, lblExportSQL);

        SchemaConvert = new JProgressBar(0, 10000);
        SchemaConvert.setValue(0);
        SchemaConvert.setStringPainted(true);
        SchemaConvert.setPreferredSize(new Dimension(500, 20));
        SchemaConvert.setVisible(false);

        sqlConvert = new JProgressBar(0, 10000);
        sqlConvert.setValue(0);
        sqlConvert.setStringPainted(true);
        sqlConvert.setPreferredSize(new Dimension(500, 20));
        sqlConvert.setVisible(false);

        timers();
        cSQL = new GridBagConstraints();
        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel1 = createConvertSchemaPanel();
       // JComponent panel2 = createConvertSQLPanel();
        JComponent panel3 = createConfigPanel();
        JComponent panel4 = makeConsolePanel();


        tabbedPane.addTab("Schema", panel1);
       // tabbedPane.addTab("SQL", panel2);
        tabbedPane.addTab("Config", panel3);
        tabbedPane.addTab("Logger", panel4);

        add(tabbedPane);

        setConnectionsList();
        selectActiveConnection();
    }

    private void timers() {
        timerConvertToOntology = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SchemaConvert.setValue(taskCS.getCurrent());
                if (taskCS.isDone()) {
                    timerConvertToOntology.stop();
                    btConvertSchema.setEnabled(true);
                    setCursor(null);
                    SchemaConvert.setVisible(false);
                    SchemaConvert.setValue(SchemaConvert.getMinimum());
                }
            }
        });
        timerConvertToRDB = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SchemaConvert.setValue(taskCO.getCurrent());
                if (taskCO.isDone()) {
                    timerConvertToRDB.stop();
                    btConvertOntology.setEnabled(true);
                    setCursor(null);
                    SchemaConvert.setVisible(false);
                    SchemaConvert.setValue(SchemaConvert.getMinimum());
                }
            }
        });
    }

    protected JComponent createConvertSQLPanel() {
        panelSQLInputData = new JPanel();
        JPanel panelButtons = new JPanel();
        sqlPanelRight = new JPanel();
        panelButtons.setLayout(new FlowLayout());
        sqlPanelRight.setLayout(new BorderLayout());
        panelSQLInputData.setLayout(new GridBagLayout());


        btConvertSQL = new JButton("Convert SQL");
        btConvertSQL.addActionListener(this);
        btMergeSQL = new JButton("Merge");
        btMergeSQL.addActionListener(this);
        btMergeSQL.setEnabled(false);
        btConvertOntologyFromSQL = new JButton("Convert Ontology");
        btConvertOntologyFromSQL.addActionListener(this);
        btConvertOntologyFromSQL.setEnabled(false);

        btAddSQL = new JButton("+");
        btAddSQL.addActionListener(this);

        btRemoveSQL = new JButton("-");
        btRemoveSQL.addActionListener(this);
        btRemoveSQL.setEnabled(false);

        cSQL.insets = new Insets(2, 2, 2, 2);
        cSQL.anchor = 17;
        cSQL.gridx = 0;
        cSQL.gridy = 2;
        panelSQLInputData.add(lblExportSQL, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 2;
        cSQL.gridwidth = 4;
        cSQL.weightx = 0.0D;
        cSQL.fill = 1;
        JScrollPane scroll = new JScrollPane(textArea);
        panelSQLInputData.add(scroll, cSQL);

        cSQL.gridx = 5;
        cSQL.gridy = 2;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 0;
        panelSQLInputData.add(btAddSQL, cSQL);

        cSQL.gridx = 6;
        cSQL.gridy = 2;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 0;
        panelSQLInputData.add(btRemoveSQL, cSQL);

        cSQL.gridx = 4;
        cSQL.gridy = 1;
        cSQL.gridwidth = 4;
        cSQL.weightx = 0.0D;
        cSQL.fill = 17;
        panelButtons.add(btConvertSQL);
        panelButtons.add(btMergeSQL);
        panelButtons.add(btConvertOntologyFromSQL);
        panelSQLInputData.add(panelButtons, cSQL);

        JPanel panelProgress = new JPanel(new FlowLayout(1));
        panelProgress.add(sqlConvert, cSQL);

        sqlPanelRight.add(panelSQLInputData, "North");
        sqlPanelRight.add(panelProgress, "South");

        JSplitPane splitPane = new JSplitPane(1);
        splitPane.setDividerLocation(250);
        splitPane.setRightComponent(sqlPanelRight);

        return splitPane;
    }

    protected JComponent createConvertSchemaPanel() {
        panelSchemaInputData = new JPanel();
        panelSchemaButtons = new JPanel();
        schemaPanelRight = new JPanel();
        schemaPanelRight.setLayout(new BorderLayout());
        panelSchemaInputData.setLayout(new GridBagLayout());
        cSchema = new GridBagConstraints();

        btConvertSchema = new JButton("Convert Schema");
        btConvertSchema.addActionListener(this);
        btMergeSchema = new JButton("Merge");
        btMergeSchema.addActionListener(this);
        btMergeSchema.setEnabled(false);
        btConvertOntology = new JButton("Convert Ontology");
        btConvertOntology.addActionListener(this);
        btConvertOntology.setEnabled(false);

        btAddSchema = new JButton("+");
        btAddSchema.addActionListener(this);

        btRemoveSchema = new JButton("-");
        btRemoveSchema.addActionListener(this);
        btRemoveSchema.setEnabled(false);

        cSchema.insets = new Insets(2, 2, 2, 2);

        cSchema.anchor = 17;

        cSchema.gridx = 0;
        cSchema.gridy = 4;
        panelSchemaInputData.add(lblExportSchema, cSchema);

        cSchema.gridx = 2;
        cSchema.gridy = 4;
        cSchema.weightx = 0.0D;
        cSchema.gridwidth = 1;
        cSchema.fill = 0;
        panelSchemaInputData.add(textExportSchemaName, cSchema);

        cSchema.gridx = 3;
        cSchema.gridy = 4;
        cSchema.weightx = 0.0D;
        cSchema.gridwidth = 1;
        cSchema.fill = 0;
        panelSchemaInputData.add(btAddSchema, cSchema);

        cSchema.gridx = 4;
        cSchema.gridy = 4;
        cSchema.weightx = 0.0D;
        cSchema.gridwidth = 1;
        cSchema.fill = 0;
        panelSchemaInputData.add(btRemoveSchema, cSchema);

        panelSchemaButtons.setLayout(new FlowLayout());
        panelSchemaButtons.add(btConvertSchema);
        panelSchemaButtons.add(btMergeSchema);
        panelSchemaButtons.add(btConvertOntology);
        cSchema.gridx = 0;
        cSchema.gridy = 1;
        cSchema.gridwidth = 4;
        cSchema.weightx = 0.0D;
        cSchema.fill = 17;
        panelSchemaInputData.add(panelSchemaButtons, cSchema);

        JPanel panelProgress = new JPanel(new FlowLayout(1));
        panelProgress.add(SchemaConvert, cSchema);

        schemaPanelRight.add(panelSchemaInputData, "North");
        schemaPanelRight.add(panelProgress, "South");

        topExport = new DefaultMutableTreeNode("");
        exportTreeModel = new DefaultTreeModel(topExport);

        treeExport = new JTree(exportTreeModel);
        treeExport.getSelectionModel().setSelectionMode(4);

        new CheckTreeManager(treeExport);

        treeExport.addTreeSelectionListener(this);

        JScrollPane treeView = new JScrollPane(treeExport);

        JSplitPane splitPane = new JSplitPane(1);
        splitPane.setDividerLocation(250);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(schemaPanelRight);

        return splitPane;
    }

    protected JComponent createConfigPanel() {
        panelConfig = new JPanel();
        panelConfig.setLayout(new BorderLayout());
        JPanel panelInputData = new JPanel();
        JPanel panelButtons = new JPanel();
        panelInputData.setLayout(new GridBagLayout());
        panelButtons.setLayout(new FlowLayout());

        cSQL = new GridBagConstraints();

        label7 = new JLabel("Connection name:");
        textConnectionName = new JTextField(31);
        textConnectionName.addActionListener(this);

        label8 = new JLabel("JDBC-Connections:");
        connectionList = new JComboBox<String>();
        connectionList.addActionListener(this);

        label9 = new JLabel("JDBC Driver: ");
        textJDBCDriver = new JTextField(31);
        textJDBCDriver.addActionListener(this);
        textJDBCDriver.setText(props.getJDBCDriver());

        label10 = new JLabel("Database URL:");
        textDBUrl = new JTextField(31);
        textDBUrl.addActionListener(this);
        textDBUrl.setText(props.getDbUrl());

        label11 = new JLabel("Userid:");
        textDBUser = new JTextField(31);
        textDBUser.addActionListener(this);
        textDBUser.setText(props.getDbUser());

        label12 = new JLabel("Password:");
        textDBPassword = new JPasswordField(31);
        textDBPassword.addActionListener(this);
        textDBPassword.setText(props.getDbPassword());

        btSaveSettings = new JButton("Save settings");
        btSaveSettings.setActionCommand("CMD_SAVE_SETTINGS");
        btSaveSettings.addActionListener(this);
        btReloadConnection = new JButton("Reload");
        btReloadConnection.setActionCommand("CMD_RELOAD_CONNECTION");
        btReloadConnection.addActionListener(this);
        btAddConnection = new JButton("Add connection");
        btAddConnection.setActionCommand("CMD_ADD_CONNECTION");
        btAddConnection.addActionListener(this);
        btDeleteConnection = new JButton("Delete connection");
        btDeleteConnection.setActionCommand("CMD_DELETE_CONNECTION");
        btDeleteConnection.addActionListener(this);

        EmptyBorder border = new EmptyBorder(new Insets(0, 0, 0, 10));

        cSQL.insets = new Insets(3, 3, 3, 3);

        cSQL.anchor = 17;

        cSQL.gridx = 1;
        cSQL.gridy = 0;
        panelInputData.add(new JLabel("         "), cSQL);
        cSQL.gridx = 1;
        cSQL.gridy = 1;
        label8.setBorder(border);
        panelInputData.add(label8, cSQL);
        cSQL.gridx = 2;
        cSQL.gridy = 1;
        cSQL.gridwidth = 1;
        cSQL.weightx = 0.0D;
        cSQL.fill = 0;
        panelInputData.add(connectionList, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 2;
        panelInputData.add(new JLabel("         "), cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 3;
        panelInputData.add(label7, cSQL);

        cSQL.gridx = 2;
        cSQL.gridy = 3;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 17;
        panelInputData.add(textConnectionName, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 4;
        panelInputData.add(label9, cSQL);

        cSQL.gridx = 2;
        cSQL.gridy = 4;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 17;
        panelInputData.add(textJDBCDriver, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 5;
        panelInputData.add(label10, cSQL);

        cSQL.gridx = 2;
        cSQL.gridy = 5;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 17;
        panelInputData.add(textDBUrl, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 6;
        panelInputData.add(label11, cSQL);

        cSQL.gridx = 2;
        cSQL.gridy = 6;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 17;
        panelInputData.add(textDBUser, cSQL);

        cSQL.gridx = 1;
        cSQL.gridy = 7;
        panelInputData.add(label12, cSQL);

        cSQL.gridx = 2;
        cSQL.gridy = 7;
        cSQL.weightx = 0.0D;
        cSQL.gridwidth = 1;
        cSQL.fill = 17;
        panelInputData.add(textDBPassword, cSQL);

        panelButtons.add(btReloadConnection);
        panelButtons.add(btSaveSettings);
        panelButtons.add(btAddConnection);
        panelButtons.add(btDeleteConnection);
        cSQL.gridx = 1;
        cSQL.gridy = 8;
        cSQL.gridwidth = 7;
        cSQL.weightx = 0.0D;
        cSQL.fill = 17;
        panelInputData.add(panelButtons, cSQL);

        panelConfig.add(panelInputData, "North");
        return panelConfig;
    }

    protected JComponent makeConsolePanel() {
        panelConsole = new JPanel();
        panelConsole.setLayout(new BorderLayout());
        console = new JTextArea(40, 40);
        console.setEditable(false);
        JScrollPane scrollPane =new JScrollPane(console,22,32);
        panelConsole.add(scrollPane, "North");
        return panelConsole;

    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("Relational Schema Integration Using Ontology");

        frame.setDefaultCloseOperation(0);
        GraphicsEnvironment.getLocalGraphicsEnvironment();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                new RelationalOWLProperties(CONNECTION_PROPERTIES_PATH).removeConnection("");
                System.exit(0);
            }
        });
        Dimension maxSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        //Dimension maxSizeFrame = new Dimension(new Double(maxSize.getWidth()).intValue() - 1, new Double(maxSize.getHeight()).intValue() - 1);
        Dimension maxContentPane = new Dimension(new Double(maxSize.getWidth()).intValue() - 100, new Double(maxSize.getHeight()).intValue() - 100);
        Dimension d = new Dimension(1000, 800);
        //frame.setMaximizedBounds(new Rectangle(maxSizeFrame));
        frame.getContentPane().setPreferredSize(new Dimension(1000, 700));
        frame.setSize(d);

        Dimension frameSize = frame.getSize();
        frame.setLocation(Math.abs((maxContentPane.width - frameSize.width) / 2),
                Math.abs((maxContentPane.height - frameSize.height) / 2));

        JComponent newContentPane = new Trigger();
        newContentPane.setOpaque(true);
        frame.getContentPane().add(new Trigger(),
                "Center");

        frame.pack();
        //frame.setExtendedState(6);
        frame.setVisible(true);
    }

    private void checkExportSchema(String schema) throws Exception {
        if ((schema == null) || (schema.length() == 0))
            throw new Exception("No schema to convert to an ontology.");
    }

    private void checkMerge(ArrayList<OWLOntology> ontology) throws Exception {
        if (ontology.size() < 2)
            throw new Exception("Not enough schema to merger. Atleast two are needed.");
        if (ontology.isEmpty())
            throw new Exception("No schema to merge.");
    }

    private void saveProperties(String path, String connectionName, boolean saveConnectionName) {
        props.setJDBCDriver(connectionName, textJDBCDriver.getText());
        props.setDbUrl(connectionName, textDBUrl.getText());
        props.setDbUser(connectionName, textDBUser.getText());
        props.setDbPassword(connectionName, textDBPassword.getText());
        if (saveConnectionName) {
            props.setConnectionName(connectionName, textConnectionName.getText());
        }
        props.store();
    }

    private void addProperties(String path, String connectionName) {
        props.addConnection(connectionName);
        props.setJDBCDriver(textJDBCDriver.getText());
        props.setDbUrl(textDBUrl.getText());
        props.setDbUser(textDBUser.getText());
        props.setDbPassword(textDBPassword.getText());
        props.store();
    }

    private void setConnectionsList() {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
            Element root = doc.getRootElement();

            Iterator<Element> iter = root.getChildren().iterator();
            ArrayList<String> items = new ArrayList<String>();
            while (iter.hasNext()) {
                items.add(iter.next().getAttribute("name").getValue().toString());
            }
            Collections.sort(items);

            Iterator<String> iter2 = items.iterator();
            while (iter2.hasNext())
                connectionList.addItem(iter2.next());
        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectActiveConnection() {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
            Element root = doc.getRootElement();

            Iterator iter = root.getChildren().iterator();
            while (iter.hasNext()) {
                Element jdbcConnection = (Element) iter.next();
                if (jdbcConnection.getAttributeValue("checked").equals("true")) {
                    connectionList.setSelectedItem(jdbcConnection.getAttributeValue("name"));
                    break;
                }
            }
        } catch (JDOMException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActiveConnection(String name) {
        props.setActiveConnection(name);
        props.store();
    }

    private void setConnectionData(String connectionName) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(CONNECTION_PROPERTIES_PATH));
            Element root = doc.getRootElement();
            Iterator<Element> iter = root.getChildren().iterator();
            while (iter.hasNext()) {
                Element jdbcConnection = iter.next();
                if (jdbcConnection.getAttributeValue("name").equals(connectionName)) {
                    textConnectionName.setText(jdbcConnection.getAttributeValue("name"));
                    textJDBCDriver.setText(jdbcConnection.getChild("jdbc-driver").getText());
                    textDBUrl.setText(jdbcConnection.getChild("dbUrl").getText());
                    textDBUser.setText(jdbcConnection.getChild("dbUser").getText());
                    textDBPassword.setText(jdbcConnection.getChild("dbPassword").getText());
                    break;
                }
            }
        } catch (JDOMException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean dataComplete() {
        if ((textJDBCDriver.getText().length() > 0) &&
                (textDBUrl.getText().length() > 0) &&
                (textDBUser.getText().length() > 0) &&
                (textDBPassword.getText().length() > 0) &&
                (textConnectionName.getText().length() > 0)) {
            return true;
        }
        return false;
    }

    protected void showExceptionDialog(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Exception message", 0);
    }

    protected void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Success Message", 0);
    }

    private void exportSchema(String schema, OWLOntologyManager manager) {
        try {
            dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
            taskCS = new ConvertSchemaTask(dbManager.getConnection(), props.getJDBCDriver(), schema, manager, this, console);
            SchemaConvert.setVisible(true);
            SchemaConvert.setMaximum(taskCS.getLengthOfTask());
            btConvertSchema.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(3));
            taskCS.go();
            timerConvertToOntology.start();
            listofOntologies.add(taskCS.getOntology());

        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void convertOntology(OWLOntology mergedOntology) {
        try {
            taskCO = new ConvertOntologyTask(dbManager.getConnection(), props.getJDBCDriver(), mergedOntology, manager, this, console);
            SchemaConvert.setVisible(true);
            SchemaConvert.setMaximum(taskCO.getLengthOfTask());
            btConvertOntology.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(3));
            taskCO.go();
            timerConvertToRDB.start();


        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        try {
            if (dataComplete()) {
                String connectionName = null;
                if (props.getConnectionNames().contains(textConnectionName.getText())) {
                    saveProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString(), false);
                    connectionName = connectionList.getSelectedItem().toString();
                } else {
                    saveProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString(), true);
                    connectionName = textConnectionName.getText();
                }

                connectionList.setActionCommand("actualizeComboBox");
                connectionList.removeAllItems();
                setConnectionsList();

                connectionList.setActionCommand("comboBoxChanged");
                connectionList.setSelectedItem(connectionName);
            } else {
                showExceptionDialog("Connection data not complete.");
            }
        } catch (RuntimeException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        try {
            if ((treeExport.getSelectionPath() != null) &&
                    (treeExport.getSelectionPath().getPath().length > 1)) {

                for (JTextField a : listOfSchema) {
                    if (a.isFocusOwner())
                        a.setText(treeExport.getSelectionPath().getPath()[1].toString());
                }
            }
        } catch (RuntimeException e1) {
            showExceptionDialog(e1.getMessage());
            e1.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == btAddSchema) {
            if (schemaIndexer > 4) {

                JOptionPane.showMessageDialog(new JFrame(), "You hit the limit", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JTextField textExportSchemaName = new JTextField(20);
                textExportSchemaName.addActionListener(this);
                textExportSchemaName.setText("");

                textExportSchemaName.addFocusListener(new FocusListener() {

                    public void focusGained(FocusEvent e) {
                    }

                    public void focusLost(FocusEvent e) {
                    }
                });

                cSchema.gridx = 0;
                cSchema.gridy = 4 + schemaIndexer;
                schemaIndexer++;
                JLabel lblExportSchema = new JLabel("Schema : " + schemaIndexer);
                panelSchemaInputData.add(lblExportSchema, cSchema);
                schemaIndexer--;
                cSchema.gridx = 2;
                cSchema.gridy = 4 + schemaIndexer;
                cSchema.weightx = 0.0D;
                cSchema.gridwidth = 1;
                cSchema.fill = 0;
                panelSchemaInputData.add(textExportSchemaName, cSchema);
                schemaIndexer++;
                schemaPanelRight.add(panelSchemaInputData, "North");
                listOfSchema.add(textExportSchemaName);
                mapOfSchemaLabel.put(textExportSchemaName, lblExportSchema);

            }
            btRemoveSchema.setEnabled(true);
        }

        if (e.getSource() == btRemoveSchema) {
            if (!listOfSchema.isEmpty())
                if (!listOfSchema.get(listOfSchema.size() - 1).getText().equals("")) {

                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure to delete " + listOfSchema.get(listOfSchema.size() - 1).getText() + "?", "Warning", dialogButton);
                    if (dialogResult == 0) {
                        deleteSchema();
                    }

                } else {
                    deleteSchema();
                }

        }

        if (e.getSource() == btAddSQL) {

            if (sqlIndexer > 4) {

                JOptionPane.showMessageDialog(new JFrame(), "You hit the limit", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {

                JTextArea textArea = new JTextArea(2, 20);
                textArea.setText("");
                textArea = new JTextArea(2, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                listOfSQL.add(textArea);
                mapOfSQLLabel.put(textArea, lblExportSQL);

                textArea.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                    }

                    public void focusLost(FocusEvent e) {
                    }
                });

                cSQL.gridx = 0;
                cSQL.gridy = 2 + sqlIndexer;
                sqlIndexer++;
                JLabel lblExportSQL = new JLabel("SQL : " + sqlIndexer);
                panelSQLInputData.add(lblExportSQL, cSQL);
                sqlIndexer--;

                cSQL.gridx = 1;
                cSQL.gridy = 2 + sqlIndexer;
                cSQL.gridwidth = 4;
                cSQL.weightx = 0.0D;
                panelSQLInputData.add(textArea, cSQL);
                sqlIndexer++;
                sqlPanelRight.add(panelSQLInputData, "North");
                listOfSQL.add(textArea);
                mapOfSQLLabel.put(textArea, lblExportSQL);

            }
            btRemoveSQL.setEnabled(true);
        }

        if (e.getSource() == btRemoveSQL) {
            if (!listOfSQL.isEmpty())
                if (!listOfSQL.get(listOfSQL.size() - 1).getText().equals("")) {

                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure to delete " + listOfSQL.get(listOfSQL.size() - 1).getText() + "?", "Warning", dialogButton);
                    if (dialogResult == 0) {
                        deleteSQL();
                    }

                } else {
                    deleteSQL();
                }

        }

        if (e.getSource() == btConvertSchema) {
            try {
                if (listOfSchema.get(0).getText().equals("")) {
                    checkExportSchema(null);
                } else {
                    for (JTextField schema : listOfSchema) {
                        checkExportSchema(schema.getText().trim());
                        exportSchema(schema.getText().trim(), manager);
                    }
                    showSuccessDialog("OWL ontology created successfully!!!");

                }
                if (listofOntologies.size() >= 2)
                    btMergeSchema.setEnabled(true);

            } catch (RuntimeException e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            } catch (Exception e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            }

        }

        if (command.equals("Merge")) {
            try {
                mo = new MergeOntology();
                if (listofOntologies.isEmpty() || listofOntologies.size() < 2) {
                    checkMerge(null);
                } else {
                    checkMerge(listofOntologies);
                    mo.merge(manager, this);
                    mergedOntology = mo.getMergedOntology();
                    btConvertOntology.setEnabled(true);
                //    btConvertOntologyFromSQL.setEnabled(true);
                    showSuccessDialog("Ontologies are merged succesfully!!!");
                }
            } catch (Exception e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            }
        }

        if (command.equals("Convert Ontology")) {
            try {
                convertOntology(mergedOntology);
                showSuccessDialog("Merged schema created successfully!!!");
            } catch (RuntimeException e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            } catch (Exception e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            }

        }
        if (e.getSource() == btReloadConnection) {
            topExport = new DefaultMutableTreeNode(textConnectionName.getText().trim());
            if (dataComplete()) {
                saveSettings();
                try {
                    createNodesDB(topExport, false);
                } catch (Exception e1) {
                    showExceptionDialog(e1.getMessage());
                    e1.printStackTrace();
                    selectActiveConnection();
                    return;
                }
                setActiveConnection(connectionList.getSelectedItem().toString());

                exportTreeModel = new DefaultTreeModel(topExport);
                treeExport.setModel(new DefaultTreeModel(topExport));
                treeExport.repaint();

                textExportSchemaName.setText("");

            }
        }
        if (e.getSource() == btSaveSettings) {
            try {
                saveSettings();
            } catch (RuntimeException e1) {
                showExceptionDialog(e1.getMessage());
                e1.printStackTrace();
            }
        }
        if (e.getSource() == btAddConnection) {
            String item = connectionList.getItemAt(connectionList.getItemCount() - 1).toString();

            if (item.toString().length() > 0) {
                connectionList.addItem("");
                connectionList.setActionCommand("actualizeComboBox");
                connectionList.setSelectedIndex(connectionList.getItemCount() - 1);
                connectionList.setActionCommand("comboBoxChanged");
                textConnectionName.setText("");
                textJDBCDriver.setText("");
                textDBUrl.setText("");
                textDBUser.setText("");
                textDBPassword.setText("");
                addProperties(CONNECTION_PROPERTIES_PATH, connectionList.getSelectedItem().toString());
            }

        }

        if (e.getSource() == btDeleteConnection) {
            if (!connectionList.getSelectedItem().toString().equals(props.getActiveConnection())) {
                props.removeConnection(connectionList.getSelectedItem().toString());
                connectionList.setActionCommand("actualizeComboBox");
                connectionList.removeAllItems();
                setConnectionsList();

                connectionList.setActionCommand("comboBoxChanged");
                connectionList.setSelectedItem(props.getActiveConnection());
                setConnectionData(connectionList.getSelectedItem().toString());
            } else {
                showExceptionDialog("Connection already used, cannot delete.");
            }

        }

        if (command.equals("comboBoxChanged")) {
            String item = connectionList.getSelectedItem().toString();

            if (props.getConnectionNames().contains("")) {
                props.removeConnection("");
            }

            connectionList.setActionCommand("actualizeComboBox");
            connectionList.removeAllItems();
            setConnectionsList();

            connectionList.setSelectedItem(item);
            connectionList.setActionCommand("comboBoxChanged");
            setConnectionData(item);
        }
    }

    private void deleteSQL() {
        JTextArea t = listOfSQL.remove(listOfSQL.size() - 1);
        panelSQLInputData.remove(t);
        JLabel l = mapOfSQLLabel.remove(t);
        panelSQLInputData.remove(l);
        sqlIndexer--;
        sqlPanelRight.add(panelSQLInputData, "North");
        if (sqlIndexer == 1) {
            btRemoveSQL.setEnabled(false);
        }
    }

    private void deleteSchema() {
        JTextField t = listOfSchema.remove(listOfSchema.size() - 1);
        panelSchemaInputData.remove(t);
        JLabel l = mapOfSchemaLabel.remove(t);
        panelSchemaInputData.remove(l);
        schemaIndexer--;
        schemaPanelRight.add(panelSchemaInputData, "North");
        if (schemaIndexer == 1) {
            btRemoveSchema.setEnabled(false);
        }
    }

    private void createNodesDB(DefaultMutableTreeNode top, boolean connectionFromConfig) throws Exception {
        DefaultMutableTreeNode databaseNode = null;
        DefaultMutableTreeNode tableNode = null;
        try {
            if (connectionFromConfig)
                dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
            else
                dbManager = DatabaseManagerFactory.getDbManagerInstance(textJDBCDriver.getText().trim(), textDBUrl.getText().trim(), textDBUser.getText().trim(), textDBPassword.getText());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        top.removeAllChildren();
        Iterator iter = dbManager.getDatabaseList().iterator();
        while (iter.hasNext()) {
            String databaseName = (String) iter.next();
            databaseNode = new DefaultMutableTreeNode(databaseName);

            Iterator iter2 = dbManager.getTableList(databaseName).iterator();
            while (iter2.hasNext()) {
                String tableName = (String) iter2.next();
                tableNode = new DefaultMutableTreeNode(tableName);
                databaseNode.add(tableNode);
            }
            top.add(databaseNode);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Trigger.createAndShowGUI();
                ;
            }
        });
    }

    private class CheckTreeSelectionModel extends DefaultTreeSelectionModel {
        private TreeModel model;

        CheckTreeSelectionModel(TreeModel model) {
            this.model = model;
            setSelectionMode(4);
        }

        public boolean isPathSelected(TreePath path, boolean dig) {
            if (!dig)
                return super.isPathSelected(path);
            while ((path != null) && (!super.isPathSelected(path)))
                path = path.getParentPath();
            return path != null;
        }

        private boolean isDescendant(TreePath path1, TreePath path2) {
            Object[] obj1 = path1.getPath();
            Object[] obj2 = path2.getPath();
            for (int i = 0; i < obj2.length; i++) {
                if (obj1[i] != obj2[i])
                    return false;
            }
            return true;
        }

        public void setSelectionPaths(TreePath[] pPaths) {
            throw new UnsupportedOperationException("not implemented yet!!!");
        }

        public void addSelectionPaths(TreePath[] paths) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath[] selectionPaths = getSelectionPaths();
                if (selectionPaths == null)
                    break;
                ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();
                for (int j = 0; j < selectionPaths.length; j++) {
                    if (isDescendant(selectionPaths[j], path))
                        toBeRemoved.add(selectionPaths[j]);
                }
                super.removeSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[0]));
            }

            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath temp = null;
                while (areSiblingsSelected(path)) {
                    temp = path;
                    if (path.getParentPath() == null)
                        break;
                    path = path.getParentPath();
                }
                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        addSelectionPath(temp.getParentPath());
                    } else {
                        if (!isSelectionEmpty())
                            removeSelectionPaths(getSelectionPaths());
                        super.addSelectionPaths(new TreePath[]{temp});
                    }
                } else super.addSelectionPaths(new TreePath[]{path});
            }
        }

        private boolean areSiblingsSelected(TreePath path) {
            TreePath parent = path.getParentPath();
            if (parent == null)
                return true;
            Object node = path.getLastPathComponent();
            Object parentNode = parent.getLastPathComponent();

            int childCount = model.getChildCount(parentNode);
            for (int i = 0; i < childCount; i++) {
                Object childNode = model.getChild(parentNode, i);
                if (childNode != node) {
                    if (!isPathSelected(parent.pathByAddingChild(childNode)))
                        return false;
                }
            }
            return true;
        }

        public void removeSelectionPaths(TreePath[] paths) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() == 1)
                    super.removeSelectionPaths(new TreePath[]{path});
                else
                    toggleRemoveSelection(path);
            }
        }

        private void toggleRemoveSelection(TreePath path) {
            Stack<TreePath> stack = new Stack<TreePath>();
            TreePath parent = path.getParentPath();
            while ((parent != null) && (!isPathSelected(parent))) {
                stack.push(parent);
                parent = parent.getParentPath();
            }
            if (parent != null) {
                stack.push(parent);
            } else {
                super.removeSelectionPaths(new TreePath[]{path});
                return;
            }
            int childCount;
            while (!stack.isEmpty()) {

                TreePath temp = (TreePath) stack.pop();
                TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();
                Object node = temp.getLastPathComponent();
                Object peekNode = peekPath.getLastPathComponent();
                childCount = model.getChildCount(node);
                for (int i = 0; i < childCount; i++) {
                    Object childNode = model.getChild(node, i);
                    if (childNode != null && childNode != peekNode)
                        super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)});

                }
            }

            super.removeSelectionPaths(new TreePath[]{parent});
        }
    }

    private class CheckTreeManager extends MouseAdapter
            implements TreeSelectionListener {
        private Trigger.CheckTreeSelectionModel selectionModel;
        private JTree tree = new JTree();
        int hotspot = new JCheckBox().getPreferredSize().width;

        public CheckTreeManager(JTree tree) {
            selectionModel = new Trigger.CheckTreeSelectionModel(tree.getModel());

            tree.addMouseListener(this);
            selectionModel.addTreeSelectionListener(this);
        }

        public void mouseClicked(MouseEvent me) {
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            if (path == null)
                return;
            if (me.getX() > tree.getPathBounds(path).x + hotspot) {
                return;
            }
            boolean selected = selectionModel.isPathSelected(path, true);
            selectionModel.removeTreeSelectionListener(this);
            try {
                if (selected)
                    selectionModel.removeSelectionPath(path);
                else
                    selectionModel.addSelectionPath(path);
            } catch (Exception e) {
                showExceptionDialog(e.getMessage());
                e.printStackTrace();
            } finally {
                selectionModel.addTreeSelectionListener(this);
                treeExport.treeDidChange();
            }
        }

        public void valueChanged(TreeSelectionEvent e) {
            tree.treeDidChange();
        }
    }
}