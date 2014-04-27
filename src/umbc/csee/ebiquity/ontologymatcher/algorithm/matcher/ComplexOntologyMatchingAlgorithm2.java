package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ComparedPathRecorder;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ILabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.DescriptionSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.NGramsLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntDatatypePropertyValue;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntObjectPropertyValue;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyValue;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.DatatypeMatcher.DatatypeGroup;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;
import umbc.csee.ebiquity.ontologymatcher.utility.DataTypeChecker;
import umbc.csee.ebiquity.ontologymatcher.utility.DateCheckResult;
import umbc.csee.ebiquity.ontologymatcher.utility.NumericCheckResult;



/***
 * 
 * This ComplexGraphMatching Class is used to compute the similarity between two
 * resources. The similarity value relies upon the similarity between the local
 * names of two resources and the similarity between the property sets of the
 * two resources.
 * 
 * NOTE that: now the two resources must be owl classes. In other words, the two
 * resources must defined as owl:class. However, future improvement will exclude
 * this restriction.
 * 
 * @author kangyan2003
 * 
 */
@Deprecated
public class ComplexOntologyMatchingAlgorithm2 {

	private enum MatchingMode {
		CLASS, INSTANCE
	}
	
	private ResourceModel sModel;
	private ResourceModel tModel;
	private ComparedPathRecorder comparedPathRecorder = new ComparedPathRecorder();
	private StringBuilder matchingResultsRecorder = new StringBuilder();
	private ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
	private ILabelSimilarity ngramSim = new NGramsLabelSimilarity(); 
	private IWordIC labelIC = new BasicWordIC();
	private int maxLevel;
	private int currentLevel;
	private MatchingMode matchingMode;
//	private List<String> sDescriptionProperties = new ArrayList<String>();
//	private List<String> tDescriptionProperties = new ArrayList<String>();
	private boolean compareClassOrInstanceLabel = true;;
	private boolean comparePropertyLabel = true;

	public ComplexOntologyMatchingAlgorithm2(ResourceModel sr, ResourceModel tr, boolean fv) {
		
		if(fv == true){
			System.out.println("Using Ngrams for label similarity");
			labelSim = new NGramsLabelSimilarity();
		}
		
		this.setMaxLevel(Integer.MAX_VALUE);
		this.currentLevel = 0;
		this.sModel = sr;
		this.tModel = tr;
	}
	
