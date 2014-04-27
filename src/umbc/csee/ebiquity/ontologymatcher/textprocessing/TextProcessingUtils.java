package umbc.csee.ebiquity.ontologymatcher.textprocessing;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class TextProcessingUtils {
	static boolean mode_removeEngStopWords = false;
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(TextProcessingUtils.class.getName());

	private static HashMap<String, String[]> cachedTextProcessing = new HashMap<String, String[]>();

	public static String[] tokenizeLabel(String label) {
		try {
			logger.entering("TextProcessingUtils", "StringTokenizer", label);
			/**
			 * Tokenizer cannot be cached because it is non-recurring variable.
			 */
			if (cachedTextProcessing.containsKey(label)) {
				logger.finest("Cache Hit!");
				String[] textTokenizer = cachedTextProcessing.get(label);
				logger.finer("# of tokens = " + textTokenizer.length);
				return textTokenizer;
			}
			String resultStr = extractWordString(label);
			logger.finer("extracted word string = " + resultStr);
			StringTokenizer textTokenizer = new StringTokenizer(resultStr);
			logger.finer("# of tokens = " + textTokenizer.countTokens());

			String[] tokens = new String[textTokenizer.countTokens()];
			for (int i = 0; textTokenizer.hasMoreTokens(); i++) {
				String token = textTokenizer.nextToken();
				tokens[i] = token;
			}

			cachedTextProcessing.put(label, tokens);
			return tokens;
		} finally {
			logger.exiting("TextProcessingUtils", "StringTokenizer");
		}
	}

	public static String extractWordString(String label) {
		StringBuffer result = new StringBuffer();
		int lastSpace = 0;

		Character c2 = null;
		for (int i = 0; i < label.length(); i++) {
			Character c = label.charAt(i);

			/**
			 * If the previous inserted character is whitespace, now consider
			 * the current character as a first letter of word if it's letter.
			 * For example, Vehicle ID... Vehicle _ID, Vehicle-ID
			 */
			if (Character.isLetter(c)/* || Character.isDigit(c) */) {
				if (c2 == null || Character.isWhitespace(c2)) {
					c2 = Character.toLowerCase(c);
					result.append(c2);
					continue;
				}
				try {
					/**
					 * if the current charter is a Upper Case, it might be the
					 * first letter of new word.
					 */
					if (Character.isUpperCase(c)) {
						/**
						 * if either before or next letter is a lower case such
						 * as Vehicle(D)ata, Vehicle(I)D, or UBL(D)ata, it might
						 * be the first letter of new word.
						 */
						if ((Character.isLowerCase(label.charAt(i - 1)) && Character
								.isLowerCase(label.charAt(i + 1)))) {
							/**
							 * For example, SchemaXPath, we consider 'X' is just
							 * prefix but not a word.
							 */
							if (i - lastSpace > 1) {
								/**
								 * Now put a whitespace before copying the
								 * current letter.
								 */
								result.append(' ');
								lastSpace = i;
							}
						} else if ((Character.isLowerCase(label.charAt(i - 1)) && Character
								.isUpperCase(label.charAt(i + 1)))) {
							// SchemaXPath
							if ((i + 2) < label.length()) {
								if (Character.isLowerCase(label.charAt(i + 2))) {
									// For SchemaXPath, append the current 'X'
									// and move to 'P' which will be copied at
									// finally code then go to 'a'
									result.append(' ');
									lastSpace = i;
									c2 = Character.toLowerCase(c);
									result.append(c2);
									c = label.charAt(++i);
								} else {
									result.append(' ');
									lastSpace = i;
								}
							} else {
								result.append(' ');
								lastSpace = i;
							}

						} else if ((Character.isUpperCase(label.charAt(i - 1)) && Character
								.isLowerCase(label.charAt(i + 1)))) {
							// e.g., POBill
							if (i - lastSpace > 1) {
								/**
								 * Now put a whitespace before copying the
								 * current letter.
								 */
								result.append(' ');
								lastSpace = i;
							}
						}
					}
				} catch (Exception e) {
				} finally {
					// In any case, the letter should be added.
					c2 = Character.toLowerCase(c);
					result.append(c2);
				}
			} else {
				/**
				 * if cur char is not a starting char and non-letter (e.g., -_,"
				 * so on) but non-whitespace, consider as whitespace. Meaning
				 * that any characters other than letter, number, or whitespace
				 * will be ignored.
				 */
				if (c2 != null && Character.isWhitespace(c2) == false) {
					if (i - lastSpace > 1) {
						c2 = ' ';
						result.append(c2);
						lastSpace = i;
					}
				}
			}

		}

		String resultStr = result.toString().trim();
		return resultStr;
	}

	public static String removeStopwords(String t) {

		t = t.replaceAll("[();\"'.,]", "");

		// extracted from http://www.ranks.nl/tools/stopwords.html
		if (mode_removeEngStopWords) {
			t = t.replaceAll(" a ", " ").replaceAll(" the ", " ").replaceAll(
					" and ", " ").replaceAll(" or ", " ").replaceAll(" in ",
					" ").replaceAll(" of ", " ").replaceAll(" to ", " ")
					.replaceAll(" that ", " ").replaceAll(" it ", " ")
					.replaceAll(" i ", " ").replaceAll(" you ", " ")
					.replaceAll(" about ", " ").replaceAll(" an ", " ")
					.replaceAll(" are ", " ").replaceAll(" as ", " ")
					.replaceAll(" at ", " ").replaceAll(" be ", " ")
					.replaceAll(" by ", " ").replaceAll(" for ", " ")
					.replaceAll(" from ", " ").replaceAll(" how ", " ")
					.replaceAll(" is ", " ").replaceAll(" on ", " ")
					.replaceAll(" this ", " ").replaceAll(" was ", " ")
					.replaceAll(" what ", " ").replaceAll(" when ", " ")
					.replaceAll(" where ", " ").replaceAll(" who ", " ")
					.replaceAll(" will ", " ").replaceAll(" into ", " ")
					.replaceAll(" which ", " ").replaceAll(" with ", " ");
		}
		return t;
	}
}