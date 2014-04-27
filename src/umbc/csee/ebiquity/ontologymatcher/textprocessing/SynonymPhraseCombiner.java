package umbc.csee.ebiquity.ontologymatcher.textprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SynonymPhraseCombiner {

	public static List<String> getSynonymPhraseList(String[] wordArray) {
		return getSynonymPhraseList(Arrays.asList(wordArray));
	}

	public static List<String> getSynonymPhraseList(List<String> wordList) {
		return getSubCombinationStringSet(getSynonyms(wordList), 0);
	}

	public static ArrayList<ArrayList<String>> getSynonyms(String[] wordArray) {
		return getSynonyms(Arrays.asList(wordArray));
	}

	public static ArrayList<ArrayList<String>> getSynonyms(List<String> wordList) {

//		PorterStemmer stemmer = new PorterStemmer();
		ArrayList<ArrayList<String>> synonymSetForWordList = new ArrayList<ArrayList<String>>();
		int sizeOfWordList = wordList.size();

		System.out.println("size of word list (in getFinalCombinationSet): "
				+ sizeOfWordList);
		for (int i = 0; i < sizeOfWordList; i++) {
			System.out.println("-------------------------");
			String word = wordList.get(i).toLowerCase().trim();
			ArrayList<String> processedSynonymSetForWord = new ArrayList<String>();
//			String stemmedWord1 = stemmer.stem(word);
			String stemmedWord1 = word;
			processedSynonymSetForWord.add(stemmedWord1);
			System.out.println("WORD: " + stemmedWord1);

			// Synonyms.load();
			Set<Set<String>> synonymSetsForWord = Synonyms.getSynonymSets(word);
			int z = 1;
			for (Set<String> synSet : synonymSetsForWord) {
				System.out.println("---- " + (z++) + " th synset ----");
				for (String syn : synSet) {
					if (!syn.trim().contains(word)) {
//						String stemmedWord2 = stemmer.stem(syn.trim());
						String stemmedWord2 = syn.trim();
						processedSynonymSetForWord.add(stemmedWord2);
						System.out.println(stemmedWord2);
					}
				}
			}
			synonymSetForWordList.add(processedSynonymSetForWord);
		}

		return synonymSetForWordList;

	}

	private static ArrayList<String> getSubCombinationStringSet(
			ArrayList<ArrayList<String>> synonymSetList, int currentIndex) {
		System.out.println("currentIndex: " + currentIndex);
		if (currentIndex >= synonymSetList.size()) {
			return null;
		} else {

			ArrayList<String> toBeReturnedList = new ArrayList<String>();
			ArrayList<String> currentList = synonymSetList.get(currentIndex);
			ArrayList<String> returnedList = getSubCombinationStringSet(
					synonymSetList, ++currentIndex);

			for (int i = 0; i < currentList.size(); i++) {
				String word = currentList.get(i);
				if (returnedList != null) {
					for (int j = 0; j < returnedList.size(); j++) {
						toBeReturnedList.add(word + returnedList.get(j));
					}
				} else {
					toBeReturnedList.add(word);
				}
			}
			return toBeReturnedList;
		}
	}

}
