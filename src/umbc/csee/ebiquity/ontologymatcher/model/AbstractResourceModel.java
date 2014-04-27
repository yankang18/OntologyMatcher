package umbc.csee.ebiquity.ontologymatcher.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.vocabulary.OWL;

public abstract class AbstractResourceModel implements ResourceModel {

	protected static List<String> excludedClassUriList;

	static {

		excludedClassUriList = new ArrayList<String>();
		excludedClassUriList.add(OWL.Class.getURI());
		excludedClassUriList.add(OWL.Thing.getURI());
	}
}
