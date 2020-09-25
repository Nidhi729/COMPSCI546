package retrieval.apps;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import index.index.IndexBuilder;
import index.index.InvertedIndex;

public class VocabularyCheck {
	public static void main(String[] args) throws IOException {
		
		String sourceFile = args[0];
		IndexBuilder builder = new IndexBuilder();
		builder.buildIndex(sourceFile, false);
		InvertedIndex index = new InvertedIndex(false);
		index.loadLookup();
		//LookupStatsMap: (Term -> (Doc Count,Term Count))
		Map<String, List<Integer>> uncompLookupStatsMap = index.getLookupStatsMap();
		
		builder = new IndexBuilder();
		builder.buildIndex(sourceFile, true);
		index = new InvertedIndex(true);
		index.loadLookup();
		Map<String, List<Integer>> compLookupStatsMap = index.getLookupStatsMap();
		
		if(uncompLookupStatsMap.equals(compLookupStatsMap))
			System.out.println("Vocabulary and Term Counts are equal");
		else
			System.out.println("Vocabulary and Term Counts are different");
			
		
		
		
		
		
		
		
	}

}
