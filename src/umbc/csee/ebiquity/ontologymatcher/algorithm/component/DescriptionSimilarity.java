package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;

public class DescriptionSimilarity {

	// private ILabelSimilarity labelSim = new MSMLabelSimilarity();
	// private ILabelSimilarity ngramSim = new NGramsLabelSimilarity();

	public static double getSimilarity(OntResourceInfo sResource, OntResourceInfo tResource) {
		ILabelSimilarity labelSim = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
		ILabelSimilarity ngramSim = new NGramsLabelSimilarity();

		String sLabel = sResource.getLocalName();
		List<String> sDescriptions = sResource.getDescriptions();
		if(sDescriptions == null) return 0.0;

		String tLabel = tResource.getLocalName();
		List<String> tDescriptions = tResource.getDescriptions();
		if(tDescriptions == null) return 0.0;
		// double ngramSimilarity = ngramSim.getSimilarity(sLabel, tLabel);
		double labelSimilarity = Math.max(labelSim.getSimilarity(sLabel, tLabel), ngramSim.getSimilarity(sLabel, tLabel));
		int num = Math.min(sDescriptions.size(), tDescriptions.size());
		double descriptionSimilarity = 0.0;
		for (int i = 0; i < num; i++) {
			double similarity = labelSim.getSimilarity(sDescriptions.get(i), tDescriptions.get(i));
			if (similarity > descriptionSimilarity) {
				descriptionSimilarity = similarity;
			}
		}

		return Math.max(labelSimilarity, descriptionSimilarity);
	}

}
