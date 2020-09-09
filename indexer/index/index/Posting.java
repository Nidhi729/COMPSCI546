package index;

import java.util.List;
import java.util.ArrayList;

/**
 * Creating a template for the basic Data Structure for inverted index
 * Mapping document to a list of positions (for a particular index term). 
 */
public class Posting {
	
	private List<Integer> positions;
	private Integer docID;
	
	/**
	 * Constructor to create a new Posting
	 * @param docID: Unique identifier of a document
	 * @param pos: Position for a particular index term to be added to list
	 */
	public Posting(Integer docID, Integer pos) {
		this.positions = new ArrayList<Integer>();
		this.positions.add(pos);
		this.docID = docID;
	}
	/**
	 * Adding each positions to its corresponding list
	 * @param pos: POsition to be added
	 */
	public void add(Integer pos) {
		this.positions.add(pos);
	}
	
	/**
	 * Convert list of positions to an array
	 * @return Array containing list of positions
	 */
	public Integer[] getPositionsArray() {
		return positions.stream().toArray(Integer[]::new);
	}
	
	/**
	 * @return Return complete posting as an array
	 */
	public ArrayList<Integer> toIntegerArray() {
		ArrayList<Integer> postingArr = new ArrayList<Integer>();
		postingArr.add(docID);
		postingArr.add(positions.size());
		postingArr.addAll(positions);
		
		return postingArr;
	}
	/**
	 * @return Return count of occurrences of a term in a document
	 */
	public Integer getTermFreq() {
		return this.positions.size();
	}
	/**
	 * @return Return document ID of the posting
	 */
	public Integer getDocId() {
		return this.docID;
	}
	/**
	 * Encode posting 
	 */
	  public void deltaEncodePositions(){
		  
		  int idx = this.positions.size()-1;
		  while(idx>=1) {
		      this.positions.set(idx, this.positions.get(idx) - this.positions.get(idx-1));
		      idx--;
		  }
	  }
		  
	  /**
	   * Decode posting
	   */
		  public void deltaDecodePositions(){
			  int idx = this.positions.size()-1;
			  while(idx>=1) {
			      this.positions.set(idx, this.positions.get(idx) + this.positions.get(idx-1));
			      idx--;
			  }
		  }
	
	

}