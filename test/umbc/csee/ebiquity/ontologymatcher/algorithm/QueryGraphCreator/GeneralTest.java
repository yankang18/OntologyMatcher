package umbc.csee.ebiquity.ontologymatcher.algorithm.QueryGraphCreator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntClassInfo;
import umbc.csee.ebiquity.ontologymatcher.algorithm.component.OntPropertyInfo;
import umbc.csee.ebiquity.ontologymatcher.model.OntResourceModel;
import umbc.csee.ebiquity.ontologymatcher.model.exception.ResourceNotFoundException;

import com.hp.hpl.jena.util.FileManager;

public class GeneralTest {
	private static String projectDir = System.getProperty("user.dir");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String dir = projectDir + "/MOntologies/thomasnet.owl";
//		String dir2 = projectDir + "/MOntologies//globalspec_modified.owl";
		String dir2 = projectDir + "/MOntologies/MSDL-Fullv2.owl";
		String dir3 = projectDir + "/MOntologies/globalspec_modified.owl";
		InputStream instream = FileManager.get().open(dir2);
//		InputStream instream2 = FileManager.get().open(dir2);
		OntResourceModel ontResModel = new OntResourceModel(instream);
		System.out.println("all named class: " + ontResModel.listAllNamedClasses().size());
		System.out.println("all property: " + ontResModel.listAllOntProperties().size());
		
//		ontResModel.preprocessing();
//		HashMap<OntPropertyInfo, HashSet<OntClassInfo>> property_domain_map = ontResModel.Property_LocalDomains_Map;
//		HashMap<OntPropertyInfo, HashSet<OntClassInfo>> property_ranges_map = ontResModel.Property_LocalRanges_Map;
//		HashMap<OntClassInfo, HashSet<OntPropertyInfo>> classAsDomain_properties = ontResModel.classAsDomain_properties_map;
//		HashMap<OntClassInfo, HashSet<OntPropertyInfo>> classAsRange_properties = ontResModel.classAsRange_properties_map;
//		HashMap<OntClassInfo, HashSet<OntClassInfo>> Class_DirectSubClasses_Map = ontResModel.Class_DirectSubClasses_Map;
//		List<OntPropertyInfo> ontPropertyList = ontResModel.listDomainOntClassProperties("http://www.nist.gov/simca/msnm/sample/glbalspec.owl#Supplier");
//		for (OntPropertyInfo ontPropertyInfo : ontPropertyList) {
//			System.out.println("property: " + ontPropertyInfo.getURI());
//			List<OntClassInfo> ontClassList = ontResModel.listImposedDomainOntClasses(ontPropertyInfo);
//			for (OntClassInfo ontClass : ontClassList) {
//				System.out.println("class: " + ontClass.getURI());
//			}
//		}
//		System.out.println("  ");
//		System.out.println("  ");
//		Iterator<Entry<OntPropertyInfo, HashSet<OntClassInfo>>> iterator1 = property_domain_map.entrySet().iterator();
//		while (iterator1.hasNext()) {
//			Entry<OntPropertyInfo, HashSet<OntClassInfo>> entry = iterator1.next();
//			System.out.println("property: " + entry.getKey().getURI());
//			HashSet<OntClassInfo> set = entry.getValue();
//			Iterator<OntClassInfo> classIterator = set.iterator();
//			while (classIterator.hasNext()) {
//				OntClassInfo ontClassInfo = classIterator.next();
//				System.out.println("   domain: " + ontClassInfo.getURI());
//			}
//		}
//		
//		System.out.println("  ");
//		System.out.println("  ");
//		Iterator<Entry<OntPropertyInfo, HashSet<OntClassInfo>>> iterator2 = property_ranges_map.entrySet().iterator();
//		while (iterator2.hasNext()) {
//			Entry<OntPropertyInfo, HashSet<OntClassInfo>> entry = iterator2.next();
//			System.out.println("property: " + entry.getKey().getURI());
//			HashSet<OntClassInfo> set = entry.getValue();
//			Iterator<OntClassInfo> classIterator = set.iterator();
//			while (classIterator.hasNext()) {
//				OntClassInfo ontClassInfo = classIterator.next();
//				System.out.println("   range: " + ontClassInfo.getURI());
//			}
//		}
//
//		System.out.println("  ");
//		System.out.println("  ");
//		Iterator<Entry<OntClassInfo, HashSet<OntPropertyInfo>>> iterator3 = classAsDomain_properties.entrySet().iterator();
//		while (iterator3.hasNext()) {
//			Entry<OntClassInfo, HashSet<OntPropertyInfo>> entry = iterator3.next();
//			System.out.println("Class As Domain: " + entry.getKey().getURI());
//			HashSet<OntPropertyInfo> set = entry.getValue();
//			Iterator<OntPropertyInfo> propertyIterator = set.iterator();
//			while (propertyIterator.hasNext()) {
//				OntPropertyInfo ontPropertyInfo = propertyIterator.next();
//				System.out.println("   Property: " + ontPropertyInfo.getURI());
//			}
//		}
//		
//		System.out.println("  ");
//		System.out.println("  ");
//		Iterator<Entry<OntClassInfo, HashSet<OntPropertyInfo>>> iterator4 = classAsRange_properties.entrySet().iterator();
//		while (iterator4.hasNext()) {
//			Entry<OntClassInfo, HashSet<OntPropertyInfo>> entry = iterator4.next();
//			System.out.println("Class As Range: " + entry.getKey().getURI());
//			HashSet<OntPropertyInfo> set = entry.getValue();
//			Iterator<OntPropertyInfo> propertyIterator = set.iterator();
//			while (propertyIterator.hasNext()) {
//				OntPropertyInfo ontPropertyInfo = propertyIterator.next();
//				System.out.println("   Property: " + ontPropertyInfo.getURI());
//			}
//		}
//		
//		System.out.println("  ");
//		System.out.println("  ");
//		Iterator<Entry<OntClassInfo, HashSet<OntClassInfo>>> iterator5 = Class_DirectSubClasses_Map.entrySet().iterator();
//		while (iterator5.hasNext()) {
//			Entry<OntClassInfo, HashSet<OntClassInfo>> entry = iterator5.next();
//			System.out.println("Class: " + entry.getKey().getURI());
//			HashSet<OntClassInfo> set = entry.getValue();
//			Iterator<OntClassInfo> subclassIterator = set.iterator();
//			while (subclassIterator.hasNext()) {
//				OntClassInfo ontSubClassInfo = subclassIterator.next();
//				System.out.println("   Sub-class: " + ontSubClassInfo.getURI());
//			}
//		}
		
//		String URI = "http://www.nist.gov/simca/msnm/sample/thomasnet.owl#hasToleranceUnit";
//		String URI = "http://www.nist.gov/simca/msnm/sample/thomasnet.owl#hasCapabilityUnitCode";
//		List<OntClassInfo> classInfoList = ontResModel.listDomains(URI);
//		List<OntPropertyInfo> superPropertyInfoList = ontResModel.listSuperPropertiesWithSimpleInfo(URI);
//		List<OntPropertyInfo> subPropertyInfoList = ontResModel.listSubPropertiesWithSimpleInfo(URI);
		
//		try {
//			ontResModel.prepareResource("http://www.owl-ontologies.com/Ontology1236208666.owl#MfgService");
//		} catch (ResourceNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<OntPropertyInfo> properties = ontResModel.listOntProperties();
			
//		System.out.println("domains: ");
//		for (OntClassInfo classInfo : classInfoList) {
//			System.out.println(classInfo.getURI());
//		}
//
//		System.out.println("super properties: ");
//		for (OntPropertyInfo propertyInfo : superPropertyInfoList) {
//			System.out.println(propertyInfo.getURI());
//		}

//		System.out.println("sub properties: ");
//		for (OntPropertyInfo propertyInfo : properties) {
//			System.out.println(propertyInfo.getURI());
//		}

	}

}