	public void prepareMatchingResources(String sURI, String tURI) {
		
		try {
			sModel.prepareResource(sURI);
			tModel.prepareResource(tURI);
			matchingResultsRecorder = new StringBuilder();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setMaxLevel(int maxLevel){
		this.maxLevel = maxLevel;
	}
		
	/***
	 * To find the best matching of <b>TOP LEVEL CLASSES</b> between two ontologies. A ontology is represented by a ResourceModel.
	 */	
	public void getTopLevelClassMapping(){
		
		this.matchingMode = MatchingMode.CLASS;
		List<OntClassInfo> sClassList = sModel.listTopLevelClasses();
		List<OntClassInfo> tClassList = tModel.listTopLevelClasses();
		CSMResult result = this.getMapping(sClassList.toArray(new OntResourceInfo[0]), tClassList.toArray(new OntResourceInfo[0]));
		System.out.println("number of source classes: " + sClassList.size());
		System.out.println("number of target classes: " + tClassList.size());
		ArrayList<ClassMapping> mappings = result.getClassMappings();
		Collections.sort(mappings);
		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "("
					+ mapping.getSimilarity() + ")");
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			this.prepareMatchingResources(sClassURI, tClassURI);
			this.resetCurrentLevel();
			MSMResult propertySetMappingResult = this.ClassMatching().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					System.out.println("* PropertyMatched: [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
							+ subMapping.getSimilarity() + ")");
				}
			}
		}
	}
	
	public List<ClassMapping> getClassMappings() {
		
		this.matchingMode = MatchingMode.CLASS;
		List<OntResourceInfo> sClassList = sModel.listAllNamedClasses();
		List<OntResourceInfo> tClassList = tModel.listAllNamedClasses();
		CSMResult result = this.getMapping(
				sClassList.toArray(new OntResourceInfo[0]),
				tClassList.toArray(new OntResourceInfo[0]));

		ArrayList<ClassMapping> mappings = result.getClassMappings();
		Collections.sort(mappings);
		
		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "("
					+ mapping.getSimilarity() + ")");
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			this.prepareMatchingResources(sClassURI, tClassURI);
			this.resetCurrentLevel();
			MSMResult propertySetMappingResult = this.ClassMatching().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					System.out.println("* PropertyMatched: [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
							+ subMapping.getSimilarity() + ")");
				}
			}
		}
		return mappings;
	}
	
	
	/***
	 * To find the best matching of <b>ALL CLASS</b> between two ontologies. A ontology is represented by a ResourceModel.
	 */	
	public List<ClassMapping> getAllClassMapping(){
		this.matchingMode = MatchingMode.CLASS;
		List<OntResourceInfo> sClassList = sModel.listAllNamedClasses();
		List<OntResourceInfo> tClassList = tModel.listAllNamedClasses();
		CSMResult result = this.getMapping(sClassList.toArray(new OntResourceInfo[0]), tClassList.toArray(new OntResourceInfo[0]));
		ArrayList<ClassMapping> mappings = result.getClassMappings();
		Collections.sort(mappings);
		return mappings;
	}
	
	
	public List<ClassMapping> getAllIndividualMapping() {
		this.matchingMode = MatchingMode.INSTANCE;
//		String name = "Restaurant";
		String name = null;
		List<OntResourceInfo> sClassList = sModel.listAllIndividuals(name);
		List<OntResourceInfo> tClassList = tModel.listAllIndividuals(name);
		CSMResult result = this.getMapping(sClassList.toArray(new OntResourceInfo[0]), tClassList.toArray(new OntResourceInfo[0]));
		ArrayList<ClassMapping> mappings = result.getClassMappings();
		Collections.sort(mappings);
		return mappings;
	}	
	
	
	public void testGetAllIndividuals(){
		this.matchingMode = MatchingMode.INSTANCE;
//		String[] names = new String[1];
//		names[0] = "Restaurant";
		String name = "Person";
		List<OntResourceInfo> sClassList = sModel.listAllIndividuals(name);
		List<OntResourceInfo> tClassList = tModel.listAllIndividuals(name);
		System.out.println(sClassList.size() + " " + tClassList.size());
	}

	/***
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

    /***
     * To find the best matching of two sets of classes or individuals from different two
	 * ontologies. A ontology is represented by a ResourceModel.
     */
	public CSMResult getMapping(OntResourceInfo[] resourceNodeSet1, OntResourceInfo[] resourceNodeSet2) {
		
		int size1 = resourceNodeSet1.length; 
		int size2 = resourceNodeSet2.length;
		System.out.println("statistics about current matching process:");
		System.out.println("# of resources from source ontology: " + size1);
		System.out.println("# of resources from target ontology: " + size2);
		return this.getResourceSetMapping(resourceNodeSet1, resourceNodeSet2);

	}

	/***
	 * This is the core algorithm to find the best matching of two sets of
	 * resources (i.e., classes and individuals) from two different ontologies.
	 * A ontology is represented by a ResourceModel.
	 * 
	 * @param sResourceNodeSet
	 * @param tResourceNodeSet
	 * @return
	 */
	private CSMResult getResourceSetMapping(OntResourceInfo[] sResourceNodeSet, OntResourceInfo[] tResourceNodeSet) { 
		
		HashMap<String, CMResult> s_cmresult_bestmatching = new HashMap<String, CMResult>();
		HashMap<String, CMResult> t_cmresult_bestmatching = new HashMap<String, CMResult>();	    
		
		for (int i = 0; i < sResourceNodeSet.length; i++) {

			for (int j = 0; j < tResourceNodeSet.length; j++) {
//				double classSimilarity = 0.0;
				String sClassURI = sResourceNodeSet[i].getURI();
				String tClassURI = tResourceNodeSet[j].getURI();
				String sLocalName = sResourceNodeSet[i].getLocalName();
				String tLocalName = tResourceNodeSet[j].getLocalName();	
//			System.out.println("s instance URI: " + sClassURI);
//			System.out.println("t instance URI: " + tClassURI);
				
				if (comparedPathRecorder.isCompared(sClassURI, tClassURI)) {
					CMResult cmResult = comparedPathRecorder.getCMResult(sClassURI, tClassURI);
					if (cmResult != null) {
						// When the cmResult is NOT null. There are two
						// scenarios (at least I know now). One is that the
						// cmResult is completed, which means that it was
						// produced in the ClassMatching() method. Another is
						// the cmReuslt was produced in this method.  
					
						CMResult source_oldmatching = s_cmresult_bestmatching.get(sClassURI); 
						CMResult target_oldmatching = t_cmresult_bestmatching.get(tClassURI); 
						
						/** reserved code */
//						if (oldResult == null) {
//							s_cmresult_bestmapping.put(sClassURI, cmResult);
////							s_t_bestmapping.put(sClassURI, tClassURI);
//						} else if (oldResult.getClassSimilarity() < cmResult.getClassSimilarity()) {
//							s_cmresult_bestmapping.put(sClassURI, cmResult);
////							s_t_bestmapping.put(sClassURI, tClassURI);
//						}
						
						// Actually, cmResult must be complete
						if (source_oldmatching == null) {
							s_cmresult_bestmatching.put(sClassURI, cmResult);
						} else if (!source_oldmatching.isCompleted() && cmResult.isCompleted()) {
							s_cmresult_bestmatching.put(sClassURI, cmResult);
						} else if ((source_oldmatching.isCompleted() && cmResult.isCompleted()) || (!source_oldmatching.isCompleted() && !cmResult.isCompleted())) {
							if (source_oldmatching.getClassSimilarity() < cmResult.getClassSimilarity()) {
								s_cmresult_bestmatching.put(sClassURI, cmResult);
							}
						}
						
						if (target_oldmatching == null) {
							t_cmresult_bestmatching.put(tClassURI, cmResult);
						} else if (!target_oldmatching.isCompleted() && cmResult.isCompleted()) {
							t_cmresult_bestmatching.put(tClassURI, cmResult);
						} else if ((target_oldmatching.isCompleted() && cmResult.isCompleted()) || (!target_oldmatching.isCompleted() && !cmResult.isCompleted())) {
							if (target_oldmatching.getClassSimilarity() < cmResult.getClassSimilarity()) {
								t_cmresult_bestmatching.put(tClassURI, cmResult);
							}
						}
						  
					} else {
						// here when two classes have already been compared. We
						// set the similarity value equal to label similarity
						// between the two class. Actually this is not the best
						// decision. Therefore, this value should
						// be changed to something more reasonable.
						
//						double localNameSimilarity = labelSim.getSimilarity(sLocalName, tLocalName);
						double localNameSimilarity = DescriptionSimilarity.getSimilarity(sResourceNodeSet[i], tResourceNodeSet[j]);
//						int sWordsetSize = labelSim.getWordsetSize(sLocalName);
//						int tWordsetSize = labelSim.getWordsetSize(tLocalName);
			
						CMResult newCMResult = new CMResult(localNameSimilarity);
						newCMResult.setSourceClassLocalName(sLocalName);
						newCMResult.setTargetClassLocalName(tLocalName);
				
						newCMResult.setSourceClassURI(sClassURI);
						newCMResult.setTargetClassURI(tClassURI);
						newCMResult.setIsCompleted(false);
				
						comparedPathRecorder.setCMResult(sClassURI, tClassURI, newCMResult);
						CMResult source_oldmatching = s_cmresult_bestmatching.get(sClassURI); 
						CMResult target_oldmatching = t_cmresult_bestmatching.get(tClassURI);
						
						// Actually, here the newCMResult must be inComplete.
						if (source_oldmatching == null) {
							s_cmresult_bestmatching.put(sClassURI, newCMResult);
						} else if (!source_oldmatching.isCompleted() && source_oldmatching.getClassSimilarity() < newCMResult.getClassSimilarity()) {
							s_cmresult_bestmatching.put(sClassURI, newCMResult);
						}
						
						if (target_oldmatching == null) {
							t_cmresult_bestmatching.put(tClassURI, newCMResult);
						} else if (!target_oldmatching.isCompleted() && target_oldmatching.getClassSimilarity() < newCMResult.getClassSimilarity()) {
							t_cmresult_bestmatching.put(tClassURI, newCMResult);
						}
					}
					
				} else {
					
					if (this.matchingMode == MatchingMode.INSTANCE) {
						
//						System.out.println(sClassURI);
						List<String> sClasses = sModel.listTypesOfIndividual(sClassURI, false);
						List<String> tClasses = tModel.listTypesOfIndividual(tClassURI, false);
						if(!this.isClassSetsOverlapping(sClasses, tClasses)){
							continue;
						}
						
					}

					// point to current comparing resources/classes.
					this.prepareMatchingResources(sClassURI, tClassURI);
//					comparedPathRecorder.pushComparedNodes(sClassURI, tClassURI);
					// matching current comparing resources/classes.
					System.out.println("[" + i + "]" + "[" + j + "]" + "- Now compare these two classes: " + sLocalName + " and " + tLocalName + "---");

					CMResult cmResult = this.ClassMatching();
//					classSimilarity = cmResult.getClassSimilarity();
					
					
					CMResult source_oldmatching = s_cmresult_bestmatching.get(sClassURI); 
					CMResult target_oldmatching = t_cmresult_bestmatching.get(tClassURI);					
					
					/** reserved code*/
//					if (oldResult == null) {
//						s_cmresult_bestmapping.put(sClassURI, cmResult);
//					
//					} else if (oldResult.getClassSimilarity() < classSimilarity) {
//						s_cmresult_bestmapping.put(sClassURI, cmResult);
//					}
					
					
					// Actually, cmResult must be complete
					if (source_oldmatching == null) {
						s_cmresult_bestmatching.put(sClassURI, cmResult);
					} else if (!source_oldmatching.isCompleted() && cmResult.isCompleted()) {
						s_cmresult_bestmatching.put(sClassURI, cmResult);
					} else if ((source_oldmatching.isCompleted() && cmResult.isCompleted()) || (!source_oldmatching.isCompleted() && !cmResult.isCompleted())) {
						if (source_oldmatching.getClassSimilarity() < cmResult.getClassSimilarity()) {
							s_cmresult_bestmatching.put(sClassURI, cmResult);
						}
					}
					
					if (target_oldmatching == null) {
						t_cmresult_bestmatching.put(tClassURI, cmResult);
					} else if (!target_oldmatching.isCompleted() && cmResult.isCompleted()) {
						t_cmresult_bestmatching.put(tClassURI, cmResult);
					} else if ((target_oldmatching.isCompleted() && cmResult.isCompleted()) || (!target_oldmatching.isCompleted() && !cmResult.isCompleted())) {
						if (target_oldmatching.getClassSimilarity() < cmResult.getClassSimilarity()) {
							t_cmresult_bestmatching.put(tClassURI, cmResult);
						}
					}
					
					/** reserved code*/
//					if (oldResultInRecord == null) {
//						comparedPathRecorder.setCMResult(sClassURI, tClassURI, cmResult);
//					} else if (!oldResultInRecord.isCompleted() && cmResult.isCompleted()) {
//						System.out.println("herer1");
//						comparedPathRecorder.setCMResult(sClassURI, tClassURI, cmResult);
//					} else if ((oldResultInRecord.isCompleted() && cmResult.isCompleted()) || (!oldResultInRecord.isCompleted() && !cmResult.isCompleted())) {
//						if (oldResultInRecord.getClassSimilarity() < cmResult.getClassSimilarity()) {
//							System.out.println("herer2");
//							comparedPathRecorder.setCMResult(sClassURI, tClassURI, cmResult);
//						}
//					}
					
//					comparedPathRecorder.setCMResult(sClassURI, tClassURI, cmResult);
				}
			}
		}
		
		ArrayList<ClassMapping> classMappingList = new ArrayList<ClassMapping>();
		ArrayList<String> matchedStr = new ArrayList<String>();
		Collection<CMResult> source_bestmatching_CMResults = s_cmresult_bestmatching.values();
		for (CMResult result : source_bestmatching_CMResults) {

			// if (result.getSourceClassLocalName().equalsIgnoreCase("Author"))
			// {
			// System.out.println(result.getSourceClassLocalName() + "   " +
			// result.getTargetClassLocalName() + "-" +
			// result.getClassSimilarity());
			// }
			ClassMapping mapping = new ClassMapping(result);
			matchedStr.add(mapping.toString());
			classMappingList.add(mapping);
		}

		Collection<CMResult> target_bestmatching_CMResults = t_cmresult_bestmatching.values();
		for (CMResult result : target_bestmatching_CMResults) {

			ClassMapping mapping = new ClassMapping(result);
			if (!matchedStr.contains(mapping.toString())) {
				classMappingList.add(mapping);
			}
		}
		CSMResult csmResult = new CSMResult(classMappingList);
		return csmResult;
	}
	
	
	/***
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	/***
	 * 
	 * @param depth
	 * @return
	 */
	public CMResult ClassMatching() {
		
		this.currentLevel ++;
		double GSimilarity = 0.0;
//		String sLocalName = sModel.getResourceLocalName();
//		String tLocalName = tModel.getResourceLocalName();
		
		String sLocalName = sModel.getResourceLocalName_curr();
		String tLocalName = tModel.getResourceLocalName_curr();
	
		String sURI = sModel.getResourceUri_curr();
		String tURI = tModel.getResourceUri_curr();
		OntPropertyInfo[] sPropertyset = null;
		OntPropertyInfo[] tPropertyset = null;
		MSMResult propertySetMatchingResult = null;

//		List<String> sDescriptions = this.sModel.getTextualDescriptions(sURI, this.sDescriptionProperties);
//		List<String> tDescriptions = this.tModel.getTextualDescriptions(tURI, this.tDescriptionProperties);//		
//		StringBuilder sLabel = new StringBuilder();
//		StringBuilder tLabel = new StringBuilder();
//		for (String descri : sDescriptions) {
//			sLabel.append(descri + " ");
//		}
//		for (String descri : tDescriptions) {
//			tLabel.append(descri + " ");
//		}
//		double localNameSimilarity = this.ngramSim.getSimilarity(sLabel.toString(), tLabel.toString());
//		double localNameSimilarity = labelSim.getSimilarity(sLocalName, tLocalName);
		
		double localNameSimilarity = DescriptionSimilarity.getSimilarity(sModel.getOntResourceInfo_curr(), tModel.getOntResourceInfo_curr());
		double propertySimilarity = 0.0;

		if (this.currentLevel > this.maxLevel) {
			GSimilarity = localNameSimilarity;
		} else {

			// String indent = this.getIndent(depth);

			// int sWordsetSize = labelSim.getWordsetSize(sLocalName);
			// int tWordsetSize = labelSim.getWordsetSize(tLocalName);

			// matchingResultsRecorder.append("Class Mapping: " + sLocalName +
			// ":" + tLocalName + " [similarity: " + localNameSimilarity +
			// "] \n");
			// matchingResultsRecorder.append(indent + "Class Mapping: " +
			// sModel.getResourceUri() + ":" + tModel.getResourceUri() +
			// " [similarity: " + localNameSimilarity + "] \n");
//			System.out.println("---- Now compare these two classes: " + sLocalName + " and " + tLocalName + "---");

			if (this.matchingMode == MatchingMode.CLASS) {

				sPropertyset = sModel.listOntClassPropertiesAsArray();
				tPropertyset = tModel.listOntClassPropertiesAsArray();

			} else if (this.matchingMode == MatchingMode.INSTANCE) {

				sPropertyset = sModel.listIndividualPropertiesAsArray();
				tPropertyset = tModel.listIndividualPropertiesAsArray();
			}

			// System.out.println("The two classes have properties:");
			// System.out.println("\""+sLocalName +
			// "\" has property set of size: " + sPropertyset.length);
			// System.out.println("\""+tLocalName +
			// "\" has property set of size: " + tPropertyset.length);
			// for (OntPropertyInfo pi : sPropertyset) {
			// System.out.println("-> property local name: " + pi.toString() +
			// " has range of size: " + pi.getAllRanges().size());
			// }
			// System.out.println(indent+" ");
			// System.out.println(indent+"\"" + tLocalName +
			// "\" has property set size: " + tPropertyset.length);
			// for (OntPropertyInfo pi : tPropertyset) {
			// System.out.println(indent+"-> property local name: " +
			// pi.toString()+ " has range of size: " +
			// pi.getAllRanges().size());
			// }

			// System.out.println("  ");
			// System.out.println("Property Match Result for class (" +
			// sLocalName + ") and (" + tLocalName + ")");

			if (sPropertyset == null || tPropertyset == null) {
				// Actually, this should not happen. Because this is caused by
				// the current resource not found.

				propertySimilarity = 0.0;
				GSimilarity = this.getClassOrInstanceSimilarity(localNameSimilarity, propertySimilarity);
			} else if (sPropertyset.length != 0 && tPropertyset.length != 0) {
				// when Both source Resource/Class and target Resource/Class
				// have
				// properties

				int sPropertySize = sPropertyset.length;
				int tPropertySize = tPropertyset.length;
		
				int matchedPropertySize = 0;
				double matchedPropertySimilarity = 0.0;

				// Comparing the two property sets of current comparing
				// resources/classes.
				propertySetMatchingResult = this.getPropertyMapping(sPropertyset, tPropertyset);
				ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
				Collections.sort(mappingList);
				float numberOfMappings = mappingList.size();
				int numberOfMappings_for_computeSimilarity = Math.round(numberOfMappings * 2 / 3);
				
				for (int i = 0; i < numberOfMappings_for_computeSimilarity; i++) {
					SubMapping subMapping = mappingList.get(i);
					matchedPropertySize++;
					matchedPropertySimilarity += subMapping.getSimilarity();
				}

				
//				for (int i = 0; i < numberOfMappings; i++) {
//					SubMapping subMapping = mappingList.get(i);
//					matchedPropertySize++;
//					matchedPropertySimilarity += subMapping.getSimilarity();
//				}
				
				propertySimilarity = matchedPropertySimilarity / matchedPropertySize;
				
				
				
				
//				int avePropertySize = (sPropertySize + tPropertySize) / 2;
//				 propertySimilarity = matchedPropertySimilarity / avePropertySize;
				 
				
				GSimilarity = this.getClassOrInstanceSimilarity(localNameSimilarity, propertySimilarity);

			} else if (sPropertyset.length == 0 && tPropertyset.length == 0) {
				// when Both source Resource/Class and target Resource/Class
				// have NO properties.
				GSimilarity = localNameSimilarity;

			} else {
				// when either the source Resource/Class or target
				// Resource/Class have properties.
				
				GSimilarity = localNameSimilarity;
//				propertySimilarity = 0.0;
//				GSimilarity = this.getGraphSimilarity(localNameSimilarity, propertySimilarity);
			}

		}

		// System.out.println("Final Result for class (" + sLocalName +
		// ") and (" + tLocalName + ")");
		// System.out.println("\""+sLocalName + "\" has property set of size: "
		// + sPropertyset.length);
		// System.out.println("\"" + tLocalName + "\" has property set size: " +
		// tPropertyset.length);
		// if (propertySetMatchingResult != null) {
		// ArrayList<SubMapping> mappingList =
		// propertySetMatchingResult.getSubMappings();
		// for (int i = 0; i < mappingList.size(); i++) {
		// SubMapping subMapping = mappingList.get(i);
		//
		// System.out.println("|  Property Matched [" + i + "]" +
		// subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() +
		// " ("
		// + subMapping.getSimilarity() + ")");
		// }
		// }
		// System.out.println("|  local name similarity: " + localNameSimilarity
		// + "  " + "property similarity: " + propertySimilarity);
		// System.out.println("overall similarity: " + GSimilarity);

		CMResult classMatchingResult = new CMResult(GSimilarity, propertySetMatchingResult);
		classMatchingResult.setLabelSimilarity(localNameSimilarity);
		classMatchingResult.setPropertySimilarity(propertySimilarity);
		classMatchingResult.setSourceClassLocalName(sLocalName);
		classMatchingResult.setTargetClassLocalName(tLocalName);
		classMatchingResult.setSourceClassURI(sURI);
		classMatchingResult.setTargetClassURI(tURI);
		if (this.matchingMode == MatchingMode.CLASS) {
			classMatchingResult.setSourcePropertySetSize(sPropertyset.length);
			classMatchingResult.setTargetPropertySetSize(tPropertyset.length);
		}
		classMatchingResult.setIsCompleted(true);
		
		this.currentLevel--;
		return classMatchingResult;
	}
	
	
	public CMResult getClassMatchingResult(String sClassURI, String tClassURI){
		this.prepareMatchingResources(sClassURI, tClassURI);
		this.matchingMode = MatchingMode.CLASS;
		this.resetCurrentLevel();
		return this.ClassMatching();
	}
	
	public CMResult getIndividualMatchingResult(String sIndividualURI, String tIndividualURI){
		this.prepareMatchingResources(sIndividualURI, tIndividualURI);
		this.matchingMode = MatchingMode.INSTANCE;
		this.resetCurrentLevel();
		return this.ClassMatching();
	}
	
	/***
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	

	/***
	 * This method is used to compute the similarity between the property sets
	 * (property instance sets) of two resources (i.e., classes/individuals).
	 * 
	 * @param sPropertyNodeSet
	 * @param tPropertyNodeSet
	 * @return
	 */
	public MSMResult getPropertyMapping(OntPropertyInfo[] sPropertyNodeSet, OntPropertyInfo[] tPropertyNodeSet) {

	    SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sPropertyNodeSet.length];
		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sPropertyNodeSet[i].getLocalName());
			slabelSet[i].setRevisedName(sPropertyNodeSet[i].getRevisedLocalName());
			slabelSet[i].setURI(sPropertyNodeSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sPropertyNodeSet[i].getLocalName()));
		}
		
		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tPropertyNodeSet.length];
		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tPropertyNodeSet[j].getLocalName());
			tlabelSet[j].setRevisedName(tPropertyNodeSet[j].getRevisedLocalName());
			tlabelSet[j].setURI(tPropertyNodeSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tPropertyNodeSet[j].getLocalName()));
		}
		return getPropertyMapping(sPropertyNodeSet, slabelSet, tPropertyNodeSet, tlabelSet);
	}
 
	/***
	 * This is the core algorithm to compute the similarity between the property
	 * sets of two resources (now classes).
	 * 
	 * @param sPropertyNodeSet
	 * @param sPropertyLabelSet
	 * @param tPropertyNodeSet
	 * @param tPropetyLabelSet
	 * @return
	 */
	private MSMResult getPropertyMapping(OntPropertyInfo[] sPropertyNodeSet, SimilarityMatrixLabel[] sPropertyLabelSet, OntPropertyInfo[] tPropertyNodeSet,
			SimilarityMatrixLabel[] tPropetyLabelSet) {
		
		SimilarityMatrix sm = new SimilarityMatrix(sPropertyNodeSet.length, tPropertyNodeSet.length);

		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			sm.setCol(j, tPropetyLabelSet[j]);
		}

		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			sm.setRow(i, sPropertyLabelSet[i]);

			for (int j = 0; j < tPropertyNodeSet.length; j++) {
				
				double propertySimilarity = 0.0;
				double propertyValueSimilarity = 0.0;
				String sourceRangeUri = null;
				String targetRangeUri = null;
				OntPropertyValue sOntPropertyValue = null;
				OntPropertyValue tOntPropertyValue = null;
				String sPropertyLocalName =  sPropertyNodeSet[i].getLocalName();
				String tPropertyLocalName =  tPropertyNodeSet[j].getLocalName();
				
//				System.out.println(" ");
//				System.out.println(" ");
				
				
//				System.out.println("---- Now compare these two properties: " + sPropertyLocalName+" and " + tPropertyLocalName + "---");
//				
				
				//System.out.println(indent+"\"" + sPropertyLocalName + "\" has range size of: " + sRanges.length);
//				for (OntClassInfo ci : sRanges) {
//					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
//				}
//
//				System.out.println(" ");
//				System.out.println(indent+"\"" + tPropertyLocalName + "\" has range size of: " + tRanges.length);
//				for (OntClassInfo ci : tRanges) {
//					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
//				}

				/**
				 * Dynamic Programming for label matching.
				 */

//				double propertyLabelSimilarity = labelSim.getSimilarity(sPropertyNodeSet[i].getRevisedLocalName(), tPropertyNodeSet[j].getRevisedLocalName());
				double propertyLabelSimilarity = DescriptionSimilarity.getSimilarity(sPropertyNodeSet[i],  tPropertyNodeSet[j]);
				
				if (this.matchingMode == MatchingMode.CLASS) {

					if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.DataTypeProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty) {

						propertySimilarity = propertyLabelSimilarity;
						// compare the similarity between two data types
						if (sPropertyNodeSet[i].getDatatype() != null && tPropertyNodeSet[j].getDatatype() != null) {
							double datatypeSimlarity = DatatypeMatcher.getSimilarityScore(sPropertyNodeSet[i].getDatatype(), tPropertyNodeSet[j].getDatatype());
							propertySimilarity = 0.8 * propertyLabelSimilarity + 0.2 * datatypeSimlarity;
						}

					} else if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.ObjectProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.ObjectProperty) {

						if (sPropertyNodeSet[i].getAllRangeClasses().size() != 0 && tPropertyNodeSet[j].getAllRangeClasses().size() != 0) {

							OntResourceInfo[] sRanges = sPropertyNodeSet[i].getAllRangeClassesAsArray();
							OntResourceInfo[] tRanges = tPropertyNodeSet[j].getAllRangeClassesAsArray();

							CSMResult result = this.getResourceSetMapping(sRanges, tRanges);
							double rangeSetSimilarity = result.getSimilarity();  
							propertySimilarity = this.getObjectPropertySimilarity(propertyLabelSimilarity, rangeSetSimilarity); 

						} else if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 && tPropertyNodeSet[j].getAllRangeClasses().size() == 0){
							propertySimilarity = propertyLabelSimilarity;
						} else {
							propertySimilarity = propertyLabelSimilarity;
//							propertySimilarity = this.getPropertySimilarity(propertyLabelSimilarity, 0.0);
						}

					} else {
						
						propertySimilarity = propertyLabelSimilarity;
//						propertySimilarity = this.getPropertySimilarity(scaledLabelSimilarity, 0.0, 0);
					}
					
				} else if (this.matchingMode == MatchingMode.INSTANCE) {
					
					if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.DataTypeProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty) {

						sOntPropertyValue = new OntDatatypePropertyValue(sPropertyNodeSet[i].getDatatype(), sPropertyNodeSet[i].getValue());
						tOntPropertyValue = new OntDatatypePropertyValue(tPropertyNodeSet[j].getDatatype(), tPropertyNodeSet[j].getValue());
						propertySimilarity = propertyLabelSimilarity;
						// compare the similarity between two data types
						if (sPropertyNodeSet[i].getDatatype() != null && tPropertyNodeSet[j].getDatatype() != null) {
							double datatypeSimlarity = DatatypeMatcher.getSimilarityScore(sPropertyNodeSet[i].getDatatype(), tPropertyNodeSet[j].getDatatype());
							propertySimilarity = 1.0 * propertyLabelSimilarity + 0.0 * datatypeSimlarity;		
						}

						propertyValueSimilarity = this.getPropertyValueSimilarity(sPropertyNodeSet[i], tPropertyNodeSet[j]);
						propertySimilarity = this.getPropertyInstanceSimilarity(propertyLabelSimilarity, propertyValueSimilarity);

					} else if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.ObjectProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.ObjectProperty) {

						OntResourceInfo[] sOntResourceInfos = new OntResourceInfo[1];
						OntResourceInfo[] tOntResourceInfos = new OntResourceInfo[1];

						sOntResourceInfos[0] = sPropertyNodeSet[i].getOntResourceInfoForObject();
						tOntResourceInfos[0] = tPropertyNodeSet[j].getOntResourceInfoForObject();
						sOntPropertyValue = new OntObjectPropertyValue(sOntResourceInfos[0].getURI(), sOntResourceInfos[0].getNamespace(), sOntResourceInfos[0].getLocalName());
						tOntPropertyValue = new OntObjectPropertyValue(tOntResourceInfos[0].getURI(), tOntResourceInfos[0].getNamespace(), tOntResourceInfos[0].getLocalName());
						
						CSMResult result = this.getResourceSetMapping(sOntResourceInfos, tOntResourceInfos);
						propertyValueSimilarity = result.getSimilarity();
						propertySimilarity = this.getPropertyInstanceSimilarity(propertyLabelSimilarity, propertyValueSimilarity);

					} else if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.ObjectProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty){
						
						OntResourceInfo[] sOntResourceInfos = new OntResourceInfo[1];
						sOntResourceInfos[0] = sPropertyNodeSet[i].getOntResourceInfoForObject();
						
						List<String> descriptions = sOntResourceInfos[0].getDescriptions();
						
						StringBuilder sLabel = new StringBuilder();
						String sPropertyValue = ""; 
						if (descriptions.size() == 0) {
							sPropertyValue = sOntResourceInfos[0].getLocalName();
						} else {
							for (String descri : descriptions) {
								sLabel.append(descri + " ");
							}
							sPropertyValue = sLabel.toString();
						}
						String tPropertyValue = tPropertyNodeSet[j].getValue();
//						System.out.println(sPropertyLocalName + " : " + tPropertyLocalName);
//						System.out.println("["+sPropertyValue + ":" + tPropertyValue+"]");

						// DescriptionSimilarity.getSimilarity(sResource,
						// tResource);
						propertyValueSimilarity = this.ngramSim.getSimilarity(sPropertyValue, tPropertyValue);

						propertySimilarity = this.getPropertyInstanceSimilarity(propertyLabelSimilarity, propertyValueSimilarity);
						
					} else if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.DataTypeProperty && tPropertyNodeSet[j].getPropertyType() == OntPropertyType.ObjectProperty) {
						OntResourceInfo[] tOntResourceInfos = new OntResourceInfo[1];
						tOntResourceInfos[0] = tPropertyNodeSet[j].getOntResourceInfoForObject();

						List<String> descriptions = tOntResourceInfos[0].getDescriptions();
						StringBuilder tLabel = new StringBuilder();
						String tPropertyValue = "";
						if (descriptions.size() == 0) {
							tPropertyValue = tOntResourceInfos[0].getLocalName();
						} else {
							for (String descri : descriptions) {
								tLabel.append(descri + " ");
							}
							tPropertyValue = tLabel.toString();
						}
						
						String sPropertyValue = sPropertyNodeSet[i].getValue();
//						System.out.println(sPropertyLocalName + " : " + tPropertyLocalName);
//						System.out.println("["+sPropertyValue + ":" + tPropertyValue+"]");
						
//						DescriptionSimilarity.getSimilarity(sResource, tResource)
						propertyValueSimilarity = this.ngramSim.getSimilarity(tPropertyValue, sPropertyValue);
						propertySimilarity = this.getPropertyInstanceSimilarity(propertyLabelSimilarity, propertyValueSimilarity);
					}
				}
				
				
