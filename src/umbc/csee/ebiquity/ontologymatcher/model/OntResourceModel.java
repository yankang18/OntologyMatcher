package umbc.csee.ebiquity.ontologymatcher.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.jena.PelletInfGraph;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression.ClassExpressionType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IndividualTypeInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLObjectPropertyDescription.ObjectPropertyRelationType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleObjectPropertyDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo.ClassType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo.ResourceType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.StopWordsRemover;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.DatatypeMatcher;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;
import umbc.csee.ebiquity.ontologymatcher.utility.Namespace;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/***
 * 
 * RDF graph about a resource
 * 
 * @author kangyan2003
 */
public class OntResourceModel extends AbstractResourceModel {

	private Model model;
	private OntModel ontModel;
	private OntModel inferredModel = null;
	private Resource resource;
	private OntResourceInfo ontResourceInfo;
	private ResourceLevel resLevel; 
	private String resUri;
	private List<String> descriptionProperties = new ArrayList<String>();
	
	private ArrayList<String> globalPropertyList = new ArrayList<String>();
	private HashMap<String, ArrayList<OntPropertyInfo>> UnionDomain_Properties_Map = new HashMap<String, ArrayList<OntPropertyInfo>>();
	private HashMap<String, ArrayList<OntPropertyInfo>> AllDomain_Properties_Map = new HashMap<String, ArrayList<OntPropertyInfo>>();
	private HashMap<String, ArrayList<String>> Property_Domains_Map = new HashMap<String, ArrayList<String>>();


	public OntResourceModel(String urlstring) throws IOException {
		this.createModel();
		URL url = new URL(urlstring);
		URLConnection uricon = url.openConnection();
		uricon.addRequestProperty("accept", "application/rdf+xml");
		InputStream instream = uricon.getInputStream();
		model.read(instream, "");
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		this.getGlobalProperties(ontModel);
	}

	public OntResourceModel(InputStream instream) {
		this.createModel();
		model.read(instream, "");
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
	
//		inferredModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ontModel);
//		((PelletInfGraph) inferredModel.getGraph()).classify();
//		inferredModel.prepare();
		this.getGlobalProperties(ontModel);
	}
	
	public OntResourceModel(InputStream instream, String format) {
		this.createModel();
		model.read(instream, "", format);
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		this.getGlobalProperties(ontModel);
	}

	public OntResourceModel(InputStream instream1, String format1, InputStream instream2, String format2) {
		// this.createModel();
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();

		if (instream1 != null && instream2 != null) {
			model1.read(instream1, "", format1);
			model2.read(instream2, "", format2);
			this.model = model1.union(model2);
		} else if (instream1 != null) {
			model1.read(instream1, "", format1);
			this.model = model1;
		} else if (instream2 != null) {
			model2.read(instream2, "", format2);
			this.model = model2;
		} else {
			this.model = ModelFactory.createDefaultModel();
		}
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		this.getGlobalProperties(ontModel);
	}

	private void createModel() {
		model = ModelFactory.createDefaultModel();
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
//		this.getGlobalProperties(ontModel);
	}
	
	public void close() {
		ontModel.close();
		model.close();
	}
	

	@Override
	public void prepareResource(String Uri) throws ResourceNotFoundException {
		
		resource = model.getResource(Uri);
		this.resUri = resource.getURI();
		// This might be not right
		if (!model.containsResource(resource))
			throw new ResourceNotFoundException("Can not find resource: " + Uri + " in current model");

		this.determineResourceType_curr();
		this.createOntResourceInfo_curr();
	} 

	/***
	 * determine the type of current resource. It can be either INSTANCE or
	 * CLASS 
	 */
	private void determineResourceType_curr() {

		StmtIterator iterator1 = model.listStatements(resource, RDF.type, OWL.Class);
		StmtIterator iterator2 = model.listStatements(null, RDF.type, resource);
		List<Statement> stmList1 = iterator1.toList();
		List<Statement> stmList2 = iterator2.toList();
		if (stmList1.size() == 0 && stmList2.size() == 0) {
			this.resLevel = ResourceLevel.INSTANCE;
		} else {
			this.resLevel = ResourceLevel.CLASS;
		}
	}
	
	private void createOntResourceInfo_curr() {
		this.ontResourceInfo = new OntResourceInfo(this.resource.getURI(), this.resource.getNameSpace(), this.resource.getLocalName());
		ontResourceInfo.setDescriptions(this.getTextualDescriptions(this.resource.getURI()));
	}

	/***
	 * return the type of current resource.
	 */
	@Override 
	public ResourceLevel getResourceLevel() {
		return this.resLevel;
	}

	@Override
	public List<String> getCategoryInformation() {

		List<String> categories = new ArrayList<String>();
		categories.addAll(this.getCategoriesByProperty(resource, RDF.type));
		categories.addAll(this.getCategoriesByProperty(resource, DCTerms.subject));
		return categories;
	}

	private ArrayList<String> getCategoriesByProperty(Resource res, Property p) {

		StmtIterator iterator = model.listStatements(res, p, (RDFNode) null);
		ArrayList<String> categories = new ArrayList<String>();
		while (iterator.hasNext()) {
			Statement stm = iterator.next();
			RDFNode node = stm.getObject(); // The node represents the
											// classification or category
											// information of the resource. We
											// can extract the category name
											// from the uri of this node or
											// dereference the uri and get the
											// category information there.

			if (node.isURIResource()) {
				// should exclude the owl:Class, owl:Thing and the like
				if (!excludedClassUriList.contains(node.asResource().getURI().trim())) {
					String localName = node.asResource().getLocalName().trim();
					categories.add(localName);
				}
			}
		}

		return categories;
	}
	
	///////
	
	public HashMap<OntPropertyInfo, HashSet<OntClassInfo>> Property_GlobalRanges_Map = new HashMap<OntPropertyInfo, HashSet<OntClassInfo>>();
	public HashMap<OntPropertyInfo, HashSet<OntClassInfo>> Property_LocalRanges_Map = new HashMap<OntPropertyInfo, HashSet<OntClassInfo>>();
	public HashMap<OntPropertyInfo, HashSet<OntClassInfo>> Property_GlobalDomains_Map = new HashMap<OntPropertyInfo, HashSet<OntClassInfo>>();
	public HashMap<OntPropertyInfo, HashSet<OntClassInfo>> Property_LocalDomains_Map = new HashMap<OntPropertyInfo, HashSet<OntClassInfo>>();
	public HashMap<OntClassInfo, HashSet<OntPropertyInfo>> classAsDomain_properties_map = new HashMap<OntClassInfo, HashSet<OntPropertyInfo>>();
	public HashMap<OntClassInfo, HashSet<OntPropertyInfo>> classAsRange_properties_map = new HashMap<OntClassInfo, HashSet<OntPropertyInfo>>();
	public HashMap<OntClassInfo, HashSet<OntClassInfo>> Class_DirectSubClasses_Map = new HashMap<OntClassInfo, HashSet<OntClassInfo>>();

	@Override
	public List<OntPropertyInfo> listDomainOntClassProperties(String classURI) {
		OntClassInfo ontClassInfo = this.getOntClass(classURI);
		List<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		HashSet<OntPropertyInfo> propertySet = classAsDomain_properties_map.get(ontClassInfo);
		if (propertySet != null) {
			ontPropertyInfoList.addAll(propertySet);
		}
		return ontPropertyInfoList;
	}
	
	@Override
	public List<OntPropertyInfo> listRangeOntClassProperties(String classURI) {
		OntClassInfo ontClassInfo = this.getOntClass(classURI);
		List<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		HashSet<OntPropertyInfo> propertySet = classAsRange_properties_map.get(ontClassInfo);
		if (propertySet != null) {
			ontPropertyInfoList.addAll(propertySet);
		}
		return ontPropertyInfoList;
	}

	@Override
	public List<OntClassInfo> listImposedRangeOntClasses(OntPropertyInfo ontPropertyInfo) {
		HashSet<OntClassInfo> rangeSet = Property_LocalRanges_Map.get(ontPropertyInfo);
		LinkedHashSet<OntClassInfo> allImposedClassSet = new LinkedHashSet<OntClassInfo>();
		List<OntClassInfo> classList = new ArrayList<OntClassInfo>();
		if (rangeSet != null) { 
			Iterator<OntClassInfo> iterator = rangeSet.iterator();
			while (iterator.hasNext()) {
				OntClassInfo range = iterator.next();
				allImposedClassSet.add(range);
				HashSet<OntClassInfo> subClassSet = Class_DirectSubClasses_Map.get(range);
				this.addImposedClasses(allImposedClassSet, subClassSet);
			}
		}
		classList.addAll(allImposedClassSet);
		return classList;
	}
	
	@Override
	public List<OntClassInfo> listImposedDomainOntClasses(OntPropertyInfo ontPropertyInfo) {
		HashSet<OntClassInfo> rangeSet = Property_LocalDomains_Map.get(ontPropertyInfo);
		LinkedHashSet<OntClassInfo> allImposedClassSet = new LinkedHashSet<OntClassInfo>();
		List<OntClassInfo> classList = new ArrayList<OntClassInfo>();
		if (rangeSet != null) {
			Iterator<OntClassInfo> iterator = rangeSet.iterator();
			while (iterator.hasNext()) {
				OntClassInfo range = iterator.next();
				allImposedClassSet.add(range);
				HashSet<OntClassInfo> subClassSet = Class_DirectSubClasses_Map.get(range);
				this.addImposedClasses(allImposedClassSet, subClassSet);
			}
		}
		classList.addAll(allImposedClassSet);
		return classList;
	}

	/**
	 * @param classSet
	 * @param subClassSet
	 */
	private void addImposedClasses(Set<OntClassInfo> allImposedClassSet, HashSet<OntClassInfo> classSet) {
		if (classSet != null) {
			Iterator<OntClassInfo> classIterator = classSet.iterator();
			while (classIterator.hasNext()) {
				OntClassInfo ontClassInfo = classIterator.next();
				allImposedClassSet.add(ontClassInfo);
				HashSet<OntClassInfo> subClassSet = Class_DirectSubClasses_Map.get(ontClassInfo);
				this.addImposedClasses(allImposedClassSet, subClassSet);
			}
		}
	}
	
	public void preprocessing() { 
		Iterator<OntProperty> ontProperties = ontModel.listAllOntProperties();
		while (ontProperties.hasNext()) {
			OntProperty ontProperty = ontProperties.next();
			if (this.isTopProperty(ontProperty)) {
//				System.out.println(ontProperty.getURI());
				List<OntClassInfo> rangeList = this.listLocalRanges(ontProperty, true);
				List<OntClassInfo> domainList = this.listLocalDomains(ontProperty, true);
				ExtendedIterator<? extends OntProperty> subProperties = ontProperty.listSubProperties(true);
				while(subProperties.hasNext()){
					OntProperty ontSubProperty = subProperties.next();
					this.populateHMsForSubProperties(rangeList, domainList,  ontSubProperty);
				}
			}
		}

		List<OntClassInfo> topOntClasses = this.listTopLevelClasses();
		for (OntClassInfo topOntClass : topOntClasses) {
			HashSet<OntPropertyInfo> propAsObjectOfDomain  = classAsDomain_properties_map.get(topOntClass);
			HashSet<OntPropertyInfo> propAsObjectOfRange  = classAsRange_properties_map.get(topOntClass);
			HashSet<OntClassInfo> subclassSet = Class_DirectSubClasses_Map.get(topOntClass);
//			List<OntClassInfo> subClasses = this.listDirectSubClasses(topOntClass);
			if (subclassSet != null) {
				Iterator<OntClassInfo> iterator = subclassSet.iterator();
				while (iterator.hasNext()) {
					OntClassInfo ontSubClassInfo = iterator.next();
					this.populateHMsForSubClasses(propAsObjectOfDomain, propAsObjectOfRange, ontSubClassInfo);
				}
			}
		}
	}

//	private List<OntClassInfo> listDirectSubClasses(OntClassInfo topOntClass) {
//		OntClass ontClass = ontModel.getOntClass(topOntClass.getURI());
//		List<OntClassInfo> subClasses = new ArrayList<OntClassInfo>();
//		if (ontClass == null) {
//			return subClasses;
//		}
//		HashSet<OntClassInfo> tempHS = new HashSet<OntClassInfo>();
//		Iterator<OntClass> subClasses1 = ontClass.listSubClasses(true);
//		while (subClasses1.hasNext()) {
//			OntClass subclass = subClasses1.next();
//			tempHS.add(this.getOntClassInfo(subclass));
//		}
//		HashSet<OntClassInfo> subClasses2 = Class_SubClasses_Map.get(topOntClass);
//		if (subClasses2 != null) {
//			tempHS.addAll(subClasses2);
//		}
//		subClasses.addAll(tempHS);
//		return subClasses;
//	}

