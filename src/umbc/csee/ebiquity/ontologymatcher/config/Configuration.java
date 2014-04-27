package umbc.csee.ebiquity.ontologymatcher.config;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {
	private static Properties configFile = new Properties();
	private static String projectDir = System.getProperty("user.dir");

	public static void main(String[] args) {

		String s = projectDir + "/conf/configuratioin/config.properties";
		System.out.println(s);
	}
	
	static {
		System.out.println("Loading config.properties ....");
		String path = projectDir + "/conf/configuration/config.properties";
		System.out.println("path: " + path);
		System.out.println("Successfully loaded config.properties");
		
		try {
			configFile.load(new FileInputStream(path));
		} catch (Exception e) {
			System.err.println("Could not access " + path + " !!!");
		}
	}

	public static String getProperty(String key) {
		String value = configFile.getProperty(key);
			System.out.println(key + "=" + value);
		if (value != null && value.startsWith("./")) {
			value = projectDir + value.substring(1);
		}
		return value;
	}

	public static boolean getBooleanProperty(String key) {
		String v = getProperty(key);
		if (v == null) {
			return false;
		}
		if (v.equalsIgnoreCase("TRUE")) {
			return true;
		}
		return false;
	}

	public static String getManualMappingFilePath() {
		return getProperty("MANUAL_MAPPING_PATH");
	}

	public static String getAbbrDefFilePath() {
		return getProperty("ABBREVIATION_DEF_PATH");
	}

	public static String getWnCachePath() {
		return getProperty("WORDNET_CACHE_PATH");
	}

	public static boolean canUpdateWnCache() {
		return getBooleanProperty("WORDNET_CACHE_UPDATE");
	}

	public static String getWnConfigPath() {
		return getProperty("WORDNET_CONFIG_XML_PATH");
	}

	public static String getWnConfigHomePath() {
		return getProperty("WORDNET_CONFIG_HOME_PATH");
	}

	public static String getSchemaMappingCardinality() {
		return getProperty("SCHEMA_MAPPING_CARDINALITY");
	}

	public static String getPathMappingCardinality() {
		return getProperty("PATH_MAPPING_CARDINALITY");
	}

	public static String getLabelMappingCardinality() {
		return getProperty("LABEL_MAPPING_CARDINALITY");
	}

	public static String getWordMappingCardinality() {
		return getProperty("WORD_MAPPING_CARDINALITY");
	}

	public static boolean canSkipLabelMapping() {
		return getBooleanProperty("SKIP_LABEL_MAPPING");
	}

	public static boolean canRemoveDuplicatedWordsInPath() {
		return getBooleanProperty("REMOVE_DUPLICATED_WORDS_INPATH");
	}
}
