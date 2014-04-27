package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.List;

public class TwoPointTwig extends Twig {

	private List<Path> paths;

	public TwoPointTwig(List<Path> paths) {
		super(Type.TwoPointTwig, paths.get(0).getNumberOfNodes());
		super.setEndPointOne(paths.get(0).getStart());
		super.setEndPointTwo(paths.get(0).getEnd());
		this.paths = paths;
	}
	
	public int getSizeOfPathList(){
		return paths.size();
	}
	
	public Path getPath(int index){
		return paths.get(index);
	}
}
