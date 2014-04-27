package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription.ObjectPropertyRelationType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

public class OWLObjectPropertyDescriptionSetMatcher {
	
	private IWordIC labelIC = new BasicWordIC();
	private OWLClassDescriptionSetMatcher classDescriptionSetMatcher;
	private OWLSimpleObjectPropertyDescriptionSetMatcher simpleObjectPropertyDescMatcher;
	private ResourceModel sResModel;
	private ResourceModel tResModel;

	public OWLObjectPropertyDescriptionSetMatcher(ResourceModel sM, ResourceModel tM) {
		this.sResModel = sM;
		this.tResModel = tM;
		this.classDescriptionSetMatcher = new OWLClassDescriptionSetMatcher(sM, tM);
		this.simpleObjectPropertyDescMatcher = new OWLSimpleObjectPropertyDescriptionSetMatcher();
	}

	public double getObjectPropertyDescriptionSetMappingSimilairty(List<OWLObjectPropertyDescription> sDescriptionSet, List<OWLObjectPropertyDescription> tDescriptionSet) {
		int sDesSize = sDescriptionSet.size();
		int tDesSize = tDescriptionSet.size();
		if (sDesSize == 0 || tDesSize == 0) {
			return 0;
		}
		OWLObjectPropertyDescription[] sDesS = new OWLObjectPropertyDescription[sDesSize];
		OWLObjectPropertyDescription[] tDesS = new OWLObjectPropertyDescription[tDesSize];
		return this.getObjectPropertyDescriptionSetMappingSimilairty(sDescriptionSet.toArray(sDesS), tDescriptionSet.toArray(tDesS));
	}

	public double getObjectPropertyDescriptionSetMappingSimilairty(OWLObjectPropertyDescription[] sDescriptionSet, OWLObjectPropertyDescription[] tDescriptionSet) {

		int sDesSize = sDescriptionSet.length;
		int tDesSize = tDescriptionSet.length;
		int maxSize = Math.max(sDesSize, tDesSize);
		MSMResult mapping = this.getDescriptionSetMapping(sDescriptionSet, tDescriptionSet);
		return this.getMatchingPairsOverallSimilarity1(mapping.getSubMappings(), maxSize);
	}
	
