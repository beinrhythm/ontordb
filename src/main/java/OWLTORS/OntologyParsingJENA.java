package OWLTORS;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyParsingJENA extends Initilizer{

	public static void main(String args[]) {
		try {
			System.out.println("OWL ontology to SQL relational schema conversion started!!!");
			Connection connection = db.createDB();

			//create the reasoning model using the base
			OntModel ontologyModel = ModelFactory.createOntologyModel();
			// use the FileManager to find the input file
			InputStream in = FileManager.get().open(inputFileName);
			if (in == null) {
				throw new IllegalArgumentException("File: " + inputFileName + " not found");
			}
			ontologyModel.read(in, "");
			OntClass ontClass;

			List<OntologyClass> ontologyClasses = new ArrayList<OntologyClass>();

			for(ExtendedIterator<OntClass> classes = ontologyModel.listClasses(); classes.hasNext();){
				ontClass = classes.next();

				if(ontClass.getLocalName()!=null){
					System.out.println("\nClass name -" + ontClass.getLocalName());

					OntologyClass ontologyClass = new OntologyClass();

					if(ontClass.getLocalName()!=null)
						ontologyClass.setName(ontClass.getLocalName());

					//Adding sub-classes
					if(ontClass.hasSubClass()){
						List<String> subClasses = new ArrayList<String>();
						OntClass subClass;

						int number=1;
						for(ExtendedIterator<OntClass> sub_Classes =ontClass.listSubClasses(); sub_Classes.hasNext();){
							subClass= sub_Classes.next();
							if(subClass.getLocalName()!=null){
								System.out.println("\tSub-class" + number++ + "- " + subClass.getLocalName());
								subClasses.add(subClass.getLocalName());
							}
						}
						ontologyClass.setSubClass(subClasses);
					}

					//Adding super-classes
					if(ontClass.hasSuperClass()){
						List<String> supClasses = new ArrayList<String>();

						OntClass supClass;
						int number=1;
						for(ExtendedIterator<OntClass> sup_Classes =ontClass.listSuperClasses(); sup_Classes.hasNext();){
							supClass= sup_Classes.next();
							if(supClass.getLocalName()!=null && !supClass.getLocalName().equalsIgnoreCase("Thing")&& !supClass.getLocalName().equalsIgnoreCase("Resource")){
								System.out.println("\tSup-class" + number++ + "- " + supClass.getLocalName());
								supClasses.add(supClass.getLocalName());
							}
						}
						ontologyClass.setSuperClass(supClasses);
					}

					//Adding disjoint-classes
					if(ontClass.listDisjointWith().hasNext()){
						List<String> disjointClasses = new ArrayList<String>();

						OntClass disjointClass;
						int number=1;
						for(ExtendedIterator<OntClass> disjoint_Classes =ontClass.listDisjointWith(); disjoint_Classes.hasNext();){
							disjointClass= disjoint_Classes.next();
							if(disjointClass.getLocalName()!=null && !disjointClass.getLocalName().equalsIgnoreCase("Thing")&& !disjointClass.getLocalName().equalsIgnoreCase("Resource")){
								System.out.println("\tDisjoint-class" + number++ + "- " + disjointClass.getLocalName());
								disjointClasses.add(disjointClass.getLocalName());
							}
						}
						ontologyClass.setDisjointClass(disjointClasses);
					}

					//Adding equivalent-classes
					if(ontClass.listEquivalentClasses().hasNext()){
						List<String> equivalentClasses = new ArrayList<String>();

						OntClass equivalentClass;
						int number=1;
						for(ExtendedIterator<OntClass> equivalent_Classes =ontClass.listEquivalentClasses(); equivalent_Classes.hasNext();){
							equivalentClass= equivalent_Classes.next();
							if(equivalentClass.getLocalName()!=null && !equivalentClass.getLocalName().equalsIgnoreCase("Thing")&& !equivalentClass.getLocalName().equalsIgnoreCase("Resource")){
								System.out.println("\tEquivalent-class" + number++ + "- " + equivalentClass.getLocalName());
								equivalentClasses.add(equivalentClass.getLocalName());
							}
						}
						ontologyClass.setEquivalentClass(equivalentClasses);
					}

					//Adding properties
					if(ontClass.listProperties().hasNext()){
						List<ClassProperty> classProperties = new ArrayList<ClassProperty>();

						ClassProperty classProperty;
						OntProperty property;
						int number=1;

						for(ExtendedIterator<OntProperty>  properties = ontClass.listDeclaredProperties(); properties.hasNext();){
							classProperty = new ClassProperty();
							property = properties.next();
							List<String> listOfDomain = new ArrayList<String>();
							List<String> listOfRanges = new ArrayList<String>();
							String type = "";

							classProperty.setName(property.getLocalName());

							//Adding domains of a property
							if(property.listDomain().hasNext()){
								OntResource domain;
								for(ExtendedIterator<? extends OntResource> domains = property.listDomain(); domains.hasNext(); ){
									domain = domains.next();
									listOfDomain.add(domain.getLocalName());
								}
							}else {
								if(property.getDomain()!=null)
									listOfDomain.add(property.getDomain().getLocalName());
								else listOfDomain.add(ontClass.getLocalName());
							}
							classProperty.setDomain(listOfDomain);

							if(property.listRange().hasNext()){
								OntResource range;
								for(ExtendedIterator<? extends OntResource> ranges = property.listRange(); ranges.hasNext();){
									range = ranges.next();
									listOfRanges.add(range.getLocalName());	
								}
							}else{
								if(property.getRange()!=null)
									listOfRanges.add(property.getRange().getLocalName());
							}
							classProperty.setRange(listOfRanges);


							if(property.isObjectProperty()){
								classProperty.setObjectProperty(true);
								type= "ObjectProperty";
							}

							if(property.isDatatypeProperty()){
								classProperty.setDataTypeProperty(true);
								type= "DataTypeProperty";
							}

							if(property.isFunctionalProperty())
								classProperty.setFunctional(true);

							if(property.isTransitiveProperty())
								classProperty.setTransitive(true);

							if(property.isTransitiveProperty())
								classProperty.setTransitive(true);

							if(property.isSymmetricProperty())
								classProperty.setSymmetric(true);

							if(ontClass.getCardinality(property)<=1 && property.isInverseFunctionalProperty())
								classProperty.setSingleValued(true);

							//						if(ontClass.getCardinality(property)>0 && property.is)

							System.out.println("\t" +type+" "+ number++	+ "- " +property.getLocalName());

							classProperties.add(classProperty);

						}

						ontologyClass.setProperties(classProperties);
					}

					ontologyClasses.add(ontologyClass);

				}
			}
			System.out.println("Creating relational schema!!!");
			createRelationalSchema.create(ontologyClasses);
			System.out.println("Relational schema created from Ontology");
			System.out.println("Good-Bye");
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


}
