package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;

public class OntResourceInfo {
	
	public enum ResourceType {
		ObjectProperty, DatatypeProperty, NamedClass, UnionClass, 
		IntersectionClass, EnumeratedClass, ComplementClass, Restriction
	}
	
	protected String localName;
	protected String Namespace;
	protected String URI;
	private String tokenizedName;
	protected List<String> descriptions;
	private ResourceType type;
	private int level;
	
	/**
	 * 
	 * @param URI
	 * @param namespace
	 * @param localname
	 */
	public OntResourceInfo(String URI, String namespace, String localname){
		this.URI = URI.trim();
		this.Namespace = namespace.trim();
		this.localName = localname.trim();	
	}

	public OntResourceInfo(String URI, String namespace, String localname, ResourceType type) {
		this(URI, namespace, localname);
		this.type = type;
	}
	
	public OntResourceInfo(){}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return this.level;
	}

	public String getLocalName() {
		return localName;
	}

	public String getNamespace() {
		return Namespace;
	}

	public String getURI() {
		return URI;
	}
	
	public void setLocalName(String localName){
		this.localName = localName;
	}
	
	public void setNamespace(String NS){
		this.Namespace = NS;
	}
	
	public void setURI(String URI){
		this.URI = URI;
	}
	
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setResourceType(ResourceType type) {
		this.type = type;
	}

	public ResourceType getResourceType() {
		return type;
	}
	
	@Override
	public String toString(){
		return this.URI;
	}
	
	@Override
	public int hashCode(){
		return this.URI.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		OntResourceInfo res = (OntResourceInfo) obj;
		if (this.getURI().equals(res.getURI())) {
			return true;
		} else {
			return false;
		}
	}

	public void setTokenizedName(String tokenizedName) {
		this.tokenizedName = tokenizedName;
	}

	public String getTokenizedName() {
		return tokenizedName;
	}
}
