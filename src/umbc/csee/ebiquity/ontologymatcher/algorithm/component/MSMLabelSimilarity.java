package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;

import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;

public class MSMLabelSimilarity implements ILabelSimilarity {
	/**
	 * Logger for this class
	 */
//	Logger logger = Logger.getLogger(MSMLabelSimilarity.class.getName());

	private IWordIC wordICHandler;
//	private LabelSimilarityCosine cosineSim = new LabelSimilarityCosine();
	private MSMWordSimilarity wordSim;
//	private CachedMSMResult cache1 = new CachedMSMResult();
//	private CachedMSMLabelResult cache2 = new CachedMSMLabelResult();
	private MAP_CARDINALITY map_cardinality = MAP_CARDINALITY.MODE_1to1;

	
	public static void main(String[] args) throws IOException {
		  
		
		MSMLabelSimilarity labelSimilarity = new MSMLabelSimilarity(MAP_CARDINALITY.MODE_MtoN);
		String string1  = "tile waterjet cutting";
		String string2 = "water jet cutting";
		String string3  = "tilewaterjetcutting";
		String string4 = "waterjetcutting";
		double sim = labelSimilarity.getSimilarity(string1, string2);
		
		System.out.println(sim);
		
		QGramsDistance metric = new QGramsDistance(new TokeniserQGram3Extended());
		sim = metric.getSimilarity(string3, string4);
		System.out.println(sim);
	}
	
	
	public MSMLabelSimilarity(MAP_CARDINALITY m_c) {
		this(new BasicWordIC(), m_c);
	}

	public MSMLabelSimilarity(IWordIC ic, MAP_CARDINALITY m_c) {
		this.wordICHandler = ic;
		wordSim = new MSMWordSimilarity();
	}

//	public MSMLabelSimilarityReduced(XMLTreeHandler s, XMLTreeHandler t) {
//		this(new XSLabelIC(Arrays.asList(new XMLTreeHandler[] { s, t })));
//	}

	@Override
	public double getSimilarity(String label1, String label2) {
		return getMapping(label1, label2).getSimilarity();
	}

	@Override
	public MSMResult getMapping(String label1, String label2) {
		return getMapping(label1, label2, false);
	}
	
	
	private List<String> LabelTokenizing(String label){
		return this.removeStopWords(AbbrConverter.convertLabel2FullWordList(label));
	}
	
	private List<String> removeStopWords(List<String> wordset){
		return StopWordsRemover.removeStopWords(wordset);
	}
	
	@Override
	public int getWordsetSize(String label){
		return LabelTokenizing(label).size();
	}

