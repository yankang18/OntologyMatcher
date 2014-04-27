package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.List;

public class OWLComplexClassDescription {
	
	private String classRelName;
	private List<ClassExpression> classExpressions;
	public OWLComplexClassDescription(String classRelationName){
		this.setClassRelationName(classRelationName);
	}
	public void setClassExpressions(List<ClassExpression> classExpressions) {
		this.classExpressions = classExpressions;
	}
	public List<ClassExpression> getClassExpressions() {
		return classExpressions;
	}
	public void setClassRelationName(String classRelName) {
		this.classRelName = classRelName;
	}
	public String getClassRelationName() {
		return classRelName;
	}

}
