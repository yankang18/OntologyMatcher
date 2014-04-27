package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;

/***
 * 
 * This class is used to calculate similarity between taxonomic structure of two classes.
 * @author kangyan2003
 */
public class HierarchyMatcher {
	private ResourceModel sModel;
	private ResourceModel tModel;
	
	private SimpleEntitySetMatcher entitySetMatcher;
	
	public HierarchyMatcher(ResourceModel sModel, ResourceModel tModel){
		this.sModel = sModel;
		this.tModel = tModel;
		this.entitySetMatcher = new SimpleEntitySetMatcher();
	}
	
	public double getSimilairty(String sResourceURI, String tResourceURI){

		List<OntResourceInfo> sSubclasses = sModel.listSubClasses(sResourceURI, true);
		List<OntResourceInfo> tSubclasses = tModel.listSubClasses(tResourceURI, true);
		List<OntResourceInfo> sSuperclasses = sModel.listSuperClasses(sResourceURI, true);
		List<OntResourceInfo> tSuperclasses = tModel.listSuperClasses(tResourceURI, true);

		double subclassSetSimilarity = entitySetMatcher.getEntityMappingSimilarity(sSubclasses, tSubclasses, true);
		double superclassSetSimilarity = entitySetMatcher.getEntityMappingSimilarity(sSuperclasses, tSuperclasses, true);
		return (0.6 * subclassSetSimilarity + 0.4 * superclassSetSimilarity);

	}
}
