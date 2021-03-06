package retrieval.apps;

import java.util.ArrayList;

import index.index.InvertedIndex;

import java.io.PrintWriter;
import java.util.*;

public class SelectTerms {
	public void generateQuery(int numTerms, boolean isCompressed) {
		try {
			InvertedIndex index = new InvertedIndex(isCompressed);
			index.loadLookup();
			Set<String> vocab =index.getVocabulary();
			ArrayList<String> words =new ArrayList<String>();
			words.addAll(vocab);

			PrintWriter queryWriter =new PrintWriter("queryterms.txt","UTF-8");
			//System.out.println("Vocab Length: "+words.size());

			Random rand = new Random(System.currentTimeMillis());
			for(int i=0;i<100;i++) {
				Set<Integer>indexes = new HashSet<Integer>();
				while(indexes.size()<numTerms) {
					int idx = rand.nextInt(words.size()-1);
					indexes.add(idx);
				}
			String result =" ";
			for(int idx:indexes) {
				result+=words.get(idx);
				result+=" ";
			}
			result = result.trim();
			queryWriter.println(result);
			}
			queryWriter.close();
		}catch(Exception e) {
	}
}
}
