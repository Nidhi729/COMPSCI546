package apps;
/*
 * Class to calculate statistics based on the source data
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TermStatistics {
	
	public Map<Integer, Integer> docLengthMap = new HashMap<Integer, Integer>();
	public Map<Integer, String> sceneIdMap = new HashMap<Integer, String>();
	public Map<Integer, String> playIdMap = new HashMap<Integer, String>();
	
	
	public TermStatistics(Map<Integer, Integer> docLength, Map<Integer, String> sceneId, Map<Integer, String> playId) {
		this.docLengthMap = docLength;
		this.sceneIdMap = sceneId;
		this.playIdMap = playId;
	}
	
	public double getAvgSceneLen() {
		
		double avgLen = 0;
		Collection<String> sceneIdList= this.sceneIdMap.values();
		Set<String> sceneIdSet = new HashSet<String>();
		
		// Find number of unique scenes
		for(String id:sceneIdList) {
			sceneIdSet.add(id);
		}
		//Finding total length of all scenes
		double lenTotal = 0;
		for (Map.Entry<Integer,Integer> entry : this.docLengthMap.entrySet()) {
			lenTotal += entry.getValue();
		}
		avgLen = (lenTotal)/(double)sceneIdSet.size();
		System.out.println("Average Scene Length: "+ avgLen);
		return avgLen;
	}
	
	public String getShortestScene() {
		
		String shortestScene = "";
		Integer minLen = Integer.MAX_VALUE;
		Map<String,Integer> sceneLen = new HashMap<String, Integer>();
		
		//Finding total length of each scene
		for (Map.Entry<Integer,String> entry : this.sceneIdMap.entrySet()) {
			if(sceneLen.containsKey(entry.getValue())) {
				int curLen = sceneLen.get(entry.getValue());
				curLen += this.docLengthMap.get(entry.getKey());
				sceneLen.put(entry.getValue(), curLen);
			}
			else 
				sceneLen.put(entry.getValue(), this.docLengthMap.get(entry.getKey()));	
		}
		//Finding scene with minimum length 
		for (Map.Entry<String,Integer> entry : sceneLen.entrySet()) {
			if(entry.getValue()<minLen) {
				minLen = entry.getValue();
				shortestScene = entry.getKey();
			}			
		}
		System.out.println("Shortest Scene: "+ shortestScene);
		return shortestScene;	
	}
	
	public String getShortestPlay() {
		
		String shortestPlay = "";
		Integer minLen = Integer.MAX_VALUE;
		Map<String,Integer> playLen = new HashMap<String, Integer>();
		
		//Finding total length of each play
		for (Map.Entry<Integer,String> entry : this.playIdMap.entrySet()) {
			
			if(playLen.containsKey(entry.getValue())) {
				int curLen = playLen.get(entry.getValue());
				curLen += this.docLengthMap.get(entry.getKey());
				playLen.put(entry.getValue(), curLen);
			}
			else 
				playLen.put(entry.getValue(), this.docLengthMap.get(entry.getKey()));	
		}
		
		//Finding play with minimum length
		for (Map.Entry<String,Integer> entry : playLen.entrySet()) {
			if(entry.getValue()<minLen) {
				minLen = entry.getValue();
				shortestPlay = entry.getKey();
			}			
		}
		System.out.println("Shortest Play: "+ shortestPlay);
		return shortestPlay;	
	}
	
	public String getLongestPlay() {
		
		String longestPlay = "";
		Integer maxLen = Integer.MIN_VALUE;
		Map<String,Integer> playLen = new HashMap<String, Integer>();
		
		//Finding total length of each play
		for (Map.Entry<Integer,String> entry : this.playIdMap.entrySet()) {
			
			if(playLen.containsKey(entry.getValue())) {
				int curLen = playLen.get(entry.getValue());
				curLen += this.docLengthMap.get(entry.getKey());
				playLen.put(entry.getValue(), curLen);
			}
			else 
				playLen.put(entry.getValue(), this.docLengthMap.get(entry.getKey()));	
		}
		
		//Finding play with maximum length
		for (Map.Entry<String,Integer> entry : playLen.entrySet()) {
			if(entry.getValue()>maxLen) {
				maxLen = entry.getValue();
				longestPlay = entry.getKey();
			}			
		}
		System.out.println("Longest Play: "+ longestPlay);
		return longestPlay;	
	}
	

}
