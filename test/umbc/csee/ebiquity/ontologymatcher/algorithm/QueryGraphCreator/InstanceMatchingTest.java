package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Evaluator;

import com.hp.hpl.jena.util.FileManager;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.ComplexOntologyMatchingAlgorithm2;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OntologyInstanceMatcher;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class InstanceMatchingTest {
	private static String projectDir = System.getProperty("user.dir");

	public static void main(String[] args) {
		
		/*
		 * NOTATION: 
		 * 
		 * sD : Source Directory
		 * tD : Target Directory
		 * sN : Source NameSpace
		 * tN : Target NameSpace
		 * sE : Source Entity
		 * tE : Target Entity
		 */
		
		String sN;
		String tN;
		String sE;
		String tE;
		
		String sD = projectDir + "/Instances/PR/person2/person21.rdf";
		String tD = projectDir + "/Instances/PR/person2/person22.rdf";
		String aD = projectDir + "/Instances/PR/person2/dataset21_dataset22_goldstandard_person.xml";
//		InstanceSetMatching(sD, tD, aD);
		
		sD = projectDir + "/Instances/PR/person1/person11.rdf";
		tD = projectDir + "/Instances/PR/person1/person12.rdf";
		aD = projectDir + "/Instances/PR/person1/dataset11_dataset12_goldstandard_person.xml";
//		InstanceSetMatching(sD, tD, aD);
		
		sD = projectDir + "/Instances/PR/restaurants/restaurant1.rdf";
		tD = projectDir + "/Instances/PR/restaurants/restaurant2.rdf";
		aD = projectDir + "/Instances/PR/restaurants/restaurant1_restaurant2_goldstandard.rdf";
//		InstanceSetMatching(sD, tD, aD);
		
		sD = projectDir + "/Instances/IIMB_SMALL/000/onto.owl";
		tD = projectDir + "/Instances/IIMB_SMALL/001/onto.owl";
		aD = projectDir + "/Instances/IIMB_SMALL/001/refalign.rdf";
		InstanceSetMatching(sD, tD, aD);
		
		/*
		 * 
		 */
		
		sD = projectDir + "/Instances/IIMB_SMALL/000/onto.owl";
		tD = projectDir + "/Instances/IIMB_SMALL/001/onto.owl";
		sN = "http://oaei.ontologymatching.org/2010/IIMBDATA/en/";
		tN = "http://oaei.ontologymatching.org/2010/IIMBDATA/en/";
		sE = "anthony_daniels";
		tE = "item6675204542847069908";
//		InstanceMatching(sD, tD, sN, tN, sE, tE);
		
		
		sD = projectDir + "/Instances/IIMB_SMALL/000/onto.owl";
		tD = projectDir + "/Instances/IIMB_SMALL/001/onto.owl";
		sN = "http://oaei.ontologymatching.org/2010/IIMBDATA/en/";
		tN = "http://oaei.ontologymatching.org/2010/IIMBDATA/en/";
		sE = "salisbury";
		tE = "item7642039279446830720";
//		InstanceMatching(sD, tD, sN, tN, sE, tE);
		

	}
	
	public static void InstanceMatching(String sD, String tD, String sN, String tN, String sE, String tE) {

		System.out.println("Source Ontology: " + sN);
		String sURI = sN + "" + sE;
		System.out.println("Target Ontology: " + tN);
		String tURI = tN + "" + tE;
		InputStream instream1 = FileManager.get().open(sD);
		InputStream instream2 = FileManager.get().open(tD);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);

		ComplexOntologyMatchingAlgorithm2 cgm2 = new ComplexOntologyMatchingAlgorithm2(sResModel, tResModel, false);
		cgm2.setMaxLevel(2);
		cgm2.compareClassOrInstanceLabel(false);
		CMResult result = cgm2.getIndividualMatchingResult(sURI, tURI);
		double labelS = result.getLabelSimilarity();
		double classS = result.getClassSimilarity();
		double propertySetS = result.getPropertySimilarity();
		
		System.out.println("label similarity: " + labelS);
		System.out.println("property set similarity: " + propertySetS);
		System.out.println("class similairty: " + classS);
		MSMResult propertySetM = result.getPropertySetMatchingResult();

		if (propertySetM != null) {
			ArrayList<SubMapping> mappingList = propertySetM.getSubMappings();
			for (int i = 0; i < mappingList.size(); i++) {
				SubMapping subMapping = mappingList.get(i);
				System.out.println("* PropertyMatched: [" + i + "]" + subMapping.s.getLocalName() + " -> " + subMapping.t.getLocalName() + " (" + subMapping.getSimilarity() + ")");
			}
		}
	}

	public static void InstanceSetMatching(String sD, String tD, String aD) {		
		
//		System.out.println("Source Ontology: " + sN);
//		System.out.println("Target Ontology: " + tN);

		File sFile = new File(sD);
		File tFile = new File(tD);
		File aFile = new File(aD);
		System.out.println("source file path: " + sFile.toURI().toString());
		System.out.println("target file path: " + tFile.toURI().toString());
		Properties params = new BasicParameters();

		try {

			AlignmentProcess processor = new OntologyInstanceMatcher();
			processor.init(sFile.toURI(), tFile.toURI());
			processor.align((Alignment) null, params);

			// Trim at various thresholds
			// Evaluate them against the references
			// and choose the one with the best F-Measure
			AlignmentParser aparser = new AlignmentParser(0);
			// Changed by Angel for Windows
			// Alignment reference = aparser.parse( "file://"+(new File (
			// "../refalign.rdf" ) . getAbsolutePath()) );
			Alignment reference = aparser.parse(aFile.toURI());
			double precison_best = 0.;
			double recall_best = 0.;
			double F_half_best = 0.;
			double F1_best = 0.;
			double F2_best = 0.;
			Alignment result = null;

			Properties p = new BasicParameters();

			for (int i = 10; i <= 20; i += 1) {
				processor.cut(((double) i) / 20);
				// This operation must be repeated because the modifications in
				// a1
				// are not taken into account otherwise
				Evaluator evaluator = new PRecEvaluator(reference, processor);
				// ExtPREvaluator evaluator2 = new ExtPREvaluator(reference,
				// processor);
				evaluator.eval(p);

				double precision = ((PRecEvaluator) evaluator).getPrecision();
				double recall = ((PRecEvaluator) evaluator).getRecall();
				double F1 = ((PRecEvaluator) evaluator).getFmeasure();
				double F2 = getFbeta(2, precision, recall);
				double F_half = getFbeta(0.5, precision, recall);

				System.err.println("Threshold " + (((double) i) / 20) + " : " + ((PRecEvaluator) evaluator).getPrecision() + "   " + ((PRecEvaluator) evaluator).getRecall() + "   "
						+ ((PRecEvaluator) evaluator).getFmeasure() + " over " + processor.nbCells() + " cells");
				System.err.println("            " + " F0.5: " + F_half + " F1: " + F1 + " F2: " + F2);
				if (((PRecEvaluator) evaluator).getFmeasure() > F1_best) {
					result = (BasicAlignment) ((BasicAlignment) processor).clone();
					precison_best = precision;
					recall_best = recall;
					F1_best = F1;
					F2_best = F2;
					F_half_best = F_half;
				}
			}
			
			System.err.println("------------------------------------------------------------------------------------------");
			System.err.println(" best:         " + " precision: " + precison_best + " recall: " + recall_best);
			System.err.println("               " + " F0.5: " + F_half_best + " F1: " + F1_best + " F2: " + F2_best);
			System.err.println("==========================================================================================");

			// Displays it as OWL Rules
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8")), true);

			AlignmentVisitor renderer = new RDFRendererVisitor(writer);
			result.render(renderer);
			writer.flush();
			writer.close();

		} catch (AlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static double getFbeta(double beta, double precision, double recall) {

		return (1 + Math.pow(beta, 2.0)) * (precision * recall) / (Math.pow(beta, 2.0) * precision + recall);
	}
}
