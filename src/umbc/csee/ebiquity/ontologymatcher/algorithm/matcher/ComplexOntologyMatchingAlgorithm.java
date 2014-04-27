package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ComparedPathRecorder;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ILabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;


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
public class ComplexOntologyMatchingAlgorithm {

	private ResourceModel sModel;
	private ResourceModel tModel;

	private ComparedPathRecorder comparedPathRecorder = new ComparedPathRecorder();
	private StringBuilder matchingResultsRecorder = new StringBuilder();
	
	private ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
	private IWordIC labelIC = new BasicWordIC();
	private int depth;

//	private static final double threshold = 0.4;

	public ComplexOntologyMatchingAlgorithm(ResourceModel sr, ResourceModel tr) {
		this.sModel = sr;
		this.tModel = tr;
	}
	
	private void prepareMatchingResources(String sURI, String tURI) {
		
		try {
			sModel.prepareResource(sURI);
			tModel.prepareResource(tURI);
			matchingResultsRecorder = new StringBuilder();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	
	/***
	 * To find the best matching of top level classes between two ontologies. A ontology is represented by a ResourceModel.
	 * @return MSMResult.
	 */
	public MSMResult getMapping(){
		List<OntClassInfo> sClassList = sModel.listTopLevelClasses();
		List<OntClassInfo> tClassList = tModel.listTopLevelClasses();
		return this.getMapping(sClassList.toArray(new OntResourceInfo[0]), tClassList.toArray(new OntResourceInfo[0]));
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
	 * To find the best matching of two sets of classes from different two
	 * ontologies. A ontology is represented by a ResourceModel.
	 * 
	 * @param sClassNodeSet class set from the source model.
	 * @param tClassNodeSet class set from the target model
	 * @return
	 */
	private MSMResult getMapping(OntResourceInfo[] sClassNodeSet, OntResourceInfo[] tClassNodeSet) {
		
		SimilarityMatrixLabel[] sClassLabelSet = new SimilarityMatrixLabel[sClassNodeSet.length];
		for (int i = 0; i < sClassNodeSet.length; i++) {
			sClassLabelSet[i] = new SimilarityMatrixLabel(sClassNodeSet[i].getLocalName());
			sClassLabelSet[i].setURI(sClassNodeSet[i].getURI());
			sClassLabelSet[i].setIC(labelIC.getIC(sClassNodeSet[i].getLocalName()));
		}

		SimilarityMatrixLabel[] tClassLabelSet = new SimilarityMatrixLabel[tClassNodeSet.length];
		for (int j = 0; j < tClassNodeSet.length; j++) {
			tClassLabelSet[j] = new SimilarityMatrixLabel(tClassNodeSet[j].getLocalName());
			tClassLabelSet[j].setURI(tClassNodeSet[j].getURI());
			tClassLabelSet[j].setIC(labelIC.getIC(tClassNodeSet[j].getLocalName()));
		}
		return getMappingByDP(sClassNodeSet, sClassLabelSet, tClassNodeSet, tClassLabelSet);
	}
	
	/***
	 * This is the core algorithm (Dynamic Programming) to find the best matching of two sets of classes
	 * from different two ontologies. A ontology is represented by a ResourceModel.
	 * 
	 * @return MSMResult.
	 */
	private MSMResult getMappingByDP(OntResourceInfo[] sClassNodeSet, SimilarityMatrixLabel[] sClassLabelSet, OntResourceInfo[] tClassNodeSet,
			SimilarityMatrixLabel[] tClassLabelSet) {
		/**
		 * Dynamic Programming for Class Match
		 */
		
		String indent = this.getIndent(depth);
		SimilarityMatrix sm = new SimilarityMatrix(sClassNodeSet.length, tClassNodeSet.length);
		
		for (int j = 0; j < tClassNodeSet.length; j++) {
			sm.setCol(j, tClassLabelSet[j]);
		}
		
		for (int i = 0; i < sClassNodeSet.length; i++) {
			sm.setRow(i, sClassLabelSet[i]);

			for (int j = 0; j < tClassNodeSet.length; j++) {
				double classSimilarity = 0.0;
				
				
				String sClassURI = sClassNodeSet[i].getURI();
				String tClassURI = tClassNodeSet[j].getURI();
				
				
//				System.out.println("@@" + sClassURI + " : " + tClassURI);
				String sLocalName = sClassNodeSet[i].getLocalName();
				String tLocalName = tClassNodeSet[j].getLocalName();				
			
				if (comparedPathRecorder.isCompared(sClassURI, tClassURI)) {
					// System.out.println(indent+"@@ in OntClassSimilarity.getMapping() is compared");
					Double similarity = comparedPathRecorder.getValue(sClassURI, tClassURI);

					if (similarity != null) {
						
						SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), similarity);
						sm.setCellAt(i, j, cell);
						
					} else {
						// here when two classes have already been compared. We
						// set the similarity value equal to label similarity
						// between the two class. Actually this is not the best
						// decision. Therefore, this value should
						// be changed to something more reasonable.
						
						double localNameSimilarity = labelSim.getSimilarity(sLocalName, tLocalName);
						SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), localNameSimilarity);
						sm.setCellAt(i, j, cell);
					}

				} else {

				
//					System.out.println(indent+"@@ in OntClassSimilarity.getMapping() NOT compared");
//					System.out.println(indent+"---- Now compare these two classes: " + "(" + i + ")" + sLocalName + " and " + "(" + j + ")" + tLocalName
//							+ "---");
					
					// point to current comparing resources/classes.
					this.prepareMatchingResources(sClassURI, tClassURI);

					comparedPathRecorder.pushComparedNodes(sModel.getResourceUri_curr(), tModel.getResourceUri_curr());
					// matching current comparing resources/classes.
					classSimilarity = this.ClassMatching().getClassSimilarity();
					
//					System.out.println("@@@@" + sClassURI + " : " + tClassURI + " with similarity: " + classSimilarity);
					comparedPathRecorder.setValue(sClassURI, tClassURI, classSimilarity);
					


					SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), classSimilarity);
					sm.setCellAt(i, j, cell);
				}

			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
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
	private CMResult ClassMatching() {

		double GSimilarity = 0.0;
		String sLocalName = sModel.getResourceLocalName_curr();
		String tLocalName = tModel.getResourceLocalName_curr();
		String sURI = sModel.getResourceUri_curr();
		String tURI = sModel.getResourceUri_curr();

//		String indent = this.getIndent(depth);

		double localNameSimilarity = labelSim.getSimilarity(sLocalName, tLocalName);
		double propertySimilarity = 0.0;
//		matchingResultsRecorder.append(indent + "Class Mapping: " + sLocalName + ":" + tLocalName + " [similarity: " + localNameSimilarity + "] \n");
//		matchingResultsRecorder.append(indent + "Class Mapping: " + sModel.getResourceUri() + ":" + tModel.getResourceUri() + " [similarity: " + localNameSimilarity + "] \n");
		OntPropertyInfo[] sPropertyset = sModel.listOntClassPropertiesAsArray();
		OntPropertyInfo[] tPropertyset = tModel.listOntClassPropertiesAsArray();

//		System.out.println(" ");
//		System.out.println(" ");
//		System.out.println("---- Now compare these two classes: " + sLocalName + " and " + tLocalName + "---");
//		System.out.println(indent+"The two classes have properties:");
//		System.out.println(indent+"\""+sLocalName + "\" has property set of size: " + sPropertyset.length);
//		for (OntPropertyInfo pi : sPropertyset) {
//			System.out.println(indent+"-> property local name: " + pi.toString() + " has range of size: " + pi.getAllRanges().size());
//		}
//		
//		System.out.println(indent+" ");
//		System.out.println(indent+"\"" + tLocalName + "\" has property set size: " + tPropertyset.length);
//		for (OntPropertyInfo pi : tPropertyset) {
//			System.out.println(indent+"-> property local name: " + pi.toString()+ " has range of size: " + pi.getAllRanges().size());
//		}

		System.out.println("  ");
		System.out.println("Property Match Result for class (" + sLocalName + ") and (" + tLocalName + ")");
		
		MSMResult propertySetMatchingResult = null;
		if (sPropertyset.length != 0 && tPropertyset.length != 0) {
			// when Both source Resource/Class and target Resource/Class have
			// properties

			int sPropertySize = sPropertyset.length;
			int tPropertySize = tPropertyset.length;
			
			int avePropertySize = (sPropertySize + tPropertySize) / 2;
			int matchedPropertySize = 0;
			double matchedPropertySimilarity = 0.0;
			

			// Comparing the two property sets of current comparing resources/classes. 
			propertySetMatchingResult = this.getPropertySetMapping(sPropertyset, tPropertyset);
			
			this.setDepth(depth);

			
			ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
			for (int i = 0; i < mappingList.size(); i++) {
				SubMapping subMapping = mappingList.get(i);

//				System.out.println(indent+"|  [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
//				+ subMapping.similarity + ")");
				
				// When the similarity between two properties is equal and
				// bigger than the threshold, then two properties are matched.
//				if (subMapping.similarity >= threshold) {
					
					matchedPropertySize++;
					matchedPropertySimilarity += subMapping.getSimilarity();
					
					System.out.println("* PropertyMatched: [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
							+ subMapping.getSimilarity() + ")");
					matchingResultsRecorder.append( " - Matched Property: " + subMapping.s.getLocalName() + " : "
							+ subMapping.t.getLocalName() + " [similarity: " + subMapping.getSimilarity() + "] \n");


//				}
			}
				
			propertySimilarity = matchedPropertySimilarity / avePropertySize;
//			propertySimilarity = matchedPropertySimilarity / matchedPropertySize;
			GSimilarity = this.getGraphSimilarity(localNameSimilarity, propertySimilarity);
			
		} else if (sPropertyset.length == 0 && tPropertyset.length == 0){
			// when Both source Resource/Class and target Resource/Class have NO properties.
//			propertySimilarity = 1.0;
			GSimilarity = localNameSimilarity;
			
		} else {
			
			// when either the source Resource/Class or target Resource/Class have properties.
			GSimilarity = this.getGraphSimilarity(localNameSimilarity, 0.0);
		}

//		matchingResultsRecorder.append("local name similarity: " + localNameSimilarity+ "  " + "property similarity: " + propertySimilarity +" \n");
		
		

		System.out.println("Final Result for class (" + sLocalName + ") and (" + tLocalName + ")");
		System.out.println("\""+sLocalName + "\" has property set of size: " + sPropertyset.length);
		System.out.println("\"" + tLocalName + "\" has property set size: " + tPropertyset.length);

		if (propertySetMatchingResult != null) {
			ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
			for (int i = 0; i < mappingList.size(); i++) {
				SubMapping subMapping = mappingList.get(i);

				System.out.println("|  [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
						+ subMapping.getSimilarity() + ")");
			}
		}
		System.out.println("|  local name similarity: " + localNameSimilarity + "  " + "property similarity: " + propertySimilarity);
		System.out.println("overall similarity: " + GSimilarity);
		
		CMResult classMatchingResult = new CMResult(GSimilarity, propertySetMatchingResult);
		classMatchingResult.setSourceClassLocalName(sLocalName);
		classMatchingResult.setTargetClassLocalName(tLocalName);
		classMatchingResult.setSourceClassURI(sURI);
		classMatchingResult.setTargetClassURI(tURI);
		classMatchingResult.setSourcePropertySetSize(sPropertyset.length);
		classMatchingResult.setTargetPropertySetSize(tPropertyset.length);
		return classMatchingResult;
	}
	
	
	public CMResult getClassMatchingResult(String sClassURI, String tClassURI){
		this.prepareMatchingResources(sClassURI, tClassURI);
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
	 * This method is used to compute the similarity between the property sets of two resources (now classes).
	 * @param sPropertyNodeSet
	 * @param tPropertyNodeSet
	 * @return 
	 */
	private MSMResult getPropertySetMapping(OntPropertyInfo[] sPropertyNodeSet, OntPropertyInfo[] tPropertyNodeSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sPropertyNodeSet.length];
		for (int i = 0; i < sPropertyNodeSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sPropertyNodeSet[i].getLocalName());
			slabelSet[i].setURI(sPropertyNodeSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sPropertyNodeSet[i].getLocalName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tPropertyNodeSet.length];
		for (int j = 0; j < tPropertyNodeSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tPropertyNodeSet[j].getLocalName());
			tlabelSet[j].setURI(tPropertyNodeSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tPropertyNodeSet[j].getLocalName()));
		}
		return getPropertySetMapping(sPropertyNodeSet, slabelSet, tPropertyNodeSet, tlabelSet);
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
	private MSMResult getPropertySetMapping(OntPropertyInfo[] sPropertyNodeSet, SimilarityMatrixLabel[] sPropertyLabelSet, OntPropertyInfo[] tPropertyNodeSet,
			SimilarityMatrixLabel[] tPropetyLabelSet) {
		
		String indent = this.getIndent(depth);
		
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

				MSMResult labelMsmResult;

				OntResourceInfo[] sRanges = sPropertyNodeSet[i].getAllRangeClassesAsArray();
				OntResourceInfo[] tRanges = tPropertyNodeSet[j].getAllRangeClassesAsArray();
				String sPropertyLocalName =  sPropertyNodeSet[i].getLocalName();
				String tPropertyLocalName =  tPropertyNodeSet[j].getLocalName();
				
//				System.out.println(" ");
				System.out.println(" ");
				System.out.println(indent+"---- Now compare these two properties: " + sPropertyLocalName+" and " + tPropertyLocalName + "---");
				System.out.println(indent+"\"" + sPropertyLocalName + "\" has range size of: " + sRanges.length);
				for (OntResourceInfo ci : sRanges) {
					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
				}

				System.out.println(" ");
				System.out.println(indent+"\"" + tPropertyLocalName + "\" has range size of: " + tRanges.length);
				for (OntResourceInfo ci : tRanges) {
					System.out.println(indent+"-> range class local name: " + ci.getLocalName());
				}

				/**
				 * Dynamic Programming for Label Match.
				 */
				labelMsmResult = labelSim.getMapping(sPropertyNodeSet[i].getRevisedLocalName(), tPropertyNodeSet[j].getRevisedLocalName());
				double propertyLabelSimilarity = labelMsmResult.getSimilarity();
				double scaledLabelSimilarity = this.scaleLabelSimilarity(propertyLabelSimilarity);

				double scaledRangeSetSimilarity = 0.0;
				MSMResult result = null;
				if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 && tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
					// When both resources/classes have no ranges. It is
					// possible that both two properties are DatatypeProperty,
					// in which case, Therefore, we should also consider the
					// similarity between the types of two properties.

					propertySimilarity = scaledLabelSimilarity;
					
				} else if (sPropertyNodeSet[i].getAllRangeClasses().size() == 0 || tPropertyNodeSet[j].getAllRangeClasses().size() == 0) {
					
					// When only one resources/classes has ranges.
					propertySimilarity = this.getPropertyMatchingSimilarity(scaledLabelSimilarity, 0.0, 0);
				} else {
					// When both resources/classes have ranges.
					
					result = this.getMapping(sRanges, tRanges);
					this.setDepth(depth + 1);
					
					if (result != null) {
						ArrayList<SubMapping> mappingList = result.getSubMappings();
						for (int l = 0; l < mappingList.size(); l++) {
							SubMapping subMapping = mappingList.get(l);

							System.out.println("|  [" + l + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " ("
									+ subMapping.getSimilarity() + ")");
						}
					}
					
					scaledRangeSetSimilarity = this.scaleRangeSetSimilarity(result.getSimilarity());
					propertySimilarity = this.getPropertyMatchingSimilarity(scaledLabelSimilarity, scaledRangeSetSimilarity, 0);
				}

				System.out.println("| property local name similarity: " + scaledLabelSimilarity + "  " + "property ranges similarity: " + scaledRangeSetSimilarity);
				System.out.println("overall similarity: " + propertySimilarity);
				
				
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

	private double scaleLabelSimilarity(double labelSimilarity) {

		// double scaleValue = 0.8 * Math.sqrt(1 - Math.pow(labelSimilarity -
		// 1.0, 2));
		double scaleValue = labelSimilarity;
		return scaleValue;
	}

	private double scaleRangeSetSimilarity(double rangeSetSimilarity) {

		// double scaleValue = Math.log(rangeSetSimilarity + alpha) /
		// Math.log(2);
		double scaleValue = rangeSetSimilarity;
		return scaleValue;

	}

	/**
	 * compute the overall similarity between two classes. The overall
	 * similarity relies upon the Label (local name) similarity between the two
	 * classes and the property set similarity between the two classes
	 * 
	 * @param localNameSimilarity
	 * @param propertySimilarity
	 * @return the overall similarity between the comparing classes.
	 */
	private double getGraphSimilarity(double localNameSimilarity, double propertySimilarity) {
		if (propertySimilarity >= localNameSimilarity) {
			return 0.8 * propertySimilarity + 0.2 * localNameSimilarity;
		} else {
			return 0.7 * localNameSimilarity + 0.3 * propertySimilarity;
		}
	}

	/***
	 * 
	 * @param propertyLabelMatchingSimilarity
	 * @param subGSimilarity
	 * @param depth
	 * @return
	 */
	private double getPropertyMatchingSimilarity(double propertyLabelMatchingSimilarity, double subGSimilarity, int depth) {
		
		if (propertyLabelMatchingSimilarity >= subGSimilarity) {
			return 0.8 * propertyLabelMatchingSimilarity + 0.2 * subGSimilarity;
		} else {
			return 0.7 * subGSimilarity + 0.3 * propertyLabelMatchingSimilarity;
		}
	}

	private String getIndent(int depth) {
//		StringBuilder indentBuilder = new StringBuilder();
//		for (int i = 0; i < depth; i++) {
//			indentBuilder.append("    ");
//		}
//		return indentBuilder.toString();
		return "";
	}

	public String getMatchingInfo() {
		return this.matchingResultsRecorder.toString();
	}
}
