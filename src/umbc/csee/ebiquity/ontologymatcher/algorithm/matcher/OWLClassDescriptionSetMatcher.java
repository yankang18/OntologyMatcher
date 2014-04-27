package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;
import java.util.List;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression.ClassExpressionType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

public class OWLClassDescriptionSetMatcher {

	private IWordIC labelIC = new BasicWordIC();
	private OWLSimpleClassDescriptionSetMatcher simpleDescriptionSetMatcher = new OWLSimpleClassDescriptionSetMatcher();
	private ResourceModel sResModel;
	private ResourceModel tResModel;

	public OWLClassDescriptionSetMatcher(ResourceModel sM, ResourceModel tM) {
		this.sResModel = sM;
		this.tResModel = tM;
	}

	public double getComplexClassDescriptionSetMappingSimilairty(List<OWLComplexClassDescription> sDescriptionSet, List<OWLComplexClassDescription> tDescriptionSet) {
		int sDesSize = sDescriptionSet.size();
		int tDesSize = tDescriptionSet.size();
		if (sDesSize == 0 || tDesSize == 0) {
			return 0;
		}
		OWLComplexClassDescription[] sDesS = new OWLComplexClassDescription[sDesSize];
		OWLComplexClassDescription[] tDesS = new OWLComplexClassDescription[tDesSize];
		return this.getComplexClassDescriptionSetMappingSimilairty(sDescriptionSet.toArray(sDesS), tDescriptionSet.toArray(tDesS));
	}

	public double getComplexClassDescriptionSetMappingSimilairty(OWLComplexClassDescription[] sDescriptionSet, OWLComplexClassDescription[] tDescriptionSet) {

		int sDesSize = sDescriptionSet.length;
		int tDesSize = tDescriptionSet.length;
		int maxSize = Math.max(sDesSize, tDesSize);
		MSMResult mapping = this.getDescriptionSetMapping(sDescriptionSet, tDescriptionSet);
		return this.getMatchingPairsOverallSimilarity(mapping.getSubMappings(), maxSize);
	}

