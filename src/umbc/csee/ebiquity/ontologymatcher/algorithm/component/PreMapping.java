package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import umbc.csee.ebiquity.ontologymatcher.config.Configuration;


//TODO: JAEWOOK - This is temporary. Do something for dynamic data import
public class PreMapping {

	static Logger logger = Logger.getLogger(PreMapping.class.getName());

	private static HashMap<String, Double> preMapping = new HashMap<String, Double>();

	static {
		logger.entering("PredefinedMapping", "PredefinedMapping");
		System.out.println("-------------------");
		System.out.println("PreMapping loading.");
		System.out.println("-------------------");
		try {
			String preMap = Configuration.getManualMappingFilePath();

//			InputStream in = null;
			try {
				logger.finer(preMap + " File Openning");
//				in = FileUtils.openInputStream(new File(preMap));
				logger.finer(preMap + " File Openning Done.");

				try {
//					List<String> wordDescList = IOUtils.readLines(in);
					List<String> wordDescList = new ArrayList<String>();
					Iterator<String> i = wordDescList.iterator();
					while (i.hasNext()) {
						String line = (String) i.next();
						logger.finest(line);
						int splitter = line.indexOf('=');
						if (splitter > 0) {
							String pair = line.substring(0, splitter);
							String similarityString = line
									.substring(splitter + 1);
							System.out.println(pair + " = " + similarityString);
							double similarity = Double
									.valueOf(similarityString);
							preMapping.put(pair.toLowerCase(), similarity);
						}
					}
				} finally {
//					IOUtils.closeQuietly(in);
				}
			} finally {
			}

		} finally {
			logger.exiting("PredefinedMapping", "PredefinedMapping");
		}
	}

	public static double getSimilarity(String concept1, String concept2) {
		if (concept1.startsWith("#")) {
			concept1 = concept1.substring(1);
		}
		if (concept2.startsWith("#")) {
			concept2 = concept2.substring(1);
		}
		String key1 = concept1 + ":" + concept2;
		key1 = key1.toLowerCase();
		String key2 = concept2 + ":" + concept1;
		key2 = key2.toLowerCase();
		if (preMapping.containsKey(key1)) {
			double similarity = preMapping.get(key1);
			return similarity;
		}
		if (preMapping.containsKey(key2)) {
			double similarity = preMapping.get(key2);
			return similarity;
		}

		return -1;
	}

}
