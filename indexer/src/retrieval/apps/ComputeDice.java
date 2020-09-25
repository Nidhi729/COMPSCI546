package retrieval.apps;

import java.io.*;
import java.util.*;

import index.index.InvertedIndex;
import index.index.Posting;
import index.index.PostingList;


public class ComputeDice {
InvertedIndex index;
 public void generateQuery(String inputFile, boolean isCompressed) {
	 try {
		 index = new InvertedIndex(isCompressed);
		 index.loadLookup();
		 
		 BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		 
		 Set<String> vocabulary = index.getVocabulary();
		 PrintWriter diceWriter = new PrintWriter("diced-queries.txt","UTF-8");
		
		 String query;

		 while((query=reader.readLine()) != null) {
			 String[] queryTerms= query.split("\\s+");
			 ArrayList<String> addedTerms =new ArrayList<String>();

			 for(int i=0;i<queryTerms.length;i++) {
				 double best=0.0;
				 String bestTerm="";
				 for(String term:vocabulary) {
					 double dice = computeDiceCoeff(queryTerms[i],term);
					 if(dice>best) { 
						 best=dice;
						 bestTerm = term;					
					 }
				 }

				 addedTerms.add(bestTerm);
			 }

			diceWriter.print(query);
			for(String term:addedTerms) {
				diceWriter.print(" "+term);
			}
			diceWriter.println();
		 }
		 reader.close();
		 diceWriter.close();
	 }catch(Exception e) {
		 	 }	 
 }
 
 public double computeDiceCoeff(String termA, String termB) throws IOException {
	 PostingList listA = index.fetchPosting(termA);
	 PostingList listB = index.fetchPosting(termB);
	 
	 int nA = index.getTermFreq(termA);
	 int nB= index.getTermFreq(termB);
	 double nAB =0.0;
	 while(listA.hasMore()) {
		 Posting a =listA.getCurrentPosting();
		 listB.skipTo(a.getDocId());
		 Posting b = listB.getCurrentPosting();
		 
		 if(b!=null &&b.getDocId().equals(a.getDocId())) {

			 Integer [] aPos = a.getPositionsArray();
			 Integer [] bPos = b.getPositionsArray();

			 for(int aidx=0;aidx<aPos.length;aidx++) {
				 for(int bidx =0;bidx<bPos.length;bidx++) {
					 if(bPos[bidx].equals(aPos[aidx]+1)) {
						 nAB++;	 
					 }
				 }
			 }
	     }
		 listA.skipTo(a.getDocId()+1);	 
	 }
	 /*
	 if(nAB>0) {
		 System.out.println("Nab: "+nAB +" Na+Nb: "+(nA+nB)+" coeff: "+nAB/(nA+nB));
		 System.out.println("--------------");
	 }*/
	 
	 return nAB/(nA+nB);
 }
 
 
 
 
 
 
 
 
 
 
 
 
 
}
