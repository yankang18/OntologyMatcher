package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;

public class ThreePointTwig extends Twig {

	private OntClassInfo intersectPoint;
	private Type type;
	private List<Path> pathList1;
	private List<Path> pathList2;

	public ThreePointTwig(List<Path> pathList1, List<Path> pathList2, Type type) throws Exception {
		super(type, pathList1.get(0).getNumberOfNodes() + pathList2.get(0).getNumberOfNodes());
		this.type = type;
		this.pathList1 = pathList1;
		this.pathList2 = pathList2;
		if (this.type == Type.ThreePointTwig_Forward) {
			super.setEndPointOne(pathList1.get(0).getStart());
			super.setEndPointTwo(pathList2.get(0).getStart());
			this.intersectPoint = pathList1.get(0).getEnd();
		} else if (this.type == Type.ThreePointTwig_Backward) {
			super.setEndPointOne(pathList1.get(0).getEnd());
			super.setEndPointTwo(pathList2.get(0).getEnd());
			this.intersectPoint = pathList1.get(0).getStart();
		} else {
			throw new Exception("Wrong Type");
		}
	}

	public OntClassInfo getIntersetPoint(){
		return this.intersectPoint;
	}
	
	public int getSizeOfPathListOne(){
		return this.pathList1.size();
	}
	
	public int getSizeOfPathListTwo(){
		return this.pathList2.size();
	}
	
	public Path getPathFromListOne(int index){
		return pathList1.get(index);
	}
	
	public Path getPathFromListTwo(int index){
		return pathList2.get(index);
	}

}
