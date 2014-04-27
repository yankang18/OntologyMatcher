package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

import com.hp.hpl.jena.util.FileManager;

import fr.inrialpes.exmo.align.impl.URIAlignment;

@Deprecated
public class OntologyInstanceMatcher extends URIAlignment implements AlignmentProcess {

	@Override
	public void align(Alignment alignment, Properties param) throws AlignmentException {

		String sUriStr = this.getOntology1URI().toString();
		String tUriStr = this.getOntology2URI().toString();

		InputStream instream = FileManager.get().open(sUriStr);
		InputStream instream2 = FileManager.get().open(tUriStr);
		ResourceModel sResModel = new OntResourceModel(instream);
		ResourceModel tResModel = new OntResourceModel(instream2);
		
//		ArrayList<String> sDescriptionProperties = new ArrayList<String>();
//		ArrayList<String> tDescriptionProperties = new ArrayList<String>();
//		sDescriptionProperties.add("http://www.w3.org/2000/01/rdf-schema#comment");
//		tDescriptionProperties.add("http://www.w3.org/2000/01/rdf-schema#comment");
//		sResModel.setDescriptionProperties(sDescriptionProperties);
//		tResModel.setDescriptionProperties(tDescriptionProperties);
		
		ArrayList<String> sDescriptionProperties = new ArrayList<String>();
		ArrayList<String> tDescriptionProperties = new ArrayList<String>();
//		sDescriptionProperties.add("http://www.okkam.org/ontology_restaurant1.owl#name");
//		tDescriptionProperties.add("http://www.okkam.org/ontology_restaurant2.owl#name");
//		sDescriptionProperties.add("http://www.okkam.org/ontology_restaurant1.owl#city");
//		tDescriptionProperties.add("http://www.okkam.org/ontology_restaurant2.owl#city");
//		sDescriptionProperties.add("http://www.okkam.org/ontology_person1.owl#surname");
//		sDescriptionProperties.add("http://www.okkam.org/ontology_person1.owl#given_name");
//		tDescriptionProperties.add("http://www.okkam.org/ontology_person2.owl#surname");
//		tDescriptionProperties.add("http://www.okkam.org/ontology_person2.owl#given_name");
		sResModel.setDescriptionProperties(sDescriptionProperties);
		tResModel.setDescriptionProperties(tDescriptionProperties);
		ComplexOntologyMatchingAlgorithm2 cgm2 = new ComplexOntologyMatchingAlgorithm2(sResModel, tResModel, false);
		cgm2.setMaxLevel(1);
		cgm2.compareClassOrInstanceLabel(false);
		List<ClassMapping> mappings = cgm2.getAllIndividualMapping();

		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "(" + mapping.getLabelSimilarity() + ")");
			System.out.println("properties similarity: " + mapping.getPropertySimilarity());
			System.out.println("mapping similarity: " + mapping.getSimilarity());
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			cgm2.prepareMatchingResources(sClassURI, tClassURI);
			// this.resetCurrentLevel();
			MSMResult propertySetMappingResult = cgm2.ClassMatching().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					// System.out.println("* PropertyMatched: [" + i + "]" +
					// subMapping.s.getLocalName() + " -> " +
					// subMapping.t.getLocalName() + " ("
					// + subMapping.getSimilarity() + ")");
					System.out.println("-> PropertyMatched: [" + i + "]" + subMapping.s.getRevisedName() + " -> " + subMapping.t.getRevisedName() + " (" + subMapping.getSimilarity() + ")");
				}
			}
		}

		ArrayList<String> instance_mapping_string_list = new ArrayList<String>(); 
		ArrayList<String> property_mapping_string_list = new ArrayList<String>();
		ArrayList<String> class_lt_mapping_string_list = new ArrayList<String>();
		try {
			for (ClassMapping mapping : mappings) {

				String sInstanceURIStr = mapping.getSourceClassURI();
				String tInstanceURIStr = mapping.getTargetClassURI();
				Double similarity = mapping.getSimilarity();

				String instance_mapping_str1 = sInstanceURIStr + "+" + tInstanceURIStr;
				URI sURI = new URI(sInstanceURIStr);
				URI tURI = new URI(tInstanceURIStr);
				
				if (!instance_mapping_string_list.contains(instance_mapping_str1)) {
					instance_mapping_string_list.add(instance_mapping_str1);
					this.addAlignCell(sURI, tURI, "=", similarity); 

				}

				
				//  Property Mapping  //

//				cgm2.prepareMatchingResources(sClassURIStr, tClassURIStr);
//				MSMResult propertySetMappingResult = cgm2.ClassMatching().getPropertySetMatchingResult();
//				if (propertySetMappingResult != null) {
//					ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
//					for (int i = 0; i < mappingList.size(); i++) {
//						SubMapping subMapping = mappingList.get(i);
//						String sPropertyURI_str = subMapping.s.getUri();
//						String tPropertyURI_str = subMapping.t.getUri();
//						String property_mapping_string = sPropertyURI_str + "+" + tPropertyURI_str;
//						if (!property_mapping_string_list.contains(property_mapping_string)) {
//							property_mapping_string_list.add(property_mapping_string);
//							URI sPropertyURI = new URI(sPropertyURI_str);
//							URI tPropertyURI = new URI(tPropertyURI_str);
//							Double property_similarity = subMapping.getSimilarity();
//							// if (property_similarity > 0.5) {
//							this.addAlignCell(sPropertyURI, tPropertyURI, "=", property_similarity);
//							// }
//						}
//					}
//				}
				

			}

//			MSMResult propertySetMatchingResult = cgm2.getPropertyMapping(sResModel.listAllOntPropertiesAsArray(), tResModel.listAllOntPropertiesAsArray());
//			if (propertySetMatchingResult != null) {
//				ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
//				for (int i = 0; i < mappingList.size(); i++) {
//					SubMapping subMapping = mappingList.get(i);
//					String sPropertyURI_str = subMapping.s.getUri();
//					String tPropertyURI_str = subMapping.t.getUri();
//					double property_similarity = subMapping.getSimilarity();
//					URI sPropertyURI = new URI(sPropertyURI_str);
//					URI tPropertyURI = new URI(tPropertyURI_str);
//					 if (property_similarity > 0.5) {
//					this.addAlignCell(sPropertyURI, tPropertyURI, "=", property_similarity);
//				}
//				}
//			}

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
