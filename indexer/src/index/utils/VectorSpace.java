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

public class VectorSpace{
	
	InvertedIndex index;
	public VectorSpace(boolean isCompressed) throws IOException{
		index = new InvertedIndex(isCompressed);
		index.loadLookup();
	}
	
	
	public List<Map.Entry<Integer,Double>> fetchQuery(String query, int k) throws IOException{
		PriorityQueue<Map.Entry<Integer,Double>> result = 
				new PriorityQueue<>(Map.Entry.<Integer,Double>comparingByValue());
		String [] qTerms = query.split("\\s+");
		PostingList[] lists = new PostingList[qTerms.length];
		
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

					currentScore+=computeScore(tf,qf,ni,N);
					scored = true;
				}
				i++;
			}
			
			if(scored) {
				currentScore /= index.docLengthMap.get(d);
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
	
	
	public double computeScore(int tf, int qf, int ni, int N) {
		double termWeight = tf>0 ? 1+Math.log(tf): 0;
		double queryWeight = qf>0 ? 1+Math.log(qf) : 0;
		double idf = Math.log((double)N/(double)ni);
			
		return termWeight*idf*queryWeight*idf;
		
	}

}
