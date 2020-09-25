package index.utils;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import index.index.*;

public class BM25 {
	
    private double k1 = 1.5;
    private double k2 = 500;
    private double b = 0.75;
    InvertedIndex index;
	
	public BM25(boolean isCompressed) throws IOException{
		index = new InvertedIndex(isCompressed);
		index.loadLookup();
	}
	
	
	public List<Map.Entry<Integer,Double>> fetchQuery(String query, int k) throws IOException{
		PriorityQueue<Map.Entry<Integer,Double>> result = 
				new PriorityQueue<>(Map.Entry.<Integer,Double>comparingByValue());
		String [] qTerms = query.split("\\s+");
		
		
		Set<String> uniqueQTerms = new HashSet<>();
		
		PostingList[] lists = new PostingList[qTerms.length];
//		//To avoid counting the score of same terms multiple times
//		for(String q:qTerms)
//			uniqueQTerms.add(q);
//		
//		PostingList[] lists = new PostingList[uniqueQTerms.size()];
//		String [] nqTerms = new String[uniqueQTerms.size()];
//		int j = 0;
//		
//		for(String q:uniqueQTerms) {
//			nqTerms[j] = q;
//			lists[j] = index.fetchPosting(q);
//			j++;
//		}
		
		System.out.println("BM25");
		
		for(int i=0;i<qTerms.length;i++) {
			lists[i] = index.fetchPosting(qTerms[i]);
		}
		//Pre-compute query-term frequency for this query
		index.computeQueryTermFreq(query);
		
		for(int d=1;d<=index.getDocCount();d++) {
			
			Double currentScore = 0.0;
			boolean scored = false;
			int i = 0;
			for(PostingList postList:lists) {				
				
				postList.skipTo(d);
				Posting post = postList.getCurrentPosting();
				
				if(post!=null && post.getDocId()==d) {
					
					int tf = post.getTermFreq();
					int qf = index.queryTermMap.get(qTerms[i]);
					int N = index.getDocCount();
					int ni = index.lookupStatsMap.get(qTerms[i]).get(0);
					double avdl = index.getAvgDocLength();
					int dl = index.docLengthMap.get(d);

					currentScore+=computeScore(tf,qf,ni,N,dl,avdl);
					scored = true;
				}
				i++;
			}
			
			if(scored) {
				result.add(new AbstractMap.SimpleEntry<Integer,Double>(d,currentScore));
				if(result.size()>k) {
					result.poll();
				}
			}
		}
		ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer,Double>>();
		scores.addAll(result);
		scores.sort(Map.Entry.<Integer,Double>comparingByValue(Comparator.reverseOrder()));
		
		for(Map.Entry<Integer, Double> s:scores)
		System.out.println("SceneId: "+index.sceneIdMap.get((s.getKey()))+" Score: "+s.getValue());
		
		index.queryTermMap.clear();
		return scores;
	}
	
	
	public double computeScore(int tf, int qf, int ni, int N,int dl, double avdl) {
	    double K = k1*((1-b) + b*(dl/avdl));
	    double firstVar = (N-ni+0.5)/(ni+0.5);
	    double secondVar = ((k1+1.0)*tf)/(K+tf);
	    double thirdVar = ((k2+1.0)*qf)/(k2+qf);

	    return Math.log(firstVar)*secondVar*thirdVar;
		
	}

}
