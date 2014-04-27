package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription.ObjectPropertyRelationType;

public class OWLSimpleObjectPropertyDescription {
	
	private List<SimpleClassExpression> classExpressions;
	private List<String> relatedProperties;
	private ObjectPropertyRelationType type;
	
	public OWLSimpleObjectPropertyDescription(ObjectPropertyRelationType type){
		this.setRelationType(type);
	}

	public void setClassExpressions(List<SimpleClassExpression> classExpressions) {
		this.classExpressions = classExpressions;
	}

	public List<SimpleClassExpression> getClassExpressions() {
		return classExpressions;
	}

	public void setRelatedProperties(List<String> relatedProperties) {
		this.relatedProperties = relatedProperties;
	}

	public List<String> getRelatedProperties() {
		return relatedProperties;
	} 

	public void setRelationType(ObjectPropertyRelationType type) {
		this.type = type;
	}
  
	public ObjectPropertyRelationType getRelationType() {
		return type; 
	}
	
	public String getRelationTypeName() {
		return type.toString().trim();
	}
}
