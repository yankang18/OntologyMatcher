package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mindswap.pellet.PropertyType;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Evaluator;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.ClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.IndividualTypeInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLComplexClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.CSMResult.ClassMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo.OntPropertyType;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntResourceInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OWLSimpleClassDescription;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.SimpleClassExpression;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.ComplexOntologyMatchingAlgorithm2;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.HierarchyMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLClassDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OWLSimpleClassDescriptionSetMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OntologyMatcher;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OntologyMatchingAlgorithm;
import umbc.csee.ebiquity.ontologymatcher.algorithm.matcher.OntologyMatchingAlgorithm.PropertySetsMatchingStrategy;
import umbc.csee.ebiquity.ontologymatcher.matcherbridge.EbiquityOntologyMatcherBridge;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;

import com.hp.hpl.jena.util.FileManager;

import eu.sealsproject.platform.res.tool.api.ToolBridgeException;
import eu.sealsproject.platform.res.tool.api.ToolException;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.ExtPREvaluator;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.eval.SemPRecEvaluator;
import fr.inrialpes.exmo.align.impl.eval.WeightedPREvaluator;
import fr.inrialpes.exmo.align.impl.eval.AveragePRGraphEvaluator;
import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import fr.inrialpes.exmo.align.util.GroupAlign;
import fr.inrialpes.exmo.align.util.GroupEval;

public class ResourceModelTest {

	private static String defaultNameSpace = "http://www.umbc.edu/ebiquity/msdl.owl#";
	private static String defaultNameSpace1 = "http://www.owl-ontologies.com/Ontology1236208666.owl#";
	private static String defaultNameSpace2 = "http://purl.org/goodrelations/v1#";
	private static String defaultNameSpace3 = "http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#";
//	private static MSMNodePathSimilarity simAlg = new MSMNodePathSimilarity();
	private static String projectDir = System.getProperty("user.dir");

	// private static XMLOutputter serializer = new XMLOutputter();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// generalTEST();
//		OWLDescriptionTEST();
//		 list_directory_test();
		// readConference_test();
//		 conferece_test();

		// CITest();
//		 CCTest();
		// matcher_bridge_test();
		 alignment_test();
		// IItest();

		// String g = "33/44/55";
		// String n = g.replace("/", "");
		// System.out.println(n);

		// long start = System.currentTimeMillis();
		// list_directory_test();
		// long end = System.currentTimeMillis();
		// long last = end - start;
		//
		// // Get elapsed time in minutes
		// float elapsedTimeMin = last / (60 * 1000F);
		// System.out.println("execution time: " + elapsedTimeMin);

	}

	public static void OWLDescriptionTEST() {

		
//		System.out.println(ClassExpression.ClassExpressionType.AllValuesFrom_Intersection.toString());
		String dir1 = projectDir + "/Ontologies/benchmarks2_new/201/onto.owl";
		String dir2 = projectDir + "/Ontologies/benchmarks2_new/101/onto.owl";
		System.err.println(dir1);
		System.err.println(dir2);
		InputStream instream1 = FileManager.get().open(dir1);
		InputStream instream2 = FileManager.get().open(dir2);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);

		
		//KVQAJFMUAHJNWSLTQQWYODYHZH
		//Evaluated-Paper
		
		//VZXCMPPYJTCYETZSXVTBUVPLOV
		//Proceedings
		
		//LBWCDGDEOGEKXVPNCBLHYVFNGM
		//Conference_Proceedings
		List<OWLSimpleClassDescription> sdescriptions = sResModel.listOWLSimpleClassDescriptions("http://oaei.ontologymatching.org/2011/benchmarks2/201/onto.owl#LBWCDGDEOGEKXVPNCBLHYVFNGM");
		List<OWLSimpleClassDescription> tdescriptions = tResModel.listOWLSimpleClassDescriptions("http://oaei.ontologymatching.org/2011/benchmarks2/101/onto.owl#Conference_Proceedings");

		for (OWLSimpleClassDescription description : sdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (SimpleClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
			}
		}
		System.out.println(" ");
		for (OWLSimpleClassDescription description : tdescriptions) {
			List<SimpleClassExpression> contents = description.getClassExpressions();
			System.out.println(" " + description.getClassRelationName());
			for (SimpleClassExpression content : contents) {
				System.out.println("-> " + content.getClassExpressionTypeName());
			}
		}

