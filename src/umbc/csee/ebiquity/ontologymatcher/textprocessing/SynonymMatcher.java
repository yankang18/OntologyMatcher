package umbc.csee.ebiquity.ontologymatcher.textprocessing;

import java.util.ArrayList;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram2Extended;

class SynonymMatcher implements Matcher {

	private boolean perserveOrder = true;
	private boolean perserveTermsLength = false;
	private double penaltyforPerserveOrder = 0.8;
	private double penaltyforPerserveTermlength = 0.85;

	private int contentTermsLength = 0;

	private QGramsDistance similarityByGGrams;
	private ArrayList<ArrayList<String>> synonymForWordList;

	public SynonymMatcher(String content) {

		String[] wordlistForContent = this.filter(TextProcessingUtils.tokenizeLabel(content));
		this.contentTermsLength = wordlistForContent.length;
		this.synonymForWordList = SynonymPhraseCombiner.getSynonyms(wordlistForContent);
		/** Getting the similarity between two string by using G-Grams */
		this.similarityByGGrams = new QGramsDistance(new TokeniserQGram2Extended());

	}
	
	private String[] filter(String[] originalWords){
		
		ArrayList<String> reservedWords = new ArrayList<String>();
		ArrayList<String> wordsToBefiltered = new ArrayList<String>();
		wordsToBefiltered.add("has");
//		wordsToBefiltered.add("is");
		
		for(String word : originalWords){
			
			if (!wordsToBefiltered.contains(word)) {
				reservedWords.add(word);
			}
		}
		
		String[] tmp = new String[reservedWords.size()];
		return reservedWords.toArray(tmp);
	}

	@Override
	public double getSimilarityScore(String query) {
		return this.getSimilarityScore(query, this.perserveOrder, this.perserveTermsLength);
	}

	@Override
	public double getSimilarityScore(String query, boolean perserveOrder, boolean perserveTermsLength) {
		this.perserveOrder = perserveOrder;
		this.perserveTermsLength = perserveTermsLength;

		/*
		 * Choosing these two thresholds is important. qualifiesTermThreshold is
		 * for choosing qualified terms, which means that choosing terms whose
		 * scores are beyond certain threshold. ratioOfQualifiedTermsThreshold
		 * is for determining which method should be used to compute the
		 * similarity score.
		 */
		double qualifiedTermThreshold = 0.70;
		double ratioOfQualifiedTermsThreshold = 0.50;
		
		/*
		 * number of terms after tokenize the query string.
		 */
		int queryTermsLength = 0;

		/*
		 * number of terms whose scores are beyond concern threshold.
		 */
		int qualifiedTermsLength = 0;

		Term[] terms = this.match(query);
		queryTermsLength = terms.length;
		ArrayList<Term> qualifiedTerms = new ArrayList<Term>();

		/*
		 * count the number of qualified terms; add them to a array list; sum up
		 * the scores of all qualified terms; sum up the scores of all terms.
		 */
		double scoreForQualifiedTerms = 0.0;
		double scoreForAllTerms = 0.0;
		for (Term term : terms) {
			System.out.println(term);
			if (term.getScore() > qualifiedTermThreshold) {
				
//				qualifiedTermsLength++;
//				qualifiedTerms.add(term);
//				scoreForQualifiedTerms += term.getScore();
				
				term.boostScore(1.0);
			}
			scoreForAllTerms += term.getScore();
		}

		/*
		 * compute the ratio of the number of qualified terms to the number of
		 * all terms. This ratio decides which average score to use.
		 */
//		double ratioOfQualifiedTerms = (double) qualifiedTermsLength / (double) queryTermsLength;

//		double averageScore = 0.0;
//		if (ratioOfQualifiedTerms >= ratioOfQualifiedTermsThreshold) {
//			averageScore = scoreForQualifiedTerms / qualifiedTermsLength;
//		} else {
//			averageScore = scoreForAllTerms / queryTermsLength;
//		}

//		averageScore = scoreForAllTerms / queryTermsLength;
		
		Term[] terms2 = new Term[qualifiedTerms.size()];

		if (this.perserveOrder && !isOrderPerserved((Term[]) qualifiedTerms.toArray(terms2))) {
//			averageScore = averageScore * penaltyforPerserveOrder;
		}

		if (this.perserveTermsLength) {

//			int maxLength = this.contentTermsLength + queryTermsLength;
			int maxLength = this.contentTermsLength > queryTermsLength ? this.contentTermsLength : queryTermsLength;
			System.out.println(" - content term length: " + this.contentTermsLength);
			System.out.println(" - query term length: " + queryTermsLength);
			System.out.println(" - max length: " + maxLength);
			int distance = this.getDistance(queryTermsLength);
			penaltyforPerserveTermlength = 1.0 - (double) distance / (double) maxLength;
			System.out.println("[-> penalty:" + penaltyforPerserveTermlength + "]");
//			averageScore = averageScore * penaltyforPerserveTermlength;
			scoreForAllTerms = scoreForAllTerms * penaltyforPerserveTermlength;
		}

		return scoreForAllTerms;
//		return averageScore;
	}

	private Term[] match(String query) {

		// PorterStemmer stemmer = new PorterStemmer();
		System.out.println("[Query: " + query + "]");
		String[] wordlistForQuery = this.filter(TextProcessingUtils.tokenizeLabel(query));
		Term[] terms = new Term[wordlistForQuery.length];

		int count = 0;
		for (int i = 0; i < wordlistForQuery.length; i++) {
			// terms[i] = new Term(stemmer.stem(wordlistForQuery[i]), i);
			terms[i] = new Term(wordlistForQuery[i], i);
			int synonymlistIndex = 0;
			for (ArrayList<String> synonymList : this.synonymForWordList) {

				// double maxScore = 0.0;
				for (String synonym : synonymList) {
					double totalSimByGGrams = this.similarityByGGrams.getSimilarity(synonym.trim(), terms[i].getWord().trim());

					// if (totalSimByGGrams > maxScore) {
					// maxScore = totalSimByGGrams;
					// }
					//
					// terms[i].setScore(totalSimByGGrams);
					// terms[i].setPositionInContent(synonymlistIndex);

					if (totalSimByGGrams > terms[i].getScore()) {
						terms[i].setScore(totalSimByGGrams);
						terms[i].setPositionInContent(synonymlistIndex);
					}

					count++;
				}
				synonymlistIndex++;
			}
		}

		// System.out.println("[-> number of iteration: " + count + "]");
		return terms;
	}

	private boolean isOrderPerserved(Term[] terms) {

		boolean isPerserveOrder = true;
		if (terms.length > 1) {
			for (int i = 1; i < terms.length; i++) {
				if (terms[i].getPositionInContent() - terms[i - 1].getPositionInContent() < 0) {
					isPerserveOrder = false;
				}
			}
		}

		System.out.println("[-> is order perserved: " + isPerserveOrder + "]");
		return isPerserveOrder;
	}

	private int getDistance(int length) {
		int distance = Math.abs(contentTermsLength - length);
//		System.out.println("[-> contentTermsLength: " + contentTermsLength + "]");
//		System.out.println("[-> qualifiedTermsLength: " + length + "]");
		System.out.println("[-> distance: " + distance + "]");
		return distance;
	}

	/**
	 * Calculate base 2 logarithm
	 * 
	 * @param x
	 *            value to take log of
	 * 
	 * @return base 2 logarithm.
	 */
	private double log2(double x) {
		// Math.log is base e, natural log, ln
		return Math.log(x) / Math.log(2);
	}

}
