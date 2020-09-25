package retrieval.apps;

import java.util.List;
import java.util.Map;

import index.index.IndexBuilder;
import index.index.InvertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;


public class ComputeQueryTime {


	public void computeTime(String queryList7, String queryList14, int topK, boolean isCompressed, String sourceFile) {

	try {
	InvertedIndex index = new InvertedIndex(isCompressed);
	index.loadLookup();
	int k = topK;
	@SuppressWarnings("unused")
	List<Map.Entry<Integer,Double>> results;
	Instant start,end;
	String inputFile = queryList7;
	BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	String query;
	while((query=reader.readLine())!=null) {
	results = index.fetchQuery(query,k);
	}
	reader.close();
	reader = new BufferedReader(new FileReader(inputFile));
	start = Instant.now();
	while((query=reader.readLine())!=null) {
	results = index.fetchQuery(query,k);
	}

	end = Instant.now();
	reader.close();
	System.out.println("Compression = "+isCompressed);
	System.out.println("seven word queries took: "+Duration.between(start,end));
	
	
	IndexBuilder builder = new IndexBuilder();
	builder.buildIndex(sourceFile,isCompressed);
	InvertedIndex nIndex = new InvertedIndex(isCompressed);
	nIndex.loadLookup();

	
	inputFile = queryList14;
	reader = new BufferedReader(new FileReader(inputFile));
	while((query = reader.readLine())!=null) {
	results = nIndex.fetchQuery(query,k);
	}
	reader.close();
	reader = new BufferedReader(new FileReader(inputFile));
	start = Instant.now();
	while((query=reader.readLine())!=null) {
	results = nIndex.fetchQuery(query,k);
	}
	end = Instant.now();
	reader.close();
	System.out.println("fourteen word queries took "+Duration.between(start, end));
	}catch(Exception ex) {
	}

	}
	}
	
