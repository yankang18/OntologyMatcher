package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

/**
 * An interface to get the similarity between two labels which consist of
 * multiple words.
 * 
 * @author Jaewook
 * 
 */
public interface ILabelSimilarity {

	/**
	 * @param label1
	 * @param label2
	 * @return
	 */
	public abstract double getSimilarity(String label1, String label2);
	
	public abstract MSMResult getMapping(String label1, String label2);
	
	public abstract MSMResult getMapping(String label1, String label2, boolean includeWordInfo);

	/**
	 * @param wordIC
	 */
	public abstract void setWordICHandler(IWordIC wordIC);

	public abstract MSMResult getAtomicMapping(String string, String string2);

	public abstract int getWordsetSize(String label);
}