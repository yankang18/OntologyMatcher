package umbc.csee.ebiquity.ontologymatcher.textprocessing;

class ExactStringMatcher implements Matcher {

	private boolean perserveOrder = true;
	private boolean perserveTermsLength = false;
	private double penaltyforPerserveOrder = 0.6;
	private double penaltyforPerserveTermlength = 0.85;
	private int queryTermsLength = 0;
	private int contentTermsLength = 0;
	private String[] wordlistForContent;

	private PorterStemmer stemmer;

	public ExactStringMatcher(String content) {

		this.stemmer = new PorterStemmer();
		this.wordlistForContent = TextProcessingUtils.tokenizeLabel(content);

		for (int i = 0; i < wordlistForContent.length; i++) {
			wordlistForContent[i] = stemmer.stem(wordlistForContent[i]);
		}
		this.contentTermsLength = wordlistForContent.length;
	}

	@Override
	public double getSimilarityScore(String query) {
		return this.getSimilarityScore(query, this.perserveOrder, this.perserveTermsLength);
	}

	@Override
	public double getSimilarityScore(String query, boolean perserveOrder, boolean perserveTermsLength) {
		this.perserveOrder = perserveOrder;
		this.perserveTermsLength = perserveTermsLength;

		Term[] terms = this.match(query);

		double score = 0.0;
		for (Term term : terms) {
			System.out.println(term);
			score += term.getScore();
		}
		
//		int numberOfTerms = terms.length;
//		score = score / numberOfTerms;
		return score;
	}

	private Term[] match(String query) {

		System.out.println("[Query: " + query + "]");
		String[] wordlistForQuery = TextProcessingUtils.tokenizeLabel(query);
		Term[] terms = new Term[wordlistForQuery.length];
		this.queryTermsLength = wordlistForQuery.length;

		int count = 0;
		for (int i = 0; i < wordlistForQuery.length; i++) {
			terms[i] = new Term(stemmer.stem(wordlistForQuery[i]), i);
			for (String str : this.wordlistForContent) {
				String strTerm = terms[i].getWord().toLowerCase();
				if (strTerm.contains(str.trim().toLowerCase()) || str.trim().toLowerCase().contains(strTerm)) {
					double score = terms[i].getScore() + 1.0;
					terms[i].setScore(score);
				}
				count++;
			}

			double score2 = terms[i].getScore() / this.contentTermsLength;
			terms[i].setScore(score2);
		}

		System.out.println("number of iteration: " + count);
		return terms;
	}
}
