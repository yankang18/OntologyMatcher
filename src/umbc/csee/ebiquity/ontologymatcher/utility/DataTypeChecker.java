package umbc.csee.ebiquity.ontologymatcher.utility;

import java.util.HashMap;

public class DataTypeChecker {
	private static String dateformat = "[/\\-\\s,]+";
	private static HashMap<String, String> month_digits_map = new HashMap<String, String>();
	static {
		
		month_digits_map.put("january", "01");
		month_digits_map.put("february", "02");
		month_digits_map.put("march", "03");
		month_digits_map.put("april", "04");
		month_digits_map.put("may", "05");
		month_digits_map.put("june", "06");
		month_digits_map.put("july", "07");
		month_digits_map.put("august", "08");
		month_digits_map.put("september", "09");
		month_digits_map.put("october", "10");
		month_digits_map.put("november", "11");
		month_digits_map.put("december", "12");
		month_digits_map.put("jan.", "01");
		month_digits_map.put("feb.", "02");
		month_digits_map.put("mar.", "03");
		month_digits_map.put("apr.", "04");
		month_digits_map.put("may", "05");
		month_digits_map.put("jun.", "06");
		month_digits_map.put("jul.", "07");
		month_digits_map.put("aug.", "08");
		month_digits_map.put("sept.", "09");
		month_digits_map.put("oct.", "10");
		month_digits_map.put("nov.", "11");
		month_digits_map.put("dec.", "12");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Date date = parseDate("21/5/2009", "d/M/yyyy", false);

		String[] d = new String[10];
		d[0] = "2111-05,12";
		d[1] = "21-5-2009";
		d[2] = "21 05  2009";
		d[3] = "21, 05,2009";
		d[4] = "21,05,2009";
		d[5] = "1,05,2009";
		d[6] = "11,05,09";
		d[7] = "May 14, 1944";
		d[8] = "1944-05-14";
		d[9] = "21,- 05,2009";

		for (String dd : d) {
			DateCheckResult r = getDate(dd);
			if (r.isDate()) {
				System.out.println(dd + " is a date" + " [" + r.getResult() + "]");

			}
		}
	}

//	public static Date parseDate(String maybeDate, String format, boolean lenient) {
//	    Date date = null;
//
//	    // test date string matches format structure using regex
//	    // - weed out illegal characters and enforce 4-digit year
//	    // - create the regex based on the local format string
//	    String reFormat = Pattern.compile("d+|M+").matcher(Matcher.quoteReplacement(format)).replaceAll("\\\\d{1,2}");
//	    System.out.println(reFormat);
//	    reFormat = Pattern.compile("y+").matcher(reFormat).replaceAll("\\\\d{4}");
//	    System.out.println(reFormat);
//	    if ( Pattern.compile(reFormat).matcher(maybeDate).matches() ) {
//
//	      // date string matches format structure, 
//	      // - now test it can be converted to a valid date
//	      SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();
//	      sdf.applyPattern(format);
//	      sdf.setLenient(lenient);
//	      try { date = sdf.parse(maybeDate); } catch (ParseException e) { }
//	    } 
//	    return date;
//	  } 
	
	public static NumericCheckResult getNumeric(String str) {

		String newStr = str.replace(" ", "").replace("-", "").replace("_", "").replace("/", "");

		char[] chars = newStr.toCharArray();
		for (char c : chars) {
			if (!Character.isDigit(c)) {
				return new NumericCheckResult(false, "");
			}
		}
		return new NumericCheckResult(true, newStr);
	}
	
	public static boolean isNumeric(String str) {

		String newStr = str.replace(" ", "").replace("-", "").replace("_", "").replace("/", "");

		char[] chars = newStr.toCharArray();
		for (char c : chars) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public static DateCheckResult getDate(String str) {

		String[] items = str.split(dateformat);
		if (items.length != 3) {
			return new DateCheckResult(false, "");
		}

		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		boolean isMonth = month_digits_map.containsKey(items[0].toLowerCase().trim());
		sb1.append(items[1]).append(items[2]);
		sb2.append(items[0]).append(items[1]).append(items[2]);
		
		boolean isSb1Numeric = isNumeric(sb1.toString());
		boolean isSb2Numeric = isNumeric(sb2.toString());
		if ((!isMonth || !isSb1Numeric) && !isSb2Numeric) {
			return new DateCheckResult(false, "");
		}

		if (isMonth) {

			String month = month_digits_map.get(items[0].toLowerCase().trim());
			if ((month.length() == 2 && items[1].length() == 2 && items[2].length() == 4)) {
				return new DateCheckResult(true, month + items[1] + items[2]);
			}
		}

		boolean isformat1 = (items[0].length() == 4 && items[1].length() == 2 && items[2].length() == 2);
		boolean isformat2 = (items[0].length() == 2 && items[1].length() == 2 && items[2].length() == 4);
		if (isformat1 || isformat2) {
			if (isformat1) {
				return new DateCheckResult(true, items[1] + items[2] + items[0]);
			} else {
				return new DateCheckResult(true, items[0] + items[1] + items[2]);
			}
		}

		return new DateCheckResult(false, "");
	}
	
	public static double compareDigits(String str1, String str2){
		
		char[] chars1 = str1.toCharArray();
		char[] chars2 = str2.toCharArray();

		if (chars1.length != chars2.length) {
			return 0.0;
		} else {

			for (int i = 0; i < chars1.length; i++) {
				if (chars1[i] != chars2[i]) {
					return 0.0;
				}
			}
			return 1.0;
		}
	}

}
