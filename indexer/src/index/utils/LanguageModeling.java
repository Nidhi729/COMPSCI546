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

public class LanguageModeling {
	
	private String modelName;
	private double mu = 1200;
    private double lambda = 0.2;

	
    InvertedIndex index;
    public LanguageModeling(boolean isCompressed, String modelName) throws IOException {
    	index = new InvertedIndex(isCompressed);
    	index.loadLookup();	
    	
    	this.modelName = modelName;
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
				
				int tf = 0;
				int dl = index.docLengthMap.get(d);
				int ci = index.lookupStatsMap.get(qTerms[i]).get(1);
				long cl = index.getTotalWordCount();
				
				if(post!=null && post.getDocId()==d) {
					tf = post.getTermFreq();
					scored = true;
				}
				if(this.modelName.equals("JelinikMercer"))
					currentScore+=scoreJelinikMercer(tf,dl,ci,cl);
				else
					currentScore+=scoreDirichlet(tf,dl,ci,cl);
						
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


    
	public double scoreDirichlet(int tf, int dl, int ci, long cl) {
		
	    double fgScore = (double)tf/(double)dl;
	    double bgScore = (double)ci/(double)cl;
	    double alpha = mu/(dl+mu);

	    return genScore(fgScore,bgScore,alpha);
	}
	
	public double scoreJelinikMercer(int tf, int dl, int ci, long cl) {	    

	    double fgScore = (double)tf/(double)dl;
	    double bgScore = (double)ci/(double)cl;
	    double alpha = lambda;
	    return genScore(fgScore,bgScore,alpha);
	}
	
	public double genScore(double fgScore,double bgScore,double alpha) {
		return Math.log((1-alpha)*fgScore + alpha*bgScore);
	}

}
