package com.thesis.rdbtoowl;

import com.thesis.rdbtoowl.interfaces.DbManager;
import com.thesis.rdbtoowl.interfaces.constants.DriverConstants;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class DatabaseManagerFactory
        implements DriverConstants {
    private static final String PROPS_PATH = System.getProperty("user.dir") + "/src/main/resources/rdbms.properties";
    private static Document RDBMSDoc;

    private static void initializeProperties() {
        try {
            SAXBuilder builder = new SAXBuilder();
            RDBMSDoc = builder.build(new File(PROPS_PATH));
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Element getDriverElement(String driver) {
        initializeProperties();
        Element root = RDBMSDoc.getRootElement();

        Iterator iter = root.getChildren().iterator();
        while (iter.hasNext()) {
            Element jdbcDriver = (Element) iter.next();
            if (jdbcDriver.getAttributeValue("driverName").equals(driver)) {
                return jdbcDriver;
            }
        }
        return null;
    }

    public static DbManager getDbManagerInstance(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception {
        initializeProperties();
        Element jdbcDriver = getDriverElement(driver);
        String managerClassName = jdbcDriver.getChild("managerClassName").getValue();

        Class[] stringArgsClass = {String.class, String.class, String.class, String.class};
        Object[] stringArgs = {driver, dbUrl, dbUser, dbPassword};
        try {
            Class dbManagerDefinition = Class.forName(managerClassName);
            Constructor stringArgsConstructor =
                    dbManagerDefinition.getConstructor(stringArgsClass);
            return (DbManager) stringArgsConstructor.newInstance(stringArgs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public static DbManager getDbManagerInstance(Connection connection, String driver) throws Exception {
        initializeProperties();
        Element jdbcDriver = getDriverElement(driver);
        String managerClassName = jdbcDriver.getChild("managerClassName").getValue();

        Class[] stringArgsClass = {Connection.class};
        Object[] stringArgs = {connection};
        try {
            Class dbManagerDefinition = Class.forName(managerClassName);
            Constructor stringArgsConstructor =
                    dbManagerDefinition.getConstructor(stringArgsClass);
            return (DbManager) stringArgsConstructor.newInstance(stringArgs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}