package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

public class SimpleStatement {
	private OntResourceInfo subject;
	private OntResourceInfo predicate;
	private OntResourceInfo object;
	public SimpleStatement(OntResourceInfo subject, OntResourceInfo predicate, OntResourceInfo object){
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	public OntResourceInfo getSubject() {return subject;}
	public OntResourceInfo getPredicate() {return predicate;}
	public OntResourceInfo getObject() {return object;}

	public String toString() {
		return this.subject.getLocalName() + " " + this.predicate.getLocalName() + " " + this.object.getLocalName();
	}
}