	@Override
	public MSMResult getMapping(String label1, String label2, boolean includeWordInfo) {
//		logger.entering("MSMLabelSimilarity", "getMapping", new Object[] { label1, label2, includeWordInfo });

//		System.out.println(label1 + " -- " + label2);
		MSMResult r;
//		ICachedSimilarityResult cache;
		ArrayList<String> nonSynWordList = null;
		if (includeWordInfo) {
			r = new MSMLabelResult();
//			cache = cache2;
			nonSynWordList = new ArrayList<String>();
		} else {
			r = new MSMResult();
//			cache = cache1;
		}

//		if (label1.equalsIgnoreCase(label2)) {
////			logger.finer("label1 == label2");
////			System.out.println("label1 == label2");
//			r.setSimilarity(1);
//			if (includeWordInfo) {
////				r.addSubMappings(new SimilarityMatrixLabel(label1), new SimilarityMatrixLabel(label2), 1);
//			}
//			return r;
//		}

		double preMapSimilarity = PreMapping.getSimilarity(label1, label2);
		if (preMapSimilarity != -1) {
//			logger.finer("{label1, label2} exists in pre-defined matching table.");
			// System.out.println("preMap: " + label1 + "-" + label2 + "=" +
			// preMapSimilarity);
			r.setSimilarity(preMapSimilarity);
			if (includeWordInfo) {
//				r.addSubMappings(new SimilarityMatrixLabel(label1), new SimilarityMatrixLabel(label2), preMapSimilarity);
			}
			return r;
		}

//		MSMResult cached = (MSMResult) cache.getCachedSimilarity(label1, label2);

//		if (cached != null) {
//			logger.finer("cache hit!");
//			System.out.println("cache hit!");
//			return cached;
//		}

//		List<String> wordList1 = AbbrConverter.convertLabel2FullWordList(label1);
//		List<String> wordList2 = AbbrConverter.convertLabel2FullWordList(label2);
		
		List<String> wordList1 = LabelTokenizing(label1);
		List<String> wordList2 = LabelTokenizing(label2);
		
		int rSize = wordList1.size();
		int cSize = wordList2.size();
		
//		System.out.println("row size = " + rSize + ", column size = " + cSize);

		/*
		 * When the label only contains number, then wordList we got after
		 * parsing the label is empty. Therefore, to get around this, we put a
		 * whitespace into the wordList when the wordList is empty. In the way,
		 * we actually place a number with a whitespace.
		 */
		if (rSize == 0) {
//			System.out.println("wordList1 size: " + rSize);
			wordList1.add("");
			rSize = wordList1.size();
		}
		if (cSize == 0) {
//			System.out.println("wordList2 size: " + cSize);
			wordList2.add("");
			cSize = wordList2.size();
		}

		SimilarityMatrix sm = new SimilarityMatrix(rSize, cSize);

		for (int i = 0; i < rSize; i++) {
			String token = wordList1.get(i);
			SimilarityMatrixLabel rowLabel = new SimilarityMatrixLabel(token);
			rowLabel.setIC(wordICHandler.getIC(token));
			sm.setRow(i, rowLabel);
//			logger.finest("label 1 [" + i + "] = " + token);
//			System.out.println("label 1 [" + i + "] = " + token);
		}
//		logger.fine("label1 tokenizer size = " + sm.getRowCount());
//		System.out.println("label1 tokenizer size = " + sm.getRowCount());

		for (int j = 0; j < cSize; j++) {
			String token = wordList2.get(j);
			SimilarityMatrixLabel colLabel = new SimilarityMatrixLabel(token);
			colLabel.setIC(wordICHandler.getIC(token));
			sm.setCol(j, colLabel);
//			logger.finest("label 2 [" + j + "] = " + token);
//			System.out.println("label 2 [" + j + "] = " + token);
		}
//		logger.fine("label2 tokenizer size = " + sm.getColCount());
//		System.out.println("label2 tokenizer size = " + sm.getColCount());

		for (int i = 0; i < sm.getRowCount(); i++) {
			String word1 = sm.getRowName(i);

			for (int j = 0; j < sm.getColCount(); j++) {
				String word2 = sm.getColName(j);

				double sim = wordSim.getSimilarity(word1, word2, nonSynWordList);

				SimilarityMatrixCell cell = new SimilarityMatrixCell(sm.getRow(i), sm.getCol(j), sim);
				sm.setCellAt(i, j, cell);
			}
		}
//		MSMUnordered msm = new MSMUnordered(AlgorithmMode.getLabelMapCardinality());
		MSMUnordered msm = new MSMUnordered(this.map_cardinality);
		MSMResult simResult = msm.getMapping(sm);

		if (includeWordInfo) {
			MSMLabelResult simResult2 = new MSMLabelResult();
			simResult2.set(simResult, nonSynWordList);
//			cache.setCachedSimilarity(label1, label2, simResult2);
			return simResult2;
		}

//		cache.setCachedSimilarity(label1, label2, simResult);

//		if (simResult == null) {
//			System.out.println("simResult == null");
//		}
		
//		logger.fine(label1 + " : " + label2 + " = " + simResult.getSimilarity());
//		System.out.println(label1 + " : " + label2 + " = " + simResult.getSimilarity());

//		logger.entering("MSMLabelSimilarity", "getMapping");

//		System.out.println();
		return simResult;
	}

	@Override
	public void setWordICHandler(IWordIC wordIC) {
		this.wordICHandler = wordIC;
	}

	// TODO: JAEWOOK FIX THIS
	@Override
	public MSMResult getAtomicMapping(String label1, String label2) {
		QGramsDistance metric = new QGramsDistance(new TokeniserQGram3Extended());
		float result = metric.getSimilarity(label1, label2);
//		logger.finest(label1 + ":" + label2 + "=" + result);

		return new MSMResult(result);
		// return getMapping(label1, label2);
		// CosineSimilarity2 cosineSim = new CosineSimilarity2();
		// float sim = cosineSim.getSimilarity(label1, label2);
		// MSMResult simResult = new MSMResult(sim);
		// return simResult;
	}
}