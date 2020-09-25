/**
 * Run me
 */
package retrieval.apps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import index.index.*;

public class BuildIndex {
  public static void main(String[] args) throws IOException {
	  
	  //Input file and compression instruction
	  String sourceFile = args[0];
	  String queryFile = args[1];
	  boolean compress = Boolean.parseBoolean(args[2]);
	  
	  System.out.println("Data source: " + sourceFile);
	  System.out.println("Compression: " + compress);
	  
	  /* Index Creation*/
	  IndexBuilder builder = new IndexBuilder();
	  builder.buildIndex(sourceFile, compress);
	  System.out.println("** Index Created **");
	  
	  /* Compare model scores */
	  int topK = 7;
	  CompareScoreModels models = new CompareScoreModels(compress);
	  models.compareScores(queryFile,topK);

	  
	  
/** Assignment 1 Run statements 
 
	  // Index Creation
	  IndexBuilder builder = new IndexBuilder();
	  builder.buildIndex(sourceFile, compress);
	  System.out.println("** Index Created **");	  
	  
	  // 7-word Query File Generation 
	  SelectTerms generate = new SelectTerms();
	  generate.generateQuery(7,compress);
	  System.out.println("7 Word Query File Generated");
	  
	  // 14-Word Query File Generation 
	  ComputeDice compute = new ComputeDice();
	  compute.generateQuery("queryterms.txt",compress);
	  System.out.println("14 Word Query File Generated");
	  
	  // Retrieval Time Computation
	  ComputeQueryTime timer = new ComputeQueryTime();
	  timer.computeTime("queryterms.txt", "diced-queries.txt", 5, compress,sourceFile);	
	  
	  // Statistics 
	  InvertedIndex index = new InvertedIndex(compress);
	  index.loadLookup();
	  index.computeStats();
	  
	  
	   //The following code is to check retrieval for "!compress" case (i.e. NOT(compress)) for the same query file 
	   //Builds the index with !compress as the command
	   //Generates 14-term query file using the old query file
	   //Computes retrieval time for comparison   
	   
	  IndexBuilder nbuilder = new IndexBuilder();
	  nbuilder.buildIndex(sourceFile, !compress);
	  System.out.println("** New Index Created **");
	  
	  // 14-Word Query File Generation 
	  ComputeDice ncompute = new ComputeDice();
	  ncompute.generateQuery("queryterms.txt",!compress);
	  System.out.println("New 14 Word Query File Generated");
	  
	  // Retrieval Time Computation
	  ComputeQueryTime ntimer = new ComputeQueryTime();
	  ntimer.computeTime("queryterms.txt", "diced-queries.txt", 5, !compress,sourceFile);	
*/  
  
  }
}
