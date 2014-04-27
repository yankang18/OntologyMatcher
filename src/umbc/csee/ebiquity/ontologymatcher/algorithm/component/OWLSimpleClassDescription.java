package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;

public class OWLSimpleClassDescription {
	
	private String classRelName;
	private List<SimpleClassExpression> classExpressions;
	public OWLSimpleClassDescription(String classRelationName){
		this.setClassRelationName(classRelationName);
	}
	public void setClassExpressions(List<SimpleClassExpression> classExpressions) {
		this.classExpressions = classExpressions;
	}
	public List<SimpleClassExpression> getClassExpressions() {
		return classExpressions;
	}
	public void setClassRelationName(String classRelName) {
		this.classRelName = classRelName;
	}
	public String getClassRelationName() {
		return classRelName;
	}

}