	   /***
     * 
     * @param sDescriptionSet
     * @param tDescriptionSet
     * @return
     */
	private MSMResult getDescriptionSetMapping(OWLObjectPropertyDescription[] sDescriptionSet, OWLObjectPropertyDescription[] tDescriptionSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sDescriptionSet.length];
		for (int i = 0; i < sDescriptionSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sDescriptionSet[i].getRelationTypeName());
			slabelSet[i].setIC(labelIC.getIC(sDescriptionSet[i].getRelationTypeName()));
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tDescriptionSet.length];
		for (int j = 0; j < tDescriptionSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tDescriptionSet[j].getRelationTypeName());
			tlabelSet[j].setIC(labelIC.getIC(tDescriptionSet[j].getRelationTypeName()));
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
		}
		
		SimilarityMatrix sm = new SimilarityMatrix(sDescriptionSet.length, tDescriptionSet.length);
		for (int j = 0; j < tDescriptionSet.length; j++) {
			sm.setCol(j, tlabelSet[j]);
		}

		for (int i = 0; i < sDescriptionSet.length; i++) {
			sm.setRow(i, slabelSet[i]);

			for (int j = 0; j < tDescriptionSet.length; j++) {

//				System.out.println("<" + i + "><" + j + ">");
				double OWLObjectPropertyDescriptionSimilarity = 0.0;
				if (sDescriptionSet[i].getRelationType() == tDescriptionSet[j].getRelationType()) {
//					System.out.println("r1: " + sDescriptionSet[i].getClassRelationName());
//					System.out.println("r2: " + tDescriptionSet[j].getClassRelationName());
					if (sDescriptionSet[i].getRelationType() == ObjectPropertyRelationType.Domain
							|| sDescriptionSet[i].getRelationType() == ObjectPropertyRelationType.Range) {
						List<ClassExpression> sClassExpressions = sDescriptionSet[i].getClassExpressions();
						List<ClassExpression> tClassExpressions = tDescriptionSet[j].getClassExpressions();
						OWLObjectPropertyDescriptionSimilarity = classDescriptionSetMatcher.getClassExpressionSetSimilarity(sClassExpressions, tClassExpressions);
					} else {
						// if the relation type is SuperProperty and InverseProperty
						List<OntResourceInfo> sObjProDesc = sDescriptionSet[i].getRelatedProperties();
						List<OntResourceInfo> tObjProDesc = tDescriptionSet[j].getRelatedProperties();
						OWLObjectPropertyDescriptionSimilarity = this.getRelatedPropertiesSetSimilarity(sObjProDesc, tObjProDesc);
					}
				}
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), OWLObjectPropertyDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		return msm.getMapping(sm);
	}

	private double getRelatedPropertiesSetSimilarity(List<OntResourceInfo> sObjProDesc, List<OntResourceInfo> tObjProDesc) {
		int sSetSize = sObjProDesc.size();
		int tSetSize = tObjProDesc.size();
		if (sSetSize == 0 || tSetSize == 0) {
			return 0;
		}
		int maxSize = Math.max(sSetSize, tSetSize);

		OntResourceInfo[] sObjProDescS = sObjProDesc.toArray(new OntResourceInfo[sSetSize]);
		OntResourceInfo[] tObjProDescS = tObjProDesc.toArray(new OntResourceInfo[tSetSize]);

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sObjProDescS.length];
		for (int i = 0; i < sObjProDescS.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sObjProDescS[i].getLocalName());
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sObjProDescS[i].getLocalName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tObjProDescS.length];
		for (int j = 0; j < tObjProDescS.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tObjProDescS[j].getLocalName());
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tObjProDescS[j].getLocalName()));
		}

		SimilarityMatrix sm = new SimilarityMatrix(sObjProDescS.length, tObjProDescS.length);

		for (int j = 0; j < tObjProDescS.length; j++) {
			sm.setCol(j, tlabelSet[j]);
		}

		for (int i = 0; i < sObjProDescS.length; i++) {
			sm.setRow(i, slabelSet[i]);

			for (int j = 0; j < tObjProDescS.length; j++) {

				double OWLDescriptionSimilarity = 0.0;
				List<OWLSimpleObjectPropertyDescription> sSimpleClassExp = sResModel.listOWLSimpleObjectPropertyDescriptions(sObjProDescS[i].getURI());
				List<OWLSimpleObjectPropertyDescription> tSimpleClassExp = tResModel.listOWLSimpleObjectPropertyDescriptions(tObjProDescS[j].getURI());
				OWLDescriptionSimilarity = this.simpleObjectPropertyDescMatcher.getSimpleObjectPropertyDescriptionSetMappingSimilairty(sSimpleClassExp, tSimpleClassExp);
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), OWLDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult = msm.getMapping(sm);
		return this.getMatchingPairsOverallSimilarity2(simResult.getSubMappings(), maxSize);
	}

	
	/*
	 * NOTE: Here using two exact the same getMatchingPairsOverallSimilarity methods just for testing purpose.
	 */
	
	private double getMatchingPairsOverallSimilarity1(ArrayList<SubMapping> mappingPairs, int maxSize) {

		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
//		System.out.println("The First Level for Property <Relation> Matching");
		for (int i = 0; i < numberOfPairs; i++) {
//			System.out.println("s p rel: [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t p rel: [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("rel sim: [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}

//		System.out.println("ovl sim: [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;
	}

	private double getMatchingPairsOverallSimilarity2(ArrayList<SubMapping> mappingPairs, int maxSize) {

		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
//		System.out.println("The Second Level for Property Matching");
		for (int i = 0; i < numberOfPairs; i++) {
//			System.out.println("s p:     [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t p:     [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("p sim:   [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}

//		System.out.println("ovl sim: [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;
	}
}
