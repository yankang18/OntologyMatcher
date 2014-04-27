package umbc.csee.ebiquity.ontologymatcher.textprocessing;

public class Term {

	private String word;
	private double score;
	private int position;
	private int positionInContent;

	public Term(String word, double score, int position) {
		this.word = word.trim();
		this.score = score;
		this.position = position;
	}

	public Term(String word, int position) {
		this.word = word.trim();
		this.score = 0.0;
		this.position = position;
	}

	public String getWord() {
		return this.word;
	}

	public double getScore() {
		return this.score;
	}
	
	public void boostScore(double factor) {

		double boostingFactor = factor;
		if (factor > 10.0) {
			boostingFactor = 10.0;
		} else if (factor < 0.0) {
			boostingFactor = 0.0;
		}

		this.score = (1 + boostingFactor) * this.score;
	}

	public int getPosition() {
		return this.position;
	}

	public int getPositionInContent() {
		return this.positionInContent;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setPositionInContent(int position) {
		this.positionInContent = position;
	}

	@Override
	public String toString() {
		return "[\"" + this.word + "\", in position of content "
				+ this.positionInContent + ", with score " + this.score + "]";
	}

}
