package umbc.csee.ebiquity.ontologymatcher.model;

public interface MatchResult extends Comparable<MatchResult>  {

	public double getCategoryMatchScore();

	public String[] getMatchedCategories();
	
	public String[] getComparedResources();


}
