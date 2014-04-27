package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.DatatypeMatcher.Datatype;
import umbc.csee.ebiquity.ontologymatcher.textprocessing.TextProcessingUtils;

public class OntPropertyInfo extends OntResourceInfo {
	
	public enum OntPropertyType {
		DataTypeProperty, ObjectProperty
	}	
	
	private String revisedLocalName;
	private OntPropertyType type;
	private String value;
//	private Resource resource;
	private OntResourceInfo ontResourceInfo;
	private List<OntResourceInfo> ranges;
	private List<OntResourceInfo> domains;
	
	private Collection<OntResourceInfo> subjectCandidates;
	private Collection<OntResourceInfo> objectCandidates;
	private Datatype datatype;

	/***
	 * 
	 * @param URI
	 * @param namespace
	 * @param localname
	 */
	public OntPropertyInfo(String URI, String namespace, String localname){
		super(URI, namespace, localname);
		this.revisedLocalName = this.filter(TextProcessingUtils.tokenizeLabel(this.getLocalName()));
		this.ranges = new ArrayList<OntResourceInfo>();
		this.domains = new ArrayList<OntResourceInfo>();
		
		this.subjectCandidates = new ArrayList<OntResourceInfo>();
		this.objectCandidates = new ArrayList<OntResourceInfo>();
	}
	
	public void setPropertyType(OntPropertyType type) {
		this.type = type;
		if (this.type == OntPropertyType.ObjectProperty) {
			this.setResourceType(ResourceType.ObjectProperty);
		} else {
			this.setResourceType(ResourceType.DatatypeProperty);
		}
	}

	public String getRevisedLocalName(){
		return this.revisedLocalName;
	}

	public OntPropertyType getPropertyType() {
		return this.type;
	}
	
	public void addRanges(OntResourceInfo range) {
		if (!this.ranges.contains(range)) {
			this.ranges.add(range);
		}
	}
	
	public void addDomain(OntResourceInfo domain){
		if (!this.domains.contains(domain)) {
			this.domains.add(domain);
		}
	}
	
	public void addSubjectCandidate(OntResourceInfo subjectCandidate){
		if (!this.subjectCandidates.contains(subjectCandidate)) {
			this.subjectCandidates.add(subjectCandidate);
		}
	}
	
	public void addObjectCandidate(OntResourceInfo objectCandidate){
		if (!this.objectCandidates.contains(objectCandidate)) {
			this.objectCandidates.add(objectCandidate);
		}
	}
	
	public Collection<OntResourceInfo> getSubjectCandidates(){
		return this.subjectCandidates;
	}
	
	public OntResourceInfo[] getSubjectCandidatesAsArray(){
		return this.subjectCandidates.toArray(new OntResourceInfo[0]); 
	}
	
	public Collection<OntResourceInfo> getObjectCandidates(){
		return this.objectCandidates;
	}
	
	public OntResourceInfo[] getObjectCandidatesAsArray(){
		return this.objectCandidates.toArray(new OntResourceInfo[0]); 
	}
	
	public List<String> getAllRanges() {
		List<String> rangelst = new ArrayList<String>();
		for (OntResourceInfo ontClass : ranges) {
			rangelst.add(ontClass.getLocalName());
		}
		return rangelst;
	}

	public Collection<OntResourceInfo> getAllRangeClasses(){
		return this.ranges;
	}
	
	public OntResourceInfo[] getAllRangeClassesAsArray(){
		return this.ranges.toArray(new OntResourceInfo[0]);
	}
	
	public List<String> getAllDomains() {
		List<String> domainList = new ArrayList<String>();
		for (OntResourceInfo ontClass : domains) {
			domainList.add(ontClass.getLocalName());
		}
		return domainList;
	}
	
	public Collection<OntResourceInfo> getAllDomainClasses(){
		return this.domains;
	}
	
	private String filter(String[] originalWords){
		
		List<String> reservedWords = new ArrayList<String>();
		reservedWords = StopWordsRemover.removeStopWords(originalWords);
		StringBuilder builder = new StringBuilder();
		for (String word : reservedWords) {
			String revisedWord = word.substring(0,1).toUpperCase() + word.substring(1);
			builder.append(revisedWord);
		}
		
		return builder.toString();
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setResourceForObject(OntResourceInfo ontObjectResourceInfo) {
		this.ontResourceInfo = ontObjectResourceInfo;
	}
	
//	public OntResourceInfo getResourceForObject() {
//		return ontResourceInfo;
//	}
	 
	public OntResourceInfo getOntResourceInfoForObject() {
		return ontResourceInfo;
	}

	public String getKey() {
		return this.getURI();
	}
	
	@Override
	public int hashCode() {
		return this.URI.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		OntPropertyInfo ontPropertyInfo = (OntPropertyInfo) obj;
		if (this.getURI().equals(ontPropertyInfo.getURI())) {
			return true;
		} else {
			return false;
		}
	}
}
