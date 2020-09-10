package index;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utilities.*;
import index.Compression;
public class IndexBuilder {
	private Map<Integer,String> sceneIdMap;
	private Map<Integer,String> playIdMap;
	private Map<String,PostingList> invertedLists;
	private Map<Integer,Integer> docLengths;
	//private Compressors compression;
	private boolean toBeCompressed;
	
	public IndexBuilder() {
		sceneIdMap = new HashMap<Integer, String>();
		playIdMap = new HashMap<Integer, String>();
		invertedLists = new HashMap<String, PostingList>();
		docLengths = new HashMap<Integer, Integer>();
	}
	private void parseFile(String filename) {
		JSONParser parser = new JSONParser();
		try {
			File testFile = new File("");
		    //String currentPath = testFile.getAbsolutePath();
		    //System.out.println("current path is: " + currentPath);
		    //Path filePath = Paths.get(currentPath, filename);
			JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(filename));
			JSONArray scenes = (JSONArray) jsonObj.get("corpus");
			for(int idx = 0;idx<scenes.size();idx++) {
				JSONObject scene = (JSONObject) scenes.get(idx);
				int docID = idx+1;
				String sceneID = (String) scene.get("sceneId");
//				System.out.println(sceneID);
				sceneIdMap.put(docID,sceneID);
				String playID = (String) scene.get("playId");
				playIdMap.put(docID,playID);
				
				String text = (String) scene.get("text");
				String[] words = text.split("\\s+");
				docLengths.put(docID,  words.length);
				
				for(int pos=0;pos<words.length;pos++) {
					String word = words[pos];
					invertedLists.putIfAbsent(word,new PostingList());
					invertedLists.get(word).add(docID,pos+1);
				}		
			}
		}
		catch(ParseException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void saveStringMap(String filename, Map<Integer,String> map) {
		List<String> lines = new ArrayList<>();
		map.forEach((k,v)->lines.add(k+" "+v));
		try {
			Path file = Paths.get(filename);
			Files.write(file, lines,Charset.forName("UTF-8"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveDocLengths(String filename){
		List<String> lines = new ArrayList<>();
		docLengths.forEach((k,v)->lines.add(k+" "+v));
		try {
			Path file = Paths.get(filename);
			Files.write(file, lines,Charset.forName("UTF-8"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
			
	private void saveInvertedLists(String lookupname, String invListname) {
		long offset = 0;
		try {
			PrintWriter lookupWriter = new PrintWriter(lookupname, "UTF-8");
			RandomAccessFile invListWriter = new RandomAccessFile(invListname, "rw");
			//Compression comp = CompressionFactory.getCompressor(compression);
			
			for(Map.Entry<String, PostingList> entry:invertedLists.entrySet()) {
				String term = entry.getKey();
				
				PostingList postings = entry.getValue();
				int docTermFreq = postings.docCount();
				int collectionTermFreq = postings.termFreq();
				//No compression
				Integer[] posts = postings.toIntegerArray();
				ByteBuffer byteBuffer = ByteBuffer.allocate(posts.length*8);
				
				//For compression
				if(toBeCompressed) {
					
//					//Delta encode each of the posting
//					for(int i=0;i<postings.postings.size();i++) {
//						Posting post = postings.postings.get(i);
//						post.deltaEncodePositions();
//					}
//					posts = postings.toIntegerArray();
//					byteBuffer = ByteBuffer.allocate(posts.length*8);
//					
//					//vByte Encode
					Compression comp = new Compression();
					comp.VByteEncode(posts,byteBuffer);
				}
				else {
					for(int post:posts)
						byteBuffer.putInt(post);
				}
				
				byte [] array =  byteBuffer.array();
				invListWriter.write(array,0,byteBuffer.position());
				long bytesWritten = invListWriter.getFilePointer()-offset;
				//System.out.println();
				lookupWriter.println(term+" "+offset+" "+bytesWritten+" "+docTermFreq+" "+collectionTermFreq);
				offset = invListWriter.getFilePointer();				
			}
			invListWriter.close();
			lookupWriter.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Here");
		}	
	}
	public void buildIndex(String source, boolean compress) {
//		this.compression = compress?Compressors.VBYTE:Compressors.EMPTY;
		this.toBeCompressed = compress;
		String invFile = compress?"invFileCompressed":"invFile";
		parseFile(source);
		saveStringMap("sceneId.txt",sceneIdMap);
		saveStringMap("playIds.txt",playIdMap);
		saveDocLengths("docLength.txt");
		saveInvertedLists("lookup.txt",invFile);
	}
	
		
	
	
	

}