	@Override
	/***
	 * List all the Top level classes, which means have no super classes, in this ResourceModel. 
	 * NOTE that the returned classes do not include OWL.Thing class.
	 * @return a list of OntClassInfo instances.
	 */
	public List<OntClassInfo> listTopLevelClasses() {
		// NOTE the Named Classes here are resources with rdf:type Class (or
		// equivalent) and a node URI.
		Iterator<OntClass> namedClasses = ontModel.listNamedClasses();
		ArrayList<OntClassInfo> results = new ArrayList<OntClassInfo>();
		while (namedClasses.hasNext()) {
			OntClass ontClass = namedClasses.next();
			if (!this.recordDirectNamedSuperClasses(ontClass) && !ontClass.getURI().equalsIgnoreCase(OWL.Thing.getURI())) {
				OntClassInfo classInfo = new OntClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName(), ClassType.NamedClass);
				results.add(classInfo);
			}
		}
		return results;
	}
	
	/***
	 * record direct named super classes of this class to HashMap 
	 * @param ontClass
	 * @return true if this class has named super classes, otherwise false.
	 */
	private boolean recordDirectNamedSuperClasses(OntClass ontClass){
		boolean hasNamedSuperClass = false;
		OntClassInfo ontSubClassInfo = getOntClassInfo(ontClass);
		Iterator<OntClass> supClasses = ontClass.listSuperClasses(true);
		while (supClasses.hasNext()) {
			OntClass supclass = supClasses.next();
			if (supclass.isURIResource()) {
				OntClassInfo ontSuperClassInfo = this.getOntClassInfo(supclass);
				HashSet<OntClassInfo> subclasses = Class_DirectSubClasses_Map.get(ontSuperClassInfo);
				if (subclasses == null) {
					subclasses = new HashSet<OntClassInfo>();
					Class_DirectSubClasses_Map.put(ontSuperClassInfo, subclasses);
				}
				subclasses.add(ontSubClassInfo);
				hasNamedSuperClass = true;
			}
		}
		
		Iterator<OntClass> equivalentClasses = ontClass.listEquivalentClasses();
		while (equivalentClasses.hasNext()) {
			OntClass equivalentClass = equivalentClasses.next();
			if (equivalentClass.isIntersectionClass()) {
				IntersectionClass intersecClass = equivalentClass.asIntersectionClass();
				RDFList rdfList = intersecClass.getOperands();
				int size = rdfList.size();
				for (int i = 0; i < size; i++) {
					Resource res = rdfList.get(i).asResource();
					if (res.isURIResource()) {
						OntClassInfo ontSuperClassInfo = getOntClassInfo(res);
						HashSet<OntClassInfo> subclasses = Class_DirectSubClasses_Map.get(ontSuperClassInfo);
						if(subclasses == null){
							subclasses = new HashSet<OntClassInfo>();
							Class_DirectSubClasses_Map.put(ontSuperClassInfo, subclasses);
						}
						subclasses.add(ontSubClassInfo);
						hasNamedSuperClass = true;
					}
				}
			} 
		}
		return hasNamedSuperClass;
	}
	
	private void populateHMsForSubProperties(List<OntClassInfo> rangeListforSupProp, List<OntClassInfo> domainListforSupProp, OntProperty ontProperty) {
		OntPropertyInfo ontPropertyInfo = this.getOntPropertyInfo(ontProperty);
		for (OntClassInfo range : rangeListforSupProp) {
			this.addGlobalRangeForProperty(range, ontPropertyInfo);
//			this.addPropertyForRangeClass(ontPropertyInfo, range);
		}
		for (OntClassInfo domain : domainListforSupProp) {
			this.addGlobalDomainForProperty(domain, ontPropertyInfo);
//			this.addPropertyForDomainClass(ontPropertyInfo, domain);
		}
		List<OntClassInfo> rangeList = this.listLocalRanges(ontProperty, true);
		rangeList.addAll(rangeListforSupProp);
		List<OntClassInfo> domainList = this.listLocalDomains(ontProperty, true);
		domainList.addAll(domainListforSupProp);
		ExtendedIterator<? extends OntProperty> subProperties = ontProperty.listSubProperties(true);
		while (subProperties.hasNext()) {
			OntProperty ontSubProperty = subProperties.next();
			this.populateHMsForSubProperties(rangeList, domainList, ontSubProperty);
		}
	}
	
	private void populateHMsForSubClasses(HashSet<OntPropertyInfo> propAsObjectOfDomain, HashSet<OntPropertyInfo> propAsObjectOfRange, OntClassInfo ontClass) {
		if (propAsObjectOfDomain != null) {
			Iterator<OntPropertyInfo> propsAsObjectOfDomain = propAsObjectOfDomain.iterator();
			while (propsAsObjectOfDomain.hasNext()) {
				OntPropertyInfo property = propsAsObjectOfDomain.next();
				this.addPropertyForDomainClass(property, ontClass);
			}
		}
		if (propAsObjectOfRange != null) {
			Iterator<OntPropertyInfo> propsAsObjectOfRange = propAsObjectOfRange.iterator();
			while (propsAsObjectOfRange.hasNext()) {
				OntPropertyInfo property = propsAsObjectOfRange.next();
				this.addPropertyForRangeClass(property, ontClass);
			}
		}
		HashSet<OntPropertyInfo> propAsObjectOfDomain2  = classAsDomain_properties_map.get(ontClass);
		HashSet<OntPropertyInfo> propAsObjectOfRange2  = classAsRange_properties_map.get(ontClass);
		HashSet<OntClassInfo> subclassSet = Class_DirectSubClasses_Map.get(ontClass);
		if (subclassSet != null) {
			Iterator<OntClassInfo> iterator = subclassSet.iterator();
			while (iterator.hasNext()) {
				OntClassInfo ontSubClassInfo = iterator.next();
				this.populateHMsForSubClasses(propAsObjectOfDomain2, propAsObjectOfRange2, ontSubClassInfo);
			}
		}
	}

	/***
	 * 
	 * @param ontProperty
	 * @param inferred
	 * @return 
	 */
	public List<OntClassInfo> listLocalRanges(OntProperty ontProperty, boolean inferred){
		ExtendedIterator<? extends OntResource> iterator = ontProperty.listRange();
		List<OntClassInfo> rangeList = new ArrayList<OntClassInfo>();
		OntPropertyInfo ontPropertyInfo = this.getOntPropertyInfo(ontProperty);
		while (iterator.hasNext()) {
			OntResource ontResource = iterator.next();
			if (ontResource.isClass()) {
				OntClass ontClass = ontResource.asClass();
				if (ontClass.isURIResource()) {
					// The range of the property is a named class
					this.populateRangeRelatedHM(rangeList, ontPropertyInfo, ontClass);
				} else if (ontClass.isUnionClass()) {
					// The range of a property is a union class,
					// then all classes in this union is the range
					// of the current property
					UnionClass unionClass = ontClass.asUnionClass();
					RDFList rdfList = unionClass.getOperands();
					int size = rdfList.size();
					for (int i = 0; i < size; i++) {
						RDFNode rdfNode = rdfList.get(i);
						if (rdfNode.isURIResource()) {
							Resource resource = rdfNode.asResource();
							// the resource should also be a instance of OntClass
							this.populateRangeRelatedHM(rangeList, ontPropertyInfo, resource);
						}
					}
				}
			}
		}

		// when there is no range information we can get from schema level of
		// this ontology, we infer the range information of this property from
		// the instance level.
		if (rangeList.size() == 0 && inferred == true) {
			// System.out.println(ontProperty.getURI());
			NodeIterator objectsOfProperty = ontModel.listObjectsOfProperty(ontProperty);
			while (objectsOfProperty.hasNext()) {
				RDFNode object = objectsOfProperty.next();
				if (object.isURIResource()) {
					Resource objRes = object.asResource();
					NodeIterator typesOfObject = ontModel.listObjectsOfProperty(objRes, RDF.type);
					while (typesOfObject.hasNext()) {
						Resource type = typesOfObject.next().asResource();
						if (type.isURIResource() && !Namespace.contains(type.getNameSpace())) {
							// System.out.println("   ---> " +
							// type.getLocalName());
							this.populateRangeRelatedHM(rangeList, ontPropertyInfo, type);
						}
					}
				}
			}
		}
		return rangeList;
	}

	public List<OntClassInfo> listLocalDomains(OntProperty ontProperty, boolean inferred) {
		ExtendedIterator<? extends OntResource> iterator = ontProperty.listDomain();
		List<OntClassInfo> domainList = new ArrayList<OntClassInfo>();
		OntPropertyInfo ontPropertyInfo = this.getOntPropertyInfo(ontProperty);
		while (iterator.hasNext()) {
			OntResource ontResource = iterator.next();
			if (ontResource.isClass()) {
				OntClass ontClass = ontResource.asClass();
				if (ontClass.isURIResource()) {
					// The domain of the property is a named class
					this.populateDomainRelatedHM(domainList, ontPropertyInfo, ontClass);
				} else if (ontClass.isUnionClass()) {
					// The domain of a property is a union class,
					// then all classes in this union is the range
					// of the current property
					UnionClass unionClass = ontClass.asUnionClass();
					RDFList rdfList = unionClass.getOperands();
					int size = rdfList.size();
					for (int i = 0; i < size; i++) {
						RDFNode rdfNode = rdfList.get(i);
						if (rdfNode.isURIResource()) {
							Resource resource = rdfNode.asResource();
							// the resource should also be a instance of OntClass
							this.populateDomainRelatedHM(domainList, ontPropertyInfo, resource);
						}
					}
				}
			}
		}

		// when there is no domain information we can get from schema level of
		// this ontology, we infer the domain information of this property from
		// the instance level.
		if (domainList.size() == 0 && inferred == true) {
			// System.out.println(ontProperty.getURI());
			ResIterator subjectsOfProperty = ontModel.listSubjectsWithProperty(ontProperty);
			while (subjectsOfProperty.hasNext()) {
				Resource subjRes = subjectsOfProperty.next();
				if (subjRes.isURIResource()) {
					NodeIterator typesOfSubject = ontModel.listObjectsOfProperty(subjRes, RDF.type);
					while (typesOfSubject.hasNext()) {
						Resource type = typesOfSubject.next().asResource();
						if (type.isURIResource() && !Namespace.contains(type.getNameSpace())) {
							// System.out.println("   ---> " +
							// type.getLocalName());
							this.populateDomainRelatedHM(domainList, ontPropertyInfo, type);
						}
					}
				}
			}
		}
		return domainList;
	}
	
	/**
	 * @param rangeList
	 * @param ontPropertyInfo
	 * @param ontClass
	 */
	private void populateRangeRelatedHM(List<OntClassInfo> rangeList, OntPropertyInfo ontPropertyInfo, Resource ontClass) {
		OntClassInfo ontClassInfo = this.getOntClassInfo(ontClass);
		rangeList.add(ontClassInfo);
		this.addGlobalRangeForProperty(ontClassInfo, ontPropertyInfo);
		this.addLocalRangeForProperty(ontClassInfo, ontPropertyInfo);
		this.addPropertyForRangeClass(ontPropertyInfo, ontClassInfo);
//		List<OntClassInfo> superClassList = this.getAllSuperClasses(ontClass);
//		for(OntClassInfo ontSuperClassInfo: superClassList){
//			this.addGlobalRangeForProperty(ontSuperClassInfo, ontPropertyInfo);
////			this.addPropertyForRangeClass(ontPropertyInfo, ontSuperClassInfo);
//			rangeList.add(ontSuperClassInfo);
//		}
	}

	/**
	 * @param rangeList
	 * @param ontPropertyInfo
	 * @param ontClass
	 */
	private void populateDomainRelatedHM(List<OntClassInfo> domanList, OntPropertyInfo ontPropertyInfo, Resource ontClass) {
		OntClassInfo ontClassInfo = this.getOntClassInfo(ontClass);
		domanList.add(ontClassInfo);
		this.addGlobalDomainForProperty(ontClassInfo, ontPropertyInfo);
		this.addLocalDomainForProperty(ontClassInfo, ontPropertyInfo);
		this.addPropertyForDomainClass(ontPropertyInfo, ontClassInfo);
//		List<OntClassInfo> superClassList = this.getAllSuperClasses(ontClass);
//		for(OntClassInfo ontSuperClassInfo: superClassList){
//			this.addGlobalDomainForProperty(ontSuperClassInfo, ontPropertyInfo);
////			this.addPropertyForDomainClass(ontPropertyInfo, ontSuperClassInfo);
//			domanList.add(ontSuperClassInfo);
//		}
	}
	
