package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.didion.jwnl.JWNLException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import shef.nlp.wordnet.similarity.SimilarityInfo;
import umbc.csee.ebiquity.ontologymatcher.config.Configuration;


public class WordNetSimCachedRepository {

	public static int NO_WORDDEF1 = -1;
	public static int NO_WORDDEF2 = -2;
	public static int NULL = -3;
	public static int JWNL_EXCEPTION = -4;

	/**
	 * The structure of the cached repository.
	 * 
	 * w1,w2,w3,w4,w5 <-- sorted (w1-w1),(w1-w2),(w1-w3)...(w1,w5)
	 * (w2-w2),(w2,w3), .. (w5,w5)
	 */
	String path = Configuration.getWnCachePath();

	private SheffieldWordNetUtils icSim = null;
	List<String> wordList;
	List<String> loadedWordList;
	List<ArrayList<Double>> loadedSimMap;

	private static WordNetSimCachedRepository thiz = null;

	public static WordNetSimCachedRepository getInstance() {
		if (thiz == null) {
			thiz = new WordNetSimCachedRepository();
		}
		return thiz;
	}

	private WordNetSimCachedRepository() {
		icSim = new SheffieldWordNetUtils();
		load();
	}

	public void load() {
		wordList = new ArrayList<String>();
		loadedWordList = new ArrayList<String>();
		loadedSimMap = new ArrayList<ArrayList<Double>>();

		InputStream in = null;
		try {
			in = FileUtils.openInputStream(new File(path));
			try {
				List<String> lines = IOUtils.readLines(in);
				if (lines.size() < 2) {
					// error... it should have at least two lines.
					System.err.println("wn-sim-cached.txt format error!");
					return;
				}
				String wordsLine = lines.get(0);
				String[] words = wordsLine.split(",");
				loadedWordList = Arrays.asList(words);

				for (int i = 1; i < lines.size(); i++) {
					String line = lines.get(i);
					String[] simList = line.split(",");
					ArrayList<Double> simList2 = new ArrayList<Double>();
					for (String sim : simList) {
						simList2.add(Double.valueOf(sim));
					}
					loadedSimMap.add(simList2);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				IOUtils.closeQuietly(in);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void save() {
		if (added == false || Configuration.canUpdateWnCache() == false) {
			return;
		}

		wordList.addAll(loadedWordList);
		Collections.sort(wordList);

		OutputStream out = null;
		try {
			out = FileUtils.openOutputStream(new File(path));
			try {
				for (int i = 0; i < wordList.size(); i++) {
					if (i != 0) {
						IOUtils.write(",", out);
					}
					IOUtils.write(wordList.get(i), out);
				}
				IOUtils.write(IOUtils.LINE_SEPARATOR, out);

				for (int i = 0; i < wordList.size(); i++) {
					IOUtils.write("1", out);
					for (int j = i + 1; j < wordList.size(); j++) {
						double sim = getSimilarity(wordList.get(i), wordList
								.get(j), false);
						if (sim < 0)
							sim = 0;
						IOUtils.write("," + sim, out);
					}
					IOUtils.write(IOUtils.LINE_SEPARATOR, out);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				IOUtils.closeQuietly(out);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	boolean added = false;

	public void add(String word) {
		added = true;
		if (wordList.contains(word) == false) {
			wordList.add(word);
		}
	}

	public double getSimilarity(String word1, String word2, boolean addNewWord) {
		word1 = word1.toLowerCase();
		word2 = word2.toLowerCase();

		if (word1.equals(word2)) {
			return 1;
		}

		int i1 = loadedWordList.indexOf(word1);
		int i2 = loadedWordList.indexOf(word2);

		if (addNewWord && icSim.isInitialized()) {
			if (i1 < 0) {
				add(word1);
			}
			if (i2 < 0) {
				add(word2);
			}
		}

		if (i1 < 0 || i2 < 0) {
			if (icSim.isInitialized() == false) {
				return NULL;
			}
			try {
				SimilarityInfo simInfo = icSim.getSimilarity(word1, word2);
				if (simInfo != null) {
					DecimalFormat shortDouble = new DecimalFormat("#.###");
					double sim = simInfo.getSimilarity();
					String simStr = shortDouble.format(sim);
					sim = Double.valueOf(simStr);
					return sim;
				}
				if (icSim.hasWordInfo(word1) == false) {
					return NO_WORDDEF1;
				}
				if (icSim.hasWordInfo(word2) == false) {
					return NO_WORDDEF2;
				}

				// System.err.println("No Wordnet definition: " + word1);
				return NULL;
			} catch (JWNLException e) {
				e.printStackTrace();
				return JWNL_EXCEPTION;
			} catch (Exception e) {
				e.printStackTrace();
				return JWNL_EXCEPTION;
			}
		}

		if (i1 > i2) {
			int tmp = i2;
			i2 = i1;
			i1 = tmp;
		}

		ArrayList<Double> simList = loadedSimMap.get(i1);
		Double sim = simList.get(i2 - i1);

		return sim;
	}

	// get Similarity and then add new words to repository...
	public double getSimilarity(String word1, String word2) {
		return getSimilarity(word1, word2, true);
	}
}
