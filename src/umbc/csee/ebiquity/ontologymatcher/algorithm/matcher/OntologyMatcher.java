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

import com.hp.hpl.jena.util.FileManager;
import fr.inrialpes.exmo.align.impl.URIAlignment;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OntologyMatchingAlgorithm.PropertySetsMatchingStrategy;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

@Deprecated
public class OntologyMatcher extends URIAlignment implements AlignmentProcess {

	
	public String align(String sFileLocator, String tFileLocator) {

		InputStream instream = FileManager.get().open(sFileLocator);
		InputStream instream2 = FileManager.get().open(tFileLocator);
		ResourceModel sResModel = new OntResourceModel(instream);
		ResourceModel tResModel = new OntResourceModel(instream2);
		ComplexOntologyMatchingAlgorithm2 cgm2 = new ComplexOntologyMatchingAlgorithm2(
				sResModel, tResModel, false);
		cgm2.getAllClassMapping();

		return "";

	}

	public String align(URI sURI, URI tURI) {

		InputStream instream = FileManager.get().open(sURI.toString());
		InputStream instream2 = FileManager.get().open(tURI.toString());
		ResourceModel sResModel = new OntResourceModel(instream);
		ResourceModel tResModel = new OntResourceModel(instream2);
		ComplexOntologyMatchingAlgorithm2 cgm2 = new ComplexOntologyMatchingAlgorithm2(
				sResModel, tResModel,false);
		cgm2.getAllClassMapping();

		return "";

	}

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
		
		OntologyMatchingAlgorithm cgm2 = new OntologyMatchingAlgorithm(sResModel, tResModel, false, PropertySetsMatchingStrategy.STRICT);
		List<ClassMapping> mappings = cgm2.getClassMappingsWithPrune();
		// List<ClassMapping> mappings = cgm2.getClassMappings();
		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "("
					+ mapping.getLabelSimilarity() + ")");
			System.out.println("properties similarity: " + mapping.getPropertySimilarity());
			System.out.println("mapping similarity: " + mapping.getSimilarity());
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			cgm2.prepareMatchingResources(sClassURI, tClassURI);
//			this.resetCurrentLevel();
			MSMResult propertySetMappingResult = cgm2.matchClassPair().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
//					System.out.println("* PropertyMatched: [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
//							+ subMapping.getSimilarity() + ")");
					System.out.println("-> PropertyMatched: [" + i + "]" + subMapping.s.getRevisedName() + " -> " + subMapping.t.getRevisedName() + " ("
							+ subMapping.getSimilarity() + ")");
				}
			}
		}

		ArrayList<String> class_mapping_string_list = new ArrayList<String>();
		ArrayList<String> property_mapping_string_list = new ArrayList<String>();
		ArrayList<String> class_lt_mapping_string_list = new ArrayList<String>();
		try {
			for (ClassMapping mapping : mappings) {

				String sClassURIStr = mapping.getSourceClassURI();
				String tClassURIStr = mapping.getTargetClassURI();
				Double similarity = mapping.getSimilarity();

				URI sURI;
				URI tURI;

				String class_mapping_str1 = sClassURIStr + "+" + tClassURIStr;
				sURI = new URI(sClassURIStr);
				tURI = new URI(tClassURIStr);

				// Equivalent Class Mapping //
				
				if (!class_mapping_string_list.contains(class_mapping_str1)) {
					class_mapping_string_list.add(class_mapping_str1);
					this.addAlignCell(sURI, tURI, "=", similarity);

				}

				List<String> s_equivalentClassURIStrlist = sResModel.listNamedEquivalentClasses(sClassURIStr);
				for (String s_eqClass_URIStr : s_equivalentClassURIStrlist) {

					String class_mapping_str2 = s_eqClass_URIStr + "+" + tClassURIStr;
					if (!class_mapping_string_list.contains(class_mapping_str2)) {
						class_mapping_string_list.add(class_mapping_str2);
						URI s_eqClass_URI = new URI(s_eqClass_URIStr);
						this.addAlignCell(s_eqClass_URI, tURI, "=", similarity);
					}
				}

				List<String> t_equivalentClassURIStrlist = tResModel.listNamedEquivalentClasses(tClassURIStr);
				for (String t_eqClass_URIStr : t_equivalentClassURIStrlist) {

					String class_mapping_str2 = sClassURIStr + "+" + t_eqClass_URIStr;
					if (!class_mapping_string_list.contains(class_mapping_str2)) {
						class_mapping_string_list.add(class_mapping_str2);
						URI t_eqClass_URI = new URI(t_eqClass_URIStr);
						this.addAlignCell(sURI, t_eqClass_URI, "=", similarity);
					}
				}
				
				// Supper class Mapping //
				
//				List<String> s_superClassURIStrlist = sResModel.listSuperClasses(sClassURIStr, true);
//				for (String s_superClass_URIStr : s_superClassURIStrlist) {
//					
//					String class_lt_mapping_str = tClassURIStr + "+" + s_superClass_URIStr;
//					if(!class_lt_mapping_string_list.contains(class_lt_mapping_str)){
//						class_lt_mapping_string_list.add(class_lt_mapping_str);
//						URI s_eqClass_URI = new URI(s_superClass_URIStr);
//						this.addAlignCell(tURI, s_eqClass_URI, "&lt;", similarity);
//					}
//				}
//				
//				List<String> t_superClassURIStrlist = tResModel.listSuperClasses(tClassURIStr, true);
//				for (String t_superClass_URIStr : t_superClassURIStrlist) {
//					
//					String class_lt_mapping_str = sClassURIStr + "+" + t_superClass_URIStr;
//					if(!class_lt_mapping_string_list.contains(class_lt_mapping_str)){
//						class_lt_mapping_string_list.add(class_lt_mapping_str);
//						URI t_eqClass_URI = new URI(t_superClass_URIStr);
//						this.addAlignCell(sURI, t_eqClass_URI, "&lt;", similarity);
//					}
//				}
				
				
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

			MSMResult propertySetMatchingResult = cgm2.matchProperties(sResModel.listAllOntPropertiesAsArray(), tResModel.listAllOntPropertiesAsArray());
			if (propertySetMatchingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					String sPropertyURI_str = subMapping.s.getURI();
					String tPropertyURI_str = subMapping.t.getURI();
					double property_similarity = subMapping.getSimilarity();
					URI sPropertyURI = new URI(sPropertyURI_str);
					URI tPropertyURI = new URI(tPropertyURI_str);
					 if (property_similarity > 0.5) {
					this.addAlignCell(sPropertyURI, tPropertyURI, "=", property_similarity);
				}
				}
			}

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
