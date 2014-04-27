package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import umbc.csee.ebiquity.ontologymatcher.config.Configuration;
import umbc.csee.ebiquity.ontologymatcher.textprocessing.*;


//TODO: JAEWOOK - This is temporary. Do something for dynamic data import
public class AbbrConverter {
	static boolean mode_useAbbrConverter = true;

	static Logger logger = Logger.getLogger(AbbrConverter.class.getName());

	private static HashMap<String, String> abbrList = new HashMap<String, String>();
	// This is just for faster access of string list of abbr full desc.
	// ex. "Purchase Order" -> {purchase, order}
	private static HashMap<String, List<String>> abbrList2 = new HashMap<String, List<String>>();

	static {
		logger.entering("AbbrConverter", "AbbrConverter");
		try {
			String abbrDefMap = Configuration.getAbbrDefFilePath();

//			InputStream in = null;
			try {
				logger.finer(abbrDefMap + " File Openning");
//				in = FileUtils.openInputStream(new File(abbrDefMap));
				logger.finer(abbrDefMap + " File Openning Done.");

				try {
//					List<String> abbrItem = IOUtils.readLines(in);
					List<String> abbrItem = new ArrayList<String>();
					Iterator<String> i = abbrItem.iterator();
					while (i.hasNext()) {
						String line = (String) i.next();
						logger.finest(line);
						int splitter = line.indexOf('=');
						if (splitter > 0) {
							String abbr = line.substring(0, splitter);
							String full = line.substring(splitter + 1);
							logger.finest(abbr + " = " + full);
							abbrList.put(abbr.toLowerCase(), full.toLowerCase());
//							ArrayList<String> wordList = new ArrayList<String>();
							String[] wordList = full.toLowerCase().split(" ");
							abbrList2.put(abbr.toLowerCase(), Arrays.asList(wordList));
						}
					}
				} finally {
//					IOUtils.closeQuietly(in);
				}
			} finally {
			}

		} finally {
			logger.exiting("AbbrConverter", "AbbrConverter");
		}
	}

	public static boolean contains(String abbr) {
		abbr = abbr.toLowerCase();
		return abbrList.containsKey(abbr);
	}
	
	public static List<String> convertToListNoChecking(String abbr) {
		return abbrList2.get(abbr);
	}

	public static String convertNoChecking(String abbr) {
		return abbrList.get(abbr);
	}

	public static String convert(String abbr) {
		// if (abbr.startsWith("#")) {
		// abbr = abbr.substring(1);
		// }
		abbr = abbr.toLowerCase();
		if (abbrList.containsKey(abbr)) {
			String full = abbrList.get(abbr);
			return full;
		}
		return abbr;
	}
	
	public static List<String> convertLabel2FullWordList(String label) {
		String[] textTokenizer = TextProcessingUtils.tokenizeLabel(label);
		
		if (mode_useAbbrConverter == false) {
			return Arrays.asList(textTokenizer);
		}
		List<String> wordList = new ArrayList<String>();
		for (String token : textTokenizer) {
			if (AbbrConverter.contains(token)) {
				List<String> abbrFull = AbbrConverter.convertToListNoChecking(token);
				wordList.addAll(abbrFull);
			} else {
				wordList.add(token);
			}
		}
		return wordList;
	}

}

