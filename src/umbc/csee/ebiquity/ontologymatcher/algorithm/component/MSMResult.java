package umbc.csee.ebiquity.ontologymatcher.algorithm.component;
import java.util.ArrayList;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;


public class MSMResult {
	private double similarity = 0;
	private ArrayList<SubMapping> subMapSimilarities = new ArrayList<SubMapping>();

	public static class SubMapping implements Comparable<SubMapping> {
		
		public SimilarityMatrixLabel s;
		public SimilarityMatrixLabel t;
		private double similarity;
		
		private String sRangeUri;
		private String tRangeUri;
		private OntPropertyType sPropertyType;
		private OntPropertyType tPropertyType;
		private OntPropertyValue sPropertyValue;
		private OntPropertyValue tPropertyValue;
		private int sRangeSize;
		private int tRangeSize;
		private Double propertyValueSimilarity;

		public SubMapping(SimilarityMatrixLabel source,
				SimilarityMatrixLabel target, double similarity) {
			this.s = source;
			this.t = target;
			this.setSimilarity(similarity);
		}
		
		public String getSourceRangeUri(){
			return this.sRangeUri;
		}
		
		public String getTargetRangeUri(){
			return this.tRangeUri;
		}
		
		public void setSourceRangeURI(String sourceURI){
			this.sRangeUri = sourceURI;
		}
		
		public void setTargetRangeURI(String targetURI){
			this.tRangeUri = targetURI;
		}
		
		public void setSourcePropertyType(OntPropertyType sPropertyType) {
			this.sPropertyType = sPropertyType;
		}

		public OntPropertyType getSourcePropertyType() {
			return sPropertyType;
		}

		public void setTargetPropertyType(OntPropertyType tPropertyType) {
			this.tPropertyType = tPropertyType;
		}

		public OntPropertyType getTargetPropertyType() {
			return tPropertyType;
		}
		
		public void setSourceRangeSize(int sRangeSize) {
			this.sRangeSize = sRangeSize;
		}

		public int getSourceRangeSize() {
			return sRangeSize;
		}

		public void setTargetRangeSize(int tRangeSize) {
			this.tRangeSize = tRangeSize;
		}

		public int getTargetRangeSize() {
			return tRangeSize;
		}
		
		public double getSimilarity(){
			return this.similarity;
		}
		
		@Override
		public int compareTo(SubMapping subMapping) {
			if (this.getSimilarity() > subMapping.getSimilarity()) {
				return -1;
			} else {
				return 1;
			}
		}

		public void setSimilarity(double similarity) {
			this.similarity = similarity;
		}

		public void setSourcePropertyValue(OntPropertyValue sPropertyValue) {
			this.sPropertyValue = sPropertyValue;
		}

		public OntPropertyValue getSourcePropertyValue() {
			return sPropertyValue;
		}

		public void setTargetPropertyValue(OntPropertyValue tPropertyValue) {
			this.tPropertyValue = tPropertyValue;
		}

		public OntPropertyValue getTargetPropertyValue() {
			return tPropertyValue;
		}

		public void setPropertyValueSimilarity(Double propertyValueSimilarity) {
			this.propertyValueSimilarity = propertyValueSimilarity;
		}

		public Double getPropertyValueSimilarity() {
			return this.propertyValueSimilarity;
		}
	}

	public MSMResult() {

	}

	public MSMResult(double sim) {
		similarity = sim;
	}

	public MSMResult(ArrayList<SubMapping> subMapping, double similarity) {
		subMapSimilarities = subMapping;
		this.setSimilarity(similarity);
	}

	public ArrayList<SubMapping> getSubMappings() {
		return subMapSimilarities;
	}
	
//	public void addSubMappings(SimilarityMatrixLabel rLabel,
//			SimilarityMatrixLabel cLabel, double similarity) {
//		subMapSimilarities.add(new SubMapping(rLabel, cLabel, similarity));
//	}

	public void setSubMappings(ArrayList<SubMapping> subMapSimilarities) {
		this.subMapSimilarities = subMapSimilarities;
	}

	public void clearSubMappings() {
		subMapSimilarities.clear();
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public double getSimilarity() {
		return similarity;
	}
}
