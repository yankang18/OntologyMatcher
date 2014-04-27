package umbc.csee.ebiquity.ontologymatcher.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleStatement;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo.ClassType;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import umbc.csee.ebiquity.ontologymatcher.query.Twig.Type;

/***
 * 
 * This class is used to generate a graph
 * @author kangyan2003
 */
public class QueryGraphCreator {
	
	private ResourceModel resModel;
	private String endClassURI;
	private List<Path> paths;
	private OntClassInfo startClassInfo;
	private OntClassInfo endClassInfo;

	// for test
	public static void main(String[] args) {
		OntClassInfo A = new OntClassInfo("A", "kangyan", "A", ClassType.NamedClass);
		OntClassInfo C = new OntClassInfo("C", "kangyan", "C", ClassType.NamedClass);
		OntClassInfo B = new OntClassInfo("B", "kangyan", "B", ClassType.NamedClass);
		OntClassInfo D = new OntClassInfo("D", "kangyan", "D", ClassType.NamedClass);
		List<OntClassInfo> nodesAsCandidates = new ArrayList<OntClassInfo>();
		nodesAsCandidates.add(A);
		nodesAsCandidates.add(B);
		nodesAsCandidates.add(C);
		nodesAsCandidates.add(D);
		QueryGraphCreator ps = new QueryGraphCreator();
		QueryGraph graph = ps.getQueryGraph(nodesAsCandidates);
		System.out.println("-------------");
		for (Twig twig : graph.getTwigList()) {
			System.out.println(twig.getKey());
		}
	}

	public QueryGraphCreator(ResourceModel model) {
		this.resModel = model;
	}

	// for test
	public QueryGraphCreator() {
	}
	
	public QueryGraph getQueryGraph(List<OntClassInfo> nodesAsCandidates){
		
		int sizeOfClass = nodesAsCandidates.size();
		if(sizeOfClass == 0){
			return null;
		}
		List<Twig> twigList = new ArrayList<Twig>();
		
		List<OntClassInfo> copyOfCandidateNodes = new ArrayList<OntClassInfo>();
		for (OntClassInfo ontClassInfo : nodesAsCandidates) {
			copyOfCandidateNodes.add(ontClassInfo);
		}
		QueryGraph queryGraph = new QueryGraph(twigList, copyOfCandidateNodes);
		
		// if only one class, this query graph will only contains a one-point twig
		if(sizeOfClass == 1){
			OnePointTwig onePointTwig = new OnePointTwig(nodesAsCandidates.get(0));
			twigList.add(onePointTwig);
			return queryGraph;
		}
		
		// if there are more than one classes, creating the query graph that is
		// consist of twigs having more than one point.
		int capability = sizeOfClass * (sizeOfClass - 1) / 2;
		PriorityQueue<Candidate> queue = new PriorityQueue<Candidate>(capability, new Comparator<Candidate>() {
			@Override
			public int compare(Candidate ca1, Candidate ca2) {
				return ca1.distanceToSubGraph() - ca2.distanceToSubGraph();
			}
		});

		List<OntClassInfo> nodesInSubGraph = new ArrayList<OntClassInfo>();
		nodesInSubGraph.add(nodesAsCandidates.get(0));
		nodesAsCandidates.remove(0);
		while (nodesAsCandidates.size() != 0) {
//			System.out.println(" ");
//			System.out.println("Nodes in Subgraph: ");
			for(OntClassInfo node: nodesInSubGraph){
				System.out.println(node.getURI());
			}
//			System.out.println("Nodes in candidate list: ");
			for(OntClassInfo node: nodesAsCandidates){
				System.out.println(node.getURI());
			}
			OntClassInfo nodeInSubGraph = nodesInSubGraph.get(nodesInSubGraph.size() - 1);
			for (int i = 0; i < nodesAsCandidates.size(); i++) {
				OntClassInfo currentCandidate = nodesAsCandidates.get(i);
				Twig currentTwig = this.twigConstructor(nodeInSubGraph, currentCandidate);
				Candidate candidate = new Candidate(currentTwig, currentCandidate);
				queue.add(candidate);
			}
			Candidate topCandidate = queue.remove();
			nodesInSubGraph.add(topCandidate.getCandidateNode());
			nodesAsCandidates.remove(topCandidate.getCandidateNode());
			twigList.add(topCandidate.getTwig());
		}
		return queryGraph;
	}
	
//	private Twig twigConstructor_ForTest(OntClassInfo endpoint1, OntClassInfo endpoint2) {
//		if (endpoint1.getURI().equals("A") && endpoint2.getURI().equals("B")) {
//			return new Twig(1, "AB");
//		} else if (endpoint1.getURI().equals("A") && endpoint2.getURI().equals("C")) {
//			return new Twig(6, "AC");
//		} else if (endpoint1.getURI().equals("A") && endpoint2.getURI().equals("D")) {
//			return new Twig(3, "AD");
//		} else if (endpoint1.getURI().equals("B") && endpoint2.getURI().equals("C")) {
//			return new Twig(6, "BC");
//		} else if (endpoint1.getURI().equals("B") && endpoint2.getURI().equals("D")) {
//			return new Twig(4, "BD");
//		} else {
//			return new Twig(2, "CD");
//		}
//	}

