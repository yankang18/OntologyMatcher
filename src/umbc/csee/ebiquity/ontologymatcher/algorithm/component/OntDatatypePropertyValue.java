package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.DatatypeMatcher.Datatype;

public class OntDatatypePropertyValue implements OntPropertyValue {

	private Datatype datatype;
	private String value;
	
	public OntDatatypePropertyValue(Datatype datatype, String value){
		this.datatype = datatype;
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public boolean isDatatypePropertyValue() {
		return true;
	}

	@Override
	public boolean isObjectPropertyValue() {
		return false;
	}

	@Override
	public OntDatatypePropertyValue asOntDatatypePropertyValue() {
		return this;
	}

	@Override
	public OntObjectPropertyValue asOntObjectPropertyValue() {
		return null;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	

}
