package com.thesis.rdbtoowl;

import com.thesis.rdbtoowl.interfaces.constants.PropertiesConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class RelationalOWLProperties implements PropertiesConstants {
	private String PROPS_PATH = System.getProperty("user.dir")+"/src/main/resources/connections.properties";
	private String SAMPLE_PROPS_PATH = "/Users/abhi.pandey/Documents/workspace/ontordb/src/main/java/com/thesis/rdbtoowl/samples/connections.properties";
	private Document propertyDoc;
	private Element jdbcConnection;

	public RelationalOWLProperties(String path) {
		this.PROPS_PATH = path;
		try {
			SAXBuilder builder = new SAXBuilder();
			try {
				this.propertyDoc = builder.build(new File(this.PROPS_PATH));
			} catch (FileNotFoundException e) {
				Thread.currentThread().getContextClassLoader();
				InputStream is = ClassLoader
						.getSystemResourceAsStream(this.SAMPLE_PROPS_PATH);
				this.propertyDoc = builder.build(is);
				store();
			}
			Element root = this.propertyDoc.getRootElement();
			Iterator<Element> iter = root.getChildren().iterator();
			while (iter.hasNext()) {
				Element jdbcConnection = (Element) iter.next();
				if (jdbcConnection.getAttributeValue("checked").equals("true"))
					this.jdbcConnection = jdbcConnection;
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void store() {
		try {
			XMLOutputter outp = new XMLOutputter();
			outp.output(this.propertyDoc, new FileOutputStream(new File(
					this.PROPS_PATH)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setActiveConnection(String connectionName) {
		//Element oldJDBCConnection = this.jdbcConnection;
		boolean foundConnection = false;

		Element root = this.propertyDoc.getRootElement();

		Iterator<Element> iter = root.getChildren().iterator();
		while (iter.hasNext()) {
			Element jdbcConnection =  iter.next();
			if (jdbcConnection.getAttributeValue("name").equals(connectionName)) {
				foundConnection = true;
				jdbcConnection.setAttribute("checked", "true");
				this.jdbcConnection = jdbcConnection;
			}

		}

		if (foundConnection) {
			iter = root.getChildren().iterator();
			while (iter.hasNext()) {
				Element jdbcConnection = iter.next();
				if ((!jdbcConnection.getAttributeValue("name").equals(
						connectionName))
						&& (jdbcConnection.getAttributeValue("checked")
								.equals("true")))
					jdbcConnection.setAttribute("checked", "false");
			}
		}
	}

	public ArrayList<String> getConnectionNames() {
		ArrayList<String> names = new ArrayList<String>();
		try {
			SAXBuilder builder = new SAXBuilder();
			this.propertyDoc = builder.build(new File(this.PROPS_PATH));

			Element root = this.propertyDoc.getRootElement();
			Iterator<Element> iter = root.getChildren().iterator();
			while (iter.hasNext()) {
				Element jdbcConnection = iter.next();
				names.add(jdbcConnection.getAttribute("name").getValue());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return names;
	}

	public Element getJDBCConnection(String name) {
		Element root = this.propertyDoc.getRootElement();
		Element jdbcConnection = null;

		Iterator<Element> iter = root.getChildren().iterator();
		while (iter.hasNext()) {
			jdbcConnection = iter.next();
			if (jdbcConnection.getAttributeValue("name").equals(name)) {
				return jdbcConnection;
			}
		}
		return null;
	}

	public void addConnection(String name) {
		this.jdbcConnection = new Element("jdbc-connection");
		this.jdbcConnection.setAttribute("name", name);
		this.jdbcConnection.setAttribute("checked", "false");
		Element child = new Element("jdbc-driver");
		this.jdbcConnection.addContent(child);
		child = new Element("dbUrl");
		this.jdbcConnection.addContent(child);
		child = new Element("dbUser");
		this.jdbcConnection.addContent(child);
		child = new Element("dbPassword");
		this.jdbcConnection.addContent(child);

		Element root = this.propertyDoc.getRootElement();
		root.addContent(this.jdbcConnection);
	}

	public void removeConnection(String name) {
		try {
			SAXBuilder builder = new SAXBuilder();
			this.propertyDoc = builder.build(new File(this.PROPS_PATH));

			Element root = this.propertyDoc.getRootElement();
			Element jdbcConnection = null;

			Iterator<Element> iter = root.getChildren().iterator();
			while (iter.hasNext()) {
				jdbcConnection = (Element) iter.next();
				if (jdbcConnection.getAttributeValue("name").equals(name)) {
					root.removeContent(jdbcConnection);
					store();
					return;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getActiveConnection() {
		Element root = this.propertyDoc.getRootElement();
		Iterator<Element> iter = root.getChildren().iterator();
		while (iter.hasNext()) {
			Element jdbcConnection = (Element) iter.next();
			if (jdbcConnection.getAttributeValue("checked").equals("true")) {
				return jdbcConnection.getAttributeValue("name");
			}
		}
		return "";
	}

	public String getConnectionName() {
		return this.jdbcConnection.getAttributeValue("name");
	}

	public String getDbUrl() {
		return this.jdbcConnection.getChild("dbUrl").getText();
	}

	public String getDbUser() {
		return this.jdbcConnection.getChild("dbUser").getText();
	}

	public String getDbPassword() {
		return this.jdbcConnection.getChild("dbPassword").getText();
	}

	public String getJDBCDriver() {
		return this.jdbcConnection.getChild("jdbc-driver").getText();
	}

	public void setConnectionName(String name) {
		this.jdbcConnection.getAttribute("name").setValue(name);
	}

	public void setConnectionName(String connection, String name) {
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null)
			jdbcConnection.setAttribute("name", name);
	}

	public void setDbUrl(String dbUrl) {
		this.jdbcConnection.getChild("dbUrl").setText(dbUrl);
	}

	public void setDbUrl(String connection, String newDbUrl) {
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null)
			jdbcConnection.getChild("dbUrl").setText(newDbUrl);
	}

	public void setDbUser(String dbUser) {
		this.jdbcConnection.getChild("dbUser").setText(dbUser);
	}

	public void setDbUser(String connection, String newDbUser) {
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null)
			jdbcConnection.getChild("dbUser").setText(newDbUser);
	}

	public void setDbPassword(String string) {
		this.jdbcConnection.getChild("dbPassword").setText(string);
	}

	public void setDbPassword(String connection, String cs) {
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null)
			jdbcConnection.getChild("dbPassword").setText(cs);
	}

	public void setJDBCDriver(String jdbcDriver) {
		this.jdbcConnection.getChild("jdbc-driver").setText(jdbcDriver);
	}

	public void setJDBCDriver(String connection, String newJDBCDriver) {
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null)
			jdbcConnection.getChild("jdbc-driver").setText(newJDBCDriver);
	}
}