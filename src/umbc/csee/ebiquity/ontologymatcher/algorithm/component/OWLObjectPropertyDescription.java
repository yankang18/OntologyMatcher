package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;

public class OWLObjectPropertyDescription {
	
	public enum ObjectPropertyRelationType{ 
		Domain, Range, InverseProperty, SuperProperty
	}
	
	private List<ClassExpression> classExpressions;
//	private List<OWLObjectPropertyDescription> relatedProperties;
	private List<OntResourceInfo> relatedProperties;
	private ObjectPropertyRelationType type;
	
	public OWLObjectPropertyDescription(ObjectPropertyRelationType type){
		this.setRelationType(type);
	} 

	public void setClassExpressions(List<ClassExpression> classExpressions) {
		this.classExpressions = classExpressions;
	}

	public List<ClassExpression> getClassExpressions() {
		return classExpressions;
	}
	
	public void setRelatedProperties(List<OntResourceInfo> relatedProperties) {
		this.relatedProperties = relatedProperties;
	}

	public List<OntResourceInfo> getRelatedProperties() {
		return relatedProperties;
	}

	public void setRelationType(ObjectPropertyRelationType type) {
		this.type = type;
	}
 
	public ObjectPropertyRelationType getRelationType() {
		return type;
	}

	public String getRelationTypeName(){
		return type.toString();
	}
}