//				double scaledRangeSetSimilarity = 0.0;
//				CSMResult result = null;
//				if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 && tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
//					// When both resources/classes have no ranges. It is
//					// possible that both two properties are DatatypeProperty,
//					// in which case, we should also consider the
//					// similarity between the types of two properties.
//					propertySimilarity = scaledLabelSimilarity;
//					if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.DataTypeProperty
//							&& tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty) {
//						
//						// compare the similarity between two data types
//						if (sPropertyNodeSet[i].getDatatype() != null && tPropertyNodeSet[j].getDatatype() != null) {
//							double datatypeSimlarity = DatatypeMatcher.getSimilarityScore(sPropertyNodeSet[i].getDatatype(), tPropertyNodeSet[j].getDatatype());
//							propertySimilarity = 0.7 * scaledLabelSimilarity + 0.3 * datatypeSimlarity;
//						}
//					}
//					
//				} else if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 || tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
//					// When only one resources/classes has ranges.
//					propertySimilarity = this.getPropertyMatchingSimilarity(scaledLabelSimilarity, 0.0, 0);
//				} else {
//					// When both resources/classes have ranges, which means both
//					// properties are ObjectTypeProperty. In this situation, we
//					// will compute the similarity between their range sets.
//
//					result = this.getMappingByHashing(sRanges, tRanges);
//					this.setDepth(depth+1);
//					
////					if (result != null) {
////						ArrayList<SubMapping> mappingList = result.getSubMappings();
////						for (int l = 0; l < mappingList.size(); l++) {
////							SubMapping subMapping = mappingList.get(l);
////
////							System.out.println("|  [" + l + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
////									+ subMapping.similarity + ")");
////						}
////					}
//					
//					scaledRangeSetSimilarity = this.scaleRangeSetSimilarity(result.getSimilarity());
//					propertySimilarity = this.getPropertyMatchingSimilarity(scaledLabelSimilarity, scaledRangeSetSimilarity, 0);
//				}