//		OWLSimpleDescriptionSetMatcher descriptionSetMatcher = new OWLSimpleDescriptionSetMatcher();
//		double similarity = descriptionSetMatcher.getSimpleClassDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
		
		
//		List<OWLComplexClassDescription> sdescriptions = sResModel.listOWLComplexClassDescriptions("http://oaei.ontologymatching.org/2011/benchmarks2/201/onto.owl#VZXCMPPYJTCYETZSXVTBUVPLOV");
//		List<OWLComplexClassDescription> tdescriptions = tResModel.listOWLComplexClassDescriptions("http://oaei.ontologymatching.org/2011/benchmarks2/101/onto.owl#Demo_Paper");
//
//		for (OWLComplexClassDescription description : sdescriptions) {
//			List<ClassExpression> contents = description.getClassExpressions();
//			System.out.println(" " + description.getClassRelationName());
//			for (ClassExpression content : contents) {
//				System.out.println("-> " + content);
//				List<OntResourceInfo> namedClassList = content.getNamedClassList();
//				for(OntResourceInfo namedClass : namedClassList){
//					System.out.println("----> " + namedClass.getLocalName());
//					System.out.println("----> " + namedClass.getURI());
//				}
//			}
//		}
//		System.out.println(" ");
//		for (OWLComplexClassDescription description : tdescriptions) {
//			List<ClassExpression> contents = description.getClassExpressions();
//			System.out.println(" " + description.getClassRelationName());
//			for (ClassExpression content : contents) {
//				System.out.println("-> " + content);
//				
//				List<OntResourceInfo> namedClassList = content.getNamedClassList();
//				for(OntResourceInfo namedClass : namedClassList){
//					System.out.println("----> " + namedClass.getLocalName());
//					System.out.println("----> " + namedClass.getURI());
//				}
//			}
//		}

