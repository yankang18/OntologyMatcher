package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

import java.io.InputStream;
import java.util.List;

import com.hp.hpl.jena.util.FileManager;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression.ClassExpressionType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLClassDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLObjectPropertyDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLSimpleClassDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLSimpleObjectPropertyDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

public class OWLConstructsRetrievalTest {
	
	private static String projectDir = System.getProperty("user.dir");
	public static void main(String[] args) {
		
		/*
		 * NOTATION: 
		 * 
		 * sD : Source Directory
		 * tD : Target Directory
		 * sN : Source NameSpace
		 * tN : Target NameSpace
		 * sE : Source Entity
		 * tE : Target Entity
		 */
		
		String sD = projectDir + "/Ontologies/benchmarks2_new/201/onto.owl";
		String tD = projectDir + "/Ontologies/benchmarks2_new/101/onto.owl";
		String sN = "http://oaei.ontologymatching.org/2011/benchmarks2/201/onto.owl";
		String tN = "http://oaei.ontologymatching.org/2011/benchmarks2/101/onto.owl";

		/*
		 * Class matching pairs
		 */
		
		// TEST_1
		String sE = "LBWCDGDEOGEKXVPNCBLHYVFNGM";
		String tE = "Conference_Proceedings";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		// TEST_2
		sE = "KVQAJFMUAHJNWSLTQQWYODYHZH";
		tE = "Evaluated_Paper";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		// TEST_3
		sE = "VZXCMPPYJTCYETZSXVTBUVPLOV";
		tE = "Proceedings";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		// TEST_4
		sE = "OTKGLBKXPWLHJYPZPGJGNKJMFE";
		tE = "Contributed_Talk";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		/*
		 * Not class matching pairs
		 */
		
		// TEST_5
		sE = "VZXCMPPYJTCYETZSXVTBUVPLOV";
		tE = "Contributed_Talk";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		// TEST_6
		sE = "LBWCDGDEOGEKXVPNCBLHYVFNGM";
		tE = "Evaluated_Paper";
//		OWLSimpleClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexClassDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLClassDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		/*
		 * Object property matching pairs
		 */
		
		// TEST_7
		sE = "NYUJDSECPHZWYCJZHURIWAPEPY";
		tE = "listsEvent";
//		OWLSimpleObjectPropertyDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionsTest(sD, tD, sN, tN, sE, tE);
		OWLComplexObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);

		// TEST_8
		sE = "QLETSSUGDXFGSGEKGTLOFYOACK";
		tE = "publisherOf";
//		OWLSimpleObjectPropertyDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionsTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);

		// TEST_9
		sE = "DKMGBISAISORTGYJFYEUEOFFOW";
		tE = "presentationOfPaper";
//		OWLSimpleObjectPropertyDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionsTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		/*
		 * Not object property marching pairs
		 */
		
		// TEST_10
		sE = "QLETSSUGDXFGSGEKGTLOFYOACK";
		tE = "presentationOfPaper";
//		OWLSimpleObjectPropertyDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionsTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
		
