package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComparedPathRecorder {

	private List<String> records = new ArrayList<String>();
	private HashMap<String, Double> classPair_similarity_map = new HashMap<String, Double>();
	private HashMap<String, CMResult> classPair_cmresult_map = new HashMap<String, CMResult>();

	public void pushComparedNodes(String sUri, String tUri) {
//		sResources.add(sUri);
//		tResources.add(tUri);
		String str = sUri.trim() + "&" + tUri.trim();
//		System.out.println("push in " + str);
		records.add(str);
		classPair_similarity_map.put(str, null);
		
	}

	public void setValue(String sUri, String tUri, double similarity){
	
		String key = sUri.trim() + "&" + tUri.trim();
		Double value = classPair_similarity_map.get(key);
		if(value == null){
			classPair_similarity_map.put(key, similarity);
		}
		
	}
	
	public void setCMResult(String sUri, String tUri, CMResult cmResult){
		
		String key = sUri.trim() + "&" + tUri.trim();
		
		CMResult value = classPair_cmresult_map.get(key);
//		if(value == null){
			classPair_cmresult_map.put(key, cmResult);
//		}
		
	}
	
	public Double getValue(String sUri, String tUri){
		
		String key = sUri.trim() + "&" + tUri.trim();
		Double value = classPair_similarity_map.get(key);
		if(value != null){
			return value.doubleValue();
		} else {
			return null;
		}	
	}
	

	public CMResult getCMResult(String sUri, String tUri) {
		String key = sUri.trim() + "&" + tUri.trim();
		return classPair_cmresult_map.get(key);
	}
	
	@Deprecated
	public void popComparedNodes() {
		records.remove(records.size() - 1);
	}

	public boolean isCompared(String sUri, String tUri) {

		String str = sUri.trim() + "&" + tUri.trim();
		if (records.contains(str.trim())) {
			return true;
		}
		return false;
	}
}