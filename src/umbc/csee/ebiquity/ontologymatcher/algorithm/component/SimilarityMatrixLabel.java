package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

public class SimilarityMatrixLabel {
	private String localName;
	private String revisedLabel;
	private String Uri;
	private String nameSpace;
	private double ic = 1;
	private Object obj;

	public SimilarityMatrixLabel(String name) {
		this.setLocalName(name);
	}

	public double getIC() {
		return ic;
	}
	
	/***
	 * Get the full path name of this label. The full path name is the path from
	 * the root to this label
	 * 
	 * @return full path name as string
	 */
	public String getRevisedName() {
		return revisedLabel;
	}
	
	public String getLocalName(){
		return this.localName;
	}
	
	public Object getObject() {
		return obj;
	}

	public void setRevisedName(String name) {
		this.revisedLabel = name;
	}

	public void setIC(double ic) {
		this.ic = ic;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}
	
	private void setLocalName(String localName){
		this.localName = localName;
	}

	public void setURI(String uri) {
		Uri = uri;
	}

	public String getURI() {
		return Uri;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getNameSpace() {
		return nameSpace;
	}

}