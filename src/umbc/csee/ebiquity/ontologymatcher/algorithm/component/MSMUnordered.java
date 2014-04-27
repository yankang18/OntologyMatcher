package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import umbc.csee.ebiquity.ontologymatcher.algorithm.component.MSMResult.SubMapping;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode.MAP_CARDINALITY;
import umbc.csee.ebiquity.ontologymatcher.config.AlgorithmMode;





public class MSMUnordered {
	/**
	 * This is mapping cadinality 1 = 1-to-1 2 = 1-to-N 3 = N-to-1 4 = N-to-M
	 */
	MAP_CARDINALITY mode_mappingCadinality = AlgorithmMode.MAP_CARDINALITY.MODE_MtoN;

	Logger logger = Logger.getLogger("MSMUnordered");

	public MSMUnordered(MAP_CARDINALITY map_CARDINALITY) {
		this.mode_mappingCadinality = map_CARDINALITY;
	}
	
	public MSMUnordered() {
	}

	public MSMResult getMapping(ISimilarityMatrix sm) {
		return getMapping(sm, false, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeumbc.edu.jwkim.xsm.mm.IMSM#getSimilarity(umbc.edu.jwkim.xsm.mm.
	 * ISimilarityMatrix)
	 */
	public MSMResult getMapping(ISimilarityMatrix sm, boolean optResult,
			int numOfOpt) {
		logger.entering("MSMUnordered", "getMapping", new Object[] { optResult,
				numOfOpt });
		int rCount = sm.getRowCount();
		int cCount = sm.getColCount();
//		System.out.println("rCount = " + rCount + ", cCount = " + cCount);
		logger.fine("rCount = " + rCount + ", cCount = " + cCount);
		if (rCount == 0 || cCount == 0) {
			logger.severe("# of elements is 0.");
			return null;
		}
		logger.fine("(" + sm.getRowName(0) + " vs " + sm.getColName(0) + ")");

		HashMap<SimilarityMatrixCell, Double> sortedSM = new HashMap<SimilarityMatrixCell, Double>();

		double sumIC1 = 0;
		double sumIC2 = 0;

		for (int i = 0; i < rCount; i++) {
			double icValue1 = sm.getRow(i).getIC();
			sumIC1 += icValue1;
			for (int j = 0; j < cCount; j++) {
				SimilarityMatrixCell cell = sm.getCellAt(i, j);

				sortedSM.put(cell, cell.getSimilarity());
			}
		}
		for (int j = 0; j < cCount; j++) {
			double icValue2 = sm.getCol(j).getIC();
			sumIC2 += icValue2;
		}

		ArrayList<Map.Entry<SimilarityMatrixCell, Double>> myArrayList = new ArrayList<Map.Entry<SimilarityMatrixCell, Double>>(
				sortedSM.entrySet());
		// Sort the values based on values first and then keys.
		Collections.sort(myArrayList, new MyComparator());

		Iterator<Map.Entry<SimilarityMatrixCell, Double>> itr = myArrayList
				.iterator();

		double sim1 = 0;
		double sim2 = 0;

		ArrayList<SubMapping> subMapSimilarities = new ArrayList<SubMapping>();

		MSMResult result;
		if (optResult) {
			result = new MSMUnorderedResult();
		} else {
			result = new MSMResult();
		}

		ArrayList<SimilarityMatrixLabel> sList = new ArrayList<SimilarityMatrixLabel>();
		ArrayList<SimilarityMatrixLabel> tList = new ArrayList<SimilarityMatrixLabel>();
		while (itr.hasNext()) {
			Map.Entry<SimilarityMatrixCell, Double> e = itr.next();
			SimilarityMatrixCell cell = e.getKey();
			double value = cell.getSimilarity();

			boolean newMap = false;
			MAP_CARDINALITY m = mode_mappingCadinality;
			if (m == MAP_CARDINALITY.MODE_1to1) {
				double icValue1 = -1;
				double icValue2 = -1;
				if (sList.contains(cell.getRow()) == false) {
					icValue1 = cell.getRow().getIC();
				}
				if (tList.contains(cell.getCol()) == false) {
					icValue2 = cell.getCol().getIC();
				}
				if (icValue1 >= 0 && icValue2 >= 0) {
					newMap = true;
					sList.add(cell.getRow());
					tList.add(cell.getCol());

					sim1 += value * icValue1;
					sim2 += value * icValue2;
				}
				
			} else {
				// new mapping for source -> target
				if (m == MAP_CARDINALITY.MODE_Mto1 || m == MAP_CARDINALITY.MODE_MtoN) {
					if (sList.contains(cell.getRow()) == false) {
						sList.add(cell.getRow());
						newMap = true;

						double icValue1 = cell.getRow().getIC();
						sim1 += value * icValue1;
						// System.out.println("s2t: " + cell.getRow().getName()
						// + ", " + icValue1 + ", " + ratioEffect1 + ", " +
						// value + ", " + sim1);
					} else if (optResult) { // optional map for source -> target
						if (((MSMUnorderedResult) result).getS2TOptionMap(
								cell.getRow().getLocalName()).size() < numOfOpt) {
							((MSMUnorderedResult) result).addS2TOptionMap(cell
									.getRow().getLocalName(), copySubMapping(cell));
						}
					}
				}
				// new mapping for target -> source
				if (m == MAP_CARDINALITY.MODE_1toN || m == MAP_CARDINALITY.MODE_MtoN) {
					if (tList.contains(cell.getCol()) == false) {
						tList.add(cell.getCol());
						newMap = true;

						double icValue2 = cell.getCol().getIC();
						sim2 += value * icValue2;
						// System.out.println("t2s: " + cell.getCol().getName()
						// + ", " + icValue2 + ", " + ratioEffect2 + ", " +
						// value + ", " + sim2);
					}
					// else if (optResult) { // optional map for source ->
					// target
					// if (((MSMUnorderedResult) result).getT2SOptionMap(
					// cell.getRow().getName()).size() < numOfOpt) {
					// ((MSMUnorderedResult)
					// result).addT2SOptionMap(cell.getRow()
					// .getName(), copySubMapping(cell));
					// }
					// }
				}
			}

			if (newMap) {
				subMapSimilarities.add(copySubMapping(cell));
				// System.out.println(keys[0] + " <-> " + keys[1] + ", " +
				// value);
			}
		}

		double sumIC = 0;
		if (mode_mappingCadinality != MAP_CARDINALITY.MODE_1toN) {
			sumIC += sumIC1;
		}
		if (mode_mappingCadinality != MAP_CARDINALITY.MODE_Mto1) {
			sumIC += sumIC2;
		}

//		System.out.println("=============");
//		System.out.println("test: " + test +" : " + "final_combineIC: " + final_combineIC);
		
		double similarity = (sim1 + sim2) / sumIC;
	

		result.setSubMappings(subMapSimilarities);
		result.setSimilarity(similarity);

		logger.fine("(" + sm.getRowName(0) + " vs " + sm.getColName(0) + ") = "
				+ result.getSimilarity());
		logger.exiting("MSMUnordered", "getMapping");
		return result;
	}

	private SubMapping copySubMapping(SimilarityMatrixCell cell) {
		SubMapping subMapping = new SubMapping(cell.getRow(), cell.getCol(), cell.getSimilarity());
		subMapping.setSourceRangeURI(cell.getSourceRangeUri());
		subMapping.setTargetRangeURI(cell.getTargetRangeUri());
		subMapping.setSourcePropertyType(cell.getSourcePropertyType());
		subMapping.setTargetPropertyType(cell.getTargetPropertyType());
		subMapping.setSourceRangeSize(cell.getSourceRangeSize());
		subMapping.setTargetRangeSize(cell.getTargetRangeSize());
		subMapping.setSourcePropertyValue(cell.getSourceOntPropertyValue());
		subMapping.setTargetPropertyValue(cell.getTargetOntPropertyValue());
		subMapping.setPropertyValueSimilarity(cell.getPropertyValueSimilarity());
		return subMapping;
	}

	private class MyComparator implements Comparator {
		public int compare(Object obj1, Object obj2) {
			int result = 0;
			Map.Entry e1 = (Map.Entry) obj1;
			Map.Entry e2 = (Map.Entry) obj2;// Sort based on values.
			Double value1 = (Double) e1.getValue();
			Double value2 = (Double) e2.getValue();
			result = value2.compareTo(value1);
			return result;
		}
	}
}
