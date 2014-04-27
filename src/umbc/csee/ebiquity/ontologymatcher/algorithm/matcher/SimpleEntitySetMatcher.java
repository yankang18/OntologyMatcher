package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.BasicWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.DescriptionSimilarity;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IWordIC;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMUnordered;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrix;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixCell;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimilarityMatrixLabel;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;

public class SimpleEntitySetMatcher {
	
	private IWordIC labelIC = new BasicWordIC();
	public double getEntityMappingSimilarity(List<OntResourceInfo> sResourceSet, List<OntResourceInfo> tResourceSet, boolean strict){
		int sResSize = sResourceSet.size();
		int tResSize = tResourceSet.size();
		if (sResSize == 0 || tResSize == 0) {
			return 0;
		}
		OntResourceInfo[] sResS = new OntResourceInfo[sResSize];
		OntResourceInfo[] tResS = new OntResourceInfo[tResSize];
		return this.getEntityMappingSimilairty(sResourceSet.toArray(sResS), tResourceSet.toArray(tResS), strict);
	}
	
	public double getEntityMappingSimilairty(OntResourceInfo[] sResourceSet, OntResourceInfo[] tPesourceSet, boolean strict) {
		MSMResult mapping = this.getEntityMapping(sResourceSet, tPesourceSet);

		if (strict == true) {
			return mapping.getSimilarity();
		}

		ArrayList<SubMapping> mappingPairs = mapping.getSubMappings();
		Collections.sort(mappingPairs);

		float numberOfPairs = mappingPairs.size();
		int numberOfPairs_for_computeSimilarity = Math.round(numberOfPairs * 2 / 3);
		double sum = 0.0;
		int count = 0;
		for (int i = 0; i < numberOfPairs_for_computeSimilarity; i++) {
			count++;
			sum += mappingPairs.get(i).getSimilarity();
		}
		return sum / count;
	}
	/***
	 * This method is used to compute the similarity between the property sets of two resources (now classes).
	 * @param sResourceSet
	 * @param tPesourceSet
	 * @return 
	 */ 
	private MSMResult getEntityMapping(OntResourceInfo[] sResourceSet, OntResourceInfo[] tPesourceSet) {
 
	    SimilarityMatrixLabel[] slabelSet = new SimilarityMatrixLabel[sResourceSet.length];
		for (int i = 0; i < sResourceSet.length; i++) {
			slabelSet[i] = new SimilarityMatrixLabel(sResourceSet[i].getLocalName());
//			slabelSet[i].setRevisedName(sResourceSet[i].getRevisedLocalName());
			slabelSet[i].setURI(sResourceSet[i].getURI());
			slabelSet[i].setIC(labelIC.getIC(sResourceSet[i].getLocalName()));
		}
		
		SimilarityMatrixLabel[] tlabelSet = new SimilarityMatrixLabel[tPesourceSet.length];
		for (int j = 0; j < tPesourceSet.length; j++) {
			tlabelSet[j] = new SimilarityMatrixLabel(tPesourceSet[j].getLocalName());
//			tlabelSet[j].setRevisedName(tPesourceSet[j].getRevisedLocalName());
			tlabelSet[j].setURI(tPesourceSet[j].getURI());
			tlabelSet[j].setIC(labelIC.getIC(tPesourceSet[j].getLocalName()));
		}
		return getEntityMapping(sResourceSet, slabelSet, tPesourceSet, tlabelSet);
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
	private MSMResult getEntityMapping(OntResourceInfo[] sResourceSet, SimilarityMatrixLabel[] sResourceLabelSet, OntResourceInfo[] tResourceSet,
			SimilarityMatrixLabel[] tResourcdLabelSet) {
		 
		SimilarityMatrix sm = new SimilarityMatrix(sResourceSet.length, tResourceSet.length);

		for (int j = 0; j < tResourceSet.length; j++) {
			sm.setCol(j, tResourcdLabelSet[j]);
		}

		for (int i = 0; i < sResourceSet.length; i++) {
			sm.setRow(i, sResourceLabelSet[i]);

			for (int j = 0; j < tResourceSet.length; j++) {
		
//				double resourceLabelSimilarity  = labelSim.getSimilarity(sResourceSet[i].getLocalName(), tResourceSet[j].getLocalName());
//				sResourceSet[i].getDescriptions();
//				tResourceSet[j].getDescriptions();
				
				double resDescriptionSimilarity = DescriptionSimilarity.getSimilarity(sResourceSet[i], tResourceSet[j]);
				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), resDescriptionSimilarity);
				sm.setCellAt(i, j, cell);
			}
		}

		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getPathMapCardinality());
		MSMResult simResult;
		simResult = msm.getMapping(sm);
		return simResult;
	}

}
