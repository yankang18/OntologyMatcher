package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

public class BasicWordIC implements IWordIC {

	@Override
	public double getIC(String word) {
		return 1;
	}

}