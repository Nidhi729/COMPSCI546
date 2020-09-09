package index;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Compression {
	
	
	/**
	 * @param input: Posting list to be compressed
	 * @param output: Storage for compressed list
	 */
	 public void VByteEncode(Integer[] postingList, ByteBuffer byteBuffer){
		    for(int i: postingList){
		      while(i>=128){
		    	  byteBuffer.put((byte)(i&0x7F));
		        i>>>=7;
		      }
		      byteBuffer.put((byte)(i|0x80));
		    }
		  }
	 
	  public void vByteDecode(byte[] compressed, ArrayList<Integer> decompressed){
		    int i = 0;
		    while(i<compressed.length){
		      int position = 0;
		      int result = ((int)compressed[i]&0x7F);
		      while((compressed[i]&0x80) == 0){
		        i++;
		        position++;
		        int unsigned_byte = ((int)compressed[i]&0x7F);
		        result |= (unsigned_byte << (7*position));
		      }
		      i++;
		      decompressed.add(result);
		    }
		  }


	
	

}
