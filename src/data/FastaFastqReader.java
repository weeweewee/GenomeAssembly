package data;

import java.io.*;

import entry.Constants;

public class FastaFastqReader {
	private String file1;
	private String file2;
	private boolean isFastQ;

	public FastaFastqReader(String fileName) {
		file1 = fileName;
		file2 = null;
	}

	public FastaFastqReader(String fileName1, String fileName2) {
		file1 = fileName1;
		file2 = fileName2;
	}
	
	public boolean Read(ValidReadMethod toCall) {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(file1));
			BufferedReader br2 = file2 == null? null :
																					new BufferedReader(new FileReader(file2));
      String line1 = "";
      String line2 = "";
      while (((line1 = br1.readLine()) != null) &&
          (br2 == null? true : ((line2 = br2.readLine()) != null))) {
        String read1 = br1.readLine();
        line2 = br2 == null? br1.readLine() : line2;
        String read2 = br2 == null? br1.readLine() : br2.readLine();

        String id1 = line1.split(Constants.SPACE_DELIMS)[0];

        if (id1.equals(line2.split(Constants.SPACE_DELIMS)[0])) {
        	toCall.RunFunction(id1, read1, read2);
        }

        if (isFastQ) {
          br1.readLine();
          br1.readLine();
          if (br2 != null) {
            br2.readLine();
            br2.readLine();
          }
        }
      }
      br1.close();
      if (br2 != null) {
      	br2.close();
      }
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public interface ValidReadMethod {
		void RunFunction(String id, String read1, String Read2);
	}
}
