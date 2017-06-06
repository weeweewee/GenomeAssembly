package entry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import data.FastaFastqReader;
import data.FastaFastqReader.ValidReadMethod;
import data.Read;
import data.Read.Valid30merMethod;
import data.RecordNode;

public class RecordFilterUniqueKmer {

  public HashSet<Long> u30mers;
  
  public RecordFilterUniqueKmer(String f30mers, String fasta) {
    u30mers = new HashSet<Long>();
    try {
    	BufferedReader kmerReader = new BufferedReader(new FileReader(f30mers));
    	hash30mers(kmerReader);
    	
    	printRecordsContainingKmers(fasta);
    } catch (IOException e) {
    	System.out.println(e);
    }
  }
  
  private void printRecordsContainingKmers(String fasta) {
  	FastaFastqReader ir = new FastaFastqReader(fasta);
  	ir.Read(new ValidReadMethod() {
  		public void RunFunction(String id, String read1, String read2) {
  			RecordNode node = new RecordNode(id, read1, read2);
  			
  			UniqueKmerInReadNoter left = new UniqueKmerInReadNoter();
  			UniqueKmerInReadNoter right = new UniqueKmerInReadNoter();

  			Read r1 = new Read(node, 1);
  			node.setRead1(r1);
  			r1.find30Mers(left);

  			Read r2 = new Read(node, 2);
  			node.setRead2(r2);
  			r2.find30Mers(right);
  			
  			if (left.containsUniqueKmer() && right.containsUniqueKmer()) {
  				System.out.println(id);
  				System.out.println(read1);
  				System.out.println(id);
  				System.out.println(read2);
  			}
  		}
  	});
  }

  private void hash30mers(BufferedReader kmerReader) throws IOException {
    String line;
    while ((line = kmerReader.readLine()) != null) {
      String[] lineArgs = line.split(Constants.SPACE_DELIMS);
      u30mers.add(Constants.CALCULATE_HASH(lineArgs[0]));
      // Adds the RC of 30mer as well.
      u30mers.add(Constants.CALCULATE_HASH(Constants.REVERSE_COMPLEMENT(lineArgs[0])));
    }
  }
  
  public static void main(String[] args) {
  	RecordFilterUniqueKmer finder = new RecordFilterUniqueKmer(args[0], args[1]);
  }

  class UniqueKmerInReadNoter implements Valid30merMethod {
  	
  	private boolean containsUniqueKmer;
  	
  	public UniqueKmerInReadNoter() {
  		containsUniqueKmer = false;
  	}
  	
  	public boolean containsUniqueKmer() {
  		return containsUniqueKmer;
  	}

  	public void RunFunction(int pos, long hash, Read thisRead) {
  		if (u30mers.contains(hash)) {
  			containsUniqueKmer = true;
  		}
  	}
  }
}
