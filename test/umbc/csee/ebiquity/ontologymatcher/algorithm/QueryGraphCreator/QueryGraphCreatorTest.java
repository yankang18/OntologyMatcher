package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.query.OntologyQueryEngine;
import umbc.csee.ebiquity.ontologymatcher.query.Path;
import umbc.csee.ebiquity.ontologymatcher.query.QueryGraph;
import umbc.csee.ebiquity.ontologymatcher.query.QueryGraphCreator;
import umbc.csee.ebiquity.ontologymatcher.query.Twig;

import com.hp.hpl.jena.util.FileManager;

public class QueryGraphCreatorTest {
	private String projectDir = System.getProperty("user.dir");
	@Test
	public void testGetQueryGraph() {
		
//		String dir = projectDir + "/MOntologies/ThomasNet_Yan/ThomasNet.owl";
		String dir = projectDir + "/MOntologies/globalspec_modified.owl";
		
		OntologyQueryEngine GSengine = new OntologyQueryEngine(dir, "RDF/XML", true);
		
		InputStream instream = FileManager.get().open(dir);
		
		String URI1 = "http://www.nist.gov/simca/msnm/sample/glbalspec.owl#Supplier";
		String URI2 = "http://www.nist.gov/simca/msnm/sample/glbalspec.owl#Capability";
		String URI3 = "http://www.nist.gov/simca/msnm/sample/glbalspec.owl#Services";
		String URI4 = "http://www.nist.gov/simca/msnm/sample/glbalspec.owl#Material";
//		String URI5 = "http://www.nist.gov/simca/msnm/sample/glbalspec.owl#ServiceDetails";
		OntResourceModel ontResModel = new OntResourceModel(instream);
		ontResModel.preprocessing();
		QueryGraphCreator pathSearcher2 = new QueryGraphCreator(ontResModel);
		
		List<OntClassInfo> nodesAsCandidates = new ArrayList<OntClassInfo>();
		nodesAsCandidates.add(ontResModel.getOntClass(URI1));
		nodesAsCandidates.add(ontResModel.getOntClass(URI3));
		nodesAsCandidates.add(ontResModel.getOntClass(URI2));
		QueryGraph queryGraph = pathSearcher2.getQueryGraph(nodesAsCandidates);
		for (Twig twig : queryGraph.getTwigList()) {
			System.out.println(" ");
			System.out.println("twig:  ");
			System.out.println("type: " + twig.getType());
			System.out.println("node num: " + twig.getNumberOfNodes());
			System.out.println(twig.getEndPointOne());
			System.out.println(twig.getEndPointTwo());
			if (twig.isThreePointTwig()) {
				System.out.println("intersect: " + twig.asThreePointTwig().getIntersetPoint());
				Path path1 = twig.asThreePointTwig().getPathFromListOne(0);
				Path path2 = twig.asThreePointTwig().getPathFromListTwo(0);
				System.out.println(path1);
				System.out.println(path2);
			} else if (twig.isTwoPointTwig()) {
				Path path = twig.asTwoPointTwig().getPath(0);
				System.out.println(path);
			}
		}
		
		String query = queryGraph.createSPARQLQuery();
		System.out.println(" ");
		System.out.println("Query:");
		System.out.println(" ");
		System.out.println(query);
		List<LinkedHashMap<String, String>> solutions = GSengine.queryLocalRDFGraph(query);
		printSolutions(solutions);
	}
	
	private void printSolutions(List<LinkedHashMap<String, String>> solutions) {
		
		if (solutions == null || solutions.size() == 0) {
			return;
		}
		LinkedHashMap<String, String> sol = solutions.get(0);
		Iterator<String> keys = sol.keySet().iterator();
		while (keys.hasNext()) {
			System.out.print(keys.next() + "       ");
		}
		System.out.print(" \n");

		for (LinkedHashMap<String, String> solution : solutions) {
			Iterator<String> values = solution.values().iterator();
			while (values.hasNext()) {
				System.out.print(values.next() + "     ");
			}
			System.out.print(" \n");
		}
	}

}
