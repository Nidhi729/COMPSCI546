package index.index;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import retrieval.apps.TermStatistics;
import index.index.*;
public class InvertedIndex {
	
	private String sceneIdsFile;
	private String playIdsFile;
	private String docLengthFile;
	private String lookupFile;
	private String invFile;
	private boolean isCompressed;
	
	public Map<String, List<Integer>> lookupReadMap = new HashMap<String, List<Integer>>();
	public Map<String, List<Integer>> lookupStatsMap = new HashMap<String, List<Integer>>();
	public Map<Integer, Integer> docLengthMap = new HashMap<Integer, Integer>();
	public Map<Integer, String> sceneIdMap = new HashMap<Integer, String>();
	public Map<Integer, String> playIdMap = new HashMap<Integer, String>();
	public Map<String, PostingList> invertedList = new HashMap<String, PostingList>();
	public Map<String, Integer> queryTermMap = new HashMap<String, Integer>();
	
	
	public InvertedIndex(boolean isCompressed) {

		this.sceneIdsFile = "sceneId.txt";
		this.playIdsFile = "playIds.txt";
		this.docLengthFile = "docLength.txt";
		this.lookupFile = "lookup.txt";
		this.isCompressed = isCompressed;
		if(isCompressed)
			this.invFile = "invFileCompressed";
		else
			this.invFile = "invFile";
	}

	public Map<String, List<Integer>> getLookupStatsMap(){
		return this.lookupStatsMap;
	}
	public void loadDocLength() throws IOException {
		
		Path path = Paths.get(this.docLengthFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.docLengthMap.put(Integer.parseInt(rowData[0]),Integer.parseInt(rowData[1]));
		}		
	}
	public void loadSceneId() throws IOException {
		
		Path path = Paths.get(this.sceneIdsFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.sceneIdMap.put(Integer.parseInt(rowData[0]),rowData[1]);
		}	
	}
	public void loadPlayId() throws IOException {
		
		Path path = Paths.get(this.playIdsFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.playIdMap.put(Integer.parseInt(rowData[0]),rowData[1]);
		}		
	}
		
	public void loadLookup() throws IOException {
		
		/* Load other maps */
		loadDocLength() ;
		loadSceneId();
		loadPlayId();
		
		/* Load Lookup Map */
		Path path = Paths.get(this.lookupFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.lookupReadMap.put(rowData[0],new ArrayList<Integer>(Arrays.asList(Integer.parseInt(rowData[1]),Integer.parseInt(rowData[2]))));
			this.lookupStatsMap.put(rowData[0],new ArrayList<Integer>(Arrays.asList(Integer.parseInt(rowData[3]),Integer.parseInt(rowData[4]))));
		}
	}
	
	public void computeStats() {
		/* Compute Statistics */
		TermStatistics compute = new TermStatistics(docLengthMap,sceneIdMap,playIdMap);
		System.out.println("------------------");
		compute.getAvgSceneLen();
		compute.getShortestScene();
		compute.getShortestPlay();
		compute.getLongestPlay();
		System.out.println("------------------");
	}
	
	public Set<String> getVocabulary() throws IOException{
		return this.lookupReadMap.keySet();
	}
	public int getTermFreq(String term) {
		return (this.lookupStatsMap.get(term).get(1));
		
	}
	public int getDocCount() {
		return this.docLengthMap.keySet().size();
	}
	public double getAvgDocLength() {

		int lenTotal = 0;
		for (Map.Entry<Integer,Integer> entry : this.docLengthMap.entrySet()) {
			lenTotal += entry.getValue();
		}
		return lenTotal/(this.docLengthMap.keySet().size());
		
	}
	public int getCurDocLength(Integer docID) {
		int len = 0;
		if(this.docLengthMap.containsKey(docID))
			len = this.docLengthMap.get(docID);
		return len;
		
	}
	public long getTotalWordCount() {
		long count = 0;
		for (Map.Entry<String,List<Integer>> entry : this.lookupStatsMap.entrySet()) {
			count += entry.getValue().get(1);
		}
		return count;
	}
	public void computeQueryTermFreq(String query) {
		
		String [] qTerms = query.split("\\s+");
		for(String qTerm: qTerms) {
			if(this.queryTermMap.containsKey(qTerm))
				this.queryTermMap.put(qTerm,this.queryTermMap.get(qTerm)+1);
			else
				this.queryTermMap.put(qTerm, 1);
		}
	}

		
	
