package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2Extended;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;

public class MSMWordSimilarity implements ITextSimilarity{
	private static HashMap<String, Double> cachedWordSimMap = new HashMap<String, Double>();
	private static ArrayList<String> cachedNonWordnetDef = new ArrayList<String>();
	private WordNetSimCachedRepository icSim;

	public MSMWordSimilarity() {
		icSim = WordNetSimCachedRepository.getInstance();
	}

	public double getSimilarity(String word1, String word2,
			ArrayList<String> nonSynWordList) {
		if (word1.equals(word2)) {
			return 1;
		}

		double preMapSimilarity = PreMapping.getSimilarity(word1, word2);
		if (preMapSimilarity != -1) {
			return preMapSimilarity;
		}

		if (cachedNonWordnetDef.contains(word1)
				|| cachedNonWordnetDef.contains(word2)) {
			return 0;
		}
		String key = word1 + "-" + word2;
		if (cachedWordSimMap.containsKey(key)) {
			return cachedWordSimMap.get(key);
		}

		double sim = 0;

		// cannot calculate using WordNet.
		if (cachedNonWordnetDef.contains(word1)
				|| cachedNonWordnetDef.contains(word2)) {
			sim = 0;
		} else {
			sim = icSim.getSimilarity(word1, word2);
			if (sim < 0) {
				if (sim == icSim.NO_WORDDEF1) {
					if (nonSynWordList != null) {
						nonSynWordList.add(word1);
					}
					cachedNonWordnetDef.add(word1);
//					System.err.println("No Wordnet definition: " + word1);
				} else if (sim == icSim.NO_WORDDEF2) {
					if (nonSynWordList != null) {
						nonSynWordList.add(word2);
					}
					cachedNonWordnetDef.add(word2);
//					System.err.println("No Wordnet definition: " + word2);
				}
//				System.err.println("Error: ");
				sim = 0;
			}
			if (sim == 0) {
				QGramsDistance metric = new QGramsDistance(
						new TokeniserQGram3Extended());
				sim = metric.getSimilarity(word1, word2);
				sim = (1 - (1 - sim) * 0.5) * sim;
			}

			cachedWordSimMap.put(key, sim);
		}

		return sim;
	}

	@Override
	public double getSimilarity(String word1, String word2) {
		return getSimilarity(word1, word2, null);
	}
}
