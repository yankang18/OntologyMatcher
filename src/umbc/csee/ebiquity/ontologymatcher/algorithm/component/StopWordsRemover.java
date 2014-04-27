package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import umbc.csee.ebiquity.ontologymatcher.textprocessing.TextProcessingUtils;

public class StopWordsRemover {

	private static Set<String> wordsToBeFiltered = new HashSet<String>();

	static {
		wordsToBeFiltered.add("has");
		wordsToBeFiltered.add("the");
		wordsToBeFiltered.add("an");
		wordsToBeFiltered.add("a");
		wordsToBeFiltered.add("one");
		wordsToBeFiltered.add("been");
		wordsToBeFiltered.add("is");
		wordsToBeFiltered.add("in");
		wordsToBeFiltered.add("for");
		wordsToBeFiltered.add("with");
		wordsToBeFiltered.add("and");
		wordsToBeFiltered.add("or");
		wordsToBeFiltered.add("are");
	}

	public static List<String> removeStopWords(List<String> wordset) {

		List<String> reservedWords = new ArrayList<String>();
		for (String word : wordset) {
			if (!wordsToBeFiltered.contains(word.toLowerCase().trim())) {
				reservedWords.add(word);
			}
		}

		return reservedWords;
	}
	
	public static List<String> removeStopWords(String[] wordset){
		return removeStopWords(Arrays.asList(wordset));
	}

	public static String removeStopWords(String description){
		StringBuilder strBuilder = new StringBuilder();
		List<String> words = removeStopWords(TextProcessingUtils.tokenizeLabel(description));
		for(String word : words){
			strBuilder.append(word + " ");
		}
		return strBuilder.toString().trim();
	}
}
