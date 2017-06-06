package data;

import java.util.Collections;
import java.util.Vector;

import entry.Constants;

public class Contig {

	private String id;
	private String read;
  private Vector<Long> uniqueKmers;
  private Vector<ContigAlignmentEdge> edges;
  private boolean kmersSorted;
  private boolean isVisited;
	
	public Contig(String i, String r) {
		id = i;
		read = r;
    uniqueKmers = new Vector<Long>();
    edges = new Vector<ContigAlignmentEdge>();
    kmersSorted = false;
    isVisited = false;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRead() {
		return read;
	}
	
	public void visited() {
		isVisited = true;
	}
	
	public boolean isVisited() {
		return isVisited;
	}
	
	public Vector<ContigAlignmentEdge> getEdges() {
		return edges;
	}

	public void resetEdges() {
	  edges = new Vector<ContigAlignmentEdge>();
	}
	
	public void addEdge(ContigAlignmentEdge e) {
		edges.add(e);
	}
	
	public Vector<Long> getUniqueKmers() {
  	if (!kmersSorted) {
  		Collections.sort(uniqueKmers);
  		kmersSorted = true;
  	}
		return uniqueKmers;
	}
	
	//TODO: Consider storing both Strings instead of recalculating all the time [reduces time increases space]
	public Contig getRCContig() {
		Contig out = new Contig(id, Constants.REVERSE_COMPLEMENT(read));
		for (int i = 0; i < uniqueKmers.size(); i++) {
			out.addUniqueKmer(Constants.REVERSE_COMPLEMENT_HASH_60(uniqueKmers.get(i)));
		}
		return out;
	}
	
	public void addUniqueKmer(long hash) {
		kmersSorted = false;
		uniqueKmers.add(hash);
	}
	
  public void find30Mers(Valid30merContigMethod toCall) {
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
  
  public boolean removeEdge(ContigAlignmentEdge edge) {
  	return edges.remove(edge);
  }

	public interface Valid30merContigMethod {
		void RunFunction(int pos, long hash, Contig thisContig);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Contig.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		Contig other = (Contig)obj;
		if (!other.id.equals(this.id)) {
			return false;
		}
		return true;
	}
}
