package umbc.csee.ebiquity.ontologymatcher.model;

import java.util.ArrayList;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IndividualTypeInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel.ResourceLevel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;


public interface ResourceModel {
	
	public enum ResourceLevel { 
		INSTANCE, CLASS
	}


	public List<String> getCategoryInformation();
	
	public List<String> listAllProperties();

	public List<String> listSuperClasses();
	
	public void prepareResource(String Uri) throws ResourceNotFoundException;

	public String getResourceUri_curr();

	public List<OntClassInfo> listTopLevelClasses();

	public List<String> listNamedEquivalentClasses(String classURI);

	public List<OntResourceInfo> listSuperClasses(String classURI, boolean direct);
	public List<OntResourceInfo> listSubClasses(String classURI, boolean direct);

	public List<OntPropertyInfo> listAllOntProperties();

	public OntPropertyInfo[] listAllOntPropertiesAsArray();

	public List<OntPropertyInfo> listOntClassProperties();

	public OntPropertyInfo[] listOntClassPropertiesAsArray();

	public List<OntPropertyInfo> listIndividualProperties();

//	public List<OntResourceInfo> listAllIndividuals();

	public OntPropertyInfo[] listIndividualPropertiesAsArray();

	public List<OntResourceInfo> listAllIndividuals(String name);

	public List<IndividualTypeInfo> listIndividualTypeInfos();

	List<OntPropertyInfo> listOntProperties();

	OntPropertyInfo[] listOntPropertiesAsArray();

	List<String> listTypesOfIndividual(String uri, boolean getURI);

//	List<String> getTextualDescriptions(String resourceURI, List<String> propertyURIs);

	List<String> listClassesOfIndividual(String uri, boolean getURI, boolean direct);

	List<String> getTextualDescriptions(String resourceURI);

	List<OntResourceInfo> listAllNamedClasses();

	String getResourceLocalName_curr();

	OntResourceInfo getOntResourceInfo_curr();

	void setDescriptionProperties(List<String> descriptionPropertyURIs);

	List<OWLComplexClassDescription> listOWLComplexClassDescriptions(String URI);

	List<OWLObjectPropertyDescription> listOWLObjectPropertyDescriptions(String URI);

	List<OWLSimpleClassDescription> listOWLSimpleClassDescriptions(String URI);

	List<OWLSimpleObjectPropertyDescription> listOWLSimpleObjectPropertyDescriptions(String URI);

	ResourceLevel getResourceLevel();

	OntClassInfo getOntClass(String URI);

	ArrayList<OntPropertyInfo> listSubPropertiesWithSimpleInfo(String URI);

	ArrayList<OntPropertyInfo> listSuperPropertiesWithSimpleInfo(String URI);

	ArrayList<OntClassInfo> listDomains(String URI);

	List<OntPropertyInfo> listLocalProperties(String classURI, int propertyRangeDepth);

	ArrayList<OntClassInfo> listRanges(String URI);

	List<OntPropertyInfo> listDomainOntClassProperties(String classURI);

	List<OntClassInfo> listImposedRangeOntClasses(OntPropertyInfo ontPropertyInfo);

	List<OntPropertyInfo> listRangeOntClassProperties(String classURI);

	List<OntClassInfo> listImposedDomainOntClasses(OntPropertyInfo ontPropertyInfo);

}
