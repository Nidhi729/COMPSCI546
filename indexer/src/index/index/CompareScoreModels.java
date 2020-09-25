package index.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import index.utils.*;

public class CompareScoreModels {
	
	private boolean compress;
	InvertedIndex index;
	public CompareScoreModels(boolean isCompressed) throws IOException {
		this.index = new InvertedIndex(isCompressed);
		index.loadLookup();
		this.compress = isCompressed;
	}

	public void TrecFormat() {
		
	}
	
	public void compareScores(String queryFile, int topK) throws IOException {
		
		Path path = Paths.get(queryFile);
		List<String> queryList = Files.readAllLines(path, StandardCharsets.UTF_8);
		PrintWriter writer;
		// Run for all queries in file for all models
		
		// Model 1: VectorSpace
		String runID = "ndavawala-vs-logtf-logidf";
		String outputFile = "vs.trecrun";
		writer = new PrintWriter(outputFile);
		int queryNum = 1;
		VectorSpace vs = new VectorSpace(this.compress);
		for(String query:queryList) {
			List<Map.Entry<Integer,Double>> results = vs.fetchQuery(query, topK);	
			int rank = 1;
			for(Map.Entry<Integer, Double> result: results) {
				writer.println("Q"+queryNum+"\tskip\t"+
			index.sceneIdMap.get(result.getKey())+"\t"+rank+"\t"+
						result.getValue()+"\t"+runID);
				rank++;
			}
			queryNum++;
		}
		writer.close();
		System.out.println("---------------");
		
		
		
		//Model 2: BM25
		runID = "ndavawala-bm25-1.5-500-0.75";
		outputFile = "bm25.trecrun";
		writer = new PrintWriter(outputFile);
		queryNum = 1;
		BM25 bm = new BM25(this.compress);
		for(String query:queryList) {
			List<Map.Entry<Integer,Double>> results = bm.fetchQuery(query, topK);	
			int rank = 1;
			for(Map.Entry<Integer, Double> result: results) {
				writer.println("Q"+queryNum+"\tskip\t"+
			index.sceneIdMap.get(result.getKey())+"\t"+rank+"\t"+
						result.getValue()+"\t"+runID);
				rank++;
			}
			queryNum++;
		}
		writer.close();
		System.out.println("---------------");
		
		// Language Modeling
		//JelinikMercer
		runID = "ndavawala-ql-jm-0.2";
		outputFile = "ql-jm.trecrun";
		writer = new PrintWriter(outputFile);
		queryNum = 1;
		LanguageModeling lm1 = new LanguageModeling(this.compress, "JelinikMercer");
		for(String query:queryList) {
			List<Map.Entry<Integer,Double>> results = lm1.fetchQuery(query, topK);	
			int rank = 1;
			for(Map.Entry<Integer, Double> result: results) {
				writer.println("Q"+queryNum+"\tskip\t"+
			index.sceneIdMap.get(result.getKey())+"\t"+rank+"\t"+
						result.getValue()+"\t"+runID);
				rank++;
			}
			queryNum++;
		}
		writer.close();
		System.out.println("---------------");
		
		//Dirichlet
		runID = "ndavawala-ql-dir-1200";
		outputFile = "ql-dir.trecrun";
		writer = new PrintWriter(outputFile);
		queryNum = 1;
		LanguageModeling lm2 = new LanguageModeling(this.compress, "Dirichlet");
		for(String query:queryList) {
			List<Map.Entry<Integer,Double>> results = lm2.fetchQuery(query, topK);	
			int rank = 1;
			for(Map.Entry<Integer, Double> result: results) {
				writer.println("Q"+queryNum+"\tskip\t"+
			index.sceneIdMap.get(result.getKey())+"\t"+rank+"\t"+
						result.getValue()+"\t"+runID);
				rank++;
			}
			queryNum++;
		}
		
		writer.close();
		
		
	}

}
