package umbc.csee.ebiquity.ontologymatcher.textprocessing;


public interface Matcher {

	public double getSimilarityScore(String query);

	/***
	 * 
	 * @param query
	 * @param perserveOrder
	 * @param perserveTermsLength
	 * @return
	 */
	public double getSimilarityScore(String query, boolean perserveOrder,
			boolean perserveTermsLength);
}
