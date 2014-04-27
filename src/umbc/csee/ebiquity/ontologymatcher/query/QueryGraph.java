package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleStatement;
import umbc.csee.ebiquity.ontologymatcher.query.Twig.Type;
import umbc.csee.ebiquity.ontologymatcher.utility.Namespace;

public class QueryGraph {

	private List<Twig> twigList;
	private List<OntClassInfo> ontClassList;
	private StringBuilder sb_QueryTriple ;
	public QueryGraph(List<Twig> twigList, List<OntClassInfo> ontClassList) {
		this.twigList = twigList;
		this.ontClassList = ontClassList;
	}
	
	public List<Twig> getTwigList() {
		return this.twigList;
	}
	
	public String createSPARQLQuery() {

		sb_QueryTriple = new StringBuilder();
		for (Twig twig : twigList) {
			if (twig.isOnePointTwig()) {
				OnePointTwig onePointTwig = twig.asOnePointTwig();
				onePointTwig.getEndPointOne();

			} else if (twig.isTwoPointTwig()) {
				TwoPointTwig twoPointTwig = twig.asTwoPointTwig();
				Path path = twoPointTwig.getPath(0);
				// twoPointTwig.getEndPointOne();
				// twoPointTwig.getEndPointTwo();
				sb_QueryTriple.append(" OPTIONAL { \n");
				sb_QueryTriple.append(" " + this.createQueryTriples(path));
				sb_QueryTriple.append(" } \n");

			} else if (twig.isThreePointTwig()) {
				ThreePointTwig threePointTwig = twig.asThreePointTwig();
				Path path1 = threePointTwig.getPathFromListOne(0);
				Path path2 = threePointTwig.getPathFromListTwo(0);
				// threePointTwig.getEndPointOne();
				// threePointTwig.getEndPointTwo();
				OntClassInfo intersectPoint = threePointTwig.getIntersetPoint();
				sb_QueryTriple.append(" OPTIONAL { \n");
				sb_QueryTriple.append(" " + this.createQueryTriples(path1));
				sb_QueryTriple.append(" " + this.createQueryTriples(path2));
				sb_QueryTriple.append(" } \n");
				if (threePointTwig.getType() == Type.ThreePointTwig_Forward) {
					this.ontClassList.add(intersectPoint);
				} else if (threePointTwig.getType() == Type.ThreePointTwig_Backward) {
					this.ontClassList.add(0, intersectPoint);
				}

			}
		}

		StringBuilder query = new StringBuilder();
		query.append(this.createSELECTSection());
		query.append(this.createWHERESection());
		return query.toString();
	}

	// It might be better to use topological sort to determine the order of
	// variables in the SELECT section
	private String createSELECTSection() {
		StringBuilder sb_SelectSection = new StringBuilder();
		sb_SelectSection.append("SELECT ");
		for (OntClassInfo ontClassInfo : this.ontClassList) {
			sb_SelectSection.append("?" + ontClassInfo.getLocalName() + " ");
		}
		sb_SelectSection.append(" \n");
		// System.out.print(sb_SelectSection.toString());
		return sb_SelectSection.toString();
	}

	private String createWHERESection() {
		StringBuilder sb_WhereSection = new StringBuilder();
		sb_WhereSection.append("WHERE { \n");
//		sb_WhereSection.append(this.createVariableTypeTriple());
		sb_WhereSection.append(this.sb_QueryTriple);
		sb_WhereSection.append("} \n");
		// System.out.print(sb_WhereSection.toString());
		return sb_WhereSection.toString().trim();
	}

	private String createQueryTriples(Path path) { 
		StringBuilder stringBuilder = new StringBuilder();
		List<SimpleStatement> statements = path.getStatements();
		for (int i = 0; i < statements.size(); i++) {
			stringBuilder.append("?" + statements.get(i).getSubject().getLocalName() + " <" + statements.get(i).getPredicate().getURI() + "> ?" + statements.get(i).getObject().getLocalName()
					+ " . \n");
		}
		// System.out.print(stringBuilder.toString());
		return stringBuilder.toString();
	}

	private String createVariableTypeTriple() {
		StringBuilder sb_VarType = new StringBuilder();
		for (OntClassInfo ontClassInfo : this.ontClassList) {
			sb_VarType.append(" ?" + ontClassInfo.getLocalName() + " <" + Namespace.RDF + "type" + "> <" + ontClassInfo.getURI() + "> . \n");
		}
		// System.out.print(sb_VarType.toString());
		return sb_VarType.toString();
	}
}