		// TEST_11
		sE = "FBHRKSYJXBOSNITPTXTGVPMDOO";
		tE = "publisherOf";
//		OWLSimpleObjectPropertyDescriptions(sD, tD, sN, tN, sE, tE);
//		OWLSimpleObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionsTest(sD, tD, sN, tN, sE, tE);
//		OWLComplexObjectPropertyDescriptionMatcherTest(sD, tD, sN, tN, sE, tE);
	}
	
	private static void OWLSimpleClassDescriptions(String sD, String tD, String sN, String tN, String sE, String tE) {

		System.out.println("Source Ontology: " + sN);
		String sURI = sN + "#" + sE;
		System.out.println("Target Ontology: " + tN);
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);

		List<OWLSimpleClassDescription> sdescriptions = sResModel.listOWLSimpleClassDescriptions(sURI);
		List<OWLSimpleClassDescription> tdescriptions = tResModel.listOWLSimpleClassDescriptions(tURI);
		System.out.println("S Entity: " + sE);
		for (OWLSimpleClassDescription description : sdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (SimpleClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
			}
		}
		System.out.println(" ");
		System.out.println("T Entity: " + tE);
		for (OWLSimpleClassDescription description : tdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (SimpleClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
			}
		}
		System.out.println(" ");
	}
	
	private static void OWLComplexClassDescriptions(String sD, String tD, String sN, String tN, String sE, String tE) {
		
		System.out.println("Source Ontology: " + sN);
		System.out.println("Target Ontology: " + tN);
		String sURI = sN + "#" + sE;
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		List<OWLComplexClassDescription> sdescriptions = sResModel.listOWLComplexClassDescriptions(sURI);
		List<OWLComplexClassDescription> tdescriptions = tResModel.listOWLComplexClassDescriptions(tURI);

		System.out.println("S Entity: " + sE);
		for (OWLComplexClassDescription description : sdescriptions) {
			List<ClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (ClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
				ClassExpressionDetail(content);
			}
		}
		System.out.println(" ");
		System.out.println("T Entity: " + tE);
		for (OWLComplexClassDescription description : tdescriptions) {
			List<ClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (ClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
				ClassExpressionDetail(content);
			}
		}
		System.out.println(" ");
	}
	
	private static void ClassExpressionDetail(ClassExpression expression){
		
		ClassExpressionType type = expression.getClassExpressionType();
//		System.out.println(expression.getClassExpressionTypeName());
		if (type == ClassExpressionType.NamedClass || type == ClassExpressionType.AllValuesFromNamedClass || type == ClassExpressionType.SomeValuesFromNamedClass) {
			
			List<OntResourceInfo> namedClasses = expression.getNamedClassList();
			for (OntResourceInfo cl : namedClasses) {
				System.out.println("    -> named class: " + cl.getLocalName());
			}
			
		} else if (type == ClassExpressionType.Union || type == ClassExpressionType.Intersection || type == ClassExpressionType.AllValuesFromIntersection
				|| type == ClassExpressionType.AllValuesFromUnion || type == ClassExpressionType.SomeValuesFromIntersection || type == ClassExpressionType.SomeValuesFromUnion) {

			List<OntResourceInfo> namedClasses = expression.getNamedClassList();
			for (OntResourceInfo cl : namedClasses) {
				System.out.println("    -> named class: " + cl.getLocalName());
			}
			
		} else if (type == ClassExpressionType.MaxCardinality || type == ClassExpressionType.MinCardinality || type == ClassExpressionType.Cardinality) {
			    System.out.println("    ->Cardinality: " + expression.getCardinality());
		} else if (type == ClassExpressionType.HasValueFrom || type == ClassExpressionType.ComplementOf) {
		} else {
		}
	}
	
	private static void OWLSimpleObjectPropertyDescriptions(String sD, String tD, String sN, String tN, String sE, String tE){
		
		System.out.println("Source Ontology: " + sN);
		System.out.println("Target Ontology: " + tN);
		String sURI = sN + "#" + sE;
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		List<OWLSimpleObjectPropertyDescription> sdescriptions = sResModel.listOWLSimpleObjectPropertyDescriptions(sURI);
		List<OWLSimpleObjectPropertyDescription> tdescriptions = tResModel.listOWLSimpleObjectPropertyDescriptions(tURI);

		System.out.println("S Entity: " + sE);
		for (OWLSimpleObjectPropertyDescription description : sdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			List<String> relatedProperties = description.getRelatedProperties();
			System.out.println(" " + description.getRelationTypeName());
			if (contents != null) {
				for (SimpleClassExpression content : contents) {
					System.out.println("-> " + content.getClassExpressionTypeName());
				}
			}
			if (relatedProperties != null) {
				for (String property : relatedProperties) {
					System.out.println("-> " + property);
				}
			}
		}
		System.out.println(" ");
		System.out.println("T Entity: " + tE);
		for (OWLSimpleObjectPropertyDescription description : tdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			List<String> relatedProperties = description.getRelatedProperties();
			System.out.println(" " + description.getRelationTypeName());
			if (contents != null) {
				for (SimpleClassExpression content : contents) {
					System.out.println("-> " + content.getClassExpressionTypeName());
				}
			}
			if (relatedProperties != null) {
				for (String property : relatedProperties) {
					System.out.println("-> " + property);
				}
			}
		}
		System.out.println(" "); 
	}

	private static void OWLComplexObjectPropertyDescriptionsTest(String sD, String tD, String sN, String tN, String sE, String tE) {

		System.out.println("Source Ontology: " + sN);
		System.out.println("Target Ontology: " + tN);
		String sURI = sN + "#" + sE;
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		List<OWLObjectPropertyDescription> sdescriptions = sResModel.listOWLObjectPropertyDescriptions(sURI);
		List<OWLObjectPropertyDescription> tdescriptions = tResModel.listOWLObjectPropertyDescriptions(tURI);

		System.out.println("S Entity: " + sE);
		for (OWLObjectPropertyDescription description : sdescriptions) {
			List<ClassExpression> contents = description.getClassExpressions();
			List<OntResourceInfo> relatedProperties = description.getRelatedProperties();
			System.out.println(" " + description.getRelationTypeName());
			if (contents != null) {
				for (ClassExpression content : contents) {
					System.out.println("-> " + content.getClassExpressionTypeName());
					ClassExpressionDetail(content);
				}
			}
			if (relatedProperties != null) {
				for (OntResourceInfo property : relatedProperties) {
					System.out.println("-> " + property.getResourceType().toString());
					System.out.println("   [" + property.getLocalName() + "]");
				}
			}
		}
		System.out.println(" ");
		System.out.println("T Entity: " + tE);
		for (OWLObjectPropertyDescription description : tdescriptions) {
			List<ClassExpression> contents = description.getClassExpressions();
			List<OntResourceInfo> relatedProperties = description.getRelatedProperties();
			System.out.println(" " + description.getRelationTypeName());
			if (contents != null) {
				for (ClassExpression content : contents) {
					System.out.println("-> " + content.getClassExpressionTypeName());
					ClassExpressionDetail(content);
				}
			}
			if (relatedProperties != null) {
				for (OntResourceInfo property : relatedProperties) {
					System.out.println("-> " + property.getResourceType().toString());
					System.out.println("   [" + property.getLocalName() + "]");
				}
			}
		}
		System.out.println(" ");
	}
	
	private static void OWLSimpleClassDescriptionMatcherTest(String sD, String tD, String sN, String tN, String sE, String tE){
		
		System.out.println("Source Ontology: " + sN);
		String sURI = sN + "#" + sE;
		System.out.println("Target Ontology: " + tN);
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		
		System.out.println("  ");
		System.out.println("  ");
		List<OWLSimpleClassDescription> sdescriptions = sResModel.listOWLSimpleClassDescriptions(sURI);
		List<OWLSimpleClassDescription> tdescriptions = tResModel.listOWLSimpleClassDescriptions(tURI);
		OWLSimpleClassDescriptionSetMatcher matcher = new OWLSimpleClassDescriptionSetMatcher();
		double similarity = matcher.getSimpleClassDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
		System.out.println("Similarity: " + similarity);
	}
	
	private static void OWLClassDescriptionMatcherTest(String sD, String tD, String sN, String tN, String sE, String tE){
		
		System.out.println("Source Ontology: " + sN);
		String sURI = sN + "#" + sE;
		System.out.println("Target Ontology: " + tN);
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		
		System.out.println("  ");
		System.out.println("  ");
		List<OWLComplexClassDescription> sdescriptions = sResModel.listOWLComplexClassDescriptions(sURI);
		List<OWLComplexClassDescription> tdescriptions = tResModel.listOWLComplexClassDescriptions(tURI);
		OWLClassDescriptionSetMatcher matcher = new OWLClassDescriptionSetMatcher(sResModel, tResModel);
		double similarity = matcher.getComplexClassDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
		System.out.println("Similarity: " + similarity);
	}
	
	private static void OWLSimpleObjectPropertyDescriptionMatcherTest(String sD, String tD, String sN, String tN, String sE, String tE){
		
		System.out.println("Source Ontology: " + sN);
		String sURI = sN + "#" + sE;
		System.out.println("Target Ontology: " + tN);
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		
		System.out.println("  ");
		System.out.println("  ");
		List<OWLSimpleObjectPropertyDescription> sdescriptions = sResModel.listOWLSimpleObjectPropertyDescriptions(sURI);
		List<OWLSimpleObjectPropertyDescription> tdescriptions = tResModel.listOWLSimpleObjectPropertyDescriptions(tURI);
		OWLSimpleObjectPropertyDescriptionSetMatcher matcher = new OWLSimpleObjectPropertyDescriptionSetMatcher();
		double similarity = matcher.getSimpleObjectPropertyDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
		System.out.println("Similarity: " + similarity);
	}
	
	private static void OWLComplexObjectPropertyDescriptionMatcherTest(String sD, String tD, String sN, String tN, String sE, String tE){
		System.out.println("Source Ontology: " + sN);
		System.out.println("Target Ontology: " + tN);
		String sURI = sN + "#" + sE;
		String tURI = tN + "#" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);
		List<OWLObjectPropertyDescription> sdescriptions = sResModel.listOWLObjectPropertyDescriptions(sURI);
		List<OWLObjectPropertyDescription> tdescriptions = tResModel.listOWLObjectPropertyDescriptions(tURI);
		OWLObjectPropertyDescriptionSetMatcher matcher = new OWLObjectPropertyDescriptionSetMatcher(sResModel, tResModel);
		double similarity = matcher.getObjectPropertyDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
		System.out.println("Similarity: " + similarity);
	}
}
