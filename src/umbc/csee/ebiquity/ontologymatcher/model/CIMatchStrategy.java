package umbc.csee.ebiquity.ontologymatcher.model;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.textprocessing.Matcher;
import umbc.csee.ebiquity.ontologymatcher.textprocessing.TextContentMatcher;
import umbc.csee.ebiquity.ontologymatcher.textprocessing.TextContentMatcher.MatchMethod;


public class CIMatchStrategy implements MatchStrategy {
	
	private static final double categoryMatchThreshold = 0.70;
	private MatchResultImpl result;
	public CIMatchStrategy(ResourceModel sr, ResourceModel tr){
		
		List<String> sCategoryList = sr.listSuperClasses();
		List<String> tCategoryList = tr.getCategoryInformation();
		
		
		System.out.println("source resource - ");
		for (String sc : sCategoryList) {
			
			System.out.println(" : " + sc);
			
		}
		
		System.out.println("target resource - ");
		for (String tc : tCategoryList) {
			
			System.out.println(" : " + tc);
		}
		
		result = new MatchResultImpl();
		result.setComparedResourceUriForSource(sr.getResourceUri_curr());
        result.setComparedResourceUriForTarget(tr.getResourceUri_curr());
		double score = this.categoryMatching(sCategoryList, tCategoryList);
		
		if(score < categoryMatchThreshold){
			this.descriptionMatching();
		}
		
		System.out.println("score: " + score);
	}
	
	private void descriptionMatching() {
		// TODO Auto-generated method stub
		
	}

	/***
	 * MSMNodePathSimilarity.RangeSetMatching() also use this method. Therefore we can refactor this method.
	 * 
	 * @param sCategories
	 * @param tCategories
	 * @return
	 */
	private double categoryMatching(List<String> sCategories, List<String> tCategories) {

		double similary = 0.0;
		for (String sCategory : sCategories) {
			Matcher matcher = TextContentMatcher.compile(sCategory, MatchMethod.SYNONYM);
			for (String tCategory : tCategories) {

				double score = matcher.getSimilarityScore(tCategory, false, true);
				if (similary < score) {
					similary = score;
					result.setCategoryMatchScore(similary);
					result.setMatchedCategoryForSource(sCategory);
					result.setMatchedCategoryForTarget(tCategory);
				}
			}

		}
		return similary;
	}
	
	@Override
	public MatchResult getMatchResult(){
		return this.result;
	}

}
