package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ILabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMLabelSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.PreMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleClassExpression;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;

public class OWLSimpleClassDescriptionSetMatcher {
	private IWordIC labelIC = new BasicWordIC();
	private ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_1to1);

	public double getSimpleClassDescriptionSetMappingSimilairty(List<OWLSimpleClassDescription> sDescriptionSet, List<OWLSimpleClassDescription> tDescriptionSet) {
		int sDesSize = sDescriptionSet.size();
		int tDesSize = tDescriptionSet.size();
		if (sDesSize == 0 || tDesSize == 0) { 
			return 0;
		}
		OWLSimpleClassDescription[] sDesS = new OWLSimpleClassDescription[sDesSize];
		OWLSimpleClassDescription[] tDesS = new OWLSimpleClassDescription[tDesSize];
		return this.getSimpleClassDescriptionSetMappingSimilairty(sDescriptionSet.toArray(sDesS), tDescriptionSet.toArray(tDesS));
	}

	public double getSimpleClassDescriptionSetMappingSimilairty(OWLSimpleClassDescription[] sDescriptionSet, OWLSimpleClassDescription[] tDescriptionSet) {
		MSMResult mapping = this.getSimpleClassDescriptionSetMapping(sDescriptionSet, tDescriptionSet);
		
		int sDesSize = sDescriptionSet.length;
		int tDesSize = tDescriptionSet.length;
		int maxSize = Math.max(sDesSize, tDesSize);
		
		List<SubMapping> mappingPairs = mapping.getSubMappings();
		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
		for (int i = 0; i < numberOfPairs; i++) {

//			System.out.println("s relation: [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t relation: [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("rel sim:    [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}
//		    System.out.println("ovl sim:    [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;
	}

	private MSMResult getSimpleClassDescriptionSetMapping(OWLSimpleClassDescription[] sDescriptionSet, OWLSimpleClassDescription[] tDescriptionSet) {

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
		return getSimpleClassDescriptionSetMapping(sDescriptionSet, slabelSet, tDescriptionSet, tlabelSet);
	}

	private MSMResult getSimpleClassDescriptionSetMapping(OWLSimpleClassDescription[] sDescriptionSet, SimilarityMatrixLabel[] sDescriptionLabelSet, OWLSimpleClassDescription[] tDescriptionSet,
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

					List<SimpleClassExpression> sExpressions = sDescriptionSet[i].getClassExpressions();
					List<SimpleClassExpression> tExpressions = tDescriptionSet[j].getClassExpressions();
					OWLDescriptionSimilarity = this.getSimpleClassExpressionSetMatchingSimilarity(sExpressions, tExpressions);
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

	/**
	 * @param sExpressions
	 * @param tExpressions
	 * @return
	 */
	public double getSimpleClassExpressionSetMatchingSimilarity(List<SimpleClassExpression> sExpressions, List<SimpleClassExpression> tExpressions) {
		
		double OWLDescriptionSimilarity;
		List<String> sExps = new ArrayList<String>();
		for (SimpleClassExpression expression : sExpressions) {
			sExps.add(expression.getClassExpressionTypeName().toLowerCase());
		}
		List<String> tExps = new ArrayList<String>();
		for (SimpleClassExpression expression : tExpressions) {
			tExps.add(expression.getClassExpressionTypeName().toLowerCase());
		}

		OWLDescriptionSimilarity = this.getStringSetSimilarity(sExps,tExps);
		return OWLDescriptionSimilarity;
	}

	public double getLabelSimilarity(String sContent, String tContent, int maxSize) {
//		System.out.println("  - s: " + sContent.toString());
//		System.out.println("  - t: " + tContent.toString());
		ArrayList<SubMapping> mappingPairs = labelSim.getMapping(sContent, tContent).getSubMappings();
		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
		for (int i = 0; i < numberOfPairs; i++) {
			// count++;
//			System.out.println("s cl exp:   [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t cl exp:   [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("cl exp sim: [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}

//		System.out.println("ovl sim:    [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;

	} 
	
	public double getStringSetSimilarity(List<String> sExps, List<String> tExps) {
		
		int sSize = sExps.size();
		int tSize = tExps.size();
		int maxSize = Math.max(sSize, tSize);
		ArrayList<SubMapping> mappingPairs = this.getStringSetMatching(sExps,tExps).getSubMappings();

		int numberOfPairs = mappingPairs.size();
		double sum = 0.0;
//		System.out.println("------------------------------");
		for (int i = 0; i < numberOfPairs; i++) {
			// count++;
//			System.out.println("s cl exp:   [" + mappingPairs.get(i).s.getLocalName() + "]");
//			System.out.println("t cl exp:   [" + mappingPairs.get(i).t.getLocalName() + "]");
//			System.out.println("cl exp sim: [" + mappingPairs.get(i).getSimilarity() + "]");
			sum += mappingPairs.get(i).getSimilarity();
		}

//		System.out.println("ovl sim:    [" + sum / maxSize + "]");
//		System.out.println("------------------------------");
		return sum / maxSize;

	} 
	
	private MSMResult getStringSetMatching(List<String> sExps, List<String> tExps) {

		int rSize = sExps.size();
		int cSize = tExps.size();
		SimilarityMatrix sm = new SimilarityMatrix(rSize, cSize);

		for (int i = 0; i < rSize; i++) {
			String token = sExps.get(i);
			SimilarityMatrixLabel rowLabel = new SimilarityMatrixLabel(token);
			sm.setRow(i, rowLabel);
		}
		for (int j = 0; j < cSize; j++) {
			String token = tExps.get(j);
			SimilarityMatrixLabel colLabel = new SimilarityMatrixLabel(token);
			sm.setCol(j, colLabel);
		}
		for (int i = 0; i < sm.getRowCount(); i++) {
			String word1 = sm.getRowName(i);

			for (int j = 0; j < sm.getColCount(); j++) {
				String word2 = sm.getColName(j);
				double sim = 0.0;
				if (word1.equals(word2)) {
					sim = 1.0;
				}

				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), sim);
				sm.setCellAt(i, j, cell);
			}
		}
		MSMUnordered msm = new MSMUnordered(MAP_CARDINALITY.MODE_1to1);
		MSMResult simResult = msm.getMapping(sm);
		return simResult;
	}
}
