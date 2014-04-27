package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

public class OntObjectPropertyValue implements OntPropertyValue {

	private String value;
	private String namespace;
	private String localname;
	
	public OntObjectPropertyValue(String value, String namespace, String localname){
		this.value = value;
		this.namespace = namespace;
		this.localname = localname;
	}
	
	@Override
	public boolean isDatatypePropertyValue() {
		return false;
	}

	@Override
	public boolean isObjectPropertyValue() {
		return true;
	}

	@Override
	public OntDatatypePropertyValue asOntDatatypePropertyValue() {
		return null;
	}

	@Override
	public OntObjectPropertyValue asOntObjectPropertyValue() {
		return this;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getLocalname() {
		return localname;
	}

}
