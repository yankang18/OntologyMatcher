package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

public class TestRopository {

	private static String[][] conference;
	private static String sConferencePath = "/Ontologies/ConferenceOntologies/";
	private static String tConferencePath = "/Ontologies/ConferenceOntologies/";
	private static String conferenceReferencePath = "/Ontologies/ConferenceOntologies/reference-alignment/";
	
	static{
		
		conference = new String[21][3];
		
		conference[0][0] = sConferencePath + "cmt.owl";
		conference[0][1] = tConferencePath + "conference.owl";
		conference[0][2] = conferenceReferencePath + "cmt-conference.rdf";
		
		conference[1][0] = sConferencePath + "cmt.owl";
		conference[1][1] = tConferencePath + "confOf.owl";
		conference[1][2] = conferenceReferencePath + "cmt-confOf.rdf";
		
		conference[2][0] = sConferencePath + "cmt.owl";
		conference[2][1] = tConferencePath + "edas.owl";
		conference[2][2] = conferenceReferencePath + "cmt-edas.rdf";
		
		conference[3][0] = sConferencePath + "cmt.owl";
		conference[3][1] = tConferencePath + "ekaw.owl";
		conference[3][2] = conferenceReferencePath + "cmt-ekaw.rdf";
		
		conference[4][0] = sConferencePath + "cmt.owl";
		conference[4][1] = tConferencePath + "iasted.owl";
		conference[4][2] = conferenceReferencePath + "cmt-iasted.rdf";
		
		conference[5][0] = sConferencePath + "cmt.owl";
		conference[5][1] = tConferencePath + "sigkdd.owl";
		conference[5][2] = conferenceReferencePath + "cmt-sigkdd.rdf";
		
		conference[6][0] = sConferencePath + "conference.owl";
		conference[6][1] = tConferencePath + "confOf.owl";
		conference[6][2] = conferenceReferencePath + "conference-confOf.rdf";
		
		conference[7][0] = sConferencePath + "conference.owl";
		conference[7][1] = tConferencePath + "edas.owl";
		conference[7][2] = conferenceReferencePath + "conference-edas.rdf";
		
		conference[8][0] = sConferencePath + "conference.owl";
		conference[8][1] = tConferencePath + "ekaw.owl";
		conference[8][2] = conferenceReferencePath + "conference-ekaw.rdf";
		
		conference[9][0] = sConferencePath + "conference.owl";
		conference[9][1] = tConferencePath + "iasted.owl";
		conference[9][2] = conferenceReferencePath + "conference-iasted.rdf";
		
		conference[10][0] = sConferencePath + "conference.owl";
		conference[10][1] = tConferencePath + "sigkdd.owl";
		conference[10][2] = conferenceReferencePath + "conference-sigkdd.rdf";
		
		conference[11][0] = sConferencePath + "confOf.owl";
		conference[11][1] = tConferencePath + "edas.owl";
		conference[11][2] = conferenceReferencePath + "confOf-edas.rdf";
		
		conference[12][0] = sConferencePath + "confOf.owl";
		conference[12][1] = tConferencePath + "ekaw.owl";
		conference[12][2] = conferenceReferencePath + "confOf-ekaw.rdf";
		
		conference[13][0] = sConferencePath + "confOf.owl";
		conference[13][1] = tConferencePath + "iasted.owl";
		conference[13][2] = conferenceReferencePath + "confOf-iasted.rdf";
		
		conference[14][0] = sConferencePath + "confOf.owl";
		conference[14][1] = tConferencePath + "sigkdd.owl";
		conference[14][2] = conferenceReferencePath + "confOf-sigkdd.rdf";
		
		conference[15][0] = sConferencePath + "edas.owl";
		conference[15][1] = tConferencePath + "ekaw.owl";
		conference[15][2] = conferenceReferencePath + "edas-ekaw.rdf";
		
		conference[16][0] = sConferencePath + "edas.owl";
		conference[16][1] = tConferencePath + "iasted.owl";
		conference[16][2] = conferenceReferencePath + "edas-iasted.rdf";
		
		conference[17][0] = sConferencePath + "edas.owl";
		conference[17][1] = tConferencePath + "sigkdd.owl";
		conference[17][2] = conferenceReferencePath + "edas-sigkdd.rdf";
		
		conference[18][0] = sConferencePath + "ekaw.owl";
		conference[18][1] = tConferencePath + "iasted.owl";
		conference[18][2] = conferenceReferencePath + "ekaw-iasted.rdf";
		
		conference[19][0] = sConferencePath + "ekaw.owl";
		conference[19][1] = tConferencePath + "sigkdd.owl";
		conference[19][2] = conferenceReferencePath + "ekaw-sigkdd.rdf";
		
		conference[20][0] = sConferencePath + "iasted.owl";
		conference[20][1] = tConferencePath + "sigkdd.owl";
		conference[20][2] = conferenceReferencePath + "iasted-sigkdd.rdf";
		
//		conference[0][0] = sConferencePath + "cmt.owl";
//		conference[0][1] = tConferencePath + "";
//		conference[0][2] = conferenceReferencePath + "";
//		
//		conference[0][0] = sConferencePath + "cmt.owl";
//		conference[0][1] = tConferencePath + "";
//		conference[0][2] = conferenceReferencePath + "";
//		
//		conference[0][0] = sConferencePath + "cmt.owl";
//		conference[0][1] = tConferencePath + "";
//		conference[0][2] = conferenceReferencePath + "";
		
		
		
		
		
		
	}
	
	
	
	public static String[][] getConference(){
		
		return conference;
	}
}