	/***
	 * construct a twig that span the two class end points.
	 */
	private Twig twigConstructor(OntClassInfo endpoint1, OntClassInfo endpoint2) {
		List<Path> paths = this.findPaths(endpoint1.getURI(), endpoint2.getURI());
		if (paths.size() == 0) {
			paths = this.findPaths(endpoint2.getURI(), endpoint1.getURI());
		}
		if (paths.size() > 0) {
			TwoPointTwig twoPointTwig = new TwoPointTwig(paths);
			return twoPointTwig;
		} else {
			// if no two-point twigs exist, we will try to find three-point
			// twigs.
			OntClassInfo intersectNode = getIntersectNode(endpoint1.getURI(), endpoint2.getURI(), true);
			if (intersectNode != null) {
				List<Path> pathList1 = this.findPaths(endpoint1.getURI(), intersectNode.getURI());
				List<Path> pathList2 = this.findPaths(endpoint2.getURI(), intersectNode.getURI());
				if (pathList1.size() != 0 && pathList2.size() != 0) {
					ThreePointTwig threePointTwig = null;
					try {
						threePointTwig = new ThreePointTwig(pathList1, pathList2, Type.ThreePointTwig_Forward);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					return threePointTwig;
				} else {
					// Actually this code section should not be visited. Because
					// if there is a intersect node exists, there must exist
					// paths from endpoint1 and endpoint2 to this node.
					return null;
				}
			} else {
				intersectNode = getIntersectNode(endpoint1.getURI(), endpoint2.getURI(), false);
				if (intersectNode != null) {
					List<Path> pathList1 = this.findPaths(intersectNode.getURI(), endpoint1.getURI());
					List<Path> pathList2 = this.findPaths(intersectNode.getURI(), endpoint2.getURI());
					if (pathList1.size() != 0 && pathList2.size() != 0) {
						ThreePointTwig threePointTwig = null;
						try {
							threePointTwig = new ThreePointTwig(pathList1, pathList2, Type.ThreePointTwig_Backward);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
						return threePointTwig;
					} else {
						// Actually this code section should not be visited.
						// Because if there is a intersect node exists, there must exist
						// paths from endpoint1 and endpoint2 to this node.
						return null;
					}
				} else {
					return null;
				}
			}
		}
	}

	public OntClassInfo getIntersectNode(String classURI1, String classURI2, boolean forward) {
		LinkedHashSet<Node> reachableNodes = this.getAllReachableNode(classURI2, forward);
		OntClassInfo startNode = resModel.getOntClass(classURI1);
		Node start = new Node(startNode);
		LinkedHashSet<Node> visited = new LinkedHashSet<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		// visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			Node r = queue.removeFirst();
			List<Node> nodes = this.getAdjacentNodes(r.getNodeURI(), forward);
			for (Node node : nodes) {
				if (!visited.contains(node)) {
					if (reachableNodes.contains(node)) {
						System.out.println("the insection is: " + node.getNodeURI());
						OntClassInfo intersectNode = resModel.getOntClass(node.getNodeURI());
						return intersectNode;
					}
					visited.add(node);
					queue.addLast(node);
				}
			}
		}
		return null;
	}
	
	/***
	 * get all the nodes that are reachable by the class represented by this classURI.
	 * @param classURI
	 * @param forward
	 * @return a list of nodes
	 */
	public LinkedHashSet<Node> getAllReachableNode(String classURI, boolean forward){
		OntClassInfo startNode = resModel.getOntClass(classURI);
		Node start = new Node(startNode);
		LinkedHashSet<Node> visited = new LinkedHashSet<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			Node r = queue.removeFirst();
//			System.out.println("@: " + r.getNodeURI() + " has adjacents: ");
			List<Node> nodes = this.getAdjacentNodes(r.getNodeURI(), forward);
			for (Node node : nodes) {
//				System.out.println("@:  -> " + node.getNodeURI());
				if (!visited.contains(node)) {
					visited.add(node);
					queue.addLast(node);
				} else {
//					System.out.println("contains: "  + node.getNodeURI());
				}
			}
		}
		
		// for test
//		System.out.println("   ");
//		Iterator<Node> iterator = visited.iterator();
//		while(iterator.hasNext()){
//			Node node = iterator.next();
//			System.out.println(" -> "+node.getNodeURI());
//		}
		//
		
		return visited;
	}
	
	/***
	 * find the shortest path between two classes in ontology.
	 * 
	 * NOTE: Now this algorithm is wrong!!!
	 * 
	 * @param startClassURI
	 * @param endClassURI
	 * @return shortest path
	 */
	public Path findShortestPath(String startClassURI, String endClassURI) {
		startClassInfo = resModel.getOntClass(startClassURI);
		endClassInfo = resModel.getOntClass(endClassURI);
		if (startClassInfo == null || endClassInfo == null) {
			this.startClassInfo = null;
			this.endClassInfo = null;
			return null;
		}
		Node start = new Node(startClassInfo);
		LinkedList<Node> visited = new LinkedList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		visited.add(start);
		queue.addLast(start);
		while (!queue.isEmpty()) {
			Node r = queue.removeFirst();
			List<Node> nodes = this.getAdjacentNodes(r.getNodeURI(), true);
			for (Node node : nodes) {
				if (visited.contains(node)) {
					continue;
				}
				if (node.getNodeURI().equals((endClassURI))) {
					visited.add(node);
					return this.createPath(visited);
				}
				visited.add(node);
				queue.addLast(node);
			}
		}
		return null;
	}

