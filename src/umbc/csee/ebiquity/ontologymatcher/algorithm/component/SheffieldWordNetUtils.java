package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import net.didion.jwnl.JWNLException;
import shef.nlp.wordnet.similarity.ICSimilarity;
import shef.nlp.wordnet.similarity.SimilarityInfo;
import shef.nlp.wordnet.similarity.SimilarityMeasure;

public class SheffieldWordNetUtils {
	private static SimilarityMeasure icSim = null;
	boolean bInit = false;

	public SheffieldWordNetUtils() {
		if (icSim == null) {
			try {
				icSim = ICSimilarity.newInstance();
				bInit = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isInitialized() {
		return bInit;
	}

	public boolean hasWordInfo(String word) {
		if (!bInit) return false;
		return icSim.hasSynsets(word);
	}

	public SimilarityInfo getSimilarity(String word1, String word2) throws JWNLException {
		if (!bInit) return null;
		return icSim.getSimilarity(word1, word2);
	}
}