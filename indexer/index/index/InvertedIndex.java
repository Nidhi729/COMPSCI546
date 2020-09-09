package index;

import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class InvertedIndex {
	
	String sceneIdsFile;
	String playIdsFile;
	String docLengthFile;
	String lookupFile;
	Set<String> vocab;
	
	
	public InvertedIndex(String sceneIds,String playIds,String docLength,String lookup) {
		this.sceneIdsFile = sceneIds;
		this.playIdsFile = playIds;
		this.docLengthFile = docLength;
		this.lookupFile = lookup;
	}
	public InvertedIndex() {
		
	}
	
	public void loadInvertedList() throws IOException{
		//Load map from lookuptable
	}
	
	public void loadLookup() throws IOException {
		//Generate Vocabulary
		
		Set<String> vocab = new HashSet<String>();
		Path path = Paths.get(this.lookupFile);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for(String line:lines) {
			vocab.add(line.split("\\s+")[0]);
		}
		this.vocab = vocab;
		
		//Generate Map
	}
	
	public Set<String> getVocabulary() throws IOException{
		return this.vocab;
	}
	
	

}
