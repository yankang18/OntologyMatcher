package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleStatement;

public class QueryGenerator {
	public static String createQuery(Path path) {

		System.out.println(" ");
		StringBuilder sb = new StringBuilder();
		String select = "?" + path.getStart().getLocalName() + " ?" + path.getEnd().getLocalName();
		sb.append("SELECT " + select + " \n");
		sb.append("WHERE{ " + "\n");
		List<SimpleStatement> statements = path.getStatements();
		for (int i = 0; i < statements.size(); i++) {
			sb.append("?" + statements.get(i).getSubject().getLocalName() + " <" + statements.get(i).getPredicate().getURI() + "> ?" + statements.get(i).getObject().getLocalName() + " . \n");
		}
		sb.append("} " + "\n");
		System.out.print(sb.toString());
		return sb.toString();
	}

	public static String createQuery(QueryGraph graph) {

		List<Twig> twigList = graph.getTwigList();
		for (Twig twig : twigList) {

			if (twig.isOnePointTwig()) {

			} else if (twig.isTwoPointTwig()) {
				
			} else if (twig.isThreePointTwig()) {
				
			}
		}

		return null;

	}
	
	private String createSelectSection(){
		return "";
	}

}
