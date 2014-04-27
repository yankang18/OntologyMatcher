package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;

public class SimilarityMatrixCell {

	private double similarity;
	private SimilarityMatrixLabel rLabel;
	private SimilarityMatrixLabel cLabel;
	private String sRangeUri;
	private String tRangeUri;
	private OntPropertyType sPropertyType;
	private OntPropertyType tPropertyType;
	private int sRangeSize;
	private int tRangeSize;
	private OntPropertyValue sOntPropertyValue;
	private OntPropertyValue tOntPropertyValue;
	private Double propertyValueSimilarity;

	public SimilarityMatrixCell(SimilarityMatrixLabel rLabel, SimilarityMatrixLabel cLabel, double similarity) {
		this.rLabel = rLabel;
		this.cLabel = cLabel;
		this.similarity = similarity;
	}

	public double getSimilarity() {
		return similarity;
	}
	
	public SimilarityMatrixLabel getRow() {
		return rLabel;
	}
	
	public SimilarityMatrixLabel getCol() {
		return cLabel;
	}

	public void setSourceRangeUri(String sRangeUri) {
		this.sRangeUri = sRangeUri;
	}

	public String getSourceRangeUri() {
		return sRangeUri;
	}

	public void setTargetRangeUri(String tRangeUri) {
		this.tRangeUri = tRangeUri;
	}

	public String getTargetRangeUri() {
		return tRangeUri;
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

	public void setSourceOntPropertyValue(OntPropertyValue sOntPropertyValue) {
		this.sOntPropertyValue = sOntPropertyValue;
	}

	public OntPropertyValue getSourceOntPropertyValue() {
		return sOntPropertyValue;
	}

	public void setTargetOntPropertyValue(OntPropertyValue tOntPropertyValue) {
		this.tOntPropertyValue = tOntPropertyValue;
	}

	public OntPropertyValue getTargetOntPropertyValue() {
		return tOntPropertyValue;
	}

	public void setPropertyValueSimilarity(Double propertyValueSimilarity) {
		this.propertyValueSimilarity = propertyValueSimilarity;
	}

	public Double getPropertyValueSimilarity() {
		return this.propertyValueSimilarity;
	}
	
	
}
