package apps;

import java.util.SortedSet;
public class DumpVocabulary {
	public static void main(String[] args) {
		Index index = new InvertedIndex();
		index.load(Boolean.parseBoolean(args[0]));
		SortedSet<String> vocabulary = new SortedSet<String>(index.getVocabulary());
		for(String term:vocabulary) {
			int freq =index.getTermFreq(term);
			int docFreq =index.getDocFreq(term);
			System.out.println(term +" "+freq + " "+ docFreq);
		}
		
	}
}
