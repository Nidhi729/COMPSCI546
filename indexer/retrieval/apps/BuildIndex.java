/**
 * Run me
 */
package apps;

import java.io.IOException;
import java.util.Set;

import index.*;

public class BuildIndex {
  public static void main(String[] args) throws IOException {
	  

	  //Input file and compression instruction
//	  String sourcefile = args[0];
//	  boolean compress = Boolean.parseBoolean(args[1]);
	 
	  //Index Creation (all files saved)
	  IndexBuilder builder = new IndexBuilder();
	  String path = "shakespeare-scenes.json";
	  /*
	  builder.buildIndex(path, false);
	  
	 
	  */
	  InvertedIndex index = new InvertedIndex("sceneId.txt","playIds.txt","docLength.txt","lookup.txt");


	  SelectTerms generate = new SelectTerms();
	  generate.generateQuery(7);
	  
	  
	  //Select Queries
	  
//	  invFileRead inv = new invFileRead("invList");
//	  inv.readFile((long) 6064772, 12);
//	  
	  
	  //Retrieval Call
//	  InvertedIndex index = new InvertedIndex("sceneId.txt","playIds.txt","docLength.txt","lookup.txt");
//	  index.loadInvertedList();
	  
	  //SelectTerms
	  //ComputeDice
	  
	  //Find scores
	  
	  
	  
  }
}
