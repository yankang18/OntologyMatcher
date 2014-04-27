package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.List;

public class ClassExpression {
	public enum ClassExpressionType {
		NamedClass, Union, Intersection, ComplementOf, AllValuesFromNamedClass, AllValuesFromUnion, AllValuesFromIntersection, AllValuesFromOther, SomeValuesFromNamedClass, 
		SomeValuesFromUnion, SomeValuesFromIntersection, SomeValuesFromOther, HasValueFrom, MaxCardinality, MinCardinality, Cardinality, Other
	}
	
	private ClassExpressionType type;
	private List<OntResourceInfo> namedClassList;
	private int cardinality;
	
	public ClassExpression(ClassExpressionType type){
		this.type = type;
		this.setNamedClassList(new ArrayList<OntResourceInfo>());
	}

	public void setNamedClassList(List<OntResourceInfo> namedClassList) {
		this.namedClassList = namedClassList;
	}

	public List<OntResourceInfo> getNamedClassList() {
		return namedClassList;
	}
	
	public ClassExpressionType getClassExpressionType(){
		return this.type;
	}
	 
	public String getClassExpressionTypeName(){
		return type.toString();
	}
	
	@Override
	public String toString(){
		return this.getClassExpressionTypeName();
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	public int getCardinality() {
		return cardinality;
	}

}
