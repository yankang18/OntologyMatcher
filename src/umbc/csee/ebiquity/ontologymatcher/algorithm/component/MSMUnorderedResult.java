package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.HashMap;

public class MSMUnorderedResult extends MSMResult {
	private HashMap<String, ArrayList<SubMapping>> s2tOptionMap = new HashMap<String, ArrayList<SubMapping>>();

	public void addS2TOptionMap(String sXPath, SubMapping map) {
		ArrayList<SubMapping> optionalMapList;
		if (s2tOptionMap.containsKey(sXPath)) {
			optionalMapList = s2tOptionMap.get(sXPath);
		} else {
			optionalMapList = new ArrayList<SubMapping>();
			s2tOptionMap.put(sXPath, optionalMapList);
		}
		optionalMapList.add(map);
	}
	
	public ArrayList<SubMapping> getS2TOptionMap(String sXPath) {
		if (s2tOptionMap.containsKey(sXPath)) {
			return s2tOptionMap.get(sXPath);
		}
		ArrayList<SubMapping> optionalMapList = new ArrayList<SubMapping>();
		s2tOptionMap.put(sXPath, optionalMapList);
		return optionalMapList;
	}

}