    /***
     * 
     * @param sDescriptionSet
     * @param tDescriptionSet
     * @return
     */
	private MSMResult getDescriptionSetMapping(OWLComplexClassDescription[] sDescriptionSet, OWLComplexClassDescription[] tDescriptionSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sDescriptionSet.length];
		for (int i = 0; i < sDescriptionSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sDescriptionSet[i].getClassRelationName());
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sDescriptionSet[i].getClassRelationName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tDescriptionSet.length];
		for (int j = 0; j < tDescriptionSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tDescriptionSet[j].getClassRelationName());
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tDescriptionSet[j].getClassRelationName()));
		}
		return getDescriptionMapping(sDescriptionSet, slabelSet, tDescriptionSet, tlabelSet);
	}

	/***
	 * This is the core algorithm to compute the similarity between the property
	 * sets of two resources (now classes).
	 * 
	 * @param sResourceSet
	 * @param sResourceLabelSet
	 * @param tResourceSet
	 * @param tResourcdLabelSet
	 * @return
	 */
	private MSMResult getDescriptionMapping(OWLComplexClassDescription[] sDescriptionSet, SimilarityMatrixLabel[] sDescriptionLabelSet, OWLComplexClassDescription[] tDescriptionSet,
			SimilarityMatrixLabel[] tDescriptionLabelSet) {
		
		SimilarityMatrix sm = new SimilarityMatrix(sDescriptionSet.length, tDescriptionSet.length);
		for (int j = 0; j < tDescriptionSet.length; j++) {
			sm.setCol(j, tDescriptionLabelSet[j]);
		}

		for (int i = 0; i < sDescriptionSet.length; i++) {
			sm.setRow(i, sDescriptionLabelSet[i]);

			for (int j = 0; j < tDescriptionSet.length; j++) {

				double OWLDescriptionSimilarity = 0.0;

				if (sDescriptionSet[i].getClassRelationName() == tDescriptionSet[j].getClassRelationName()) {
//					System.out.println("r1: " + sDescriptionSet[i].getClassRelationName());
//					System.out.println("r2: " + tDescriptionSet[j].getClassRelationName());
					List<ClassExpression> sClassExpressions = sDescriptionSet[i].getClassExpressions();
					List<ClassExpression> tClassExpressions = tDescriptionSet[j].getClassExpressions();

					OWLDescriptionSimilarity = this.getClassExpressionSetSimilarity(sClassExpressions, tClassExpressions);
				}

				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), OWLDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult = msm.getMapping(sm);
		return simResult;
	}

	public double getClassExpressionSetSimilarity(List<ClassExpression> sClassExpressions, List<ClassExpression> tClassExpressions) {

		int sExpSize = sClassExpressions.size();
		int tExpSize = tClassExpressions.size();
		if (sExpSize == 0 || tExpSize == 0) {
			return 0;
		}
		int maxSize = Math.max(sExpSize, tExpSize);

		ClassExpression[] sExpS = new ClassExpression[sExpSize];
		ClassExpression[] tExpS = new ClassExpression[tExpSize];

		MSMResult simResult = this.getClassExpressionSetMapping(sClassExpressions.toArray(sExpS), tClassExpressions.toArray(tExpS));
		return this.getMatchingPairsOverallSimilarity(simResult.getSubMappings(), maxSize);
	}

	private MSMResult getClassExpressionSetMapping(ClassExpression[] sClassExpressionSet, ClassExpression[] tClassExpressionSet) {

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sClassExpressionSet.length];
		for (int i = 0; i < sClassExpressionSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sClassExpressionSet[i].getClassExpressionTypeName());
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sClassExpressionSet[i].getClassExpressionTypeName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tClassExpressionSet.length];
		for (int j = 0; j < tClassExpressionSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tClassExpressionSet[j].getClassExpressionTypeName());
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tClassExpressionSet[j].getClassExpressionTypeName()));
		}
		return getClassExpressionSetMapping(sClassExpressionSet, slabelSet, tClassExpressionSet, tlabelSet);
	}

	private MSMResult getClassExpressionSetMapping(ClassExpression[] sExpressionSet, SimilarityMatrixLabel[] sDescriptionLabelSet, ClassExpression[] tExpressionSet,
			SimilarityMatrixLabel[] tDescriptionLabelSet) {

		SimilarityMatrix sm = new SimilarityMatrix(sExpressionSet.length, tExpressionSet.length);

		for (int j = 0; j < tExpressionSet.length; j++) {
			sm.setCol(j, tDescriptionLabelSet[j]);
		}

		for (int i = 0; i < sExpressionSet.length; i++) {
			sm.setRow(i, sDescriptionLabelSet[i]);

			for (int j = 0; j < tExpressionSet.length; j++) {

				double OWLDescriptionSimilarity = 0.0;

				if (sExpressionSet[i].getClassExpressionType() == tExpressionSet[j].getClassExpressionType()) {

					ClassExpressionType type = sExpressionSet[i].getClassExpressionType();
					// System.out.println("Complex Class Expression ->" +
					// sExpressionSet[i].getType());
					if (type == ClassExpressionType.Other || type == ClassExpressionType.AllValuesFromOther || type == ClassExpressionType.SomeValuesFromOther) {
						OWLDescriptionSimilarity = 0.0;
					} else if (type == ClassExpressionType.Cardinality || type == ClassExpressionType.MaxCardinality || type == ClassExpressionType.MinCardinality) {
						if (sExpressionSet[i].getCardinality() == tExpressionSet[j].getCardinality()) {
							OWLDescriptionSimilarity = 1.0;
						}
					} else if (type == ClassExpressionType.HasValueFrom) {
						OWLDescriptionSimilarity = 1.0;
					} else {
						List<OntResourceInfo> sNamedClasses = sExpressionSet[i].getNamedClassList();
						List<OntResourceInfo> tNamedClasses = tExpressionSet[j].getNamedClassList();
						OWLDescriptionSimilarity = this.getNamedClassSetSimilarity(sNamedClasses, tNamedClasses);

					}
				}

				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), OWLDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}

	private double getNamedClassSetSimilarity(List<OntResourceInfo> sNamedClasses, List<OntResourceInfo> tNamedClasses) {

		int sSetSize = sNamedClasses.size();
		int tSetSize = tNamedClasses.size();
		if (sSetSize == 0 || tSetSize == 0) {
			return 0;
		}
		int maxSize = Math.max(sSetSize, tSetSize);

		OntResourceInfo[] sNamedClassS = sNamedClasses.toArray(new OntResourceInfo[sSetSize]);
		OntResourceInfo[] tNamedClassS = tNamedClasses.toArray(new OntResourceInfo[tSetSize]);

		SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sNamedClassS.length];
		for (int i = 0; i < sNamedClassS.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sNamedClassS[i].getLocalName());
			// slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			// slabelSet[i].setUri(sDescriptionSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sNamedClassS[i].getLocalName()));
		}

		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tNamedClassS.length];
		for (int j = 0; j < tNamedClassS.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tNamedClassS[j].getLocalName());
			// tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			// tlabelSet[j].setUri(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tNamedClassS[j].getLocalName()));
		}

		SimilarityMatrix sm = new SimilarityMatrix(sNamedClassS.length, tNamedClassS.length);

		for (int j = 0; j < tNamedClassS.length; j++) {
			sm.setCol(j, tlabelSet[j]);
		}

		for (int i = 0; i < sNamedClassS.length; i++) {
			sm.setRow(i, slabelSet[i]);

			for (int j = 0; j < tNamedClassS.length; j++) {

				double OWLDescriptionSimilarity = 0.0;
				List<OWLSimpleClassDescription> sSimpleClassExp = sResModel.listOWLSimpleClassDescriptions(sNamedClassS[i].getURI());
				List<OWLSimpleClassDescription> tSimpleClassExp = tResModel.listOWLSimpleClassDescriptions(tNamedClassS[j].getURI());
				OWLDescriptionSimilarity = simpleDescriptionSetMatcher.getSimpleClassDescriptionSetMappingSimilairty(sSimpleClassExp, tSimpleClassExp);
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), OWLDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}
		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult = msm.getMapping(sm);
		return this.getMatchingPairsOverallSimilarity(simResult.getSubMappings(), maxSize);
	}

	private double getMatchingPairsOverallSimilarity(ArrayList<SubMapping> mappingPairs, int maxSize) {

		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
		for (int i = 0; i < numberOfPairs; i++) {
//			 System.out.println("|s element: " +
//			 mappingPairs.get(i).s.getLocalName());
//			 System.out.println("|t element: " +
//			 mappingPairs.get(i).t.getLocalName());
//			 System.out.println("|sim: " + mappingPairs.get(i).getSimilarity());
			sum += mappingPairs.get(i).getSimilarity();
		}

//		 System.out.println("|overall sim: " + sum / maxSize);
//		System.out.println("------------------------------");
		return sum / maxSize;

	}

}
