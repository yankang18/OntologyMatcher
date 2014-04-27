package umbc.csee.ebiquity.ontologymatcher.utility;

import java.util.ArrayList;
import java.util.List;

public class Namespace {
	
	public static final String OWL = "http://www.w3.org/2002/07/owl#";
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	
	private static List<String> standardNS;
	
	static {
		standardNS = new ArrayList<String>();
		standardNS.add(OWL);
		standardNS.add(RDF);
		standardNS.add(RDFS);
	}
	
	public static boolean contains(String ns){
		return standardNS.contains(ns);
	}
}
