package umbc.csee.ebiquity.ontologymatcher.query;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;

public class Twig {

	public enum Type {
		
		ThreePointTwig_Backward(4), ThreePointTwig_Forward(3), TwoPointTwig(2), OnePointTwig(1);
		private int typeCode;

		private Type(int typeCode) {
			this.typeCode = typeCode;
		}

		public int getCode() {
			return this.typeCode;
		}
	}
	
	private OntClassInfo endpoint1;
	private OntClassInfo endpoint2;
	private int numberOfNodes;
	private Type type;
	
	// just for test
	private String key;
	
	public Twig(Type type, int numberOfNodes){
		this.type = type;
		this.numberOfNodes = numberOfNodes;
	}
	
	// this constructor just for test!
	public Twig(int numberOfNodes, String key) {
		this.numberOfNodes = numberOfNodes;
		this.key = key;
	}
	
	public Type getType(){
		return this.type;
	}
	public OntClassInfo getEndPointOne(){
		return endpoint1;
	}
	
	public OntClassInfo getEndPointTwo(){
		return endpoint2;
	}
	
	protected void setEndPointOne(OntClassInfo endpoint1) {
		this.endpoint1 = endpoint1;
	}

	protected void setEndPointTwo(OntClassInfo endpoint2) {
		this.endpoint2 = endpoint2;
	}
	
	

	public boolean isOnePointTwig() {
		if (this.type == Type.OnePointTwig) {
			return true;
		}
		return false;
	}
	
	public boolean isTwoPointTwig() {
		if (this.type == Type.TwoPointTwig) {
			return true;
		}
		return false;
	}

	public boolean isThreePointTwig() {
		if (this.type == Type.ThreePointTwig_Backward || this.type == Type.ThreePointTwig_Forward) {
			return true;
		}
		return false;
	}
	
	public OnePointTwig asOnePointTwig() {
		return (OnePointTwig) this;
	}

	public TwoPointTwig asTwoPointTwig() {
		return (TwoPointTwig) this;
	}

	public ThreePointTwig asThreePointTwig() {
		return (ThreePointTwig) this;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public String getKey() {
		return key;
	}
}
