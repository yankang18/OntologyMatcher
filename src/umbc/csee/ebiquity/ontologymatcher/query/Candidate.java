package umbc.csee.ebiquity.ontologymatcher.query;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;

public class Candidate {
	private OntClassInfo candidateNode;
	private Twig twig;
	private int numberNodesOfTwig;
	
	public Candidate(Twig twig, OntClassInfo candidateNode){
		this.candidateNode = candidateNode;
		this.twig = twig;
		this.numberNodesOfTwig = twig.getNumberOfNodes();
	}
	
	public OntClassInfo getCandidateNode(){
		return this.candidateNode;
	}
	
	public int distanceToSubGraph(){
		return this.numberNodesOfTwig;
	}
	
	public Twig getTwig(){
		return this.twig;
	}

	@Override
	public String toString() {
		return this.candidateNode.getURI().trim();
	}

	@Override
	public int hashCode() {
		return this.candidateNode.getURI().trim().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		Candidate ca = (Candidate) obj;
		if (this.toString().equals(ca.toString())) {
			return true;
		} else {
			return false;
		}
	}
}
