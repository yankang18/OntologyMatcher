package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;


/***
 * A very simple class to create semantic links between classes.
 * @author kangyan2003
 */
public class SemanticLinkCreator {

	private ResourceModel sModel;
	private ResourceModel tModel;
	private double classEqualThreshold = 0.92;
	private double propertySimilarityThreshold = 0.75;
	
	private StringBuilder sb;

	public SemanticLinkCreator(ResourceModel sr, ResourceModel tr) {
		this.sModel = sr;
		this.tModel = tr;
	}
	
	public String getDetails(){
		return this.sb.toString();
	}

	public String createLinkBetweenClasses(ClassMapping mapping) {

		double overallSim = mapping.getSimilarity();
		double labelSim = mapping.getLabelSimilarity();
		double propSim = mapping.getPropertySimilarity();
		if (overallSim >= this.classEqualThreshold) {
			return "Exact Match";
		} else {
			if (labelSim >= 0.92 || propSim >= 0.92) {
				return "Close Match";
			} else {
				if (overallSim >= 0.5) {
					return "Related Match";
				} else {
					return "No Match";
				}
			}
		}
	}
	
	public String createLink(String sURI, String tURI) {

		sb = new StringBuilder();
		ComplexOntologyMatchingAlgorithm cgm = new ComplexOntologyMatchingAlgorithm(sModel, tModel);
		CMResult result = cgm.getClassMatchingResult(sURI, tURI);

		double classSimilarity = result.getClassSimilarity();
		MSMResult propertySetMatchingResult = result.getPropertySetMatchingResult();
//		double propertySetSimilarity = propertySetMatchingResult.getSimilarity();

		double sPropertySetSize = result.getSourcePropertySetSize();
		double tPropertySetSize = result.getTargetPropertySetSize();
		double maxPropetySetSize = 0.0;
		if (sPropertySetSize >= tPropertySetSize) {
			maxPropetySetSize = sPropertySetSize;
		} else {
			maxPropetySetSize = tPropertySetSize;
		}

		double minPropertySetSize = 0.0;
		if (sPropertySetSize >= tPropertySetSize) {
			minPropertySetSize = tPropertySetSize;
		} else {
			minPropertySetSize = sPropertySetSize;
		}

		int numOfMatchedPropertyPair = 0;
		if (propertySetMatchingResult != null) {

			ArrayList<SubMapping> mappingList = propertySetMatchingResult.getSubMappings();
			for (int i = 0; i < mappingList.size(); i++) {
				SubMapping subMapping = mappingList.get(i);

				sb.append(subMapping.s.getLocalName() + " :  "
						+ subMapping.t.getLocalName() + " WITH SIMILARITY " + subMapping.getSimilarity()+ " \n");
				
				if (subMapping.getSimilarity() >= propertySimilarityThreshold) {
					numOfMatchedPropertyPair++;
				}
			}
		}

		if (classSimilarity >= this.classEqualThreshold) {
			return "Equal";
		} else {

			if (numOfMatchedPropertyPair / maxPropetySetSize >= 0.66) {

				return "Equal";

			} else {

				if (numOfMatchedPropertyPair / minPropertySetSize >= 0.66) {

					if (sPropertySetSize <= tPropertySetSize) {
						return "subClassOf";
					} else {
						return "superClassOf";
					}

				} else {
					return "relatedMathch";
				}

			}

		}
	}
}
