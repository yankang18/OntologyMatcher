package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

public interface OntPropertyValue {
	
	public boolean isDatatypePropertyValue();
	public boolean isObjectPropertyValue();
	public OntDatatypePropertyValue asOntDatatypePropertyValue();
	public OntObjectPropertyValue asOntObjectPropertyValue();
	public String getValue();
}
