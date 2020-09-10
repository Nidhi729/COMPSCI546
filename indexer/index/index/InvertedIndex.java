package index;

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

public class InvertedIndex {
	
	String sceneIdsFile;
	String playIdsFile;
	String docLengthFile;
	String lookupFile;
	String invFile;
	boolean isCompressed;
	
	Map<String, List<Integer>> lookupReadMap = new HashMap<String, List<Integer>>();
	Map<String, List<Integer>> lookupStatsMap = new HashMap<String, List<Integer>>();
	Map<String, Integer> docLengthMap = new HashMap<String, Integer>();
	Map<String, PostingList> invertedList = new HashMap<String, PostingList>();
	
	
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
	
	
	public int fromByteArray(byte[] bytes) { return ByteBuffer.wrap(bytes).getInt(); }


	/* SIRRRR 
	public PostingList fetchPosting(String term) throws IOException{

		PostingList ret = new PostingList();
		RandomAccessFile reader = new RandomAccessFile(this.invFile, "rw");
		
		//Fetch offset and bytes from lookup map
		List<Integer> data= this.lookupReadMap.get(term);
		long offset = (long) data.get(0);
		int buffLength = data.get(1);
		
		//Allocate memory
		byte[] buff = new byte[buffLength];
		IntBuffer intBuff = IntBuffer.allocate(buff.length);
		
		//Read 
		reader.seek(offset);
		reader.read(buff,0,buffLength);
		
		//Decode if necessary
		if(this.isCompressed) {
			Compression comp = new Compression();
			comp.vByteDecode(buff,intBuff);
		}
		else {	
			ByteBuffer bytes = ByteBuffer.wrap(buff);
			bytes.rewind();
			intBuff.put(bytes.asIntBuffer());	
		}
		//Fetch Posting
		int [] rawData = new int[intBuff.position()];
		intBuff.rewind();
		intBuff.get(rawData);
		ret.fromIntegerArray(rawData);

		return ret;
	
		}
	*/
	
	
	public PostingList fetchPosting(String term) throws IOException{

		PostingList ret = new PostingList();
		RandomAccessFile reader = new RandomAccessFile(this.invFile, "rw");
		
		//Fetch offset and bytes from lookup map
		List<Integer> data= this.lookupReadMap.get(term);
		long offset = (long) data.get(0);
		int buffLength = data.get(1);
		
		//Allocate memory
		byte[] buff = new byte[buffLength];
		IntBuffer intBuff = IntBuffer.allocate(buff.length);
		
		//Read 
		reader.seek(offset);
		reader.read(buff,0,buffLength);
		
		//Decode if necessary
		if(this.isCompressed) {
			Compression comp = new Compression();
			comp.vByteDecode(buff,intBuff);
			//Fetch Posting
			int [] rawData = new int[intBuff.position()];
			intBuff.rewind();
			intBuff.get(rawData);
			ret.fromIntegerArray(rawData);

			return ret;
		}
		else {	
//			ByteBuffer bytes = ByteBuffer.wrap(buff);
//			bytes.rewind();
//			intBuff.put(bytes.asIntBuffer());	
			
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
			reader.close();
			
			ret.postingsIndex = 0;
			return ret;
			
		}

	
		}
		

	public void loadDocLength() throws IOException {
		
		Path path = Paths.get(this.docLengthFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.docLengthMap.put(rowData[0],Integer.parseInt(rowData[1]));
		}
		
	}
		

	
	
	public void loadLookup() throws IOException {
		
		loadDocLength() ;
		//Generate Vocabulary
		Path path = Paths.get(this.lookupFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			String[] rowData = line.split("\\s+");
			this.lookupReadMap.put(rowData[0],new ArrayList<Integer>(Arrays.asList(Integer.parseInt(rowData[1]),Integer.parseInt(rowData[2]))));
			this.lookupStatsMap.put(rowData[0],new ArrayList<Integer>(Arrays.asList(Integer.parseInt(rowData[3]),Integer.parseInt(rowData[4]))));
		}

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
	
	

}
