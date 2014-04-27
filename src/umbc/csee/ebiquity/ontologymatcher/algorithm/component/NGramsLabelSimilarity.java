package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;

public class NGramsLabelSimilarity implements ILabelSimilarity {

	@Override
	public double getSimilarity(String label1, String label2) {

		double sim = 0.0;
		QGramsDistance metric = new QGramsDistance(new TokeniserQGram3Extended());
		sim = metric.getSimilarity(label1, label2);
		return sim;
	}

	@Override
	public MSMResult getMapping(String label1, String label2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MSMResult getMapping(String label1, String label2, boolean includeWordInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWordICHandler(IWordIC wordIC) {
		// TODO Auto-generated method stub

	}

	@Override
	public MSMResult getAtomicMapping(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getWordsetSize(String label) {
		// TODO Auto-generated method stub
		return 0;
	}

}
