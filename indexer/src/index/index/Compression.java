package index.index;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Compression {
	
	
	 public void VByteEncode(Integer[] inp, ByteBuffer byteBuffer){
		 deltaEncode(inp);
		    for(int i: inp){
		      while(i>=128){
		    	  byteBuffer.put((byte)(i&0x7F));
		        i>>>=7;
		      }
		      byteBuffer.put((byte)(i|0x80));
		    }
		  }
	 
	  public void vByteDecode(byte[] compressed, IntBuffer decompressed){

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
		      decompressed.put(result);
		    }
		    deltaDecode(decompressed);
		  }
	
	/**
	 * @param input: Posting list to be compressed
	 * @param output: Storage for compressed list
	 */
	public void deltaEncode(Integer[] inp) {
		
		int index = 0;
		int oldDoc = 0;
		int oldPos = 0;
		int initPos = 0;
		int initDoc = inp[index++];
		while(index<inp.length) {
			int count = inp[index++];
			try {
				oldDoc = inp[index+count];
				inp[index+count] -= initDoc;
				initDoc = oldDoc;
			} catch(ArrayIndexOutOfBoundsException e) {
				
			}
			initPos = inp[index];
			int j = 1;
			while(j<count) {
				oldPos = inp[index+j];
				inp[index+j] -= initPos;
				initPos = oldPos;
				j++;
			}
			index+=count+1;	
		}
	}
	public void deltaDecode(IntBuffer output) {
		int [] intArr = output.array();
		int index=0;
		while(index<output.position()) {
			int firstDoc = intArr[index++];
			int count = intArr[index++];
			try {
				intArr[index+count] += firstDoc;
			}catch(ArrayIndexOutOfBoundsException e) {
				
			}
			int initPos = intArr[index];
			int j=1;
			while(j<count) {
				intArr[index+j] += initPos;
				initPos = intArr[index+j];
				j++;
			}
			index+=count;
		}	
	}
	


}