//	public List<OntClassInfo> getAllSuperClasses(Resource resource) {
//		List<OntClassInfo> ontClassList = new ArrayList<OntClassInfo>();
//		OntClass ontClass = ontModel.getOntClass(resource.getURI());
//		if (ontClass == null) {
//			return ontClassList;
//		}
//		this.getDirectSuperClasses(ontClassList, ontClass);
//		return ontClassList;
//	}
//	
//	private void getDirectSuperClasses(List<OntClassInfo> ontClassList, OntClass ontClass) {
//		Iterator<OntClass> classes1 = ontClass.listSuperClasses(true);
//		Iterator<OntClass> classes2 = ontClass.listEquivalentClasses();
//		while (classes1.hasNext()) {
//			OntClass cl = classes1.next();
//			if (cl.isURIResource()) {
//				OntClassInfo ontClassInfo = this.getOntClassInfo(cl);
//				ontClassList.add(ontClassInfo);
//				this.getDirectSuperClasses(ontClassList, cl);
//			}
//		}
//		while (classes2.hasNext()) {
//			OntClass cl = classes2.next();
//			if (cl.isIntersectionClass()) {
//				IntersectionClass intersecClass = cl.asIntersectionClass();
//				RDFList rdfList = intersecClass.getOperands();
//				int size = rdfList.size();
//				for (int i = 0; i < size; i++) {
//					RDFNode rdfNode = rdfList.get(i);
//					if (rdfNode.isURIResource()) {
//						Resource resource = rdfNode.asResource();
//						OntClassInfo ontClassInfo = this.getOntClassInfo(resource);
//						ontClassList.add(ontClassInfo);
//					}
//				}
//			}
//		}
//	}

	private void addGlobalDomainForProperty(OntClassInfo domain, OntPropertyInfo property){
		HashSet<OntClassInfo> domainSet = Property_GlobalDomains_Map.get(property);
		if(domainSet == null){
			domainSet = new HashSet<OntClassInfo>();	
			Property_GlobalDomains_Map.put(property, domainSet);
		}
		domainSet.add(domain);
	}
	
	private void addLocalDomainForProperty(OntClassInfo domain, OntPropertyInfo property){
		HashSet<OntClassInfo> domainSet = Property_LocalDomains_Map.get(property);
		if(domainSet == null){
			domainSet = new HashSet<OntClassInfo>();	
			Property_LocalDomains_Map.put(property, domainSet);
		}
		domainSet.add(domain);
	}
	
	private void addPropertyForDomainClass(OntPropertyInfo property, OntClassInfo domain) {
		HashSet<OntPropertyInfo> propertySet = classAsDomain_properties_map.get(domain);
		if(propertySet == null){
			propertySet = new HashSet<OntPropertyInfo>();	
			classAsDomain_properties_map.put(domain, propertySet);
		}
		propertySet.add(property);
	}
	
	private void addGlobalRangeForProperty(OntClassInfo range, OntPropertyInfo property){
		HashSet<OntClassInfo> rangeSet = Property_GlobalRanges_Map.get(property);
		if(rangeSet == null){
			rangeSet = new HashSet<OntClassInfo>();	
			Property_GlobalRanges_Map.put(property, rangeSet);
		}
		rangeSet.add(range);
	}
	
	private void addLocalRangeForProperty(OntClassInfo range, OntPropertyInfo ontPropertyInfo) {
		HashSet<OntClassInfo> rangeSet = Property_LocalRanges_Map.get(ontPropertyInfo);
		if(rangeSet == null){
			rangeSet = new HashSet<OntClassInfo>();	
			Property_LocalRanges_Map.put(ontPropertyInfo, rangeSet);
		}
		rangeSet.add(range);		
	}
	
	private void addPropertyForRangeClass(OntPropertyInfo property, OntClassInfo range) {
		HashSet<OntPropertyInfo> propertySet = classAsRange_properties_map.get(range);
		if(propertySet == null){
			propertySet = new HashSet<OntPropertyInfo>();	
			classAsRange_properties_map.put(range, propertySet);
		}
		propertySet.add(property);
	}
	
	private OntPropertyInfo getOntPropertyInfo(OntProperty ontProperty){
		
		OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
		if (ontProperty.isObjectProperty()) {
			ontPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
		} else {
			ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
		}
		return ontPropertyInfo;
	}
	
	private OntClassInfo getOntClassInfo(Resource ontClass){
		OntClassInfo ontClassInfo = new OntClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName(), ClassType.NamedClass);
		return ontClassInfo;
	}

	/***
	 * @param ontProperty
	 * @return true means this property is a top property, otherwise false.
	 */
	private boolean isTopProperty(OntProperty ontProperty) {
//		System.out.println(ontProperty.getURI());
		Property topObjectProperty = ontModel.createProperty("http://www.w3.org/2002/07/owl#topObjectProperty");
		Property topDataProperty = ontModel.createProperty("http://www.w3.org/2002/07/owl#topDataProperty");
		if (ontProperty.hasSuperProperty(topObjectProperty, true) || ontProperty.hasSuperProperty(topDataProperty, true)) {
			return true;
		}
		OntProperty superProperty = ontProperty.getSuperProperty();
		if (superProperty == null) {
			return true;
		}
		return false;
	}

	//////
	
	private void getGlobalProperties(OntModel model) {
		System.out.println("Get global properties...");
		int counterAll = 0;

		Iterator<OntProperty> ontProperties = model.listAllOntProperties();
		while (ontProperties.hasNext()) {
			
			OntProperty ontProperty = ontProperties.next();
			if (ontProperty.getDomain() == null) { 
				/*
				 * When the property has no domain, which means that it's global
				 * property.
				 */
													
//				System.out.println("------>" + ontProperty.getURI());
				globalPropertyList.add(ontProperty.getURI().trim());
				counterAll++;

			} else {
				/*
				 * When the property has domain
				 */
				
//				String propertyStr = this.getPropertyString(ontProperty);
				
//				System.out.println("---------------");
//				System.out.println("property: " + ontProperty.getURI());
				ExtendedIterator<? extends OntResource> iterator = ontProperty.listDomain();
				while (iterator.hasNext()) {

					OntResource ontResource = iterator.next();
//					System.out.println("  ++++? Domain: " + ontResource.getLocalName());
					if (ontResource.isClass()) {
						OntClass ontClass = ontResource.asClass();

						if (ontClass.isURIResource()) {
							/*
							 * The domain of the property is a named class
							 */
							
//							if (Property_Domains_Map.containsKey(ontProperty.getURI())) {
//
//								List<String> domains = Property_Domains_Map.get(ontProperty.getURI());
//								if (!domains.contains(ontClass.getURI())) {
//									domains.add(ontClass.getURI());
//								}
//
//							} else {
//								List<String> newDomains = new ArrayList<String>();
//								newDomains.add(ontClass.getURI());
//								Property_Domains_Map.put(ontProperty.getURI(), (ArrayList<String>) newDomains);
//							}
							
//							System.out.println("  URI Domain: " + ontClass.getLocalName());
						} else {

							if (ontClass.isUnionClass()) { 
								/*
								 * The domain of a property is a Union class,
								 * then all classes in the union is the domain
								 * of current property
								 */
								
//								System.out.println("Union Domain--> " + ontClass.getURI());
								UnionClass unionClass = ontClass.asUnionClass();

								// returns a list of the operands of this expression
								RDFList rdfList = unionClass.getOperands();
								int size = rdfList.size();
								for (int i = 0; i < size; i++) {
									
									RDFNode rdfNode = rdfList.get(i);
									if (rdfNode.isURIResource()) {

										// This resource is the domain of
										// current property
										Resource resource = rdfNode.asResource();

										// System.out.println("--> " +
										// resource.getURI());
										// System.out.println("--> " +
										// propertyStr);
										
										
//										if (Property_Domains_Map.containsKey(ontProperty.getURI())) {
//
//											List<String> domains = Property_Domains_Map.get(ontProperty.getURI());
//											if (!domains.contains(resource.getURI())) {
//												domains.add(resource.getURI());
//											}
//
//										} else {
//											List<String> newDomains = new ArrayList<String>();
//											newDomains.add(resource.getURI());
//											Property_Domains_Map.put(ontProperty.getURI(), (ArrayList<String>) newDomains);
//										}

										//
										if (UnionDomain_Properties_Map.containsKey(resource.getURI())) {

											ArrayList<OntPropertyInfo> propertyList = UnionDomain_Properties_Map.get(resource.getURI());
											if (!propertyList.contains(ontProperty.getURI())) {

												if (ontProperty.isDatatypeProperty()) {
													OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
													ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
													propertyList.add(ontPropertyInfo);

												} else if (ontProperty.isObjectProperty()) {
													OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
													ontPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
													propertyList.add(ontPropertyInfo);
												}

											}

										} else {

											if (ontProperty.isDatatypeProperty()) {
												ArrayList<OntPropertyInfo> propertyList = new ArrayList<OntPropertyInfo>();
												OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
												ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
												propertyList.add(ontPropertyInfo);
												UnionDomain_Properties_Map.put(resource.getURI(), propertyList);

											} else if (ontProperty.isObjectProperty()) {
												ArrayList<OntPropertyInfo> propertyList = new ArrayList<OntPropertyInfo>();
												OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
												ontPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
												propertyList.add(ontPropertyInfo);
												UnionDomain_Properties_Map.put(resource.getURI(), propertyList);
											}

										}
									}
								}
							} else if (ontClass.isIntersectionClass()) {
								/*
								 * The domain of a property is a Intersection class,
								 * then all classes in the union is the domain
								 * of current property
								 */
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public List<OntPropertyInfo> listAllOntProperties() {
		
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		Iterator<ObjectProperty> objectPropertylist = ontModel.listObjectProperties();
		Iterator<DatatypeProperty> datatypePropertylist = ontModel.listDatatypeProperties();
		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		while (objectPropertylist.hasNext()) {
			ObjectProperty objectProperty = objectPropertylist.next();
	
			ExtendedIterator<? extends OntResource> ranges = objectProperty.listRange();
//			OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(objectProperty.getURI());
			OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(objectProperty.getURI(), objectProperty.getNameSpace(), objectProperty.getLocalName());
			newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
			newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(objectProperty.getURI()));
			ontPropertyInfoList.add(newOntPropertyInfo);
			while (ranges.hasNext()) {
				
				OntResource range = ranges.next();
				// Now only consider class that is URI resource. We also should consider range for union class
				if (range.isURIResource() && range.isClass()) {
					OntClass rangeClass = range.asClass();
					OntResourceInfo ontClassInfo = new OntResourceInfo();
					ontClassInfo.setLocalName(rangeClass.getLocalName());
					ontClassInfo.setNamespace(rangeClass.getNameSpace());
					ontClassInfo.setURI(rangeClass.getURI());
					ontClassInfo.setDescriptions(this.getTextualDescriptions(rangeClass.getURI()));
					ontClassInfo.setLevel(1);
					newOntPropertyInfo.addRanges(ontClassInfo);
				}
			}
		}
		
		while (datatypePropertylist.hasNext()) {
			DatatypeProperty datatypeProperty = datatypePropertylist.next();
//			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(datatypeProperty.getURI());
			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(datatypeProperty.getURI(), datatypeProperty.getNameSpace(), datatypeProperty.getLocalName());
			ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
			ontPropertyInfo.setDescriptions(this.getTextualDescriptions(datatypeProperty.getURI()));
			OntResource resource = datatypeProperty.getRange();
			if (resource != null) {
				if (resource.isLiteral()) {
					Literal literal = resource.asLiteral();
					RDFDatatype datatype = literal.getDatatype();
					if (datatype != null) {
						// add datatype information to ontPropertyInfo
					}
				}
			}
			// here should add the type of the value type of this property.
			ontPropertyInfoList.add(ontPropertyInfo);
		}
		
		return ontPropertyInfoList;
	}
	
	@Override
	public OntPropertyInfo[] listAllOntPropertiesAsArray() {
		List<OntPropertyInfo> toReturn = this.listAllOntProperties();
		if (toReturn != null) {
			return toReturn.toArray(new OntPropertyInfo[0]);
		}
		return null;
	}
	
	
	private void addRanges(OntPropertyInfo OntPropertyInfo, OntClass rangeClass, int depth) {
		
		if (depth == 0)
			return;
		
		Iterator<OntClass> subClasses = rangeClass.listSubClasses(true);
		while (subClasses.hasNext()) {

			OntClass subOntClass = subClasses.next();
//			System.out.println("-" + subOntClass.getLocalName());
			OntResourceInfo subOntClassInfo = new OntResourceInfo();
			subOntClassInfo.setLocalName(subOntClass.getLocalName());
			subOntClassInfo.setNamespace(subOntClass.getNameSpace());
			subOntClassInfo.setURI(subOntClass.getURI());
			subOntClassInfo.setDescriptions(this.getTextualDescriptions(subOntClass.getURI()));
//			subOntClassInfo.setLevel(2);
			OntPropertyInfo.addRanges(subOntClassInfo);
			this.addRanges(OntPropertyInfo, subOntClass, depth - 1);
		}
		
	}
	
	
	@Override
	public List<OntPropertyInfo> listOntProperties() {
		// We can refactor the code here, because there are lots of replication.

//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		if (this.resLevel == ResourceLevel.CLASS) {

			OntClass ontClass = ontModel.getOntClass(resUri);
			if (ontClass == null) {
				return null;
			}
			// System.out.println("Class: " + ontClass.getLocalName());

			Iterator<OntProperty> ontProperties = ontClass.listDeclaredProperties();
			while (ontProperties.hasNext()) {

				OntProperty ontProperty = ontProperties.next();
				
//				System.out.println("properties: "  + ontProperty.getLocalName());
				if (!globalPropertyList.contains(ontProperty.getURI().trim())) {

					if (ontProperty.isObjectProperty()) {

						// System.out.println("|  object property: " + ontProperty.getLocalName());
				
						// OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI());
						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
						newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
						ontPropertyInfoList.add(newOntPropertyInfo);
						ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
						while (ranges.hasNext()) {

							OntResource range = ranges.next();

							// Now only consider class that is URI resource. We
							// also should consider range for union class
							if (range.isURIResource() && range.isClass()) {

								OntClass rangeClass = range.asClass();
								OntResourceInfo ontClassInfo = new OntResourceInfo();
								ontClassInfo.setLocalName(rangeClass.getLocalName());
								ontClassInfo.setNamespace(rangeClass.getNameSpace());
								ontClassInfo.setURI(rangeClass.getURI());
								ontClassInfo.setDescriptions(this.getTextualDescriptions(rangeClass.getURI()));
//								ontClassInfo.setLevel(1);
								newOntPropertyInfo.addRanges(ontClassInfo);
								this.addRanges(newOntPropertyInfo, rangeClass, 0);
					
							}
						}

					} else if (ontProperty.isDatatypeProperty()) {

						// System.out.println("|  data type property: " +
						// ontProperty.getLocalName());

						OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
						ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
						ontPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
						OntResource resource = ontProperty.getRange();
						if (resource != null) {
							// System.out.println("resource: "+
							// resource.getLocalName());
							ontPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));

						} else {
							ontPropertyInfo.setDatatype(null);
						}
				
						ontPropertyInfoList.add(ontPropertyInfo);

					}
				}
			}

			List<OntPropertyInfo> newOntProperties = new ArrayList<OntPropertyInfo>();

			// NOTE that we should change anything of the OntPropertyInfo
			// instances that store in the Domain_Properties_Map.
			List<OntPropertyInfo> properties = this.UnionDomain_Properties_Map.get(ontClass.getURI());
			if (properties != null) {
				for (OntPropertyInfo ontPropertyInfo : properties) {

					OntProperty ontProperty = ontModel.getOntProperty(ontPropertyInfo.getURI());
					if (ontProperty.isObjectProperty()) {

						// System.out.println("|  object property: " +
						// ontProperty.getLocalName());
						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
						// OntPropertyInfo newOntPropertyInfo = new
						// OntPropertyInfo(ontPropertyInfo.getURI());
						newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
						newOntProperties.add(newOntPropertyInfo);
						ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
						while (ranges.hasNext()) {

							OntResource range = ranges.next();

							// Now only consider class that is URI resource. We
							// also should consider range for union class
							if (range.isURIResource() && range.isClass()) {

								OntClass rangeClass = range.asClass();
								OntResourceInfo ontClassInfo = new OntResourceInfo();
								ontClassInfo.setLocalName(rangeClass.getLocalName());
								ontClassInfo.setNamespace(rangeClass.getNameSpace());
								ontClassInfo.setURI(rangeClass.getURI());
								ontClassInfo.setDescriptions(this.getTextualDescriptions(rangeClass.getURI()));
//								ontClassInfo.setLevel(1);
								newOntPropertyInfo.addRanges(ontClassInfo);
								this.addRanges(newOntPropertyInfo, rangeClass, 0);

							}
						}
					} else if (ontProperty.isDatatypeProperty()) {

						// System.out.println("|  data type property: " +
						// ontProperty.getLocalName());

						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
						// OntPropertyInfo newOntPropertyInfo = new
						// OntPropertyInfo(ontProperty.getURI());
						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
						newOntPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);

						OntResource resource = ontProperty.getRange();
						if (resource != null) {

							newOntPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));
							//
						} else {
							newOntPropertyInfo.setDatatype(null);
						}
				
						newOntProperties.add(newOntPropertyInfo);

					}
				}

				ontPropertyInfoList.addAll(newOntProperties);
			}
		}
		return ontPropertyInfoList;
	}
	
	
	
	@Override
	public OntPropertyInfo[] listOntPropertiesAsArray() {
		List<OntPropertyInfo> toReturn = this.listOntProperties();
		if (toReturn != null) {
			return toReturn.toArray(new OntPropertyInfo[0]);
		}
		return null;
	}
	
	@Override
	public List<OntPropertyInfo> listLocalProperties(String classURI, int propertyRangeDepth ){
		
		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		OntClass ontClass = ontModel.getOntClass(classURI);
		if (ontClass == null) {
			return ontPropertyInfoList;
		}
		// System.out.println("Class: " + ontClass.getLocalName());
		Iterator<OntProperty> ontProperties = ontClass.listDeclaredProperties();
		while (ontProperties.hasNext()) {

			OntProperty ontProperty = ontProperties.next();
			if (!globalPropertyList.contains(ontProperty.getURI().trim())) {

				if (ontProperty.isObjectProperty()) {

					// System.out.println("|  object property: " + ontProperty.getLocalName());
					// OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI());
					OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
					newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
					newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
					ontPropertyInfoList.add(newOntPropertyInfo);
					ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
					while (ranges.hasNext()) {

						OntResource range = ranges.next();
						// Now only consider class that is URI resource. We
						// also should consider range for union class
						if (range.isURIResource() && range.isClass()) {
							OntClass rangeClass = range.asClass();
							OntResourceInfo ontClassInfo = new OntResourceInfo();
							ontClassInfo.setLocalName(rangeClass.getLocalName());
							ontClassInfo.setNamespace(rangeClass.getNameSpace());
							ontClassInfo.setURI(rangeClass.getURI());
//							ontClassInfo.setLevel(1);
							newOntPropertyInfo.addRanges(ontClassInfo);
							this.addRanges(newOntPropertyInfo, rangeClass, propertyRangeDepth);
						}
					}

				} else if (ontProperty.isDatatypeProperty()) {

					// System.out.println("|  data type property: " +
					// ontProperty.getLocalName());
					OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
					ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
					ontPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
					OntResource resource = ontProperty.getRange();
					if (resource != null) {
						// System.out.println("resource: "+
						// resource.getLocalName());
						ontPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));
					} else {
						ontPropertyInfo.setDatatype(null);
					}
					ontPropertyInfoList.add(ontPropertyInfo);
				}
			}
		}

		List<OntPropertyInfo> newOntProperties = new ArrayList<OntPropertyInfo>();
		// NOTE that we should change anything of the OntPropertyInfo
		// instances that store in the Domain_Properties_Map.
		List<OntPropertyInfo> properties = this.UnionDomain_Properties_Map.get(ontClass.getURI());
		if (properties != null) {
			for (OntPropertyInfo ontPropertyInfo : properties) {
				OntProperty ontProperty = ontModel.getOntProperty(ontPropertyInfo.getURI());
				if (ontProperty.isObjectProperty()) {
					// System.out.println("|  object property: " +
					// ontProperty.getLocalName());
					OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
					// OntPropertyInfo newOntPropertyInfo = new
					// OntPropertyInfo(ontPropertyInfo.getURI());
					newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
					newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
					newOntProperties.add(newOntPropertyInfo);
					ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
					while (ranges.hasNext()) {
						OntResource range = ranges.next();
						// Now only consider class that is URI resource. We
						// also should consider range for union class
						if (range.isURIResource() && range.isClass()) {
							OntClass rangeClass = range.asClass();
							OntResourceInfo ontClassInfo = new OntResourceInfo();
							ontClassInfo.setLocalName(rangeClass.getLocalName());
							ontClassInfo.setNamespace(rangeClass.getNameSpace());
							ontClassInfo.setURI(rangeClass.getURI());
//							ontClassInfo.setLevel(1);
							newOntPropertyInfo.addRanges(ontClassInfo);
							this.addRanges(newOntPropertyInfo, rangeClass, propertyRangeDepth);
						}
					}
				} else if (ontProperty.isDatatypeProperty()) {

					// System.out.println("|  data type property: " +
					// ontProperty.getLocalName());
					OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
					// OntPropertyInfo newOntPropertyInfo = new
					// OntPropertyInfo(ontProperty.getURI());
					newOntPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
					newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
					OntResource resource = ontProperty.getRange();
					if (resource != null) {
						newOntPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));
					} else {
						newOntPropertyInfo.setDatatype(null);
					}
					newOntProperties.add(newOntPropertyInfo);
				}
			}
			ontPropertyInfoList.addAll(newOntProperties);
		}
		return ontPropertyInfoList;
	}
	
	
	/**
	 * Should refactor the code here!!
	 * 
	 * */
	@Override
	public List<OntPropertyInfo> listOntClassProperties() {
		// We can refactor the code here, because there are lots of replication.

//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		List<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		if (this.resLevel == ResourceLevel.CLASS) {
			ontPropertyInfoList = this.listLocalProperties(resUri, 2);
			
//			OntClass ontClass = ontModel.getOntClass(resUri);
//			if (ontClass == null) {
//				return ontPropertyInfoList;
//			}
//			// System.out.println("Class: " + ontClass.getLocalName());
//
//			Iterator<OntProperty> ontProperties = ontClass.listDeclaredProperties();
//			while (ontProperties.hasNext()) {
//
//				OntProperty ontProperty = ontProperties.next();
//				if (!globalPropertyList.contains(ontProperty.getURI().trim())) {
//
//					if (ontProperty.isObjectProperty()) {
//
//						// System.out.println("|  object property: " + ontProperty.getLocalName());
//				
//						// OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI());
//						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
//						newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
//						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
//						ontPropertyInfoList.add(newOntPropertyInfo);
//						ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
//						while (ranges.hasNext()) {
//
//							OntResource range = ranges.next();
//
//							// Now only consider class that is URI resource. We
//							// also should consider range for union class
//							if (range.isURIResource() && range.isClass()) {
//
//								OntClass rangeClass = range.asClass();
//								OntResourceInfo ontClassInfo = new OntResourceInfo();
//								ontClassInfo.setLocalName(rangeClass.getLocalName());
//								ontClassInfo.setNamespace(rangeClass.getNameSpace());
//								ontClassInfo.setURI(rangeClass.getURI());
////								ontClassInfo.setLevel(1);
//								newOntPropertyInfo.addRanges(ontClassInfo);
//								this.addRanges(newOntPropertyInfo, rangeClass, 2);
//					
//							}
//						}
//
//					} else if (ontProperty.isDatatypeProperty()) {
//
//						// System.out.println("|  data type property: " +
//						// ontProperty.getLocalName());
//
//						OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
//						ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
//						ontPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
//						OntResource resource = ontProperty.getRange();
//						if (resource != null) {
//							// System.out.println("resource: "+
//							// resource.getLocalName());
//							ontPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));
//
//						} else {
//							ontPropertyInfo.setDatatype(null);
//						}
//				
//						ontPropertyInfoList.add(ontPropertyInfo);
//
//					}
//				}
//			}
//
//			List<OntPropertyInfo> newOntProperties = new ArrayList<OntPropertyInfo>();
//
//			// NOTE that we should change anything of the OntPropertyInfo
//			// instances that store in the Domain_Properties_Map.
//			List<OntPropertyInfo> properties = this.UnionDomain_Properties_Map.get(ontClass.getURI());
//			if (properties != null) {
//				for (OntPropertyInfo ontPropertyInfo : properties) {
//
//					OntProperty ontProperty = ontModel.getOntProperty(ontPropertyInfo.getURI());
//					if (ontProperty.isObjectProperty()) {
//
//						// System.out.println("|  object property: " +
//						// ontProperty.getLocalName());
//						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
//						// OntPropertyInfo newOntPropertyInfo = new
//						// OntPropertyInfo(ontPropertyInfo.getURI());
//						newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
//						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
//						newOntProperties.add(newOntPropertyInfo);
//						ExtendedIterator<? extends OntResource> ranges = ontProperty.listRange();
//						while (ranges.hasNext()) {
//
//							OntResource range = ranges.next();
//
//							// Now only consider class that is URI resource. We
//							// also should consider range for union class
//							if (range.isURIResource() && range.isClass()) {
//
//								OntClass rangeClass = range.asClass();
//								OntResourceInfo ontClassInfo = new OntResourceInfo();
//								ontClassInfo.setLocalName(rangeClass.getLocalName());
//								ontClassInfo.setNamespace(rangeClass.getNameSpace());
//								ontClassInfo.setURI(rangeClass.getURI());
////								ontClassInfo.setLevel(1);
//								newOntPropertyInfo.addRanges(ontClassInfo);
//								this.addRanges(newOntPropertyInfo, rangeClass, 2);
//
//							}
//						}
//					} else if (ontProperty.isDatatypeProperty()) {
//
//						// System.out.println("|  data type property: " +
//						// ontProperty.getLocalName());
//
//						OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
//						// OntPropertyInfo newOntPropertyInfo = new
//						// OntPropertyInfo(ontProperty.getURI());
//						newOntPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
//						newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
//						OntResource resource = ontProperty.getRange();
//						if (resource != null) {
//							newOntPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(resource.getLocalName()));
//						} else {
//							newOntPropertyInfo.setDatatype(null);
//						}
//						newOntProperties.add(newOntPropertyInfo);
//					}
//				}
//
//				ontPropertyInfoList.addAll(newOntProperties);
//			}
		}
		return ontPropertyInfoList;
	}
		

	@Override
	public OntPropertyInfo[] listOntClassPropertiesAsArray() { 
		List<OntPropertyInfo> toReturn = this.listOntClassProperties();
//		if (toReturn != null) {
			return toReturn.toArray(new OntPropertyInfo[0]);
//		}
//		return null;
	}

	
	@Override
	public List<OntPropertyInfo> listIndividualProperties() {

		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
//		System.out.println("------------------------");
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
//		Individual individual = ontModel.getIndividual(resUri);
//		if (individual == null) {
//			return null;
//		}
		
		Resource individual = this.model.getResource(resUri);
		if (individual == null) {
			return null;
		}

		StmtIterator stmtIterator = individual.listProperties();
//		System.out.println("INDIVIDUAL: "+individual.getURI());
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Property property = statement.getPredicate();
			RDFNode rdfNode = statement.getObject();
			if (rdfNode != null) {
				
				if (rdfNode.isLiteral()) {
					String value = rdfNode.asLiteral().getLexicalForm();
					String datatypeURI = rdfNode.asLiteral().getDatatypeURI();
					OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(property.getURI(), property.getNameSpace(), property.getLocalName());
					newOntPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
					newOntPropertyInfo.setValue(value);
					newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(property.getURI()));
					
					if(datatypeURI == null){
						newOntPropertyInfo.setDatatype(null);
					} else {
						String datatype = datatypeURI.split("#")[1];
						newOntPropertyInfo.setDatatype(DatatypeMatcher.getDatatype(datatype));
					}
				
					ontPropertyInfoList.add(newOntPropertyInfo);
//					System.out.println("1 "+ property.getLocalName());
				} else if (rdfNode.isURIResource() && !property.getURI().contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					
					Resource resource  = rdfNode.asResource();
					OntResourceInfo ontObjectResourceInfo = new OntResourceInfo(resource.getURI(), resource.getNameSpace(), resource.getLocalName());
					ontObjectResourceInfo.setDescriptions(this.getTextualDescriptions(resource.getURI()));
//					String value = rdfNode.asResource().getURI();
					OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(property.getURI(), property.getNameSpace(), property.getLocalName());
					newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
					newOntPropertyInfo.setResourceForObject(ontObjectResourceInfo);
					newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(property.getURI()));
					ontPropertyInfoList.add(newOntPropertyInfo);
//					System.out.println("2 "+ property.getURI());
			
				} else {
				
				}
				
			} else {
	
			}
		}