//				System.out.println("| property local name similarity: " + scaledLabelSimilarity + "  " + "property ranges similarity: " + scaledRangeSetSimilarity);
//				System.out.println("overall similarity: " + propertySimilarity);
				
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), propertySimilarity);
				cell.setSourceRangeUri(sourceRangeUri);
				cell.setTargetRangeUri(targetRangeUri);
				cell.setSourcePropertyType(sPropertyNodeSet[i].getPropertyType());
				cell.setTargetPropertyType(tPropertyNodeSet[j].getPropertyType());
				cell.setSourceRangeSize(sPropertyNodeSet[i].getAllRangeClasses().size());
				cell.setTargetRangeSize(tPropertyNodeSet[j].getAllRangeClasses().size());
				cell.setSourceOntPropertyValue(sOntPropertyValue);
				cell.setTargetOntPropertyValue(tOntPropertyValue);
				cell.setPropertyValueSimilarity(propertyValueSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}
	


	private double getPropertyValueSimilarity(OntPropertyInfo sOntPropertyInfo, OntPropertyInfo tOntPropertyInfo) {
		
//		System.out.println(sOntPropertyInfo.getLocalName() + " : " + tOntPropertyInfo.getLocalName());
		double valueSimilarity = 0.0;
		DatatypeGroup sDatatypeGroup = null;
		DatatypeGroup tDatatypeGroup = null;
		String sPropertyValue = sOntPropertyInfo.getValue();
		String tPropertyValue = tOntPropertyInfo.getValue();
		
//		System.out.println(sPropertyValue + " : " + tPropertyValue);
		if (sOntPropertyInfo.getDatatype() != null && tOntPropertyInfo.getDatatype() != null) {
			sDatatypeGroup = DatatypeMatcher.getGroupType(sOntPropertyInfo.getDatatype());
			tDatatypeGroup = DatatypeMatcher.getGroupType(tOntPropertyInfo.getDatatype());
		} else if (sOntPropertyInfo.getDatatype() == null && tOntPropertyInfo.getDatatype() == null) {
			sDatatypeGroup = DatatypeGroup.STRING;
			tDatatypeGroup = DatatypeGroup.STRING;
		} else {
			if (sOntPropertyInfo.getDatatype() == null) {
				sDatatypeGroup = DatatypeGroup.STRING;
				tDatatypeGroup = DatatypeMatcher.getGroupType(tOntPropertyInfo.getDatatype());
			} else {
				sDatatypeGroup = DatatypeMatcher.getGroupType(tOntPropertyInfo.getDatatype());
				tDatatypeGroup = DatatypeGroup.STRING;
			}
		}
		
		if (sDatatypeGroup == DatatypeGroup.STRING && tDatatypeGroup == DatatypeGroup.STRING) {
			
			
			DateCheckResult sD = DataTypeChecker.getDate(sPropertyValue);
			DateCheckResult tD = DataTypeChecker.getDate(tPropertyValue);

			if (sD.isDate() && tD.isDate()) {

				String sR = sD.getResult();
				String tR = tD.getResult();
				valueSimilarity = DataTypeChecker.compareDigits(sR, tR);
				return valueSimilarity;
			}
			
			NumericCheckResult sN = DataTypeChecker.getNumeric(sPropertyValue);
			NumericCheckResult tN = DataTypeChecker.getNumeric(tPropertyValue);
			if (sN.isNumeric() && tN.isNumeric()) {

				String sR = sN.getResult();
				String tR = tN.getResult();
				valueSimilarity = DataTypeChecker.compareDigits(sR, tR);
				return valueSimilarity;
			}
			
//			MSMResult labelSimilarityResult = labelSim.getMapping(sPropertyValue, tPropertyValue);
			valueSimilarity = ngramSim.getSimilarity(sPropertyValue, tPropertyValue);
			return valueSimilarity;
//			System.out.println("2 -> " + sPropertyValue + " : " + tPropertyValue + " : " + valueSimilarity);
		} else if (sDatatypeGroup == DatatypeGroup.DECIMAL && tDatatypeGroup == DatatypeGroup.DECIMAL) {
//			System.out.println("3 -> " + sPropertyValue + " : " + tPropertyValue);
			int sValue = Integer.valueOf(sPropertyValue);
			int tValue = Integer.valueOf(tPropertyValue);
			int diff = Math.round(Math.abs(sValue - tValue));

			if (diff == 0) {
				valueSimilarity = 1.0;
			}
			return valueSimilarity;

		} else if (sDatatypeGroup == DatatypeGroup.FLOAT && tDatatypeGroup == DatatypeGroup.FLOAT) {
//			System.out.println("4 -> " + sPropertyValue + " : " + tPropertyValue);
			double sValue = Double.valueOf(sPropertyValue);
			double tValue = Double.valueOf(tPropertyValue);
			int diff = (int) Math.round(Math.abs(sValue - tValue));
			if (diff == 0) {
				valueSimilarity = 1.0;
			}
			return valueSimilarity;

		} else {
			return valueSimilarity;
		}
	}
		
	/**
	 * To check if two set of classes overlaps
	 * @param tClasses
	 * @param sClasses
	 * @return true if overlaps, false otherwise
	 */
	private boolean isClassSetsOverlapping(List<String> tClasses, List<String> sClasses){
		
		for(String tClass : tClasses){
			for(String sClass : sClasses){
				double sim = ngramSim.getSimilarity(tClass, sClass);
				if(sim > 0.8){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * compute the overall similarity between two classes. The overall
	 * similarity is a combination of similarity between class labels and the
	 * similarity between property sets of the two classes.
	 * 
	 * @param localNameSimilarity
	 * @param propertySimilarity
	 * @return the overall similarity between the comparing classes.
	 */
	private double getClassOrInstanceSimilarity(double localNameSimilarity, double propertySetSimilarity) {

		if (this.compareClassOrInstanceLabel) {
			if (propertySetSimilarity >= localNameSimilarity) {
				return 0.8 * propertySetSimilarity + 0.2 * localNameSimilarity;
			} else {
				return 0.8 * localNameSimilarity + 0.2 * propertySetSimilarity;
			}
		} else {
			return propertySetSimilarity;
		}
	}

	/***
	 * compute overall similarity between two object properties. This overall
	 * similarity is a combination of similarity between property labels and
	 * similarity between properties ranges
	 * 
	 * @param propertyLabelSimilarity
	 * @param propertyRangeSimilarity
	 * @return overall similarity between two properties
	 */
	private double getObjectPropertySimilarity(double propertyLabelSimilarity, double propertyRangeSimilarity) {
		
		if (propertyLabelSimilarity >= propertyRangeSimilarity) { 
			return 0.8 * propertyLabelSimilarity + 0.2 * propertyRangeSimilarity;
		} else {
			return 0.67 * propertyRangeSimilarity + 0.33 * propertyLabelSimilarity;
		}
	}

	/***
	 * compute overall similarity between two property instances. Property
	 * instance has two parts: property and its value, which can be literal or
	 * resource (namely other class). This overall similarity is a combination
	 * of similarity between property labels and similarity between properties
	 * values
	 * 
	 * @param propertyLabelSimilarity
	 * @param propertyValueSimilarity
	 * @return overall similarity between two property instance.
	 */
	private double getPropertyInstanceSimilarity(double propertyLabelSimilarity, double propertyValueSimilarity) {
		
		// The idea behind the if-statement below is this: When
		// we are comparing Datatype properties of two
		// individuals, the values of the properties are what we
		// are focusing. However, before we compute the
		// similarity between values of two Datatype properties,
		// we should first make sure that these two Datatype
		// properties are highly similar or in other words they
		// should be considered the same. Otherwise, dissimilar
		// properties may be matched and the similarity score of
		// their values may be computed (This is because similar
		// properties not necessarily have higher similarity
		// between their values compare to dissimilar properties
		// ). This is not what we
		// expect. However, two properties with highly similar
		// values indicate that these two properties are similar
		// with high possibility. We should take both factors
		// into consideration. Therefore, when two properties
		// are considered to be the same, we then compute the
		// similarity between their values. On the other hand,
		// when the values of two properties are highly similar,
		// we treat the two properties are the same and record
		// the similarity score of their values. In other
		// scenarios, properties compared are either dissimilar
		// or with low similarity between their values. We then
		// give then low similarity score.
		if (propertyLabelSimilarity >= 0.7 || propertyValueSimilarity >= 0.7) {
			return 0.2 * propertyLabelSimilarity + 0.8 * propertyValueSimilarity;
		} else {
			return 0.8 * propertyValueSimilarity;
		}
	}

	public String getMatchingInfo() {
		return this.matchingResultsRecorder.toString();
	}
	
	public void resetCurrentLevel(){
		this.currentLevel = 0;
	}
	
	public void compareClassOrInstanceLabel(boolean compare){
		this.compareClassOrInstanceLabel = compare;
	}
	
	public void comparePropertyLabel(boolean compare){
		this.comparePropertyLabel = compare;
	}
	

//	/***
//	 * Add a set of properties from each ontology (source and target). From
//	 * these properties textual descriptions of classes/individuals can be
//	 * extracted. For example, if in source ontology, rdfs:label is used to
//	 * provide textual description about classes, you can this property to
//	 * extracted textual information about classes from source ontology. This is
//	 * useful, when local names of classes/individuals are meaningless words.
//	 * 
//	 * @param sDescriptionProperties
//	 * @param tDescriptionProperties
//	 */
//	public void setDescriptionProperties(List<String> sDescriptionProperties, List<String> tDescriptionProperties) {
//		this.sDescriptionProperties = sDescriptionProperties;
//		this.tDescriptionProperties = tDescriptionProperties;
//	}
}
