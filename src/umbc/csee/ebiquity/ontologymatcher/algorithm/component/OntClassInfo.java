package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.List;


public class OntClassInfo extends OntResourceInfo  {
	
	public enum ClassType {
		NamedClass, UnionClass, IntersectionClass, EnumeratedClass, ComplementClass, Restriction
	}
	
	private List<OntClassInfo> operands;
	private ClassType classType;
	private String description;

	/**
	 * 
	 * @param URI
	 * @param namespace
	 * @param localname
	 * @param type
	 */
	public OntClassInfo(String URI, String namespace, String localname, ClassType type) {
		super(URI, namespace, localname);
		this.operands = new ArrayList<OntClassInfo>();
		this.classType = type;
	}
	
	public void addOperands(OntClassInfo ontClassInfo){
		this.operands.add(ontClassInfo);
	}

	public ClassType getClassType() {
		return classType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
