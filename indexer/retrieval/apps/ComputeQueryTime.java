package apps;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;

import index.InvertedIndex;

public class ComputeQueryTime {


	public void computeTime(String queryList7, String queryList14, int topK, boolean isCompressed) {

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
	System.out.println("seven word queries for compression = " + isCompressed+" took "+Duration.between(start,end));
	
	inputFile = queryList7;
	reader = new BufferedReader(new FileReader(inputFile));
	while((query = reader.readLine())!=null) {
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
	System.out.println("fourteen word queries for compression = " + isCompressed+" took "+Duration.between(start, end));
	}catch(Exception ex) {
	}

	}
	}
	
