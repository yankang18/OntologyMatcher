package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.ArrayList;
import java.util.List;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleStatement;

public class Path implements Comparable<Path>{

	private List<SimpleStatement> statements;
	private OntClassInfo start;
	private OntClassInfo end;
	private int numberOfNodes;

	public Path(List<SimpleStatement> statements) {
		this.statements = statements;
	}

	public List<SimpleStatement> getStatements() {
		return statements;
	}
	
	public List<String> getStatementStrings() {
		List<String> stmts = new ArrayList<String>();
		for (SimpleStatement stmt : this.statements) {
			stmts.add(stmt.toString());
		}
		return stmts;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		SimpleStatement stmt_start = statements.get(0);
		sb.append(stmt_start.getSubject().getLocalName() + " " + stmt_start.getPredicate().getLocalName() + " " + stmt_start.getObject().getLocalName());
		for (int i = 1; i < statements.size(); i++) {

			sb.append(" " + statements.get(i).getPredicate().getLocalName() + " " + statements.get(i).getObject().getLocalName());
		}

		return sb.toString();
	}

	public void setStart(OntClassInfo start) {
		this.start = start;
	}

	public OntClassInfo getStart() {
		return start;
	}

	public void setEnd(OntClassInfo end) {
		this.end = end;
	}

	public OntClassInfo getEnd() {
		return end;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	@Override
	public int compareTo(Path path) {
		return this.getNumberOfNodes() - path.getNumberOfNodes();
	}
}
