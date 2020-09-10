package apps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class invFileRead {

	private String invFile;
	public invFileRead(String filename) {
		this.invFile=filename;
	}
	
	public int fromByteArray(byte[] bytes) { return ByteBuffer.wrap(bytes).getInt(); }

	public void readFile(long offset, int buffLength) throws IOException {
		
		RandomAccessFile reader;
		reader = new RandomAccessFile(this.invFile, "rw");
		byte[] buffer = new byte[buffLength];
		reader.seek(offset);
		reader.read(buffer, 0, buffLength);
		int off = 0; 
		while (off < buffLength) {
			int docId = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
			//System.out.println(docId);
			off += 4;
			int tf = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
			Integer[] pos = new Integer[tf];
			for (int i = 0; i < tf; i++) {
				pos[i] = fromByteArray(Arrays.copyOfRange(buffer, off, off + 4));
				//System.out.println(pos[i]);
				off += 4;
				if(off>=buffLength) break;
				}
			//System.out.println("-----");

		}
		reader.close();
		
	}

	
}
