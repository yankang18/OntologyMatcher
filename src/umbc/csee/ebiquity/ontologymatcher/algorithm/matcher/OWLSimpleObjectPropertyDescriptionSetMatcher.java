package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.List;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription.ObjectPropertyRelationType;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;

public class OWLSimpleObjectPropertyDescriptionSetMatcher {

	private IWordIC labelIC = new BasicWordIC();
	private OWLSimpleClassDescriptionSetMatcher simpleClassDescriptionSetMatcher = new OWLSimpleClassDescriptionSetMatcher();

	public double getSimpleObjectPropertyDescriptionSetMappingSimilairty(List<OWLSimpleObjectPropertyDescription> sDescriptionSet, List<OWLSimpleObjectPropertyDescription> tDescriptionSet) {
		
		int sDesSize = sDescriptionSet.size();
		int tDesSize = tDescriptionSet.size();
		if (sDesSize == 0 || tDesSize == 0) { 
			return 0;
		}
		int maxSize = Math.max(sDesSize, tDesSize);
		OWLSimpleObjectPropertyDescription[] sDesS = new OWLSimpleObjectPropertyDescription[sDesSize];
		OWLSimpleObjectPropertyDescription[] tDesS = new OWLSimpleObjectPropertyDescription[tDesSize];

		MSMResult mapping = this.getSimpleObjectPropertyDescriptionSetMapping(sDescriptionSet.toArray(sDesS), tDescriptionSet.toArray(tDesS));
		List<SubMapping> mappingPairs = mapping.getSubMappings();
		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
		for (int i = 0; i < numberOfPairs; i++) {

//			System.out.println("s rel:   [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t rel:   [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("rel sim: [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}

//		System.out.println("ovl sim: [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;
	}

	private MSMResult getSimpleObjectPropertyDescriptionSetMapping(OWLSimpleObjectPropertyDescription[] sDescriptionSet, OWLSimpleObjectPropertyDescription[] tDescriptionSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sDescriptionSet.length];
		for (int i = 0; i < sDescriptionSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sDescriptionSet[i].getRelationTypeName());
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sDescriptionSet[i].getRelationTypeName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tDescriptionSet.length];
		for (int j = 0; j < tDescriptionSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tDescriptionSet[j].getRelationTypeName());
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tDescriptionSet[j].getRelationTypeName()));
		}
		return getSimpleObjectPropertyDescriptionSetMapping(sDescriptionSet, slabelSet, tDescriptionSet, tlabelSet);
	}

	private MSMResult getSimpleObjectPropertyDescriptionSetMapping(OWLSimpleObjectPropertyDescription[] sDescriptionSet, SimilarityMatrixLabel[] sDescriptionLabelSet, OWLSimpleObjectPropertyDescription[] tDescriptionSet,
			SimilarityMatrixLabel[] tDescriptionLabelSet) {

		SimilarityMatrix sm = new SimilarityMatrix(sDescriptionSet.length, tDescriptionSet.length);

		for (int j = 0; j < tDescriptionSet.length; j++) {
			sm.setCol(j, tDescriptionLabelSet[j]);
		}

		for (int i = 0; i < sDescriptionSet.length; i++) {
			sm.setRow(i, sDescriptionLabelSet[i]);

			for (int j = 0; j < tDescriptionSet.length; j++) {

				double simpleClassExpressionSetSimilarity = 0.0;

				if (sDescriptionSet[i].getRelationType() == tDescriptionSet[j].getRelationType()) {

					if (sDescriptionSet[i].getRelationType() == ObjectPropertyRelationType.Domain
							|| sDescriptionSet[i].getRelationType() == ObjectPropertyRelationType.Range) {
						List<SimpleClassExpression> sClassExpressions = sDescriptionSet[i].getClassExpressions();
						List<SimpleClassExpression> tClassExpressions = tDescriptionSet[j].getClassExpressions();
						simpleClassExpressionSetSimilarity = simpleClassDescriptionSetMatcher.getSimpleClassExpressionSetMatchingSimilarity(sClassExpressions, tClassExpressions);
					} else {
						
						/*
						 * if the relation type is SuperProperty and
						 * InverseProperty
						 */
						List<String> sRelatedObjPros = sDescriptionSet[i].getRelatedProperties();
						List<String> tRelatedObjPros = tDescriptionSet[j].getRelatedProperties();
						
//						StringBuilder sRelatedObjProsBuilder = new StringBuilder();
//						for (String p : sRelatedObjPros) {
//							sRelatedObjProsBuilder.append(p.toLowerCase() + " ");
//						}
//						StringBuilder tRelatedObjProsBuilder = new StringBuilder();
//						for (String p : tRelatedObjPros) {
//							tRelatedObjProsBuilder.append(p.toLowerCase() + " ");
//						}
//
//						int sSize = sRelatedObjPros.size();
//						int tSize = tRelatedObjPros.size();
//						int maxSize = Math.max(sSize, tSize);
						simpleClassExpressionSetSimilarity = simpleClassDescriptionSetMatcher.getStringSetSimilarity(sRelatedObjPros, tRelatedObjPros);
//						simpleClassExpressionSetSimilarity = simpleClassDescriptionSetMatcher.getLabelSimilarity(sRelatedObjProsBuilder.toString(), tRelatedObjProsBuilder.toString(), maxSize);
					}
				}
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), simpleClassExpressionSetSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}
}
