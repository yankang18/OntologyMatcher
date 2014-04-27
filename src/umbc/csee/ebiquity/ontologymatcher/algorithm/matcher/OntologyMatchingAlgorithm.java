package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.DescriptionSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ILabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.NGramsLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;

/***
 * 
 * This OntologyMatchingAlgorithm Class is used to compute the similarity
 * between classes and properties between two different ontologies.
 * 
 * The similarity between two classes relies on the similarity between their
 * taxonomy structures and the similarity between the properties
 * 
 * The similarity between two properties currently relies on the similarity
 * between labels of the two properties and the similarity between ranges of the
 * two properties.
 * 
 * @author Yan Kang
 * 
 */
public class OntologyMatchingAlgorithm {

	private ResourceModel sModel;
	private ResourceModel tModel;
	private HierarchyMatcher HMather;
	private OWLClassDescriptionSetMatcher classDescriptionSetMatcher; 
	private OWLObjectPropertyDescriptionSetMatcher objProDescriptionSetMatcher;
	private ComparedPathRecorder comparedPathRecorder = new ComparedPathRecorder();
	private ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
	private IWordIC labelIC = new BasicWordIC();
	private ILabelSimilarity ngramSim = new NGramsLabelSimilarity(); 
	int cursor = 0;
	
	private PropertySetsMatchingStrategy PSMStrategy;
	private float alpha = 0.66f;

	/***
	 * three levels for computing the similarity between properties of two
	 * classes.
	 * 
	 * STRICT - most strict level, this similarity is computed by (sum of
	 * similarities of all matched property pairs) / (average of the numbers of
	 * properties of each of the two classes)
	 * 
	 * NORMAL - (sum of similarities of all matched property pairs) / (the
	 * number of all matched property pairs)
	 * 
	 * LENIENT - (sum of similarities of top alpha (<1) of all matched property
	 * pairs) / (the number of top alpha matched property pairs)
	 * 
	 * @author kangyan2003
	 */
	public enum PropertySetsMatchingStrategy {
		STRICT, NORMAL, LENIENT
	}
	public OntologyMatchingAlgorithm(){
		labelSim = new NGramsLabelSimilarity();
	}
	
	/***
	 * 
	 * @param sr
	 * @param tr
	 * @param isfaster
	 * @param strategy
	 */
	public OntologyMatchingAlgorithm(ResourceModel sr, ResourceModel tr, boolean isfaster, PropertySetsMatchingStrategy strategy) {
		System.out.println("Ontology Matching Algorithm");

		if (isfaster == true) {
			System.out.println("Using Ngrams for label similarity");
			labelSim = new NGramsLabelSimilarity();
		}

		this.sModel = sr;
		this.tModel = tr;
		this.classDescriptionSetMatcher = new OWLClassDescriptionSetMatcher(sModel, tModel);
		this.objProDescriptionSetMatcher = new OWLObjectPropertyDescriptionSetMatcher(sModel, tModel);
		this.HMather = new HierarchyMatcher(sr, tr);
		this.setPropertySetsMatchingStrategy(strategy);
	}
	
