package umbc.csee.ebiquity.ontologymatcher.query;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.FileManager;

public class OntologyQueryEngine {
	
	private static final String DBpedia_SPARQLEndpoint = "http://dbpedia.org/sparql";
	private static String projectDir = System.getProperty("user.dir");
	
	private Model knowledgeBaseModel;
	public OntologyQueryEngine(String fileLocation, String lang, boolean infer){
		
		if (infer == false) {
			knowledgeBaseModel = this.getLocalRDFGraphModel(fileLocation, lang);
		} else {
			knowledgeBaseModel = this.getLocalInfRDFGraphModel(fileLocation, lang);
		}
	}
	
//	/***
//	 * return matched resource URIs
//	 * 
//	 * @param uri
//	 * @return
//	 */
//	public List<String> lookupResource(String localName) throws QueryConstructionException {
//
//		Query query = this.constructQuery(localName);
//		if (query == null)
//			throw new QueryConstructionException("Query Construction fails. One of the possibilities is that the query string might have problem");
//
//		List<String> resources = new ArrayList<String>();
//		QueryExecution queryExec = QueryExecutionFactory.sparqlService(DBpedia_SPARQLEndpoint, query);
//		ResultSet results = queryExec.execSelect();
//		try {
//			while (results.hasNext()) {
//				QuerySolution solution = results.nextSolution();
//				Resource res = solution.getResource("m");
//				System.out.println(res.getURI());
//				resources.add(res.getURI());
//			}
//
//		} finally {
//			queryExec.close();
//		}
//
//		return resources;
//	}

//	private Query constructQuery(String localName) throws QueryParseException {
//
//		
//		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
//		                     "SELECT DISTINCT ?m " + 
//		                     "WHERE {" + 
//		                     "?m rdfs:label "+ "\""+localName +"\""+ "@en." + 
//		                     "}";
//		System.out.println(queryString);
//		Query query;
//		try {
//			query = QueryFactory.create(queryString);
//		} catch (QueryParseException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return query;
//
//	}
	
	
//	/***
//	 * 
//	 * get remote RDF graph by using Uri, which can be seen as the graph name.
//	 * 
//	 * @param uri
//	 * @return Model
//	 */
//	private Model getRemoteRDFGraphModel(String uri, String lang){
//		Model model = ModelFactory.createDefaultModel();
//		model.read(uri, lang);
//		return model;
//	}
	
	private Model getLocalRDFGraphModel(String fileLocation, String lang){
		Model model = ModelFactory.createDefaultModel();
		System.out.println("file location: " + fileLocation);
		InputStream instream = FileManager.get().open(fileLocation);
		model.read(instream, "", lang);
		return model;
	}

	private Model getLocalInfRDFGraphModel(String fileLocation, String lang) {
		Model model = this.getLocalRDFGraphModel(fileLocation, lang);
		Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		return ModelFactory.createInfModel(reasoner, model);
	}

//	public List<HashMap<String, String>> queryLocalRDFGraph(String fileLocation, String queryString, String lang) {
//
//		Query query;
//		try {
//			query = QueryFactory.create(queryString);
//		} catch (Exception e) {
//			return null;
//		}
//
//		Model model = this.getLocalRDFGraphModel(fileLocation, lang);
//		InfModel infModel = ModelFactory.createRDFSModel(model);
//		return this.getQueryResult(query, infModel);
//	}
	
	public List<LinkedHashMap<String, String>> queryLocalRDFGraph(String queryString) {
		Query query;
		try {
			query = QueryFactory.create(queryString);
		} catch (Exception e) {
			return null;
		}
		return this.getQueryResult(query);
	}


//	/***
//	 * run query against a RDF graph. This RDF graph will be retrieved by using
//	 * its uri. And then the query results will be returned
//	 * 
//	 * @param uri uniform resource identifier for the RDF graph.
//	 * @param queryString query string
//	 * @return a list of solutions. Every item of the list is an instance of
//	 *         HashMap. The keys are variable names defined by user, the values
//	 *         are values of the variables.
//	 */
//	public List<HashMap<String, String>> queryAgainstRemoteRDFGraph(String uri, String queryString, String lang) {
//
//		Query query;
//		try {
//			query = QueryFactory.create(queryString);
//		} catch (Exception e) {
//			return null;
//		}
//	
//		Model model = this.getRemoteRDFGraphModel(uri, lang);
//		InfModel infModel = ModelFactory.createRDFSModel(model);
//		return this.getQueryResult(query, infModel);
//	}
	
//	private List<HashMap<String, String>> getQueryResult(Query query, InfModel model){
//		
//		QueryExecution qe = QueryExecutionFactory.create(query, model);
//		ResultSet results = qe.execSelect();
//		
////		ResultSetFormatter.out(results);
//		
//		List<String> vars = results.getResultVars();
//		ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
//		while (results.hasNext()) {
//
//			QuerySolution solution = results.next();
//			HashMap<String, String> var_value_map = new HashMap<String, String>();
//			for (String var : vars) {
//
//				RDFNode rdfNode = solution.get(var);
//				if (rdfNode != null) {
//					if (rdfNode.isLiteral()) {
//						String value = rdfNode.asLiteral().getLexicalForm();
//
//						System.out.println("1 "+"type: " + rdfNode.asLiteral().getDatatypeURI());
//						var_value_map.put(var, value);
//					} else if (rdfNode.isResource()) {
//						String value = rdfNode.asResource().getURI();
//						var_value_map.put(var, value);
//					} else {
//						var_value_map.put(var, null);
//					}
//				} else {
//					var_value_map.put(var, null);
//				}
//
//			}
//			resultList.add(var_value_map);
//		}
//
//		return resultList;
//	}
	
	private List<LinkedHashMap<String, String>> getQueryResult(Query query){
		
		QueryExecution qe = QueryExecutionFactory.create(query, knowledgeBaseModel);
		ResultSet results = qe.execSelect();
		
//		ResultSetFormatter.out(results);
		
		List<String> vars = results.getResultVars();
		ArrayList<LinkedHashMap<String, String>> resultList = new ArrayList<LinkedHashMap<String, String>>();
		while (results.hasNext()) {

			QuerySolution solution = results.next();
			LinkedHashMap<String, String> var_value_map = new LinkedHashMap<String, String>();
			for (String var : vars) {

				RDFNode rdfNode = solution.get(var);
				if (rdfNode != null) {
					if (rdfNode.isLiteral()) {
						String value = rdfNode.asLiteral().getLexicalForm();

//						System.out.println("1 "+"type: " + rdfNode.asLiteral().getDatatypeURI());
						var_value_map.put(var, value);
					} else if (rdfNode.isResource()) {
						String value = rdfNode.asResource().getLocalName();
						var_value_map.put(var, value);
					} else {
						var_value_map.put(var, null);
					}
				} else {
					var_value_map.put(var, null);
				}

			}
			resultList.add(var_value_map);
		}

		return resultList;
	}

}