//		System.out.println("------------------------");	
		return ontPropertyInfoList;
	}
	
	@Override
	public OntPropertyInfo[] listIndividualPropertiesAsArray() { 
		List<OntPropertyInfo> toReturn = this.listIndividualProperties();
		if (toReturn != null) {
			return toReturn.toArray(new OntPropertyInfo[0]);
		}
		return null;
	}
	
	
	@Override
	public List<String> listSuperClasses() {

//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, this.model);
		ArrayList<String> superClasses = new ArrayList<String>();
		if (this.resLevel == ResourceLevel.CLASS) {
			OntClass ontClass = ontModel.getOntClass(resUri);
			ExtendedIterator<OntClass> iterator = ontClass.listSuperClasses();
			while (iterator.hasNext()) {
				
				OntClass superClass = iterator.next();
				// should exclude the owl:Class, owl:Thing and the like
				if (!excludedClassUriList.contains(superClass.getURI().trim())) {
					superClasses.add(superClass.getLocalName().trim());

				}
			}

		}
		return superClasses;
	}
	
	@Override
	public List<OntResourceInfo> listSuperClasses(String classURI, boolean direct) {

//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, this.model);
		OntClass ontClass = ontModel.getOntClass(classURI);
		ArrayList<OntResourceInfo> namedSuperclasslist = new ArrayList<OntResourceInfo>();
		if (ontClass == null) {
			return namedSuperclasslist;
		}

		Iterator<OntClass> iterator = ontClass.listSuperClasses(direct);
		while (iterator.hasNext()) {
			OntClass superclass = iterator.next();
			if (superclass.isURIResource()) {
//				System.out.println(superClass.getNameSpace());
				if (!Namespace.contains(superclass.getNameSpace())) {
					OntResourceInfo res = new OntResourceInfo(superclass.getURI(),superclass.getNameSpace(), superclass.getLocalName());
					res.setDescriptions(this.getTextualDescriptions(superclass.getURI()));
					namedSuperclasslist.add(res);
				}
			}
		}
		return namedSuperclasslist;
	}
	
   @Override
   public List<OntResourceInfo> listSubClasses(String classURI, boolean direct) {
	   
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, this.model);
		OntClass ontClass = ontModel.getOntClass(classURI);
		ArrayList<OntResourceInfo> namedSubclasslist = new ArrayList<OntResourceInfo>();
		if (ontClass == null) {
			return namedSubclasslist;
		}

		Iterator<OntClass> iterator = ontClass.listSubClasses(direct);
		while (iterator.hasNext()) {
			OntClass subclass = iterator.next();
			if (subclass.isURIResource()) {
				if (!Namespace.contains(subclass.getNameSpace())) {
					
					OntResourceInfo res = new OntResourceInfo(subclass.getURI(),subclass.getNameSpace(), subclass.getLocalName());
					res.setDescriptions(this.getTextualDescriptions(subclass.getURI()));
					namedSubclasslist.add(res);
				}
			}
		}
		return namedSubclasslist;
	   
	   
   }
	
	@Override
	public List<String> listNamedEquivalentClasses(String classURI) {

//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		OntClass ontClass = ontModel.getOntClass(classURI);
		ArrayList<String> namedEquivalentClasslist = new ArrayList<String>();
		if (ontClass == null) {
			return namedEquivalentClasslist;
		}

		Iterator<OntClass> iterator = ontClass.listEquivalentClasses();
		while (iterator.hasNext()) {
			OntClass eqClass = iterator.next();
			if (eqClass.isURIResource()) {
				// System.out.println(classURI+ " equivalent class: " +
				// eqClass.getURI());
				namedEquivalentClasslist.add(eqClass.getURI());
			}
		}
		return namedEquivalentClasslist;
	}
	
	@Override
	/***
	 * List all the classes in this ResourceModel. NOTE that the returned classes do not include OWL.Thing class.
	 * @return a list of OntClassInfo instances.
	 */
	public List<OntResourceInfo> listAllNamedClasses(){ 
		
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		int counter = 0;
		// NOTE the Named Classes here are resources with rdf:type Class (or
		// equivalent) and a node URI.
		Iterator<OntClass> allClasses = ontModel.listNamedClasses();
		ArrayList<OntResourceInfo> results = new ArrayList<OntResourceInfo>();
		while (allClasses.hasNext()) {
			
			OntClass ontClass = allClasses.next();
			if (ontClass.isURIResource() && !ontClass.getURI().equalsIgnoreCase(OWL.Thing.getURI())) {

				counter ++;
//				System.out.println("*" + ontClass.getURI());
				OntResourceInfo classInfo = new OntResourceInfo();
				classInfo.setLocalName(ontClass.getLocalName());
				classInfo.setNamespace(ontClass.getNameSpace());
				classInfo.setURI(ontClass.getURI());
				classInfo.setDescriptions(this.getTextualDescriptions(ontClass.getURI()));
				results.add(classInfo);
			}
		}
		
		System.out.println("number of class: " + counter);
		return results;
	}
	
	/***
	 * get information of all classes in current ontology.
	 */
	@Override
	public List<IndividualTypeInfo> listIndividualTypeInfos(){

		HashMap<String, IndividualTypeInfo> typeInfoMap = new HashMap<String, IndividualTypeInfo>();
		StmtIterator statements = this.model.listStatements(null, RDF.type, (RDFNode) null);
		while (statements.hasNext()) {
			
			Statement statement = statements.next();
			Resource subject = statement.getSubject();
			List<String> classURIs = this.listTypesOfIndividual(subject, true);
			int numOfClass = classURIs.size();
			OntResourceInfo[] individualClasslst = new OntResourceInfo[numOfClass];
			
			int index = 0;
			for (String URI : classURIs) {
				Resource cl = this.model.getResource(URI);
				OntResourceInfo ontResourceInfo = new OntResourceInfo(cl.getURI(), cl.getNameSpace(), cl.getLocalName());
				individualClasslst[index] = ontResourceInfo;
				index++;
			}
			
			IndividualTypeInfo newIndividualTypeInfo = new IndividualTypeInfo(individualClasslst);
			if(!typeInfoMap.containsKey(newIndividualTypeInfo.getKey())){
				typeInfoMap.put(newIndividualTypeInfo.getKey(), newIndividualTypeInfo);
			}
			
			IndividualTypeInfo individualTypeInfo = typeInfoMap.get(newIndividualTypeInfo.getKey());
			if (subject.isURIResource()) {
				List<Property> propertylst = this.listProperties(subject);
				for(Property property: propertylst){
					OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(property.getURI(), property.getNameSpace(), property.getLocalName());
					individualTypeInfo.addProperty(ontPropertyInfo);
				}	
			}
		}
		
		ArrayList<IndividualTypeInfo> typeInfolst = new ArrayList<IndividualTypeInfo>();
		typeInfolst.addAll(typeInfoMap.values());
		return typeInfolst;
		
	}
	
	/***
	 * list all classes the inputed individual (represented by its URI) belongs
	 * to. If getURI is true, returns a list of URIs of these class. If false,
	 * returns a list of local names of these classes. If direct is true, only
	 * returns direct classes of this individual, otherwise returns both direct
	 * and indirect classes of this individual. [NOTE: this method seems have
	 * bug!!!]
	 * 
	 * */
	@Override
	public List<String> listClassesOfIndividual(String uri, boolean getURI, boolean direct) {
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, this.model);
		Individual individual = ontModel.getIndividual(uri);
		ArrayList<String> classURIs = new ArrayList<String>();

		if (individual == null) {
			return classURIs;
		}

		System.out.println("individual " + individual.getURI());
		Iterator<OntClass> classes = individual.listOntClasses(direct);
		if (classes.hasNext()) {
			OntClass cl = classes.next();
			String URI = cl.getURI();
			String localName = cl.getLocalName();

			System.out.println("class: " + cl.getLocalName());
			if (getURI == true) {
				classURIs.add(URI);
			} else {
				classURIs.add(localName);
			}
		}
		return classURIs;
	}
	
	public List<String> listTypesOfIndividual_curr(boolean getURI){
		return this.listTypesOfIndividual(this.resource, getURI);
	}
	
	@Override
	public List<String> listTypesOfIndividual(String uri, boolean getURI){
		
		Resource res = this.model.getResource(uri);
		return this.listTypesOfIndividual(res, getURI);

	}
	
	
