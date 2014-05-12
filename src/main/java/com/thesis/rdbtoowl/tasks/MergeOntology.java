package com.thesis.rdbtoowl.tasks;

import com.google.common.base.Strings;
import com.thesis.rdbtoowl.Trigger;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.method.*;
import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import javax.swing.*;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class MergeOntology {

    private static final Logger log = Logger.getLogger(MergeOntology.class);
    private OWLOntologyManager manager;
    private Trigger ro;
    private OWLOntology mergedOntology;
    private String typeOfAlignment;
    private double trim;
    private String nativePath = CreateOntology.ontologyFile;

    public MergeOntology(OWLOntologyManager manager, Trigger ro, String typeOfAlignment, double trim) {
        this.manager = manager;
        this.ro = ro;
        this.typeOfAlignment = typeOfAlignment;
        this.trim = trim;
    }

    public void merge() {
        try {
            Iterator<OWLOntology> iter = manager.getOntologies().iterator();

            while (iter.hasNext()) {
                OWLOntology o = iter.next();
                log.info("Merging ontology- " + o);
            }
            String aligned = createAlignment();
            OWLOntologyMerger merger = new OWLOntologyMerger(manager);
            IRI unionOntologyIRI = IRI.create("Union");
            OWLOntology union = merger.createMergedOntology(manager, unionOntologyIRI);

            File temp = File.createTempFile("temp", "owl");
            temp.deleteOnExit();

            manager.saveOntology(union, IRI.create(temp.toURI()));

            setMergedOntology(refineMergedOntology(aligned, temp));
            log.info("Ontologies are merged");
        } catch (OWLOntologyCreationException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
            e.printStackTrace();
        }

    }


    private OWLOntology refineMergedOntology(String aligned, File temp) {
        try {
            String content = FileUtils.readFileToString(temp, "UTF-8");
            content = content.substring(0, content.indexOf("</rdf:RDF>")) + aligned + content.substring(content.indexOf("</rdf:RDF>"));
            File merged = new File(nativePath + "Union.owl");

            if (!merged.exists()) {
                merged.createNewFile();
            }

            FileUtils.writeStringToFile(merged, content, "UTF-8");
            OWLOntology mergedOntology = manager.loadOntologyFromOntologyDocument(merged);
            return mergedOntology;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createAlignment() {
        File folder = new File(nativePath);

        File[] listOfFiles = folder.listFiles();

        Iterator<OWLOntology> iter = manager.getOntologies().iterator();
        URI onto1 = null;
        URI onto2 = null;

        if (iter.hasNext()) {
            OWLOntology o = iter.next();
            for (File file : listOfFiles) {
                if (file.isFile()) {

                    if (o.getOntologyID().getOntologyIRI().toString().equals(file.getName().split("\\.")[0])) {
                        onto1 = file.toURI();
                    }
                }
            }
        }
        if (iter.hasNext()) {
            OWLOntology o = iter.next();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (o.getOntologyID().getOntologyIRI().toString().equals(file.getName().split("\\.")[0])) {
                        onto2 = file.toURI();
                    }
                }
            }
        }

        // Aligning
        AlignmentProcess ap1 = null, ap2 = null;
        Properties params1 = new BasicParameters(), params2 = new BasicParameters();
        Alignment reference1, reference2 = null;
        File refineAlignment1, refineAlignment2;
        AlignmentParser aparser = new AlignmentParser(0);
        try {
            if (typeOfAlignment.equals("NativeAlignment")) {
                File nativeAlignment1 = new File(nativePath + "output1.rdf");
                refineAlignment1 = refineAlignment(nativeAlignment1, "Native");
                File nativeAlignment2 = new File(nativePath + "output2.rdf");
                refineAlignment2 = refineAlignment(nativeAlignment2, "Native");

            } else {
                if (typeOfAlignment.equals("ClassStructAlignment")) {
                    ap1 = new ClassStructAlignment();
                    ap2 = new ClassStructAlignment();
                }

                if (typeOfAlignment.equals("EditDistNameAlignment")) {
                    ap1 = new EditDistNameAlignment();
                    ap2 = new EditDistNameAlignment();
                }

                if (typeOfAlignment.equals("NameAndPropertyAlignment")) {
                    ap1 = new NameAndPropertyAlignment();
                    ap2 = new NameAndPropertyAlignment();
                }

                if (typeOfAlignment.equals("NameEqAlignment")) {
                    ap1 = new NameEqAlignment();
                    ap2 = new NameEqAlignment();
                }

                if (typeOfAlignment.equals("SMOANameAlignment")) {
                    ap1 = new SMOANameAlignment();
                    ap2 = new SMOANameAlignment();

                }

                if (typeOfAlignment.equals("StringDistAlignment")) {
                    ap1 = new StringDistAlignment();
                    ap2 = new StringDistAlignment();

                }

                if (typeOfAlignment.equals("StrucSubsDistAlignment")) {
                    ap1 = new StrucSubsDistAlignment();
                    ap2 = new StrucSubsDistAlignment();

                }

                if (typeOfAlignment.equals("SubsDistNameAlignment")) {
                    ap1 = new SubsDistNameAlignment();
                    ap2 = new SubsDistNameAlignment();

                }

                File nativeAlignment1 = new File(nativePath + "output1.rdf");
                refineAlignment1 = refineAlignment(nativeAlignment1, "Native");
                reference1 = aparser.parse(refineAlignment1.toURI());

                File nativeAlignment2 = new File(nativePath + "output2.rdf");
                refineAlignment2 = refineAlignment(nativeAlignment2, "Native");
                reference2 = aparser.parse(refineAlignment2.toURI());

                File alignRDF1 = findAlignment(onto1, onto2, ap1, params1, "refalign1");
                File alignRDF2 = findAlignment(onto2, onto1, ap2, params2, "refalign2");

                File refineAlignment3 = refineAlignment(alignRDF1, typeOfAlignment);
                File refineAlignment4 = refineAlignment(alignRDF2, typeOfAlignment);

                Alignment reference3 = aparser.parse(refineAlignment3.toURI());
                Alignment reference4 = aparser.parse(refineAlignment4.toURI());
                return getAlignmentOWL(reference1, reference2, reference3, reference4);
            }

            reference1 = aparser.parse(refineAlignment1.toURI());
            reference2 = aparser.parse(refineAlignment2.toURI());
            return getAlignmentOWL(reference1, reference2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlignmentException e) {
            e.printStackTrace();
        }
        return null;

    }

    private File findAlignment(URI onto1, URI onto2, AlignmentProcess ap, Properties params, String filename) throws AlignmentException, IOException {
        PrintWriter writer;
        AlignmentVisitor renderer;
        ap.init(onto1, onto2);
        ap.align((Alignment) null, params);
        ap.cut(trim);

        File alignRDF = File.createTempFile(filename,"rdf");
        alignRDF.deleteOnExit();
        writer = new PrintWriter(new FileWriter(alignRDF, false), true);
        renderer = new RDFRendererVisitor(writer);
        ap.render(renderer);

        writer.flush();
        writer.close();
        return alignRDF;
    }

    private String getAlignmentOWL(Alignment reference1, Alignment reference2) throws IOException, AlignmentException {
        String content1 = extractAlignmentString(reference1, "temp1");
        String content2 = extractAlignmentString(reference2, "temp2");
        String content = content1+ "\n" +content2;
        return content;
    }

    private String getAlignmentOWL(Alignment reference1, Alignment reference2, Alignment reference3, Alignment reference4)throws IOException, AlignmentException {

        String content1 = extractAlignmentString(reference1, "temp1");
        String content2 = extractAlignmentString(reference2, "temp2");
        String content3 = extractAlignmentString(reference3, "temp3");
        String content4 = extractAlignmentString(reference4, "temp4");

        String content = content1+ "\n" +content2+ "\n" +content3 + "\n" +content4;

        return content;
    }

    private String extractAlignmentString(Alignment reference, String tempFile) throws IOException, AlignmentException {
        PrintWriter writer;
        AlignmentVisitor renderer;
        File temp = File.createTempFile(tempFile,"owl");
        temp.deleteOnExit();

        writer = new PrintWriter(new FileWriter(temp, false), true);
        renderer = new OWLAxiomsRendererVisitor(writer);
        reference.render(renderer);
        writer.flush();
        writer.close();

        String content = FileUtils.readFileToString(temp, "UTF-8");
        content = content.replaceAll("file:" + nativePath, "");
        content = content.substring(content.lastIndexOf("</owl:Ontology>"), content.indexOf("</rdf:RDF>")).replaceAll("</owl:Ontology>", "").trim();
        return content;
    }

    private File refineAlignment(File file, String type) {
        try {
            XmlParser xmlParser = new XmlParser();
            ArrayList<String> alignedResultList = xmlParser.parseRead(file);
            String[] a = alignedResultList.toArray(new String[alignedResultList.size()]);
            JList list = new JList(a);
            int choice = JOptionPane.showConfirmDialog(ro, list, "Review alignment- "+ type, JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION) {
                if (!typeOfAlignment.equals("NativeAlignment")) {
                    trim = Double.parseDouble(JOptionPane.showInputDialog(ro, "Enter the trim value", "0.5"));
                    createAlignment();
                } else {

                    Object[] alignmentMethods = {"NativeAlignment", "ClassStructAlignment",
                            "EditDistNameAlignment", "NameAndPropertyAlignment", "NameEqAlignment",
                            "SMOANameAlignment", "StringDistAlignment", "StrucSubsDistAlignment", "SubsDistNameAlignment"};
                    typeOfAlignment = (String) JOptionPane.showInputDialog(ro, "Choose one of these Alignment Methods", "Alignment Methods", JOptionPane.PLAIN_MESSAGE, null, alignmentMethods, alignmentMethods[0]);

                    if (Strings.isNullOrEmpty(typeOfAlignment)) {
                        typeOfAlignment = "NativeAlignment";
                    }


                    if (!typeOfAlignment.equals("NativeAlignment")) {
                        trim = Double.parseDouble(JOptionPane.showInputDialog(ro, "Enter the trim value", "0.5"));
                    }
                    if (Strings.isNullOrEmpty(String.valueOf(trim))) {
                        trim = Double.parseDouble("0.5");
                    }
                    createAlignment();
                }
            }

            ArrayList<String> removeAlignmentList = new ArrayList<String>();
            for (int i : list.getSelectedIndices()) {
                removeAlignmentList.add(alignedResultList.get(i));
            }
            return xmlParser.parseEdit(file, removeAlignmentList);

        } catch (NullPointerException e) {
            trim = Double.parseDouble(JOptionPane.showInputDialog(ro, "Enter the trim value", "0.5"));
            createAlignment();

        }
        return null;
    }

    private void showExceptionDialog(String message) {
        JOptionPane.showMessageDialog(ro, message, "Exception message", 0);
    }

    public OWLOntology getMergedOntology() {
        return mergedOntology;
    }

    public void setMergedOntology(OWLOntology mergedOntology) {
        this.mergedOntology = mergedOntology;
    }
}

