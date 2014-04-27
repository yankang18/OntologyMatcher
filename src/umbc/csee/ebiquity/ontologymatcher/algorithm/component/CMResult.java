package umbc.csee.ebiquity.ontologymatcher.algorithm.component;


public class CMResult{
	
	private double similarity = 0.0; 
	private double labelSimilarity = 0.0;
	private double propertySimilarity = 0.0;
	private MSMResult propertySetMatchingResult = null;
	private String sClassLocalName = "";
	private String tClassLocalName = "";
	private String sClassRevisedName = "";
	private String tClassRevisedName = "";
	private String sClassURI = "";
	private String tClassURI = "";
	private int sPropertySetSize = 0;
	private int tPropertySetSize = 0;
	private boolean isCompleted = false;
	
	public CMResult(double similarity, MSMResult propertySetMatchingResult) {
		this.similarity = similarity;
		this.propertySetMatchingResult = propertySetMatchingResult;
	}
	
	public CMResult(double similarity){
		this.similarity = similarity;
		this.propertySetMatchingResult = null;
	}

	//
	public void setSourceClassLocalName(String sClassLocalName) {
		this.sClassLocalName = sClassLocalName;
	}

	public String getSourceClassLocalName() {
		return sClassLocalName;
	}

	public void setTargetClassLocalName(String tClassLocalName) {
		this.tClassLocalName = tClassLocalName;
	}

	public String getTargetClassLocalName() {
		return tClassLocalName;
	}
	
	
	//
	public void setSourceClassRevisedName(String sClassRevisedName) {
		this.sClassRevisedName = sClassRevisedName;
	}

	public String getSourceClassRevisedName() {
		return sClassRevisedName;
	}

	public void setTargetClassRevisedName(String tClassRevisedName) {
		this.tClassRevisedName = tClassRevisedName;
	}

	public String getTargetClassRevisedName() {
		return tClassRevisedName;
	}
	

	public void setSourceClassURI(String sClassURI) {
		this.sClassURI = sClassURI;
	}

	public String getSourceClassURI() {
		return sClassURI;
	}

	public void setTargetClassURI(String tClassURI) {
		this.tClassURI = tClassURI;
	}

	public String getTargetClassURI() {
		return tClassURI;
	}
	
	public double getClassSimilarity(){
		return this.similarity;
	}
	
	public MSMResult getPropertySetMatchingResult(){
		return this.propertySetMatchingResult;
	}

	public void setSourcePropertySetSize(int sourcePropertySetSize) {
		this.sPropertySetSize = sourcePropertySetSize;
	}

	public int getSourcePropertySetSize() {
		return sPropertySetSize;
	}

	public void setTargetPropertySetSize(int targetPropertySetSize) {
		this.tPropertySetSize = targetPropertySetSize;
	}

	public int getTargetPropertySetSize() {
		return tPropertySetSize;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setLabelSimilarity(double labelSimilarity) {
		this.labelSimilarity = labelSimilarity;
	}

	public double getLabelSimilarity() {
		return labelSimilarity;
	}

	public void setPropertySimilarity(double propertySimilarity) {
		this.propertySimilarity = propertySimilarity;
	}

	public double getPropertySimilarity() {
		return propertySimilarity;
	}
}
