package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression.ClassExpressionType;

public class SimpleClassExpression {
	private ClassExpressionType type;
	public SimpleClassExpression(ClassExpressionType type){
		this.type = type;
	}
	public String getClassExpressionTypeName(){
		return this.type.toString().trim();
	}
	
	public ClassExpressionType getClassExpressionType(){
		return this.type;
	}
	
}
