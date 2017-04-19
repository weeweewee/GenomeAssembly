package entry;

import java.util.*;
import java.io.*;
import data.*;
import data.FastaFastqReader.ValidReadMethod;

//TODO: can consider adding mapped positions to DS. allowing us to verify if
//its actually correct
public class ConnectedComponents {

  private TreeMap<Long, ArrayList<Read>> u30mers;
  // TODO: maybe nodes dun need node
  private HashMap<String, RecordNode> nodes;

  public ConnectedComponents(String f30mers, String fasta) {
    u30mers = new TreeMap<Long, ArrayList<Read>>();
    nodes = new HashMap<String, RecordNode>();

    try {
      BufferedReader kmerReader = new BufferedReader(new FileReader(f30mers));
      hash30mers(kmerReader);

      //TODO: preprocess nodes to check for what Unique Kmers they contain and 
      // add to u30mers. Also, need to maintain a list of Longs for each read
      // Can consider switching  DS of nodes. (whats the point now?, 
      // Looks like we need DS for reads instead of nodes)
      createNodes(fasta);

      connectNodes();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  private void connectNodes() {
    //TODO: switch from using nodes, to using sorted order of kmers
    //For all records with particular kmer, check only if they both don't
    //contain a kmer that is smaller than current one
    // Checking of kmer now more complicated cause we need best consensus on
    // indexDiff and close enough hamming distance
  	for (Long kmer : u30mers.keySet()) {
  		ArrayList<Read> containingKmers = u30mers.get(kmer);
  		for (int i = 0; i < containingKmers.size(); i++) {
  			for (int j = i + 1; j < containingKmers.size(); j++) {

  				//Do checks only if they dont both possess a same kmer smaller than current kmer.
  				//If hamming distance not too far, connect
  			}
  		}
  	}
    //for (String id: nodes.keySet()) {
    //  RecordNode thisNode = nodes.get(id);
    //  connectUKmers(thisNode, thisNode.getRead1(), 1);
    //  connectUKmers(thisNode, thisNode.getRead2(), 2);
    //}
  }

  private void connectUKmers(RecordNode node, String read, int readIndex) {
    int rolling = 0;
    int hashed = 0;
    for (int i = 0; i < read.length(); i++) {
      char currentChar = read.charAt(i);
      if (currentChar == Constants.AVAILABLE_CHARS[3]) {
        rolling = 0;
        hashed = 0;
        continue;
      }
      rolling <<= 2;
      rolling |= charToInteger(currentChar);
      hashed++;
      if (hashed >= 9) {
        //TODO: what about rc. Consider later.
        ArrayList<Read> nodesToCompare = 
          u30mers.get(rolling & Constants.NINE_MASK);
        for (int j = 0; j < nodesToCompare.size(); j++) {
          Read thisShareRead = nodesToCompare.get(j);
          // Find hamming between this 2 and check if they are close enough.
          // if they are, add edge between them
          
          // TODO: consider output all pairs that are not close enough
          // TODO: consider output all pairs that are close enough to verify
        }
        nodesToCompare.add(new Read(node, readIndex));
      }
    }

  }

  public void createNodes(String fasta) {
  	FastaFastqReader ir = new FastaFastqReader(fasta);

  	ir.Read(new ValidReadMethod() {
  		public void RunFunction(String id, String read1, String read2) {
  			RecordNode node = new RecordNode(id, read1, read2);
  			nodes.put(id, node);
  		}
  	});
  }

  private void hash30mers(BufferedReader kmerReader) throws IOException {
    String line;
    while ((line = kmerReader.readLine()) != null) {
      String[] lineArgs = line.split(Constants.SPACE_DELIMS);
      u30mers.put(calculateHash(lineArgs[0]), new ArrayList<Read>());
    }
  }

  private long calculateHash(String toHash) {
    long result = 0;
    for (int i = 0; i < toHash.length(); i++) {
      result <<= 2;
      result |= (charToInteger(toHash.charAt(i)));
    }
    return result;
  }

  private static int charToInteger(char c) {
    switch (c) {
      case 'A':
        return 0;
      case 'C':
        return 1;
      case 'G':
        return 2;
      case 'T':
        return 3;
      default:
        return -1;
    }
  }
  
  // Entry Point
  public static void main(String[] args) {
  }
}