	/***
	 * find all the paths between two classes in ontology.
	 * @param startClassURI
	 * @param endClassURI
	 * @return a list of paths
	 */
	public List<Path> findPaths(String startClassURI, String endClassURI) {
		startClassInfo = resModel.getOntClass(startClassURI);
		endClassInfo = resModel.getOntClass(endClassURI);
		this.paths = new ArrayList<Path>();
		if (startClassInfo == null || endClassInfo == null) {
			this.startClassInfo = null;
			this.endClassInfo = null;
			return this.paths; 
		}
		this.endClassURI = endClassURI;
		Node start = new Node(startClassInfo);
		LinkedList<Node> visited = new LinkedList<Node>();
		visited.add(start);
		this.depthSearch(visited);
		Collections.sort(paths);
		return this.paths;
	}

	private void depthSearch(LinkedList<Node> visited) {
//		this.printPath(visited);
		List<Node> nodes = this.getAdjacentNodes(visited.getLast().getNodeURI(), true);
		for (Node node : nodes) {
			if (visited.contains(node)) {
				continue;
			}
			if (node.getNodeURI().equals((endClassURI))) {
				visited.add(node);
				this.recordPaths(visited);
				visited.removeLast();
				continue;
			}
			visited.add(node);
			this.depthSearch(visited);
			visited.removeLast();
		}
	}

//	private void printPath(List<Node> visited) {
//		StringBuilder sb = new StringBuilder();
//		for (Node node : visited) {
//			sb.append(" " + node.getOntClass().getLocalName());
//		}
//		System.out.println(sb.toString());
//	}

	private Path createPath(List<Node> visited) {
		int numberOfNodes = visited.size();
		Node start = visited.get(0);
		Node next = visited.get(1);
		List<SimpleStatement> statements = new ArrayList<SimpleStatement>();
		SimpleStatement stmt_start = new SimpleStatement(start.getOntClass(), next.getOntProperty(), next.getOntClass());
		statements.add(stmt_start);
		for (int i = 2; i < visited.size(); i++) {
			Node before = visited.get(i - 1);
			Node curr = visited.get(i);
			SimpleStatement stmt = new SimpleStatement(before.getOntClass(), curr.getOntProperty(), curr.getOntClass());
			statements.add(stmt);
		}
		Path path = new Path(statements);
		path.setStart(startClassInfo);
		path.setEnd(endClassInfo);	
		path.setNumberOfNodes(numberOfNodes);
		return path;
	}
	
	private void recordPaths(List<Node> visited) {
		this.paths.add(this.createPath(visited));
	}

	private LinkedList<Node> getAdjacentNodes(String lastURI, boolean forward) {
		// System.out.println("Node: " + lastURI);
		LinkedList<Node> nodeList = new LinkedList<Node>();
		List<OntPropertyInfo> ontPropertyList = null;
		if (forward) {
			ontPropertyList = resModel.listDomainOntClassProperties(lastURI);
		} else {
			ontPropertyList = resModel.listRangeOntClassProperties(lastURI);
		}
		for (OntPropertyInfo ontPropertyInfo : ontPropertyList) {
			// System.out.println("property: " + ontPropertyInfo.getURI());
			List<OntClassInfo> ontClassList = null;
			if (forward) {
				ontClassList = resModel.listImposedRangeOntClasses(ontPropertyInfo);
			} else {
				ontClassList = resModel.listImposedDomainOntClasses(ontPropertyInfo);
			}
			for (OntClassInfo ontClass : ontClassList) {
				// System.out.println("class: " + ontClass.getURI()); 
				Node node = new Node(ontPropertyInfo, ontClass);
				nodeList.add(node);
			}
		}
		return nodeList;
	}
}

class Node {

	private OntResourceInfo ontProperty;
	private OntResourceInfo ontClass;
	public Node(OntResourceInfo ontClass) {
		this.ontClass = ontClass;
	}

	public Node(OntResourceInfo ontProperty, OntResourceInfo ontClass) {
		this.ontClass = ontClass;
		this.ontProperty = ontProperty;
	}

	public OntResourceInfo getOntProperty() {
		return ontProperty;
	}

	public OntResourceInfo getOntClass() {
		return ontClass;
	}

	public String getNodeURI() {
		return this.ontClass.getURI().trim();
	}

	@Override
	public String toString() {
		return this.ontClass.getURI().trim();
	}
	
	@Override
	public int hashCode(){
		return this.getNodeURI().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Node node = (Node) obj;
		if (this.getNodeURI().equals(node.getNodeURI())) {
			return true;
		}
		return false;
	}
}
