/**
 * Run me
 */
package apps;

import java.io.IOException;
import java.util.Set;

import index.*;

public class BuildIndex {
  public static void main(String[] args) throws IOException {
	  
/**
	  //Input file and compression instruction
	  String sourcefile = args[0];
	  boolean compress = Boolean.parseBoolean(args[1]);
	  **/
	  boolean compress = true;
	  //Index Creation (all files saved)
	  IndexBuilder builder = new IndexBuilder();
	  String path = "shakespeare-scenes.json";

	  builder.buildIndex(path, compress);

	  /*
	  SelectTerms generate = new SelectTerms();
	  generate.generateQuery(7,compress);
	  System.out.println("Completed");
	  
	  ComputeDice compute = new ComputeDice();
	  compute.generateQuery("queryterms.txt",compress);
	  */
	  
	  ComputeQueryTime timer = new ComputeQueryTime();
	  timer.computeTime("queryterms.txt", "diced-queries.txt", 5, compress);
  
	  
//	  InvertedIndex index = new InvertedIndex(compress);
//	  index.loadLookup();
//	  System.out.println(index.getDocCount());
//	  PostingList post = (index.fetchPosting("incomprehensible"));
//	  System.out.println(post.toString());
//	  PostingList post1 = (index.fetchPosting("lie"));
//	  System.out.println(post1.toString());
	  
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
