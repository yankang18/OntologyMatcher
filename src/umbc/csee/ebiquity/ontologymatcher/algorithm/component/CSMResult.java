package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.Collections;


public class CSMResult {
	
	private double overallSimilarity = 0.0;
	private ArrayList<ClassMapping> classMappings = new ArrayList<ClassMapping>();

	public static class ClassMapping implements Comparable<ClassMapping> {

		private CMResult cmResult;
		private String sClassURI;
		private String tClassURI;
		private String matchingStr;

//		@Deprecated
//		public ClassMapping(String sourceClassURI, String targetClassURI, CMResult cmResult) {
//			this.cmResult = cmResult;
//			this.sClassURI = sourceClassURI;
//			this.tClassURI = targetClassURI;
//			this.matchingStr = cmResult.getSourceClassURI().trim() + "+" + cmResult.getTargetClassURI().trim();
//			
//		}
		
		public ClassMapping(CMResult cmResult) {
			this.cmResult = cmResult;
			this.matchingStr = cmResult.getSourceClassURI().trim() + "+" + cmResult.getTargetClassURI().trim();
		}
		
		public String getSourceClassURI(){
			return this.cmResult.getSourceClassURI();
		}
		
		public String getTargetClassURI(){
			return this.cmResult.getTargetClassURI();
		}
		
		public String getSourceClassLocalName(){
			return this.cmResult.getSourceClassLocalName();
		}
		
		public String getTargetClassLocalName(){
			return this.cmResult.getTargetClassLocalName();
		}
		
		public double getSimilarity(){
			return this.cmResult.getClassSimilarity();
		}
		
		public double getLabelSimilarity(){
			return this.cmResult.getLabelSimilarity();
		}
		
		public double getPropertySimilarity(){
			return this.cmResult.getPropertySimilarity();
		}

		@Override
		public int compareTo(ClassMapping classMapping) {
			if (this.getSimilarity() > classMapping.getSimilarity()) {
				return -1;
			} else {
				return 1;
			}
		}

		@Override
		public String toString() {
			return this.matchingStr;
		}
	}
	
	public CSMResult(ArrayList<ClassMapping> classMappings) {
		this.classMappings = classMappings;
		this.computeOverallSimilarity();
	}
	
	public ArrayList<ClassMapping> getClassMappings() {
		return classMappings;
	}
	
	public double getSimilarity() {
		return overallSimilarity;
	}
	
	private void computeOverallSimilarity(){

		Collections.sort(classMappings);
		if (classMappings.size() == 0) {
			this.overallSimilarity = 0.0;
			return;
		}
		ClassMapping mapping = classMappings.get(0);
		if (mapping != null) {
			this.overallSimilarity = mapping.getSimilarity();
		} else {
			this.overallSimilarity = 0.0;
		}
		
//		int numOfMappings = 0;
//		double sumOfSimilarities = 0.0;
//		for (ClassMapping mapping : classMappings) {
//			numOfMappings++;
//			sumOfSimilarities += mapping.getSimilarity();
//		}
//		this.overallSimilarity = sumOfSimilarities / numOfMappings;
	}
}
