package umbc.csee.ebiquity.ontologymatcher.model;

public class MatchResultImpl implements MatchResult {
	
	private double categoryMatchScore;
	private String[] categoryMatched = new String[2];
	private String[] resourceMatched = new String[2];

	public void setCategoryMatchScore(double categoryMatchScore) {
		this.categoryMatchScore = categoryMatchScore;
	}
	
	public void setMatchedCategoryForSource(String category){
		this.categoryMatched[0] = category;
	}
	
	public void setMatchedCategoryForTarget(String category){
		this.categoryMatched[1] = category;
	}
	
	public void setComparedResourceUriForSource(String Uri){
		this.resourceMatched[0] = Uri;
	}
	
	public void setComparedResourceUriForTarget(String Uri){
		this.resourceMatched[1] = Uri;
	}
	
	@Override
	public String[] getMatchedCategories() {
		return this.categoryMatched;
	}
	
	@Override
	public double getCategoryMatchScore() {
		return categoryMatchScore;
	}

	@Override
	public int compareTo(MatchResult o) {

		if (this.categoryMatchScore > o.getCategoryMatchScore()) {
			return -1;
		} else if (this.categoryMatchScore < o.getCategoryMatchScore()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	

	@Override
	public String[] getComparedResources() {
		return resourceMatched;
	}

	
	

}
