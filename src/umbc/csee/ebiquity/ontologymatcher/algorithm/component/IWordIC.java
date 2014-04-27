package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

/**
 * An interface to get the information content for a given word
 * 
 * @author Jaewook
 */
public interface IWordIC {
	/**
	 * @param word
	 *            the string word to be asked for its information contents
	 * @return information contents of double type
	 */
	public double getIC(String word);
}