//	/**
//	 * Get the names of all the class the given individual is type of. 
//	 * @param individual should be a named individual
//	 * @return a list of class names string.
//	 */
//	private List<String> getClassNames(Resource individual) {
//
//		List<String> classNameList = new ArrayList<String>();
//
//		if (individual == null || !this.isIndividual(individual)) {
//			return classNameList;
//		}
//		StmtIterator iterator = individual.listProperties(RDF.type);
//		while (iterator.hasNext()) {
//
//			Statement statement = iterator.next();
//			RDFNode object = statement.getObject();
//			if (object.isResource()) {
//
//				Resource objRes = object.asResource();
//				String URI = objRes.getURI();
//				String localName = objRes.getLocalName();
//				
//				if (!URI.contains(Namespace.OWL) && !URI.contains(Namespace.RDFS) && !URI.contains(Namespace.RDF)) {
//					classNameList.add(URI);
//				}
//			}
//		}
//		return classNameList;
//	}
	
	/***
	 * list all classes the inputed individual belongs to. If getURI is true,
	 * returns a list of URIs of these class. If false, returns a list of local
	 * names of these classes. Here we call this method list"Types"ofIndividual
	 * is because, we do not assume the ontology uses rdf:type owl:class to
	 * classify a individual. In other words, we do not assume it's a ontology
	 * based on OWL. It can be RDF or RDFS
	 * 
	 * @param individual
	 * @param getURI
	 * @return a list of classes represented either by their local names or URIs
	 */
	private List<String> listTypesOfIndividual(Resource individual, boolean getURI) {

		List<String> typesList = new ArrayList<String>();
		if (individual == null || !this.isIndividual(individual)) {
			return typesList;
		}
		StmtIterator iterator = individual.listProperties(RDF.type);
		while (iterator.hasNext()) {

			Statement statement = iterator.next();
			RDFNode object = statement.getObject();
			if (object.isResource()) {

				Resource objRes = object.asResource();
				String URI = objRes.getURI();
				String localName = objRes.getLocalName();

				if (!URI.contains(Namespace.OWL) && !URI.contains(Namespace.RDFS) && !URI.contains(Namespace.RDF)) {
					if (getURI == true) {
						typesList.add(URI);
					} else {
						typesList.add(localName);
					}
				}
			}
		}
		return typesList;
	}
	
	/***
	 * To check whether a given resource is an individual. The individual should
	 * be a type of user-defined class (e.g., user_nampespace:Yan rdf:type
	 * uns:Teather). It should not be a class or property or statement or other
	 * constructs defined in RDF/RDFS/OWL. However, this is not a full-fledged
	 * individual checking. For example, if a user-defined type is actually a
	 * property, this method can not check this.
	 * 
	 * @param resource
	 * @return boolean
	 */
	private boolean isIndividual(Resource resource) {
		
	
		if (!resource.isURIResource()) {
			return false;
		}
		
		if (this.ontModel.getIndividual(resource.getURI()) != null){
			return true;
		}
		
		StmtIterator iterator = resource.listProperties(RDF.type);
		if (iterator == null)
			return false;
		
		while (iterator.hasNext()) {
			
			Statement statement = iterator.next();
			RDFNode object = statement.getObject();
			if (object.isResource()) {
				Resource objRes = object.asResource();
				String URI = objRes.getURI();	
				if (URI.equalsIgnoreCase("http://www.w3.org/2002/07/owl#NamedIndividual")) {
					return true;
				}
				if (URI.contains(Namespace.OWL) || URI.contains(Namespace.RDFS) || URI.contains(Namespace.RDF)) {
					return false;
				}
				
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * list all the individuals of a given class. If the passed in argument is
	 * null, return all individuals.
	 */
	@Override
	public List<OntResourceInfo> listAllIndividuals(String className) {

		// System.out.println("here " + className);
		ResIterator iterator = this.model.listResourcesWithProperty(RDF.type);
		ArrayList<OntResourceInfo> results = new ArrayList<OntResourceInfo>();

		while (iterator.hasNext()) {
			Resource resource = iterator.next();

			if (this.isIndividual(resource)) {
				List<String> classNameList = this.listTypesOfIndividual(resource, false);

				// System.out.println(classNameList.get(0));
				// System.out.println("-- " + className);
				if (classNameList != null) {
					if (className == null || classNameList.contains(className.trim())) {

						OntResourceInfo individualInfo = new OntResourceInfo();
						individualInfo.setLocalName(resource.getLocalName());
						// System.out.println(resource.getURI());
						individualInfo.setNamespace(resource.getNameSpace());
						individualInfo.setURI(resource.getURI());
						individualInfo.setDescriptions(this.getTextualDescriptions(resource.getURI()));
						results.add(individualInfo);
					}

				}
			}
		}
		return results;
	}
	
	
	
	/***
	 * return all the properties of the given resource. These properties do not include properties from namespace RDF, RDFS and OWL.
	 * @param resource
	 * @return a list of Property in the form of List</Property/>
	 */
	private List<Property> listProperties(Resource resource){

		List<Property> properties = new ArrayList<Property>();
		StmtIterator statements = resource.listProperties();
		while (statements.hasNext()) {
			Statement statement = statements.next();
			Property property = statement.getPredicate();
			String propertyURI = property.getURI();
			if (!propertyURI.contains(Namespace.OWL) && !propertyURI.contains(Namespace.RDFS) && !propertyURI.contains(Namespace.RDF)) {
				properties.add(property);
			}

		}
		return properties;
	}
	
	
	/***
	 * list all the properties of this resource. 
	 * 
	 * @return
	 */
	@Override
	public List<String> listAllProperties(){
		
//		List<OntResourceInfo> individuals = listAllIndividuals(null);
//		
//		int PC = 0;
//		HashMap<String, Integer> propertyTOindex = new HashMap<String, Integer>();
//		HashMap<Integer, String> indexTOproperty = new HashMap<Integer, String>();
//		for(OntResourceInfo resInfo: individuals){
//			Resource res = this.model.getResource(resInfo.getURI());
//			StmtIterator statements = res.listProperties();
//			
//			while(statements.hasNext()){
//				Statement statement = statements.next();
//				String propertyURI = statement.getPredicate().getURI();
//				if(propertyTOindex.get(propertyURI) == null){
//					propertyTOindex.put(propertyURI, ++PC);
//					indexTOproperty.put(PC, propertyURI);
//				}
//				
//				int index = propertyTOindex.get(propertyURI);
//				
//				
//				System.out.print(statement.getPredicate().getLocalName() + "  ");
//			}
//			System.out.print("\n");
//		}
		
		List<String> properties = new ArrayList<String>();
		return properties;
	}

	/***
	 * To check if the input class has super class. The super class must have a URI.
	 * @param ontClass
	 * @return TURE for has super class; FALSE for has no super class.
	 */
//	private boolean hasNamedSuperClass(OntClass ontClass){
//		
//		Iterator<OntClass> directSuperClasses = ontClass.listSuperClasses();
//		
////		while(directSuperClasses.hasNext()){
////			 OntClass superClass = directSuperClasses.next();
////			 if(superClass.isURIResource()){
////				 return true;
////			 }
////		}
//		
//		while(directSuperClasses.hasNext()){
//				 return true;
//		}
//		return false;
//	}
	
	@Override
	public String getResourceLocalName_curr(){ 
		return this.resource.getLocalName();
	}
	
	@Override
	public String getResourceUri_curr(){
		return this.resource.getURI();
	}
	
	@Override
	public OntResourceInfo getOntResourceInfo_curr(){
		return this.ontResourceInfo;
	}
	
//	@Override
//	public String getResourceLocalName_test() {
//
//		Property property = this.model.getProperty("http://oaei.ontologymatching.org/2010/IIMBTBOX/name");
//	
//		
//		
//		if(property == null){
//			return this.resource.getLocalName();
//		}
////		System.out.println(resource.getURI());
////		System.out.println(property.getURI());
//		
//		Statement stmt = this.resource.getProperty(property);
//		if(stmt == null) {
//			return this.resource.getLocalName();
//		}
//		String name = "";
//	
//		if (stmt.getObject().isLiteral()) {
//			Literal literal = stmt.getObject().asLiteral();
//			name = literal.getLexicalForm();
//		}
//		return name;
//	}
	
	
//	@Override
//	public List<String> getTextualDescriptions(String resourceURI, List<String> propertyURIs){
//
//	
//		List<String> textualDescriptions = new ArrayList<String>();
////		if(propertyURIs.size() == 0){
////			textualDescriptions.add(this.resource.getLocalName());
////			return textualDescriptions;
////		}
//		
//		Resource res = this.model.getResource(resourceURI);
//		if (res == null || !res.isURIResource()) {
//			textualDescriptions.add("");
//			return textualDescriptions;
//		}
//		
//		boolean hasTD = false;
//		for (String propertyURI : propertyURIs) {
//			Property property = this.model.getProperty(propertyURI);
//			if (property != null) {
//				Statement stmt = res.getProperty(property);
//				if (stmt != null && stmt.getObject().isLiteral()) {
//					Literal literal = stmt.getObject().asLiteral();
//					hasTD = true;
//					textualDescriptions.add(StopWordsRemover.removeStopWords(literal.getLexicalForm()));
//				}
//
//			}
//		}
//
//		if (!hasTD) {
//			textualDescriptions.add(StopWordsRemover.removeStopWords(res.getLocalName()));
//		}
//
//		return textualDescriptions;
//		
//	}
	
	@Override
	public List<String> getTextualDescriptions(String resourceURI){

	
		List<String> textualDescriptions = new ArrayList<String>();
//		if(propertyURIs.size() == 0){
//			textualDescriptions.add(this.resource.getLocalName());
//			return textualDescriptions;
//		}
		
		Resource res = this.model.getResource(resourceURI);
		if (res == null || !res.isURIResource()) {
			return textualDescriptions;
		}
		
	
		for (String propertyURI : descriptionProperties) {
			Property property = this.model.getProperty(propertyURI);
			if (property != null) {
				Statement stmt = res.getProperty(property);
				if (stmt != null && stmt.getObject().isLiteral()) {
					Literal literal = stmt.getObject().asLiteral();
					textualDescriptions.add(StopWordsRemover.removeStopWords(literal.getLexicalForm()));
				}

			}
		}

		return textualDescriptions;
		
	}
	
	@Override
	/***
	 * 
	 * @param descriptionProperties
	 */
	public void setDescriptionProperties(List<String> descriptionPropertyURIs) {
		this.descriptionProperties = descriptionPropertyURIs;
	
	}
	
	
	@Override
	public List<OWLObjectPropertyDescription> listOWLObjectPropertyDescriptions(String URI){
		
		ObjectProperty objectProperty = ontModel.getObjectProperty(URI);
		List<OWLObjectPropertyDescription> objPropertyDecriptions = new ArrayList<OWLObjectPropertyDescription>();
		if (objectProperty != null) {
			
			ExtendedIterator<? extends OntResource> ranges = objectProperty.listRange();
			ExtendedIterator<? extends OntResource> domains = objectProperty.listDomain();
			ExtendedIterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties(true);
			ExtendedIterator<? extends OntProperty> inverseProperties = objectProperty.listInverse();
			
			if (superProperties.hasNext()) {
				OWLObjectPropertyDescription objectProDesc = new OWLObjectPropertyDescription(ObjectPropertyRelationType.SuperProperty);
				List<OntResourceInfo> relatedProperties = new ArrayList<OntResourceInfo>();
				objectProDesc.setRelatedProperties(relatedProperties);
				objPropertyDecriptions.add(objectProDesc);
				while (superProperties.hasNext()) {
					OntProperty supProperty = superProperties.next();
					if (supProperty.isObjectProperty()) {
						relatedProperties.add(new OntResourceInfo(supProperty.getURI(), supProperty.getNameSpace(), supProperty.getLocalName(), ResourceType.ObjectProperty));
					}
				}
			}
			if (inverseProperties.hasNext()) {
				OWLObjectPropertyDescription objectProDesc = new OWLObjectPropertyDescription(ObjectPropertyRelationType.InverseProperty);
				List<OntResourceInfo> relatedProperties = new ArrayList<OntResourceInfo>();
				objectProDesc.setRelatedProperties(relatedProperties);
				objPropertyDecriptions.add(objectProDesc);
				while (inverseProperties.hasNext()) {
					OntProperty invProperty = inverseProperties.next();
					if (invProperty.isObjectProperty()) {
						relatedProperties.add(new OntResourceInfo(invProperty.getURI(), invProperty.getNameSpace(), invProperty.getLocalName(), ResourceType.ObjectProperty));
					}
				}
			}
			if (ranges.hasNext()) {
				OWLObjectPropertyDescription objectProDesc = new OWLObjectPropertyDescription(ObjectPropertyRelationType.Range);
				List<ClassExpression> classExpressions = new ArrayList<ClassExpression>();
				objectProDesc.setClassExpressions(classExpressions);
				objPropertyDecriptions.add(objectProDesc);
				while (ranges.hasNext()) {
					OntResource range = ranges.next();
					if (range.isClass()) {
						OntClass rangeClass = range.asClass();
						classExpressions.add(this.getComplexClassExpression(rangeClass));
					}
				}
			}
			if (domains.hasNext()) {
				OWLObjectPropertyDescription objectProDesc = new OWLObjectPropertyDescription(ObjectPropertyRelationType.Domain);
				List<ClassExpression> classExpressions = new ArrayList<ClassExpression>();
				objectProDesc.setClassExpressions(classExpressions);
				objPropertyDecriptions.add(objectProDesc);
				while (domains.hasNext()) {
					OntResource domain = domains.next();
					if (domain.isClass()) {
						OntClass domainClass = domain.asClass();
						classExpressions.add(this.getComplexClassExpression(domainClass));
					}
				}
			}
		}
		return objPropertyDecriptions;
	}
	
	@Override
	public List<OWLSimpleObjectPropertyDescription> listOWLSimpleObjectPropertyDescriptions(String URI){
		
		ObjectProperty objectProperty = ontModel.getObjectProperty(URI);
		List<OWLSimpleObjectPropertyDescription> objPropertyDecriptions = new ArrayList<OWLSimpleObjectPropertyDescription>();
		if (objectProperty != null) {
			
			ExtendedIterator<? extends OntResource> ranges = objectProperty.listRange();
			ExtendedIterator<? extends OntResource> domains = objectProperty.listDomain();
			ExtendedIterator<? extends OntProperty> superProperties = objectProperty.listSuperProperties(true);
			ExtendedIterator<? extends OntProperty> inverseProperties = objectProperty.listInverse();

			if (superProperties.hasNext()) {
				OWLSimpleObjectPropertyDescription objectProDesc = new OWLSimpleObjectPropertyDescription(ObjectPropertyRelationType.SuperProperty);
				List<String> relatedProperties = new ArrayList<String>();
				objectProDesc.setRelatedProperties(relatedProperties);
				objPropertyDecriptions.add(objectProDesc);
				while (superProperties.hasNext()) {
					OntProperty supProperty = superProperties.next();
					if (supProperty.isObjectProperty()) {
						relatedProperties.add("ObjectProperty");
					} else {
						relatedProperties.add("DatatypeProperty");
					}
				}
			}
			if (inverseProperties.hasNext()) {
				OWLSimpleObjectPropertyDescription objectProDesc = new OWLSimpleObjectPropertyDescription(ObjectPropertyRelationType.InverseProperty);
				List<String> relatedProperties = new ArrayList<String>();
				objectProDesc.setRelatedProperties(relatedProperties);
				objPropertyDecriptions.add(objectProDesc);
				while (inverseProperties.hasNext()) {
					OntProperty invProperty = inverseProperties.next();
					if (invProperty.isObjectProperty()) {
						relatedProperties.add("ObjectProperty");
					} else {
						relatedProperties.add("DatatypeProperty");
					}
				}
			}
			if (ranges.hasNext()) {
				OWLSimpleObjectPropertyDescription objectProDesc = new OWLSimpleObjectPropertyDescription(ObjectPropertyRelationType.Range);
				List<SimpleClassExpression> classExpressions = new ArrayList<SimpleClassExpression>();
				objectProDesc.setClassExpressions(classExpressions);
				objPropertyDecriptions.add(objectProDesc);
				while (ranges.hasNext()) {
					OntResource range = ranges.next();
					if (range.isClass()) {
						OntClass rangeClass = range.asClass();
						classExpressions.add(this.getSimpleClassExpression(rangeClass));
					}
				}
			}
			if (domains.hasNext()) {
				OWLSimpleObjectPropertyDescription objectProDesc = new OWLSimpleObjectPropertyDescription(ObjectPropertyRelationType.Domain);
				List<SimpleClassExpression> classExpressions = new ArrayList<SimpleClassExpression>();
				objectProDesc.setClassExpressions(classExpressions);
				objPropertyDecriptions.add(objectProDesc);
				while (domains.hasNext()) {
					OntResource domain = domains.next();
					if (domain.isClass()) {
						OntClass domainClass = domain.asClass();
						classExpressions.add(this.getSimpleClassExpression(domainClass));
					}
				}
			}
		}
		return objPropertyDecriptions;
	}
	
	@Override
	public List<OWLSimpleClassDescription> listOWLSimpleClassDescriptions(String URI){
		 
		OntClass ontClass = ontModel.getOntClass(URI);
		List<OWLSimpleClassDescription> descriptions = new ArrayList<OWLSimpleClassDescription>();
		if (ontClass != null) {

			Iterator<OntClass> sp_classes = ontClass.listSuperClasses(true);
			if (sp_classes.hasNext()) {
				OWLSimpleClassDescription description = new OWLSimpleClassDescription("SuperClasses");
				description.setClassExpressions(this.getSimpleClassExpressions(sp_classes));
				descriptions.add(description);  
			}

			Iterator<OntClass> dj_classes = ontClass.listDisjointWith();
			if (dj_classes.hasNext()) {
				OWLSimpleClassDescription description = new OWLSimpleClassDescription("DisjointWithClasses");
				description.setClassExpressions(this.getSimpleClassExpressions(dj_classes));
				descriptions.add(description);
			}

			Iterator<OntClass> eq_classes = ontClass.listEquivalentClasses();
			if (eq_classes.hasNext()) {
				OWLSimpleClassDescription description = new OWLSimpleClassDescription("EquivalentClasses");
				description.setClassExpressions(this.getSimpleClassExpressions(eq_classes));
				descriptions.add(description);
			}
		}
		return descriptions;
	}  
	
	private List<SimpleClassExpression> getSimpleClassExpressions(Iterator<OntClass> classes) {
		List<SimpleClassExpression> classExpressions = new ArrayList<SimpleClassExpression>();
		while (classes.hasNext()) {
			OntClass cl = classes.next();
			classExpressions.add(this.getSimpleClassExpression(cl));
		}
		return classExpressions;
	}
	
	private SimpleClassExpression getSimpleClassExpression(OntClass cl) {
		
		if (cl.isURIResource()) {
			return new SimpleClassExpression(ClassExpressionType.NamedClass);
		} else if (cl.isRestriction()) {

			Restriction restriction = cl.asRestriction();
			if (restriction.isAllValuesFromRestriction()) {

				Resource resource = restriction.asAllValuesFromRestriction().getAllValuesFrom();
				OntResource ontResource = this.ontModel.getOntResource(resource);
				if (ontResource.isClass()) {

					OntClass ontClass = ontResource.asClass();
					if (ontClass.isUnionClass()) {
						return new SimpleClassExpression(ClassExpressionType.AllValuesFromUnion);
					} else if (ontClass.isIntersectionClass()) {
						return new SimpleClassExpression(ClassExpressionType.AllValuesFromIntersection);
					} else if (ontClass.isURIResource()) {
						return new SimpleClassExpression(ClassExpressionType.AllValuesFromNamedClass);
					} else {
						return new SimpleClassExpression(ClassExpressionType.Other);
					}
				} else {
					return new SimpleClassExpression(ClassExpressionType.Other);
				}
			} else if (restriction.isSomeValuesFromRestriction()) {
				Resource resource = restriction.asSomeValuesFromRestriction().getSomeValuesFrom();
				OntResource ontResource = this.ontModel.getOntResource(resource);
				if (ontResource.isClass()) {

					OntClass ontClass = ontResource.asClass();
					if (ontClass.isUnionClass()) {
						return new SimpleClassExpression(ClassExpressionType.SomeValuesFromUnion);
					} else if (ontClass.isIntersectionClass()) {
						return new SimpleClassExpression(ClassExpressionType.SomeValuesFromIntersection);
					} else if (ontClass.isURIResource()) {
						return new SimpleClassExpression(ClassExpressionType.SomeValuesFromNamedClass);
					} else {
						return new SimpleClassExpression(ClassExpressionType.Other);
					}
				} else {
					return new SimpleClassExpression(ClassExpressionType.Other);
				}
			} else if (restriction.isCardinalityRestriction()) {
				return new SimpleClassExpression(ClassExpressionType.Cardinality);
			} else if (restriction.isHasValueRestriction()) {
				return new SimpleClassExpression(ClassExpressionType.HasValueFrom);
			} else if (restriction.isMaxCardinalityRestriction()) {
				return new SimpleClassExpression(ClassExpressionType.MaxCardinality);
			} else if (restriction.isMinCardinalityRestriction()) {
				return new SimpleClassExpression(ClassExpressionType.MinCardinality);
			} else {
				return new SimpleClassExpression(ClassExpressionType.Other);
			}
		} else if (cl.isIntersectionClass()) {
			return new SimpleClassExpression(ClassExpressionType.Intersection);
		} else if (cl.isUnionClass()) {
			return new SimpleClassExpression(ClassExpressionType.Union);
		} else if (cl.isComplementClass()) {
			return new SimpleClassExpression(ClassExpressionType.ComplementOf);
		} else {
			return new SimpleClassExpression(ClassExpressionType.Other);
		}
	}
	
	@Override
	public List<OWLComplexClassDescription> listOWLComplexClassDescriptions(String URI) { 
		
		OntClass ontClass = ontModel.getOntClass(URI);
		List<OWLComplexClassDescription> descriptions = new ArrayList<OWLComplexClassDescription>();
		if (ontClass != null) {

			Iterator<OntClass> sp_classes = ontClass.listSuperClasses(true);
			if (sp_classes.hasNext()) {
				OWLComplexClassDescription description = new OWLComplexClassDescription("SuperClasses");
				description.setClassExpressions(this.getComplexClassExpressions(sp_classes));
				descriptions.add(description);
			}

			Iterator<OntClass> dj_classes = ontClass.listDisjointWith();
			if (dj_classes.hasNext()) {
				OWLComplexClassDescription description = new OWLComplexClassDescription("DisjointWithClasses");
				description.setClassExpressions(this.getComplexClassExpressions(dj_classes));
				descriptions.add(description);
			}

			Iterator<OntClass> eq_classes = ontClass.listEquivalentClasses();
			if (eq_classes.hasNext()) {
				OWLComplexClassDescription description = new OWLComplexClassDescription("EquivalentClasses");
				description.setClassExpressions(this.getComplexClassExpressions(eq_classes));
				descriptions.add(description);
			}

		}
		return descriptions;
	} 
 
	private List<ClassExpression> getComplexClassExpressions(Iterator<OntClass> classes) {

		List<ClassExpression> classExpressions = new ArrayList<ClassExpression>();
		while (classes.hasNext()) {
			OntClass cl = classes.next();
			classExpressions.add(this.getComplexClassExpression(cl));
		}
		return classExpressions;
	}
	
	
	private ClassExpression getComplexClassExpression(OntClass cl){
		
		if (cl.isURIResource()) {
			
			ClassExpression ce = new ClassExpression(ClassExpressionType.NamedClass);
			List<OntResourceInfo> ontResourceInfoList = new ArrayList<OntResourceInfo>();
			OntResourceInfo ontResourceInfo = new OntResourceInfo(cl.getURI(), cl.getNameSpace(), cl.getLocalName());
			ontResourceInfoList.add(ontResourceInfo);
			ce.setNamedClassList(ontResourceInfoList);
			return ce;
			
		} else if (cl.isRestriction()) {
			
			ClassExpression ce;
			Restriction restriction = cl.asRestriction();
			if (restriction.isAllValuesFromRestriction()) {

				Resource resource = restriction.asAllValuesFromRestriction().getAllValuesFrom();
				OntResource ontResource = this.ontModel.getOntResource(resource);
				if (ontResource.isClass()) {

					OntClass ontClass = ontResource.asClass();
					if (ontClass.isUnionClass()) {

						ce = new ClassExpression(ClassExpressionType.AllValuesFromUnion);
						UnionClass unionClass = ontClass.asUnionClass();
						RDFList rdfList = unionClass.getOperands();
						ce.setNamedClassList(this.getNamedClasses(rdfList));

					} else if (ontClass.isIntersectionClass()) {

						ce = new ClassExpression(ClassExpressionType.AllValuesFromIntersection);
						IntersectionClass intersectionClass = ontClass.asIntersectionClass();
						RDFList rdfList = intersectionClass.getOperands();
						ce.setNamedClassList(this.getNamedClasses(rdfList));

					} else if (ontClass.isURIResource()) {

						ce = new ClassExpression(ClassExpressionType.AllValuesFromNamedClass);
						List<OntResourceInfo> ontResourceInfoList = new ArrayList<OntResourceInfo>();
						OntResourceInfo ontResourceInfo = new OntResourceInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName());
						ontResourceInfoList.add(ontResourceInfo);
						ce.setNamedClassList(ontResourceInfoList);

					} else {
						ce = new ClassExpression(ClassExpressionType.AllValuesFromOther);
					}
					
				} else {

					ce = new ClassExpression(ClassExpressionType.AllValuesFromOther);
				}

			} else if (restriction.isSomeValuesFromRestriction()) {
				
				Resource resource = restriction.asSomeValuesFromRestriction().getSomeValuesFrom();
				OntResource ontResource = this.ontModel.getOntResource(resource);
				if (ontResource.isClass()) {

					OntClass ontClass = ontResource.asClass();
					if (ontClass.isUnionClass()) {

						ce = new ClassExpression(ClassExpressionType.SomeValuesFromUnion);
						UnionClass unionClass = ontClass.asUnionClass();
						RDFList rdfList = unionClass.getOperands();
						ce.setNamedClassList(this.getNamedClasses(rdfList));

					} else if (ontClass.isIntersectionClass()) {

						ce = new ClassExpression(ClassExpressionType.SomeValuesFromIntersection);
						IntersectionClass intersectionClass = ontClass.asIntersectionClass();
						RDFList rdfList = intersectionClass.getOperands();
						ce.setNamedClassList(this.getNamedClasses(rdfList));

					} else if (ontClass.isURIResource()) {

						ce = new ClassExpression(ClassExpressionType.SomeValuesFromNamedClass);
						List<OntResourceInfo> ontResourceInfoList = new ArrayList<OntResourceInfo>();
						OntResourceInfo ontResourceInfo = new OntResourceInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName());
						ontResourceInfoList.add(ontResourceInfo);
						ce.setNamedClassList(ontResourceInfoList);

					} else {
						ce = new ClassExpression(ClassExpressionType.SomeValuesFromOther);
					}
					
				} else {

					ce = new ClassExpression(ClassExpressionType.SomeValuesFromOther);
				}
				
			} else if (restriction.isCardinalityRestriction()) {
				ce = new ClassExpression(ClassExpressionType.Cardinality);
				CardinalityRestriction CR = restriction.asCardinalityRestriction();
				ce.setCardinality(CR.getCardinality());
			} else if (restriction.isHasValueRestriction()) {
				ce = new ClassExpression(ClassExpressionType.HasValueFrom);
			} else if (restriction.isMaxCardinalityRestriction()) {
				ce = new ClassExpression(ClassExpressionType.MaxCardinality);
				MaxCardinalityRestriction maxCR = restriction.asMaxCardinalityRestriction();
				maxCR.getMaxCardinality();
				ce.setCardinality(maxCR.getMaxCardinality());
			} else if (restriction.isMinCardinalityRestriction()) {
				ce = new ClassExpression(ClassExpressionType.MinCardinality);
				MinCardinalityRestriction minCR = restriction.asMinCardinalityRestriction();
				minCR.getMinCardinality();
				ce.setCardinality(minCR.getMinCardinality());
			} else {
				ce = new ClassExpression(ClassExpressionType.Other);
			}
			return ce;
		} else if (cl.isIntersectionClass()) {	
			ClassExpression ce = new ClassExpression(ClassExpressionType.Intersection);
			IntersectionClass intersectionClass = cl.asIntersectionClass();
			RDFList rdfList = intersectionClass.getOperands();
			ce.setNamedClassList(this.getNamedClasses(rdfList));
			return ce;
		} else if (cl.isUnionClass()) {
			ClassExpression ce = new ClassExpression(ClassExpressionType.Union);
			UnionClass unionClass = cl.asUnionClass();
			RDFList rdfList = unionClass.getOperands();
			ce.setNamedClassList(this.getNamedClasses(rdfList));
			return ce;
		} else if (cl.isComplementClass()) {
			ClassExpression ce = new ClassExpression(ClassExpressionType.ComplementOf);
			return ce;

		} else {
			ClassExpression ce = new ClassExpression(ClassExpressionType.Other);
			return ce;
		}
		
		
	}

	/***
	 * 
	 * @param rdfList
	 * @return
	 */
	private List<OntResourceInfo> getNamedClasses(RDFList rdfList) {
		List<OntResourceInfo> ontResourceInfoList = new ArrayList<OntResourceInfo>();
		int size = rdfList.size();
		for (int i = 0; i < size; i++) {
			RDFNode rdfNode = rdfList.get(i);
			if (rdfNode.isURIResource()) {
				OntResource ontResource = this.ontModel.getOntResource(rdfNode.asResource());
				if (ontResource.isClass()) {
					OntResourceInfo ontResourceInfo = new OntResourceInfo(ontResource.getURI(), ontResource.getNameSpace(), ontResource.getLocalName(), ResourceType.NamedClass);
					ontResourceInfoList.add(ontResourceInfo);
				}
			}
		}
		return ontResourceInfoList;
	}
	
	@Override
	public OntClassInfo getOntClass(String URI) {
		OntClass ontClass = this.ontModel.getOntClass(URI);
		if (ontClass == null) {
			return null;
		}
		OntClassInfo ontClassInfo = new OntClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName(), ClassType.NamedClass);
		return ontClassInfo;
	}
	
	@Override
	public ArrayList<OntClassInfo> listRanges(String URI) {

		ArrayList<OntClassInfo> rangeList = new ArrayList<OntClassInfo>();
		OntProperty ontProperty = this.ontModel.getOntProperty(URI);
		if (ontProperty == null) {
			return rangeList;
		}
		ExtendedIterator<? extends OntResource> iterator = ontProperty.listRange();
		while (iterator.hasNext()) {
			OntResource ontResource = iterator.next();
			if (ontResource.isClass()) {
				OntClass ontClass = ontResource.asClass();
				if (ontClass.isURIResource()) {
					OntClassInfo ontClassInfo = new OntClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName(), ClassType.NamedClass);
					rangeList.add(ontClassInfo);
				} else {

					if (ontResource.asClass().isUnionClass()) {
						UnionClass unionClass = ontResource.asClass().asUnionClass();
						RDFList rdfList = unionClass.getOperands();
						int size = rdfList.size();
						// System.out.println("  Range:");
						for (int i = 0; i < size; i++) {
							RDFNode rdfNode = rdfList.get(i);
							// Answer true if and only if this RDFNode is an
							// named resource
							if (rdfNode.isURIResource()) {
								
								Resource resource = rdfNode.asResource();
								OntClassInfo ontClassInfo = new OntClassInfo(resource.getURI(), resource.getNameSpace(), resource.getLocalName(), ClassType.NamedClass);
								rangeList.add(ontClassInfo);
							}
						}
					}
				}
			}
		}
		return rangeList;
	}
	
	@Override
	public ArrayList<OntClassInfo> listDomains(String URI) {
		
		
		System.out.println("Property2 " + URI);
		ArrayList<OntClassInfo> domainList = new ArrayList<OntClassInfo>();
		OntProperty property = this.ontModel.getOntProperty(URI);
		if (property == null) {
			return domainList;
		}
		
		System.out.println("Property3 " + URI);
//		System.out.println("Property:" + property.getURI());
		ExtendedIterator<? extends OntResource> iterator = property.listDomain();
		while (iterator.hasNext()) {
			OntResource ontResource = iterator.next();
//			System.out.println("domain: " + ontResource.getURI());
//			System.out.println("is class: " + ontResource.isClass());
			if (ontResource.isClass()) {
				OntClass ontClass = ontResource.asClass();
				if (ontClass.isURIResource()) {
					OntClassInfo ontClassInfo = new OntClassInfo(ontClass.getURI(), ontClass.getNameSpace(), ontClass.getLocalName(), ClassType.NamedClass);
					domainList.add(ontClassInfo);

				} else {

					if (ontClass.isUnionClass()) {
						UnionClass unionClass = ontClass.asUnionClass();
						RDFList rdfList = unionClass.getOperands();
						int size = rdfList.size();
						for (int i = 0; i < size; i++) {
							RDFNode rdfNode = rdfList.get(i);
							if (rdfNode.isURIResource()) {
								/*
								 * Answer true if and only if this RDFNode is an
								 * named resource
								 */
								Resource resource = rdfNode.asResource();
								OntClassInfo ontClassInfo = new OntClassInfo(resource.getURI(), resource.getNameSpace(), resource.getLocalName(), ClassType.NamedClass);
								domainList.add(ontClassInfo);
							}
						}
					}
				}
			}
		}
		return domainList;
	}
	
	@Override
	public ArrayList<OntPropertyInfo> listSubPropertiesWithSimpleInfo(String URI){
		
		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		OntProperty property = this.ontModel.getOntProperty(URI);
		if (property == null) {
			return ontPropertyInfoList;
		}
		
		ExtendedIterator<? extends OntProperty> ontProperties = property.listSubProperties(true);
		while (ontProperties.hasNext()) {
			OntProperty ontProperty = ontProperties.next();
			this.createSimpleOntPropertyInfo(ontPropertyInfoList, ontProperty);
		}
		return ontPropertyInfoList;
		
	}
	
	@Override
	public ArrayList<OntPropertyInfo> listSuperPropertiesWithSimpleInfo(String URI){
		
		
		ArrayList<OntPropertyInfo> ontPropertyInfoList = new ArrayList<OntPropertyInfo>();
		OntProperty property = this.ontModel.getOntProperty(URI);
		if (property == null) {
			return ontPropertyInfoList;
		}
		
		ExtendedIterator<? extends OntProperty> ontProperties = property.listSuperProperties(true);
		while (ontProperties.hasNext()) {
			OntProperty ontProperty = ontProperties.next();
			this.createSimpleOntPropertyInfo(ontPropertyInfoList, ontProperty);
		}
		return ontPropertyInfoList;
	}

	/**
	 * @param ontPropertyInfoList
	 * @param ontProperty
	 */
	private void createSimpleOntPropertyInfo(ArrayList<OntPropertyInfo> ontPropertyInfoList, OntProperty ontProperty) {
		if (ontProperty.isObjectProperty()) {

			OntPropertyInfo newOntPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
			newOntPropertyInfo.setPropertyType(OntPropertyType.ObjectProperty);
			newOntPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
			ontPropertyInfoList.add(newOntPropertyInfo);
			
		} else if (ontProperty.isDatatypeProperty()) {

			OntPropertyInfo ontPropertyInfo = new OntPropertyInfo(ontProperty.getURI(), ontProperty.getNameSpace(), ontProperty.getLocalName());
			ontPropertyInfo.setPropertyType(OntPropertyType.DataTypeProperty);
			ontPropertyInfo.setDescriptions(this.getTextualDescriptions(ontProperty.getURI()));
			ontPropertyInfoList.add(ontPropertyInfo);
		}
	}
}
