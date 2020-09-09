package apps;

import java.io.*;
import java.util.*;

import index.Posting;
import index.PostingList;
import index.InvertedIndex;

public class ComputeDice {
 Index index;
 public static void main(String[] args) {
	 try {
		 ComputeDice obj = new ComputeDice();
		 String inputFile =args[0];
		 obj.index = new InvertedIndex();
		 obj.index.load(Boolean.parseBoolean(args[1]));
		 BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		 Set<String> vocabulary = obj.index.getVocabulary();
		 PrintWriter diceWriter = new PrintWriter("diced-queries.txt","UTF-8");
		 String query;
		 while((query=reader.readLine()) != null) {
			 String queryTerms[]= query.split("\\s+");
			 ArrayList<String> addedTerms =new ArrayList<String>();
			 for(int i=0;i<queryTerms.length;i++) {
				 double best=0;
				 String bestTerm="";
				 for(String term:vocabulary) {
					 double dice = obj.ComputeDice(queryTerms[i],term);
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
 
 double computeDice(String termA, String termB) {
	 PostingList listA = index.getPostings(termA);
	 PostingList listB = index.getPostings(termB);
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
	 return nAB/(nA+nB);
 }
 
 
 
 
 
 
 
 
 
 
 
 
 
}
