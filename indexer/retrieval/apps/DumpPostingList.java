package apps;

import java.util.SortedSet;

import index.PostingList;

public class DumpPostingList {
public static void main(String[] args) {
	Index index = new InvertedIndex();
	index.load(Boolean.parseBoolean(args[0]));
	SortedSet<String> vocabulary = new SortedSet<String>(index.getVocabulary());
	for(String term:vocabulary) {
		PostingList list = index.getPostings(term);
		System.out.println(term +"->"+list.toString());
	}
	
}
}
