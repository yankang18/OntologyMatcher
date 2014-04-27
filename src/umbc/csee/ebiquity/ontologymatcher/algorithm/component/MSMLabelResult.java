package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;

public class MSMLabelResult extends MSMResult{
	private ArrayList<String> nonSynWordList;

	public MSMLabelResult() {
		
	}

	public void set(MSMResult simResult, ArrayList<String> nonSynWordList) {
		super.setSimilarity(simResult.getSimilarity());
		super.setSubMappings(simResult.getSubMappings());
		this.nonSynWordList = nonSynWordList;
	}
	
	public boolean hasWordInfo(String word) {
		if (nonSynWordList.contains(word)) {
			return false;
		} else {
			return true;
		}
	}
	
	public ArrayList<String> getNonSynWordList() {
		return nonSynWordList;
	}
}