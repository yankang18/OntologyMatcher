package umbc.csee.ebiquity.ontologymatcher.query;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;

public class OnePointTwig extends Twig {

	public OnePointTwig(OntClassInfo endpoint) {
		super(Type.OnePointTwig, 1);
		super.setEndPointOne(endpoint);
		super.setEndPointTwo(endpoint);
	}

}
