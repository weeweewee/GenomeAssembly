package data;

import java.util.ArrayList;
import java.util.Collections;

import entry.Constants;

public class Read {
  private RecordNode node;
  private int readIndex;
  private ArrayList<Long> uniqueKmers;
  private boolean kmersSorted;

  public Read(RecordNode n, int readI) {
    node = n;
    readIndex = readI;
    uniqueKmers = new ArrayList<Long>();
    kmersSorted = false;
  }

  public RecordNode getNode() {
    return node;
  }

  public int getIndexRead() {
    return readIndex;
  }
  
  public void addIdentifier(long idHash) {
  	kmersSorted = false;
  	uniqueKmers.add(idHash);
  }
  
  public ArrayList<Long> getUniqueKmers() {
  	if (!kmersSorted) {
  		Collections.sort(uniqueKmers);
  		kmersSorted = true;
  	}
  	return uniqueKmers;
  }
  
  public void find30Mers(Valid30merMethod toCall) {
    String read = readIndex == 1? node.getReadString1() : node.getReadString2();
    long rolling = 0;
    int hashed = 0;
    for (int i = 0; i < read.length(); i++) {
      char currentChar = read.charAt(i);
      if (currentChar == Constants.AVAILABLE_CHARS[3]) {
        rolling = 0;
        hashed = 0;
        continue;
      }
      rolling <<= 2;
      rolling |= Constants.CHAR_TO_INTEGER(currentChar);
      hashed++;
      if (hashed >= 30) {
      	toCall.RunFunction(i - 29, rolling & Constants.THIRTY_MASK, this);
      }
    }
  }

	public interface Valid30merMethod {
		void RunFunction(int pos, long hash, Read currentRead);
	}
}