//		OWLClassDescriptionSetMatcher descriptionSetMatcher = new OWLClassDescriptionSetMatcher(sResModel, tResModel);
//		double similarity = descriptionSetMatcher.getComplexClassDescriptionSetMappingSimilairty(sdescriptions, tdescriptions);
//		System.out.println("similarity: " + similarity);
	}

	public static void generalTEST() {
		//
		// String dir1 = projectDir + "/Ontologies/GoodRelation_v1.owl";
		// String dir2 = projectDir + "/Ontologies/MSDL-Fullv2.owl";
		// String dir3 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		// String dir4 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		// String dir5 = projectDir + "/Ontologies/mfg.owl";
		// String dir6 = projectDir + "/Ontologies/benchmarks1/205/onto.rdf";

		String dir = projectDir + "/Ontologies/academicCourseCatalogs/mini_cornell_washington/washington.owl";

		String dir11 = projectDir + "/Ontologies/ConferenceOntologies/cmt.owl";
		String dir12 = projectDir + "/Ontologies/ConferenceOntologies/ekaw.owl";

		String dir3 = projectDir + "/Ontologies/benchmarks2/201-4/onto.owl";
		String dir4 = projectDir + "/Ontologies/benchmarks2/101/onto.owl";

		System.err.println(dir3);
		InputStream instream1 = FileManager.get().open(dir3);
		InputStream instream2 = FileManager.get().open(dir4);
		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);

		String sResUri = "http://oaei.ontologymatching.org/2011/benchmarks2/201-4/onto.owl#CRBYEEZIGBPYOHNLXKCLJKQHCP";
		String tResUri = "http://oaei.ontologymatching.org/2011/benchmarks2/101/onto.owl#Conference_Session";

		HierarchyMatcher HMather = new HierarchyMatcher(sResModel, tResModel);
		double sim = HMather.getSimilairty(sResUri, tResUri);
		System.out.println("Hierarchical Similarity: " + sim);

		// List<OntResourceInfo> superclasses =
		// sResModel.listSuperClasses(sResUri, true);
		// for(OntResourceInfo superclass : superclasses){
		// System.out.println("super class: " + superclass.getLocalName());
		// }
		//
		//
		// List<OntResourceInfo> subclasses = sResModel.listSubClasses(sResUri,
		// true);
		// for(OntResourceInfo subclass : subclasses){
		// System.out.println("sub class: " + subclass.getLocalName());
		// }

		// try {

		// resModel.prepareResource("http://iit.demokritos.gr/~vspiliop/washington.owl#instance1004");
		// resModel.prepareResource("http://oaei.ontologymatching.org/2011/benchmarks2/201-4/onto.owl#CRBYEEZIGBPYOHNLXKCLJKQHCP");
		// } catch (ResourceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	// public static void CITest() {
	//
	// String dir1 = projectDir + "/Ontologies/mfg.owl";
	// InputStream instream = FileManager.get().open(dir1);
	// ResourceModel sResModel = new OntResourceModel(instream);
	//
	// try {
	// sResModel.prepareResource(defaultNameSpace + "Nickel");
	// ResourcesLookupEngine engine = new ResourcesLookupEngine();
	// List<String> tResList = engine.lookupResource("Nickel");
	// ArrayList<MatchResult> resultList = new ArrayList<MatchResult>();
	//
	// for (String tRes : tResList) {
	// ResourceModel tResModel = new OntResourceModel(tRes);
	// tResModel.prepareResource(tRes);
	//
	// StrategyFactory factory = new StrategyFactory();
	// MatchStrategy strategy = factory.createStrategy(sResModel, tResModel);
	// MatchResult result = strategy.getMatchResult();
	// resultList.add(result);
	//
	// // System.out.println(" matched categories score: " +
	// // result.getCategoryMatchScore());
	// // System.out.println(" matched category for source: " +
	// // result.getMatchedCategories()[0]);
	// // System.out.println(" matched category for target: " +
	// // result.getMatchedCategories()[1]);
	// //
	// // System.out.println("@@@@####@@@@");
	//
	// }
	//
	// Collections.sort(resultList);
	//
	// for (MatchResult result : resultList) {
	//
	// System.out.println(" matched resource uri for source: " +
	// result.getComparedResources()[0]);
	// System.out.println(" matched resource uri for target: " +
	// result.getComparedResources()[1]);
	// System.out.println(" matched categories score: " +
	// result.getCategoryMatchScore());
	// System.out.println(" matched category for source: " +
	// result.getMatchedCategories()[0]);
	// System.out.println(" matched category for target: " +
	// result.getMatchedCategories()[1]);
	//
	// System.out.println("@@@@####@@@@");
	//
	// }
	//
	// } catch (ResourceNotFoundException e) {
	// e.printStackTrace();
	// } catch (QueryConstructionException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// System.out.println("succeed!!");
	//
	// }

	public static void list_directory_test() {

		String dirPath = projectDir + "/Ontologies/benchmarks2_new/";
		String dir_s = projectDir + "/Ontologies/benchmarks2_new/101/onto.owl";

		// String dirPath = projectDir + "/Ontologies/benchmarks2/";
		// String dirPath = projectDir + "/Ontologies/benchmarks2_248266/";
		// String dir_s = projectDir + "/Ontologies/benchmarks2/101/onto.owl";
		File dir = new File(dirPath);
		// The list of files can also be retrieved as File objects
		File[] files = dir.listFiles();

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() && file.getName().startsWith("2");
			}
		};
		files = dir.listFiles(fileFilter);
		File sFile = new File(dir_s);

		try {

			Properties params = new BasicParameters();
			double sum_f_measure = 0.;
			double sum_precision = 0.;
			double sum_recall = 0.;
			double num_test = files.length;
			double num_zero_f_measure = 0;
			for (File file : files) {

				String t_path = file.getAbsolutePath() + "/onto.owl";
				String refalign_path = file.getAbsolutePath() + "/refalign.rdf";
				File tFile = new File(t_path);

				// System.out.println(sFile.toURI());
				// System.out.println(tFile.toURI());
				// System.out.println(new File(refalign_path).toURI());

				AlignmentProcess processor = new OntologyMatcher();
				processor.init(sFile.toURI(), tFile.toURI());
				processor.align((Alignment) null, params);

				// Trim at various thresholds
				// Evaluate them against the references
				// and choose the one with the best F-Measure
				AlignmentParser aparser = new AlignmentParser(0);
				// Changed by Angel for Windows
				// Alignment reference = aparser.parse( "file://"+(new File (
				// "../refalign.rdf" ) . getAbsolutePath()) );
				Alignment reference = aparser.parse(new File(refalign_path).toURI());

				double best = 0.;
				double precision = 0.;
				double recall = 0.;

				Alignment result = null;
				// result = (BasicAlignment)((BasicAlignment)processor).clone();
				Properties p = new BasicParameters();

				System.out.println();
				System.out.println(sFile.getAbsoluteFile() + "-" + tFile.getAbsoluteFile());
				for (int i = 8; i <= 20; i += 1) {
					processor.cut(((double) i) / 20);
					// This operation must be repeated because the modifications
					// in a1
					// are not taken into account otherwise
					Evaluator evaluator = new PRecEvaluator(reference, processor);
					// ExtPREvaluator evaluator2 = new ExtPREvaluator(reference,
					// processor);
					evaluator.eval(p);

					System.err.println("Threshold " + (((double) i) / 20) + " : " + ((PRecEvaluator) evaluator).getPrecision() + "   " + ((PRecEvaluator) evaluator).getRecall() + "   "
							+ ((PRecEvaluator) evaluator).getFmeasure() + " over " + processor.nbCells() + " cells");

					if (((PRecEvaluator) evaluator).getFmeasure() > best) {
						result = (BasicAlignment) ((BasicAlignment) processor).clone();
						best = ((PRecEvaluator) evaluator).getFmeasure();
						precision = ((PRecEvaluator) evaluator).getPrecision();
						recall = ((PRecEvaluator) evaluator).getRecall();
					}
				}

				if (best == 0.0) {
					num_zero_f_measure++;
				}
				sum_recall += recall;
				sum_precision += precision;
				sum_f_measure += best;
				System.err.println("for best result : " + precision + "   " + recall + "   " + best);
			}

			double ave_recall = sum_recall / num_test;
			double ave_precision = sum_precision / num_test;
			double ave_f_measure = sum_f_measure / num_test;
			double num_nonzero_f_measure = num_test - num_zero_f_measure;
			double nonzero_ave_recall = sum_recall / num_nonzero_f_measure;
			double nonzero_ave_precision = sum_precision / num_nonzero_f_measure;
			double nonzero_ave_f_measure = sum_f_measure / num_nonzero_f_measure;
			System.out.println("total number of test: " + num_test);
			System.out.println("number of zero-f-measure: " + num_zero_f_measure);
			System.out.println("number of nonzero-f-measure: " + num_nonzero_f_measure);
			System.out.println("sum of precision, recall, f-measure: " + sum_precision + "; " + sum_recall + "; " + sum_f_measure);
			System.out.println("average precision, recall, f-measure: " + ave_precision + "; " + ave_recall + "; " + ave_f_measure);
			System.out.println("average precision, recall, f-measure [non-zero f-measure]: " + nonzero_ave_precision + "; " + nonzero_ave_recall + "; " + nonzero_ave_f_measure);

		} catch (AlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readConference_test() {

		String[][] conferences = TestRopository.getConference();

		for (String[] conference : conferences) {

			String dir1 = projectDir + conference[0];
			String dir2 = projectDir + conference[1];
			String dir3 = projectDir + conference[2];

			File sFile = new File(dir1);
			File tFile = new File(dir2);
			File rFile = new File(dir3);

			System.out.println(dir1);
			System.out.println(dir2);
			System.out.println(dir3);

			System.out.println(" ");

		}
	}

	public static void conferece_test() {

		String[][] conferences = TestRopository.getConference();

		double precision_sum = 0.0;
		double recall_sum = 0.0;
		double F_half_sum = 0.0;
		double F1_sum = 0.0;
		double F2_sum = 0.0;
		double count = 0.0;

		for (String[] conference : conferences) {
			count++;
			String dir1 = projectDir + conference[0];
			String dir2 = projectDir + conference[1];
			String dir3 = projectDir + conference[2];

			File sFile = new File(dir1);
			File tFile = new File(dir2);
			File rFile = new File(dir3);

			// System.out.println(dir1);
			// System.out.println(dir2);
			// System.out.println(dir3);

			System.out.println(conference[0] + "-" + conference[1]);
			Properties params = new BasicParameters();

			try {

				AlignmentProcess processor = new OntologyMatcher();
				processor.init(sFile.toURI(), tFile.toURI());
				processor.align((Alignment) null, params);

				// Trim at various thresholds
				// Evaluate them against the references
				// and choose the one with the best F-Measure
				AlignmentParser aparser = new AlignmentParser(0);
				// Changed by Angel for Windows
				// Alignment reference = aparser.parse( "file://"+(new File (
				// "../refalign.rdf" ) . getAbsolutePath()) );
				Alignment reference = aparser.parse(rFile.toURI());

				double precison_best = 0.;
				double recall_best = 0.;
				double F_half_best = 0.;
				double F1_best = 0.;
				double F2_best = 0.;

				Alignment result = null;
				// result = (BasicAlignment)((BasicAlignment)processor).clone();

				Properties p = new BasicParameters();

				for (int i = 4; i <= 10; i += 1) {
					processor.cut(((double) i) / 10);
					// This operation must be repeated because the modifications
					// in a1
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

					System.err.println("Threshold " + (((double) i) / 10) + " : " + ((PRecEvaluator) evaluator).getPrecision() + ", " + ((PRecEvaluator) evaluator).getRecall() + "  over "
							+ processor.nbCells() + " cells");
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

				precision_sum += precison_best;
				recall_sum += recall_best;
				F_half_sum += F_half_best;
				F1_sum += F1_best;
				F2_sum += F2_best;

				System.err.println("------------------------------------------------------------------------------------------");
				System.err.println(" best:         " + " precision: " + precison_best + " recall: " + recall_best);
				System.err.println("               " + " F0.5: " + F_half_best + " F1: " + F1_best + " F2: " + F2_best);
				System.err.println("==========================================================================================");
				// Displays it as OWL Rules
				// PrintWriter writer = new PrintWriter(new BufferedWriter(
				// new OutputStreamWriter(System.out, "UTF-8")), true);
				//

				// AlignmentVisitor renderer = new RDFRendererVisitor(writer);
				// result.render(renderer);
				// writer.flush();
				// writer.close();

			} catch (AlignmentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(" ");
		}

		System.out.println("**********************************");
		System.err.println("sum:     [precision = " + precision_sum + "]" + "[recall=" + recall_sum + "]");
		System.err.println("         [F0.5 = " + F_half_sum + "], " + "[F1=" + F1_sum + "], " + "[F2=" + F2_sum + "]");

		System.err.println("total num of comparison: " + count);
		System.err.println("average: [precision = " + precision_sum / count + "]" + "[recall=" + recall_sum / count + "]");
		System.err.println("         [F0.5 = " + F_half_sum / count + "], " + "[F1=" + F1_sum / count + "], " + "[F2=" + F2_sum / count + "]");

	}

	public static void alignment_test() {

		String dir1 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		String dir2 = projectDir + "/Ontologies/benchmarks1/301/onto.rdf";
		String dir3 = projectDir + "/Ontologies/benchmarks1/301/refalign.rdf";
		
		
	    String dir21 = projectDir+ "/Ontologies/ConferenceOntologies/OpenConf.owl";
	    String dir31 = projectDir+ "/Ontologies/ConferenceOntologies/MyReview.owl";

		String dir11 = projectDir + "/Ontologies/ConferenceOntologies/ekaw.owl";
		String dir12 = projectDir + "/Ontologies/ConferenceOntologies/iasted.owl";
		String dir13 = projectDir + "/Ontologies/ConferenceOntologies/reference-alignment/ekaw-iasted.rdf";

		String dir14 = projectDir + "/Ontologies/benchmarkOntologies301_304/302303/303.rdf";
		String dir15 = projectDir + "/Ontologies/benchmarkOntologies301_304/302303/302.rdf";
		String dir16 = projectDir + "/Ontologies/benchmarkOntologies301_304/302303/refalign.rdf";

		String dir17 = projectDir + "/Ontologies/academicCourseCatalogs/mini_washington_washington/washington.owl";
		String dir18 = projectDir + "/Ontologies/academicCourseCatalogs/mini_washington_washington/mini_washington.owl";
		String dir19 = projectDir + "/Ontologies/academicCourseCatalogs/mini_washington_washington/refalign.rdf";

		File sFile = new File(dir21);
		File tFile = new File(dir31);
		System.out.println("source file path: " + sFile.toURI().toString());
		System.out.println("target file path: " + tFile.toURI().toString());

		Properties params = new BasicParameters();

		try {

			AlignmentProcess processor = new OntologyMatcher();
			processor.init(sFile.toURI(), tFile.toURI());
			processor.align((Alignment) null, params);

			// Trim at various thresholds
			// Evaluate them against the references
			// and choose the one with the best F-Measure
			AlignmentParser aparser = new AlignmentParser(0);
			// Changed by Angel for Windows
			// Alignment reference = aparser.parse( "file://"+(new File (
			// "../refalign.rdf" ) . getAbsolutePath()) );
			Alignment reference = aparser.parse(new File(dir13).toURI());

			double best = 0.;
			Alignment result = null;
			// result = (BasicAlignment)((BasicAlignment)processor).clone();

			Properties p = new BasicParameters();

			for (int i = 4; i <= 10; i += 1) {
				processor.cut(((double) i) / 10);
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

				System.err.println("Threshold " + (((double) i) / 10) + " : " + ((PRecEvaluator) evaluator).getPrecision() + "   " + ((PRecEvaluator) evaluator).getRecall() + "   "
						+ ((PRecEvaluator) evaluator).getFmeasure() + " over " + processor.nbCells() + " cells");
				System.err.println("            " + " F0.5: " + F_half + " F1: " + F1 + " F2: " + F2);
				if (((PRecEvaluator) evaluator).getFmeasure() > best) {
					result = (BasicAlignment) ((BasicAlignment) processor).clone();
					best = ((PRecEvaluator) evaluator).getFmeasure();
				}
			}

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

	public static void matcher_bridge_test() {

		EbiquityOntologyMatcherBridge bridge = new EbiquityOntologyMatcherBridge();
		String dir7 = projectDir + "/Ontologies/benchmarkOntologies301_304/301303/301.rdf";
		String dir8 = projectDir + "/Ontologies/benchmarkOntologies301_304/301303/303.rdf";

		String dir1 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		String dir2 = projectDir + "/Ontologies/benchmarks1/205/onto.rdf";
		String dir3 = projectDir + "/Ontologies/benchmarks1/205/refalign.rdf";

		String dir11 = projectDir + "/Ontologies/ConferenceOntologies/cmt.owl";
		String dir12 = projectDir + "/Ontologies/ConferenceOntologies/Conference.owl";

		try {

			File sFile = new File(dir11);
			File tFile = new File(dir12);

			System.out.println("source file path: " + sFile.toURI().toString());
			System.out.println("target file path: " + tFile.toURI().toString());

			URL url = bridge.align(sFile.toURI().toURL(), tFile.toURI().toURL());
			System.out.println(url.toString());

		} catch (ToolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ToolBridgeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void CCTest() {

		// String projectDir = System.getProperty("user.dir");
		// String dir = projectDir + "/Ontologies/mfg.owl";
		String dir2 = projectDir + "/Ontologies/GoodRelation_v1.owl";
		String dir1 = projectDir + "/Ontologies/MSDL-Fullv2.owl";
		// String dir1 = projectDir + "/Ontologies/GoodRelation_v1.owl";
		String dir3 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		String dir4 = projectDir + "/Ontologies/benchmarks1/101/onto.rdf";
		String dir5 = projectDir + "/Ontologies/mfg.owl";

		String dir7 = projectDir + "/Ontologies/benchmarkOntologies301_304/301302/301.rdf";
		String dir8 = projectDir + "/Ontologies/benchmarkOntologies301_304/301302/302.rdf";

		String dir9 = projectDir + "/Ontologies/ConferenceOntologies/confOf.owl";
		String dir10 = projectDir + "/Ontologies/ConferenceOntologies/sigkdd.owl";

		String dir11 = projectDir + "/Ontologies/ConferenceOntologies/cmt.owl";
		String dir12 = projectDir + "/Ontologies/ConferenceOntologies/sigkdd.owl";

		String dir13 = projectDir + "/Ontologies/benchmarks1/205/onto.rdf";

		String dir21 = projectDir + "/Ontologies/ConferenceOntologies/cmt.owl";
		String dir22 = projectDir + "/Ontologies/ConferenceOntologies/confOf.owl";
		String dir23 = projectDir + "/Ontologies/ConferenceOntologies/reference-alignment/cmt-ekaw.rdf";
		
		String dir32 = projectDir + "/Ontologies/benchmarks2_new/201/onto.owl";
		String dir31 = projectDir + "/Ontologies/benchmarks2_new/101/onto.owl";

		InputStream instream1 = FileManager.get().open(dir31);
		InputStream instream2 = FileManager.get().open(dir32);

		ResourceModel sResModel = new OntResourceModel(instream1);
		ResourceModel tResModel = new OntResourceModel(instream2);

		// try {
		// tResModel.prepareResource("http://oaei.ontologymatching.org/2011/benchmarks/205/onto.rdf#PageInterval");
		// } catch (ResourceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// List<OntPropertyInfo> properties = tResModel.listOntProperties();
		//
		// for (OntPropertyInfo property : properties) {
		//
		// System.out.println(property.getLocalName());
		// System.out.println(property.getPropertyType());
		//
		// if(property.getPropertyType() == OntPropertyType.DataTypeProperty){
		// System.out.println(property.getDatatype());
		// }
		//
		//
		// }

		// tResModel.listTopLevelClasses();
		// sResModel.listTopLevelClasses();

		// try {
		// tResModel.prepareResource("http://oaei.ontologymatching.org/2011/benchmarks/205/onto.rdf#Periodical");
		// } catch (ResourceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// OntPropertyInfo[] infos = tResModel.listOntPropertiesAsArray();
		//
		// for(OntPropertyInfo info : infos ){
		//
		// System.out.println(info.getURI());
		// }

		// OntClassSetSimilarity classSetSimilarity = new
		// OntClassSetSimilarity(sResModel, tResModel);
		// MSMResult result = classSetSimilarity.getMapping();

		OntologyMatchingAlgorithm cgm2 = new OntologyMatchingAlgorithm(sResModel, tResModel, true, PropertySetsMatchingStrategy.STRICT);
		// MSMResult result = cgm.getMapping();
		// cgm2.getAllClassMapping();
		// List<ClassMapping> mappings = cgm2.getClassMappings();
		List<ClassMapping> mappings = cgm2.getClassMappingsWithPrune();

		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "(" + mapping.getLabelSimilarity() + ")");
			System.out.println("properties similarity: " + mapping.getPropertySimilarity());
			System.out.println("mapping similarity: " + mapping.getSimilarity());
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			cgm2.prepareMatchingResources(sClassURI, tClassURI);
			// this.resetCurrentLevel();
			MSMResult propertySetMappingResult = cgm2.matchClassPair().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					// System.out.println("* PropertyMatched: [" + i + "]" +
					// subMapping.s.getLocalName() + " -> " +
					// subMapping.t.getLocalName() + " ("
					// + subMapping.getSimilarity() + ")");
					System.out.println("-> PropertyMatched: [" + i + "]" + subMapping.s.getRevisedName() + " -> " + subMapping.t.getRevisedName() + " (" + subMapping.getSimilarity() + ")");
				}
			}
		}

		// ComplexGraphMatching cgm = new ComplexGraphMatching(sResModel,
		// tResModel);
		// cgm.setDepth(0);
		// MSMResult result = cgm.getMapping();

		// LinkCreator linkCreator = new LinkCreator(sResModel, tResModel);
		//
		// System.out.println("============= ");
		// String link = linkCreator.createLink(defaultNameSpace2 + "Offering",
		// defaultNameSpace+"RFQ");
		// System.out.println("link: " + link);
		// System.out.println(linkCreator.getDetails());

		// StringBuilder sb = new StringBuilder();
		// ArrayList<SubMapping> mappingList = result.getSubMappings();
		// for (int i = 0; i < mappingList.size(); i++) {
		// SubMapping subMapping = mappingList.get(i);
		//
		// sb.append("--------------------- \n");
		// sb.append("[" + i + "]" + subMapping.s.getLocalName() + " -> " +
		// subMapping.t.getLocalName() + " (" + subMapping.similarity +
		// ")  \n");
		//
		// // sb.append(subMapping.s.getLocalName() + " " +
		// linkCreator.createLink(subMapping.s.getUri(), subMapping.t.getUri())
		// + " "
		// // + subMapping.t.getLocalName() + " \n");
		//
		// // sb.append(linkCreator.getDetails());
		// sb.append("--------------------- \n");
		//
		// }
		// System.out.println("============= ");
		// System.out.println(sb.toString());

		//
		// GraphMatching gm = new GraphMatching(sResModel, tResModel);
		// gm.prepareMatchingResources(defaultNameSpace3 + "Reference",
		// defaultNameSpace3 + "Reference");
		// gm.beginMatching(0);
		//
		// System.out.println(" ------- ");
		// System.out.println(gm.getMatchingInfo());

	}

	public static void IItest() {

		String dir = projectDir + "/Ontologies/academicCourseCatalogs/mini_cornell_washington/washington.owl";

		String dir2 = projectDir + "/Ontologies/ConferenceOntologies/OpenConf.owl";
		String dir3 = projectDir + "/Ontologies/ConferenceOntologies/MyReview.owl";

		String dir4 = projectDir + "/Instances/PR/restaurants/restaurant1.rdf";
		String dir5 = projectDir + "/Instances/PR/restaurants/restaurant2.rdf";

		String dir6 = projectDir + "/Instances/PR/person2/person21.rdf";
		String dir7 = projectDir + "/Instances/PR/person2/person22.rdf";

		String dir8 = projectDir + "/Instances/PR/person1/person11.rdf";

		String dir9 = projectDir + "/IIMB/000/onto.owl/";
		String dir10 = projectDir + "/IIMB/001/onto.owl/";

		// InputStream instream = FileManager.get().open(dir);
		// ResourceModel resModel = new OntResourceModel(instream);

		InputStream instream2 = FileManager.get().open(dir6);
		InputStream instream3 = FileManager.get().open(dir7);

		ResourceModel sResModel = new OntResourceModel(instream2);
		ResourceModel tResModel = new OntResourceModel(instream3);

		// List<OntResourceInfo> classes = sResModel.listAllClasses();
		// List<String> properties = sResModel.listAllProperties();
		// try {
		// sResModel.prepareResource("http://oaei.ontologymatching.org/2010/IIMBDATA/en/dhadkan");
		// String name = sResModel.getResourceLocalName_test();
		// List<String> classes =
		// sResModel.listClassesOfIndividual("http://oaei.ontologymatching.org/2010/IIMBDATA/en/dhadkan");
		// List<String> types =
		// sResModel.listTypesOfIndividual("http://oaei.ontologymatching.org/2010/IIMBDATA/en/dhadkan");
		// for(String cl: classes){
		// System.err.println("class: " + cl);
		// }
		//
		// for(String type: types){
		// System.err.println("type: " + type);
		// }
		// System.err.println("name: " + name);
		//
		//
		// } catch (ResourceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// List<OntResourceInfo> individuals =
		// sResModel.listAllIndividuals("Person");
		// for (OntResourceInfo cl : individuals) {
		// System.out.println("individuals: " + cl.getURI());
		// }

		// List<IndividualTypeInfo> typeInfolst =
		// sResModel.listIndividualTypeInfos();
		//
		// System.out.println("Start");
		// for (OntResourceInfo cl : classes) {
		// System.out.println("class: " + cl.getURI());
		// }
		//
		// // for (String s : properties) {
		// // System.out.println("property: " + s);
		// // }
		// System.out.println("End");
		//
		// System.out.println("==================");
		// for (IndividualTypeInfo typeInfo : typeInfolst) {
		// System.out.println(typeInfo.getKey());
		// List<OntPropertyInfo> propertylst = typeInfo.listProperties();
		// for (OntPropertyInfo property : propertylst) {
		// System.out.println("  - " + property.getLocalName());
		// }
		// }

		long startTime = System.currentTimeMillis();
		ArrayList<String> sDescriptionProperties = new ArrayList<String>();
		ArrayList<String> tDescriptionProperties = new ArrayList<String>();
		// sDescriptionProperties.add("http://www.okkam.org/ontology_restaurant1.owl#name");
		// tDescriptionProperties.add("http://www.okkam.org/ontology_restaurant2.owl#name");
		sDescriptionProperties.add("http://www.okkam.org/ontology_person1.owl#surname");
		sDescriptionProperties.add("http://www.okkam.org/ontology_person1.owl#given_name");
		tDescriptionProperties.add("http://www.okkam.org/ontology_person2.owl#surname");
		tDescriptionProperties.add("http://www.okkam.org/ontology_person2.owl#given_name");
		sResModel.setDescriptionProperties(sDescriptionProperties);
		tResModel.setDescriptionProperties(tDescriptionProperties);
		ComplexOntologyMatchingAlgorithm2 cgm2 = new ComplexOntologyMatchingAlgorithm2(sResModel, tResModel, false);
		cgm2.setMaxLevel(1);
		List<ClassMapping> mappings = cgm2.getAllIndividualMapping();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("running time: " + totalTime);

		int counter = 0;
		for (ClassMapping mapping : mappings) {
			counter++;
			System.out.println(" ");
			System.out.println("[" + counter + "]" + mapping.getSourceClassLocalName() + " : " + mapping.getTargetClassLocalName() + "(" + mapping.getLabelSimilarity() + ")");
			System.out.println("properties similarity: " + mapping.getPropertySimilarity());
			System.out.println("mapping similarity: " + mapping.getSimilarity());
			String sClassURI = mapping.getSourceClassURI();
			String tClassURI = mapping.getTargetClassURI();

			System.out.println("source URI: " + sClassURI);
			System.out.println("target URI: " + tClassURI);

			cgm2.prepareMatchingResources(sClassURI, tClassURI);
			// this.resetCurrentLevel();
			MSMResult propertySetMappingResult = cgm2.ClassMatching().getPropertySetMatchingResult();
			if (propertySetMappingResult != null) {
				ArrayList<SubMapping> mappingList = propertySetMappingResult.getSubMappings();
				for (int i = 0; i < mappingList.size(); i++) {
					SubMapping subMapping = mappingList.get(i);
					// System.out.println("* PropertyMatched: [" + i + "]" +
					// subMapping.s.getLocalName() + " -> " +
					// subMapping.t.getLocalName() + " ("
					// + subMapping.getSimilarity() + ")");
					System.out.println("-> PropertyMatched: [" + i + "]" + subMapping.s.getRevisedName() + " -> " + subMapping.t.getRevisedName() + " (" + subMapping.getSimilarity() + ")");
				}
			}
		}

		// cgm2.testGetAllIndividuals();

		// try {
		//
		// sResModel.prepareResource("http://openconf#Gui_Yi_Chuang");
		// //
		// resModel.prepareResource("http://oaei.ontologymatching.org/2011/benchmarks/205/onto.rdf#Book");
		// } catch (ResourceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// sResModel.listIndividualProperties();

		// resModel.listAllIndividuals();

	}

}
