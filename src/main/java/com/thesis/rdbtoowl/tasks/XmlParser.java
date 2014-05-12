package com.thesis.rdbtoowl.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XmlParser {

    private SAXBuilder builder = new SAXBuilder();

    private String uri = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment";
    private Namespace ns;

    public ArrayList<String> parseRead(File file) {
        try {

            if (file.getName().equals("refalign.rdf")) {
                uri += "#";
                ns = Namespace.getNamespace(uri);
            }
            Document document = (Document) builder.build(file);
            Element rootNode = document.getRootElement();

            Element alignment = rootNode.getChild("Alignment", ns);
            List list = alignment.getChildren("map", ns);

            ArrayList<String> result = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {

                Element node = (Element) list.get(i);
                Element cell = (Element) node.getChild("Cell", ns);

                Element entity1 = (Element) cell.getContent().get(1);
                String temp = entity1.getAttributes().get(0).getValue();
                String class1 = temp.substring(temp.lastIndexOf("/") + 1, temp.length());

                Element entity2 = (Element) cell.getContent().get(3);
                String temp2 = entity2.getAttributes().get(0).getValue();
                String class2 = temp2.substring(temp2.lastIndexOf("/") + 1, temp2.length());

                Element measure;
                String m;
                measure = (Element) cell.getContent().get(5);
                if (measure.getName().equals("measure"))
                    m = measure.getValue();
                else {
                    measure = (Element) cell.getContent().get(7);
                    m = measure.getValue();
                }

//                 System.out.println(class1 + " matches with " + class2 + " with measure : " + m);

                result.add(class1 + "=" + class2 + ":" + m);

            }
            return result;

        } catch (IOException io) {
            io.printStackTrace();
        } catch (JDOMException jdomex) {
            jdomex.printStackTrace();
        }
        return null;
    }

    public File parseEdit(File editFile, ArrayList<String> remove) {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = (Document) builder.build(editFile);
            Element rootNode = document.getRootElement();

            Element alignment = rootNode.getChild("Alignment", ns);
            List list = alignment.getChildren("map", ns);
            Iterator iter = list.iterator();
            List<Element> elements = new ArrayList<Element>();
            while (iter.hasNext()) {
                Element node = (Element) iter.next();
                for (String r : remove) {
                    String[] s = r.split(":");
                    String[] split = s[0].split("=");
                    String base = split[0];
                    String match = split[1];
                    Element cell = (Element) node.getChild("Cell", ns);

                    Element entity1 = (Element) cell.getContent().get(1);
                    String temp = entity1.getAttributes().get(0).getValue();
                    String class1 = temp.substring(temp.lastIndexOf("/") + 1, temp.length());

                    Element entity2 = (Element) cell.getContent().get(3);
                    String temp2 = entity2.getAttributes().get(0).getValue();
                    String class2 = temp2.substring(temp2.lastIndexOf("/") + 1, temp2.length());

                    if (class1.equals(base) && class2.equals(match)) {
                        elements.add(node);
                    }
                }
            }
            for (Element e : elements) {
                e.getParent().removeContent(e);
            }

            XMLOutputter xmlOutput = new XMLOutputter();

            xmlOutput.setFormat(Format.getPrettyFormat());
            //      xmlOutput.output(document, System.out);
            if (editFile.getName().contains("output")) {
                File temp = File.createTempFile("temp", "rdf");
                temp.deleteOnExit();
                xmlOutput.output(document, new FileWriter(temp));
                return temp;
            }
            xmlOutput.output(document, new FileWriter(editFile.getAbsolutePath()));
            return editFile;

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String ar[]) {
        final File fXmlFile = new File("/Users/abhi.pandey/Documents/output1.rdf");
        XmlParser x = new XmlParser();
        x.parseRead(fXmlFile);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Bdate=to_date:5");
        list.add("DBdate=to_date:2");
        list.add("Mgr_start_date=to_date:1");
        list.add("Dept_Locations=dept_emp:3");
        list.add("Fname=dept_name:4");
        list.add("Pname=dept_name:5");
        list.add("Dependent=departments:1");
        list.add("Dependent_name=dept_name:2");
        x.parseEdit(fXmlFile, list);
    }
}