	public void prepareMatchingResources(String sURI, String tURI) {
		
		try {
			sModel.prepareResource(sURI);
			tModel.prepareResource(tURI);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<ClassMapping> getClassMappings() {
		
		List<OntResourceInfo> sClassList = sModel.listAllNamedClasses();
		List<OntResourceInfo> tClassList = tModel.listAllNamedClasses();
		CSMResult result = this.match(
				sClassList.toArray(new OntResourceInfo[0]),
				tClassList.toArray(new OntResourceInfo[0]));

		ArrayList<ClassMapping> mappings = result.getClassMappings();
		Collections.sort(mappings);
		return mappings;
	}
	
	public List<ClassMapping> getClassMappingsWithPrune() {
		return this.pruneMappings(this.getClassMappings());
	}
	
	/***
	 * This is the core algorithm to find the best matching between classes
	 * from heterogeneous two ontologies. 
	 * 
	 * @param sClassNodeSet
	 * @param tClassNodeSet
	 * @return
	 */
	public CSMResult match(OntResourceInfo[] sClassNodeSet, OntResourceInfo[] tClassNodeSet) {
		
		int sSize = sClassNodeSet.length;
		int tSize = tClassNodeSet.length;
		System.out.println("number of classes in source ontology: " + sSize);
		System.out.println("number of classes in target ontology: " + tSize);
		
		HashMap<String, CMResult> s_cmresult_bestmatching = new HashMap<String, CMResult>();
		HashMap<String, CMResult> t_cmresult_bestmatching = new HashMap<String, CMResult>();	    
		
		for (int i = 0; i < sClassNodeSet.length; i++) {

			for (int j = 0; j < tClassNodeSet.length; j++) {
//				double classSimilarity = 0.0;
				String sClassURI = sClassNodeSet[i].getURI();
				String tClassURI = tClassNodeSet[j].getURI();
				String sLocalName = sClassNodeSet[i].getLocalName();
				String tLocalName = tClassNodeSet[j].getLocalName();	
			
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
						
						/** test code */
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
						double localNameSimilarity = DescriptionSimilarity.getSimilarity(sClassNodeSet[i], tClassNodeSet[j]);
//						double localNameSimilarity = labelSim.getSimilarity(sLocalName, tLocalName);
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

					// point to current comparing resources/classes.
					this.prepareMatchingResources(sClassURI, tClassURI);

					comparedPathRecorder.pushComparedNodes(sClassURI, tClassURI);
					// matching current comparing resources/classes.
					CMResult cmResult = this.matchClassPair();
//					classSimilarity = cmResult.getClassSimilarity();
					
					
					CMResult source_oldmatching = s_cmresult_bestmatching.get(sClassURI); 
					CMResult target_oldmatching = t_cmresult_bestmatching.get(tClassURI);					
					
					/** test code*/
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
					
					/** test code*/
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
					
					comparedPathRecorder.setCMResult(sClassURI, tClassURI, cmResult);
				}
			}
		}
		
		ArrayList<ClassMapping> classMappingList = new ArrayList<ClassMapping>();
//		ArrayList<String> matchedStr = new ArrayList<String>();
		Set<String> matchingLookupSet = new HashSet<String>();
		Collection<CMResult> source_bestmatching_CMResults = s_cmresult_bestmatching.values(); 
		for (CMResult result : source_bestmatching_CMResults) {
			ClassMapping mapping = new ClassMapping(result);
				matchingLookupSet.add(mapping.toString());
				classMappingList.add(mapping);
		}
		
		Collection<CMResult> target_bestmatching_CMResults = t_cmresult_bestmatching.values(); 
		for (CMResult result : target_bestmatching_CMResults) {
			ClassMapping mapping = new ClassMapping(result);
			if (!matchingLookupSet.contains(mapping.toString())) {
				classMappingList.add(mapping);
			}
		}
		CSMResult csmResult = new CSMResult(classMappingList);
		return csmResult;
	}
	
	/***
	 * match a pair of classes
	 */
	public CMResult matchClassPair() {

		double GSimilarity = 0.0;
		String sLocalName = sModel.getResourceLocalName_curr();
		String tLocalName = tModel.getResourceLocalName_curr();
		String sURI = sModel.getResourceUri_curr();
		String tURI = tModel.getResourceUri_curr();
		double resDescriptionSimilarity = DescriptionSimilarity.getSimilarity(sModel.getOntResourceInfo_curr(), tModel.getOntResourceInfo_curr());
		double hierarchySimilarity = HMather.getSimilairty(sURI, tURI);
//		double hierarchySimilarity = 0.0;
		
//		List<OWLComplexClassDescription> sdescriptions = sModel.listOWLComplexClassDescriptions(sURI);
//		List<OWLComplexClassDescription> tdescriptions = tModel.listOWLComplexClassDescriptions(tURI);
		
//		double owlDescriptionSimilarity = classDescriptionSetMatcher.getComplexClassDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
//		double labelSimilarity = Math.max(hierarchySimilarity, resDescriptionSimilarity);
		double labelSimilarity = 0.0;
		if (hierarchySimilarity == 0) {
			labelSimilarity = resDescriptionSimilarity;
		} else {
			labelSimilarity = 0.3 * hierarchySimilarity + 0.7 * resDescriptionSimilarity;
		}
		
//		int sWordsetSize = labelSim.getWordsetSize(sLocalName);
//		int tWordsetSize = labelSim.getWordsetSize(tLocalName);
		double similarityOfPropertySets = 0.0;

		OntPropertyInfo[] sPropertyset = sModel.listOntPropertiesAsArray();
		OntPropertyInfo[] tPropertyset = tModel.listOntPropertiesAsArray();

		
		// BEGIN TEST SECTION
//		System.out.println(" ");
//		System.out.println(" ");
//		System.out.println("---- Now compare these two classes: " + sLocalName + " and " + tLocalName + "---");
//		System.out.println("The two classes have properties:");
//		System.out.println("\""+sLocalName + "\" has property set of size: " + sPropertyset.length);
//		System.out.println("\""+tLocalName + "\" has property set of size: " + tPropertyset.length);
//		for (OntPropertyInfo pi : sPropertyset) {
//			System.out.println("-> property local name: " + pi.toString() + " has range of size: " + pi.getAllRanges().size());
//		}
//		System.out.println(indent+" ");
//		System.out.println(indent+"\"" + tLocalName + "\" has property set size: " + tPropertyset.length);
//		for (OntPropertyInfo pi : tPropertyset) {
//			System.out.println(indent+"-> property local name: " + pi.toString()+ " has range of size: " + pi.getAllRanges().size());
//		}

//		System.out.println("  ");
//		System.out.println("Property Match Result for class (" + sLocalName + ") and (" + tLocalName + ")");
		
		// END TEST SECTION
		
		MSMResult propertySetsMatchingResult = null;
		
		if (sPropertyset == null || tPropertyset == null) {
			/*
			 * Actually, this should not happen. Because this is caused by the
			 * current resource not found.
			 */
			
			similarityOfPropertySets = 0.0;
			GSimilarity = this.getSimilarityOfClassPair(labelSimilarity, similarityOfPropertySets);
		} else if (sPropertyset.length != 0 && tPropertyset.length != 0) {
			/*
			 * When both classes have properties.
			 */
			
			// Comparing the two properties of the two classes
			propertySetsMatchingResult = this.matchProperties(sPropertyset, tPropertyset);
			similarityOfPropertySets = this.getSimilarityOfPropertySets(propertySetsMatchingResult);
			GSimilarity = this.getSimilarityOfClassPair(labelSimilarity, similarityOfPropertySets);
			
		} else if (sPropertyset.length == 0 && tPropertyset.length == 0){
			/*
			 * When both two classes have NO properties.
			 */
			
			GSimilarity = labelSimilarity;
			
		} else {
			/*
			 * When one of the two classes has no properties
			 */
			
//			GSimilarity = labelSimilarity;
			GSimilarity = this.getSimilarityOfClassPair(labelSimilarity, similarityOfPropertySets);
		}

//		System.out.println("Final Result for class (" + sLocalName + ") and (" + tLocalName + ")");
//		System.out.println("\""+sLocalName + "\" has property set of size: " + sPropertyset.length);
//		System.out.println("\"" + tLocalName + "\" has property set size: " + tPropertyset.length);
//		if (propertySetMatchingResult != null) {
//			ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
//			for (int i = 0; i < mappingList.size(); i++) {
//				SubMapping subMapping = mappingList.get(i);
//
//				System.out.println("|  [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
//						+ subMapping.similarity + ")");
//			}
//		}
//		System.out.println("|  local name similarity: " + localNameSimilarity + "  " + "property similarity: " + propertySimilarity);
//		System.out.println("overall similarity: " + GSimilarity);
		
//		if (owlDescriptionSimilarity == 1.0) {
//			GSimilarity = 0.4 * GSimilarity + 0.6 * owlDescriptionSimilarity;
//		}

		CMResult classMatchingResult = new CMResult(GSimilarity, propertySetsMatchingResult);
		classMatchingResult.setLabelSimilarity(labelSimilarity);
		classMatchingResult.setPropertySimilarity(similarityOfPropertySets);
		classMatchingResult.setSourceClassLocalName(sLocalName);
		classMatchingResult.setTargetClassLocalName(tLocalName);
		classMatchingResult.setSourceClassURI(sURI);
		classMatchingResult.setTargetClassURI(tURI);
		classMatchingResult.setSourcePropertySetSize(sPropertyset.length);
		classMatchingResult.setTargetPropertySetSize(tPropertyset.length);
		classMatchingResult.setIsCompleted(true);
		return classMatchingResult;
	}
	
	public CMResult getClassMatchingResult(String sClassURI, String tClassURI){
		this.prepareMatchingResources(sClassURI, tClassURI);
		return this.matchClassPair();
	}

	/***
	 * compute similarity between the properties of two classes.
	 * 
	 * @param sPropertyNodeSet
	 * @param tPropertyNodeSet
	 * @return
	 */
	public MSMResult matchProperties(OntPropertyInfo[] sPropertyNodeSet, OntPropertyInfo[] tPropertyNodeSet) {

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
		return matchProperties(sPropertyNodeSet, slabelSet, tPropertyNodeSet, tlabelSet);
	}

	/***
	 * This is the core algorithm to compute the similarity between the
	 * properties two classes.
	 * 
	 * @param sPropertyNodeSet
	 * @param sPropertyLabelSet
	 * @param tPropertyNodeSet
	 * @param tPropetyLabelSet
	 * @return
	 */
	private MSMResult matchProperties(OntPropertyInfo[] sPropertyNodeSet, SimilarityMatrixLabel[] sPropertyLabelSet, OntPropertyInfo[] tPropertyNodeSet,
			SimilarityMatrixLabel[] tPropetyLabelSet) {
		
		SimilarityMatrix sm = new SimilarityMatrix(sPropertyNodeSet.length, tPropertyNodeSet.length);

		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			sm.setCol(j, tPropetyLabelSet[j]);
		}

		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			sm.setRow(i, sPropertyLabelSet[i]);

			for (int j = 0; j < tPropertyNodeSet.length; j++) {
				
				double propertySimilarity = 0.0;
				String sourceRangeUri = null;
				String targetRangeUri = null;

				OntResourceInfo[] sRanges = sPropertyNodeSet[i].getAllRangeClassesAsArray();
				OntResourceInfo[] tRanges = tPropertyNodeSet[j].getAllRangeClassesAsArray();
				String sURI =  sPropertyNodeSet[i].getURI();
				String tURI =  tPropertyNodeSet[j].getURI();
				
				// **** This section is for test ***
				// BEGIN TEST SECTION
				
//				System.out.println(" ");
//				System.out.println(" ");
//				System.out.println(indent+"---- Now compare these two properties: " + sPropertyLocalName+" and " + tPropertyLocalName + "---");
//				System.out.println(indent+"\"" + sPropertyLocalName + "\" has range size of: " + sRanges.length);
//				for (OntResourceInfo ci : sRanges) {
//					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
//				}
//
//				System.out.println(" ");
//				System.out.println(indent+"\"" + tPropertyLocalName + "\" has range size of: " + tRanges.length);
//				for (OntResourceInfo ci : tRanges) {
//					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
//				}
				
				// END TEST SECTION

				/**
				 * Dynamic Programming for Label Match.
				 */
//				labelMsmResult = labelSim.getMapping(sPropertyNodeSet[i].getRevisedLocalName(), tPropertyNodeSet[j].getRevisedLocalName());
//				double propertyLabelSimilarity = labelMsmResult.getSimilarity();
				double similarityOfPropertyLabels = labelSim.getSimilarity(sPropertyNodeSet[i].getRevisedLocalName(), tPropertyNodeSet[j].getRevisedLocalName());
//				double similarityOfPropertyLabels = DescriptionSimilarity.getSimilarity(sPropertyNodeSet[i], tPropertyNodeSet[j]);

				double similarityOfRanges = 0.0;
				CSMResult rangeSetsMatchingResult = null;
				if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 && tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
					/*
					 * When both properties have no ranges. It is possible that
					 * both two properties are DatatypeProperty, in which case,
					 * we should also consider the similarity between the types
					 * of two properties.
					 */
					
					propertySimilarity = similarityOfPropertyLabels;
					if (sPropertyNodeSet[i].getPropertyType() == OntPropertyType.DataTypeProperty
							&& tPropertyNodeSet[j].getPropertyType() == OntPropertyType.DataTypeProperty) {
						
						// compare the similarity between two data types
//						if (sPropertyNodeSet[i].getDatatype() != null && tPropertyNodeSet[j].getDatatype() != null) {
//							double datatypeSimlarity = DatatypeMatcher.getSimilarityScore(sPropertyNodeSet[i].getDatatype(), tPropertyNodeSet[j].getDatatype());
//							propertySimilarity = 0.8 * scaledLabelSimilarity + 0.2 * datatypeSimlarity;
//						}
					}
					
				} else if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 || tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
					/*
					 * When only one of the properties has ranges.
					 */
					
					propertySimilarity = similarityOfPropertyLabels;
//					propertySimilarity = this.getPropertyMatchingSimilarity(scaledLabelSimilarity, 0.0, 0);
				} else {
					/*
					 * When both properties have ranges meaning that both
					 * properties are ObjectTypeProperty. In this case, we will
					 * compute the similarity between their ranges (classes).
					 */

					rangeSetsMatchingResult = this.match(sRanges, tRanges);
					
//					if (result != null) {
//						ArrayList<SubMapping> mappingList = result.getSubMappings();
//						for (int l = 0; l < mappingList.size(); l++) {
//							SubMapping subMapping = mappingList.get(l);
//
//							System.out.println("|  [" + l + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
//									+ subMapping.similarity + ")");
//						}
//					}
					
//					List<OWLObjectPropertyDescription> sObjProDesc = sModel.listOWLObjectPropertyDescriptions(sURI);
//					List<OWLObjectPropertyDescription> tObjProDesc = tModel.listOWLObjectPropertyDescriptions(tURI);
				
//					double objProDescSimilarity = objProDescriptionSetMatcher.getObjectPropertyDescriptionSetMappingSimilairty(sObjProDesc, tObjProDesc);
					similarityOfRanges = rangeSetsMatchingResult.getSimilarity();
					propertySimilarity = this.getSimilarityPropertyPair(similarityOfPropertyLabels, similarityOfRanges);
//					propertySimilarity = objProDescSimilarity;
//					System.out.println("property similarity: " + propertySimilarity);
					
//					if (objProDescSimilarity == 1.0) {
//						propertySimilarity = 0.4 * propertySimilarity + 0.6 * objProDescSimilarity;
//					}
				}

//				System.out.println("| property local name similarity: " + scaledLabelSimilarity + "  " + "property ranges similarity: " + scaledRangeSetSimilarity);
//				System.out.println("overall similarity: " + propertySimilarity);
				
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), propertySimilarity);
				cell.setSourceRangeUri(sourceRangeUri);
				cell.setTargetRangeUri(targetRangeUri);
				cell.setSourcePropertyType(sPropertyNodeSet[i].getPropertyType());
				cell.setTargetPropertyType(tPropertyNodeSet[j].getPropertyType());
				cell.setSourceRangeSize(sPropertyNodeSet[i].getAllRangeClasses().size());
				cell.setTargetRangeSize(tPropertyNodeSet[j].getAllRangeClasses().size());
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}
	
	/***
	 * 
	 * @param mappings
	 * @return
	 */
	public List<ClassMapping> pruneMappings(List<ClassMapping> mappings){
		
		System.out.println("in pruneMappings!!");
		
		HashMap<String, ClassMapping> classMappingMap = new HashMap<String, CSMResult.ClassMapping>() ;
		HashMap<String, ClassMapping> matchingLookupMap = new HashMap<String, ClassMapping>();
		for (ClassMapping mapping : mappings) {
			matchingLookupMap.put(mapping.toString(), mapping);
			classMappingMap.put(mapping.toString(), mapping);
		}
		
		for(ClassMapping mapping : mappings){
			String sourceUri = mapping.getSourceClassURI();
			String targetUri = mapping.getTargetClassURI();
			String matchingStr = mapping.toString();
			double matchingScore = mapping.getSimilarity();
			List<OntResourceInfo> sAncestors = sModel.listSuperClasses(sourceUri, false);
			List<OntResourceInfo> tAncestors = tModel.listSuperClasses(targetUri, false);
		
			for (OntResourceInfo sAncestor : sAncestors) {
				String ancMatchingStr = sAncestor.getURI() + "+" + targetUri.trim();
				if (matchingLookupMap.containsKey(ancMatchingStr)) {

					double ancMatchingScore = matchingLookupMap.get(ancMatchingStr).getSimilarity();
					if (classMappingMap.containsKey(matchingStr) && matchingScore < ancMatchingScore) {
						classMappingMap.remove(matchingStr);
					}
				}
			}
			
		
			for (OntResourceInfo tAncestor : tAncestors) {
				String ancMatchingStr = sourceUri.trim() + "+" + tAncestor.getURI();
//				System.out.println(ancMatchingStr);
				if (matchingLookupMap.containsKey(ancMatchingStr)) {

					double ancMatchingScore = matchingLookupMap.get(ancMatchingStr).getSimilarity();
					if (classMappingMap.containsKey(matchingStr) && matchingScore < ancMatchingScore) {
						classMappingMap.remove(matchingStr);
					}
				}
			}

		}
		
		ArrayList<ClassMapping> newMappings = new ArrayList<ClassMapping>(classMappingMap.values());
		Collections.sort(newMappings);
		return newMappings;
		
	}

	/**
	 * compute the overall similarity between two classes. The overall
	 * similarity relies upon the similarity between taxonomic structures of the
	 * two classes and the similarity between properties of the two classes
	 * 
	 * @param taxonomyStructuresSimilarity
	 * @param propertySetsSimilarity
	 * @return the overall similarity between two classes.
	 */
	private double getSimilarityOfClassPair(double taxonomyStructuresSimilarity, double propertySetsSimilarity) {
		if (propertySetsSimilarity >= taxonomyStructuresSimilarity) {
			return 0.8 * propertySetsSimilarity + 0.2 * taxonomyStructuresSimilarity;
		} else {
			return 0.8 * taxonomyStructuresSimilarity + 0.2 * propertySetsSimilarity;
		}
	}

	/***
	 * compute the overall similarity between two properties. The overall
	 * similarity relies upon the similarity between labels of the two
	 * properties and the similarity between ranges of the two properties.
	 * 
	 * @param similarityOfPropertyLabels
	 * @param similarityOfRanges
	 * @return the overall similarity between two properties.
	 */
	private double getSimilarityPropertyPair(double similarityOfPropertyLabels, double similarityOfRanges) {
		if (similarityOfPropertyLabels >= similarityOfRanges) {
			return 0.8 * similarityOfPropertyLabels + 0.2 * similarityOfRanges;
		} else {
			return 0.6 * similarityOfRanges + 0.4 * similarityOfPropertyLabels;
		}
	}
	
	/***
	 * 
	 * @param propertySetsMatchingResult
	 * @return
	 */
	private double getSimilarityOfPropertySets(MSMResult propertySetsMatchingResult ){
		
		if (this.PSMStrategy == PropertySetsMatchingStrategy.STRICT) {
			/*
			 * STRICT level
			 */
			
			return propertySetsMatchingResult.getSimilarity();
			
		} else if (this.PSMStrategy == PropertySetsMatchingStrategy.NORMAL) {
			/*
			 * NORMAL level
			 */
			
			ArrayList<SubMapping> propMatchingPairs = propertySetsMatchingResult.getSubMappings();
			double matchedPropertySimilarity = 0.0;
			float numberOfPairs = propMatchingPairs.size();
			for (int i = 0; i < numberOfPairs; i++) {
				matchedPropertySimilarity += propMatchingPairs.get(i).getSimilarity();
			}
			return matchedPropertySimilarity / numberOfPairs;
			
		} else {
			/*
			 * LENIENT level
			 */
			
			ArrayList<SubMapping> propMatchingPairs = propertySetsMatchingResult.getSubMappings();
			Collections.sort(propMatchingPairs);
			double matchedPropertySimilarity = 0.0;
			float numberOfPairs = propMatchingPairs.size();
			int numberOfMappings_for_computeSimilarity = Math.round(numberOfPairs * this.alpha);
			for (int i = 0; i < numberOfMappings_for_computeSimilarity; i++) {
				matchedPropertySimilarity += propMatchingPairs.get(i).getSimilarity();
			}
			return matchedPropertySimilarity / numberOfMappings_for_computeSimilarity;
		}
		
	}

	/***
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/***
	 * set the strategy for computing the properties of two classes.
	 * 
	 * @param pSMStrategy
	 */
	public void setPropertySetsMatchingStrategy(PropertySetsMatchingStrategy pSMStrategy) {
		PSMStrategy = pSMStrategy;
	}
}