	/**
	 * Check if invertedList map contains posting list for search term. If yes return list.
	 * If map does not contain term, compute its posting  list, add to the map and return the list. 
	 * @param term: Search Term
	 * @return Posting List of the search term
	 * @throws IOException
	 */
	public PostingList fetchPosting(String term) throws IOException{
		
		if(invertedList.isEmpty())
			loadLookup();
		
		PostingList ret = new PostingList();
		if(this.invertedList.containsKey(term)) {
			ret =  this.invertedList.get(term);
		}
		else {
			ret = computePosting(term);
			this.invertedList.put(term, ret);
		}	
		ret.postingsIndex = 0;
		return ret;
		}
	
	/** Find Posting List of a search term */
	public PostingList computePosting(String term)throws IOException {
		PostingList ret = new PostingList();
		RandomAccessFile reader = new RandomAccessFile(this.invFile, "rw");
		
		/*Fetch offset and bytes to read from lookup map */
		List<Integer> data= this.lookupReadMap.get(term);
		long offset = (long) data.get(0);
		int buffLength = data.get(1);
		
		/* Allocate memory */
		byte[] buff = new byte[buffLength];
		IntBuffer intBuff = IntBuffer.allocate(buff.length);
		
		/*Read*/ 
		reader.seek(offset);
		reader.read(buff,0,buffLength);
		
		if(this.isCompressed) {
			Compression comp = new Compression();
			comp.vByteDecode(buff,intBuff);
			int [] rawData = new int[intBuff.position()];
			intBuff.rewind();
			intBuff.get(rawData);
			ret.fromIntegerArray(rawData);
		}
		else {	
			ret = readInvFile(buff,buffLength);
			ret.postingsIndex = 0;	
		}
		reader.close();
		return ret;
	}

	
	/** Read from Inverted File and generate Posting List*/
	public PostingList readInvFile(byte[] buff, int buffLength) {
		PostingList ret = new PostingList();
		int off = 0; 
		while (off < buffLength) {
			int docId = fromByteArray(Arrays.copyOfRange(buff, off, off + 4));
			off += 4;
			int tf = fromByteArray(Arrays.copyOfRange(buff, off, off + 4));
			List<Integer> pos = new ArrayList<Integer>();
			off += 4;
			for (int i = 0; i < tf; i++) {
				pos.add(fromByteArray(Arrays.copyOfRange(buff, off, off + 4)));
				off += 4;
				}
			 ret.add(new Posting(docId,pos));			
		}
		ret.postingsIndex = 0;
		return ret;
		
	}
	
	public int fromByteArray(byte[] bytes){ 
		return ByteBuffer.wrap(bytes).getInt(); 
		}
	
	/**Query Retrieval: Naive (Count of freq)*/
	public List<Map.Entry<Integer,Double>> fetchQuery(String query, int k) throws IOException{
		PriorityQueue<Map.Entry<Integer,Double>> result = 
				new PriorityQueue<>(Map.Entry.<Integer,Double>comparingByValue());
		String [] qTerms = query.split("\\s+");
		PostingList[] lists = new PostingList[qTerms.length];
		for(int i=0;i<qTerms.length;i++) {
			lists[i] = fetchPosting(qTerms[i]);
		}
		for(int d=1;d<=getDocCount();d++) {
			Double currentScore = 0.0;
			for(PostingList postList:lists) {
				postList.skipTo(d);
				Posting post = postList.getCurrentPosting();
				if(post!=null && post.getDocId()==d) {
					currentScore+=post.getTermFreq();
				}
			}
			result.add(new AbstractMap.SimpleEntry<Integer,Double>(d,currentScore));
			if(result.size()>k) {
				result.poll();
			}	
		}
		ArrayList<Map.Entry<Integer, Double>> scores = new ArrayList<Map.Entry<Integer,Double>>();
		scores.addAll(result);
		scores.sort(Map.Entry.<Integer,Double>comparingByValue(Comparator.reverseOrder()));
		return scores;
	}

	

}
