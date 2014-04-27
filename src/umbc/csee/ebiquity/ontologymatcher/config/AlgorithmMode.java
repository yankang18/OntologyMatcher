package umbc.csee.ebiquity.ontologymatcher.config;

public class AlgorithmMode {
	public static enum MAP_CARDINALITY {
		MODE_1to1, MODE_1toN, MODE_Mto1, MODE_MtoN
	};

	private static MAP_CARDINALITY schemaMapCadinality = convertMapCardinality(Configuration
			.getSchemaMappingCardinality());
	private static MAP_CARDINALITY pathMapCadinality = convertMapCardinality(Configuration
			.getPathMappingCardinality());
	private static MAP_CARDINALITY labelMapCadinality = convertMapCardinality(Configuration
			.getLabelMappingCardinality());

	private static MAP_CARDINALITY convertMapCardinality(String c) {
		if (c.equalsIgnoreCase("MODE_1to1")) {
			return MAP_CARDINALITY.MODE_1to1;
		} else if (c.equalsIgnoreCase("MODE_1toN")) {
			return MAP_CARDINALITY.MODE_1toN;
		} else if (c.equalsIgnoreCase("MODE_Mto1")) {
			return MAP_CARDINALITY.MODE_Mto1;
		} else if (c.equalsIgnoreCase("MODE_MtoN")) {
			return MAP_CARDINALITY.MODE_MtoN;
		}
		return MAP_CARDINALITY.MODE_1to1;
	}

	public static MAP_CARDINALITY getSchemaMapCardinality() {
		return schemaMapCadinality;
	}

	public static void setSchemaMapCardinality(MAP_CARDINALITY m) {
		schemaMapCadinality = m;
	}

	public static MAP_CARDINALITY getPathMapCardinality() {
		return pathMapCadinality;
	}

	public static void setPathMapCardinality(MAP_CARDINALITY m) {
		pathMapCadinality = m;
	}

	public static MAP_CARDINALITY getLabelMapCardinality() {
		return labelMapCadinality;
	}

	public static void setLabelMapCardinality(MAP_CARDINALITY m) {
		labelMapCadinality = m;
	}
	
	public static boolean canSkipLabelMapping() {
		return Configuration.canSkipLabelMapping();
	}

	public static boolean canRemoveDuplicatedWordsInPath() {
		return Configuration.canRemoveDuplicatedWordsInPath();
	}

}
