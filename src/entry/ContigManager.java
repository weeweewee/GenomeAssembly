package entry;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import data.ConnectedContigContractionTask;
import data.Contig;
import data.ContigAlignmentEdge;
import data.UniqueKmerInContigFinder;
import data.UniqueKmerIndexFinder;

//TODO: concerned with memory of strings that are too long. Especially ids
//TODO: part of the reason why so many edges in this and CC is multiple edges between 2 nodes. see if can fix
public class ContigManager {

  public ConcurrentHashMap<Long, ArrayList<Contig>> u30mers;
  private ConcurrentHashMap<String, Contig> nodes;

  private static int contigId = 0;

  //Hard-coded value
  public static String hard_coded_trace = "@M01853:1:000000000-A3RU7:1:2107:19074:14234@M01853:1:000000000-A3RU7:1:1110:20941:22354J@M01853:1:000000000-A3RU7:1:1101:7878:19334J@M01853:1:000000000-A3RU7:1:2103:19702:11003@M01853:1:000000000-A3RU7:1:1109:11379:18009J@M01853:1:000000000-A3RU7:1:2107:11716:5341J@M01853:1:000000000-A3RU7:1:1113:12573:27286@M01853:1:000000000-A3RU7:1:2110:21155:19258J@M01853:1:000000000-A3RU7:1:1105:14491:26845@M01853:1:000000000-A3RU7:1:2108:11728:20998@M01853:1:000000000-A3RU7:1:1107:5002:21249@M01853:1:000000000-A3RU7:1:2113:24915:9801@M01853:1:000000000-A3RU7:1:2110:10627:17976@M01853:1:000000000-A3RU7:1:1101:5534:18910J@M01853:1:000000000-A3RU7:1:1109:18896:10088J@M01853:1:000000000-A3RU7:1:1103:10885:14384JJJ@M01853:1:000000000-A3RU7:1:1112:12029:14012JJ@M01853:1:000000000-A3RU7:1:1107:15452:12327@M01853:1:000000000-A3RU7:1:1107:21843:23022J@M01853:1:000000000-A3RU7:1:2113:4637:18367JJ@M01853:1:000000000-A3RU7:1:2104:6052:11200@M01853:1:000000000-A3RU7:1:1101:12550:25182J@M01853:1:000000000-A3RU7:1:1104:20183:6812@M01853:1:000000000-A3RU7:1:2113:21459:21810JJ@M01853:1:000000000-A3RU7:1:1103:23559:21324@M01853:1:000000000-A3RU7:1:1105:8311:24527JJ@M01853:1:000000000-A3RU7:1:1101:13853:6436@M01853:1:000000000-A3RU7:1:1106:17127:26349JJ@M01853:1:000000000-A3RU7:1:1113:13390:18311J@M01853:1:000000000-A3RU7:1:1107:14184:5570J@M01853:1:000000000-A3RU7:1:1114:7741:12654JJ@M01853:1:000000000-A3RU7:1:2107:10949:18704J@M01853:1:000000000-A3RU7:1:2113:14313:8728J@M01853:1:000000000-A3RU7:1:1111:12773:20088@M01853:1:000000000-A3RU7:1:2111:12155:24082J@M01853:1:000000000-A3RU7:1:1102:3588:11190JJ@M01853:1:000000000-A3RU7:1:1113:14004:26976J@M01853:1:000000000-A3RU7:1:1104:17806:9568J@M01853:1:000000000-A3RU7:1:1105:22473:23931@M01853:1:000000000-A3RU7:1:1102:19052:4657@M01853:1:000000000-A3RU7:1:2111:12814:7935J@M01853:1:000000000-A3RU7:1:1103:18203:17408JJJ@M01853:1:000000000-A3RU7:1:1112:9548:15988@M01853:1:000000000-A3RU7:1:1109:4958:20814@M01853:1:000000000-A3RU7:1:2101:9365:16229J@M01853:1:000000000-A3RU7:1:1104:3537:15338@M01853:1:000000000-A3RU7:1:1111:10454:13304JJ@M01853:1:000000000-A3RU7:1:1106:14510:11950@M01853:1:000000000-A3RU7:1:1108:24481:14066J@M01853:1:000000000-A3RU7:1:1104:13932:17441@M01853:1:000000000-A3RU7:1:1106:11636:19781@M01853:1:000000000-A3RU7:1:2103:18318:19579J@M01853:1:000000000-A3RU7:1:1114:12770:24260JJJJ@M01853:1:000000000-A3RU7:1:1110:8209:12845J@M01853:1:000000000-A3RU7:1:2108:16206:20319J@M01853:1:000000000-A3RU7:1:2105:7979:11955J@M01853:1:000000000-A3RU7:1:2108:15232:5617@M01853:1:000000000-A3RU7:1:1101:9559:18842@M01853:1:000000000-A3RU7:1:2112:20993:26492JJ@M01853:1:000000000-A3RU7:1:1102:6922:20175@M01853:1:000000000-A3RU7:1:1106:8398:15695@M01853:1:000000000-A3RU7:1:2109:24462:17144J@M01853:1:000000000-A3RU7:1:2102:15236:7524JJJ@M01853:1:000000000-A3RU7:1:2108:23693:5289J@M01853:1:000000000-A3RU7:1:1111:27940:13707J@M01853:1:000000000-A3RU7:1:2111:12675:13216J@M01853:1:000000000-A3RU7:1:1113:17703:7897J@M01853:1:000000000-A3RU7:1:1111:10848:19901J@M01853:1:000000000-A3RU7:1:1107:13766:7814J@M01853:1:000000000-A3RU7:1:1112:16386:5211@M01853:1:000000000-A3RU7:1:2107:14055:13579JJ@M01853:1:000000000-A3RU7:1:1111:26044:16141J@M01853:1:000000000-A3RU7:1:1101:13164:19502@M01853:1:000000000-A3RU7:1:1112:6917:13494JJ@M01853:1:000000000-A3RU7:1:2114:11429:6960J@M01853:1:000000000-A3RU7:1:2102:23781:18673@M01853:1:000000000-A3RU7:1:1110:27310:19140JJJ@M01853:1:000000000-A3RU7:1:2104:14860:6832J@M01853:1:000000000-A3RU7:1:2102:9909:11965@M01853:1:000000000-A3RU7:1:2101:19720:7005J@M01853:1:000000000-A3RU7:1:2111:10783:16195JJJ@M01853:1:000000000-A3RU7:1:1112:12587:7952CORJJJJJJ";
  public static ArrayList<String> trace1 = new ArrayList<String>();
  public static StringBuilder trace1String = new StringBuilder();
  public static ArrayList<String> trace2 = new ArrayList<String>();
  public static StringBuilder trace2String = new StringBuilder();
	
  // just call with u30mers.keyset() from ConnectedComponents 
  public ContigManager(Set<Long> f30mers) {
  	u30mers = new ConcurrentHashMap<Long, ArrayList<Contig>>();
  	nodes = new ConcurrentHashMap<String, Contig>();
  	for (long key: f30mers) {
  		u30mers.put(key, new ArrayList<Contig>());
  	}
  }
  
  public static String getNewContigId() {
  	return "@contig" + contigId++;
  }
  
  public static void addToTrace1(String id, String message) {
  	if (id != null) {
  		if (!trace1.contains(id)) {
  			trace1.add(id);
  		}
  	}
  	trace1String.append(message);
  }

  public static void addToTrace2(String id, String message) {
  	if (id != null) {
  		if (!trace2.contains(id)) {
  			trace2.add(id);
  		}
  	}
  	trace2String.append(message);
  }
  
  //TODO: add constructor to do everything from the start (ie. Read u30mers 
  // and read single reads to join them)
  
  // just call with all (unjoined + joined or just joined) nodes from ConnectedComponents
  public void addContig(String id, String read) {
  	// Don't use predetermined ids.
  	 //while (nodes.get(id) != null) {
  	//	 id += "b";
  	 //}
  	Contig toAdd = new Contig(getNewContigId(), read);
  	if (id.equals(hard_coded_trace)) {
  		if (trace1.size() == 0) {
  			trace1.add(toAdd.getId());
  		} else {
  			trace2.add(toAdd.getId());
  		}
  	}
  	nodes.put(toAdd.getId(), toAdd);
  	toAdd.find30Mers(new UniqueKmerInContigFinder(true, u30mers));
  }
  
  public void joinContigs() {
  	printStats();

    long startTime = System.currentTimeMillis();
  	connectGraph();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("connecting nodes took: " + totalTime/60000);
  	
    startTime = System.currentTimeMillis();
  	contractGraph();
    endTime = System.currentTimeMillis();
    totalTime = endTime - startTime;
    System.out.println("contracting graph took: " + totalTime/60000);
  	
  	System.out.println("contigs printing");
  	printContigs();
  }
  
  private void printStats() {
  	System.out.println("num contigs: " + nodes.size());
  }
  
  //TODO: need to do a version of comparedBefore (in CC) since it takes wayyy too long
  private void connectGraph() {
  	for (Long kmer : u30mers.keySet()) {
  		long kmerRC = Constants.REVERSE_COMPLEMENT_HASH_60(kmer);
  		if (kmer > kmerRC) {
  			// Only carry out kmer checks for smaller instance.
  			continue;
  		}
  		ArrayList<Contig> containingKmers = u30mers.get(kmer);
  		ArrayList<Contig> containingKmersRC = u30mers.get(kmerRC);

  		// Pairwise comparison of both lists.
  		for (int i = 0; i < containingKmers.size(); i++) {
  			Contig c1 = containingKmers.get(i);
  			for (int j = i+1; j < containingKmers.size(); j++) {
  				Contig c2 = containingKmers.get(j);
  				if (c1.equals(c2)) {
  					continue;
  				}
  				if (comparedBefore(c1, c2, kmer)) {
  					continue;
  				}
  				ContigAlignmentEdge e1 = getContigScore(c1,c2,false);
  				ContigAlignmentEdge e2 = getContigScore(c1,c2,true);
  				ContigAlignmentEdge betterEdge = null; 
  				if (e1.getWeight() >= e2.getWeight()) {
  					if (e1.getWeight() > 0) {
  						betterEdge = e1;
  					}
  				} else {
  					e2.setContig2(c2);
  					betterEdge = e2;
  				}
  				if (betterEdge == null) {
  					continue;
  				}
  				c1.addEdge(betterEdge);
  				c2.addEdge(betterEdge);
  			}
  		}
  		for (int i = 0; i < containingKmersRC.size(); i++) {
  			Contig c1 = containingKmersRC.get(i);
  			for (int j = i+1; j < containingKmersRC.size(); j++) {
  				Contig c2 = containingKmersRC.get(j);
  				if (c1.equals(c2)) {
  					continue;
  				}
  				if (comparedBefore(c1, c2, kmerRC)) {
  					continue;
  				}
  				ContigAlignmentEdge e1 = getContigScore(c1,c2,false);
  				ContigAlignmentEdge e2 = getContigScore(c1,c2,true);
  				ContigAlignmentEdge betterEdge = null; 
  				if (e1.getWeight() >= e2.getWeight()) {
  					if (e1.getWeight() > 0) {
  						betterEdge = e1;
  					}
  				} else {
  					e2.setContig2(c2);
  					betterEdge = e2;
  				}
  				if (betterEdge == null) {
  					continue;
  				}
  				c1.addEdge(betterEdge);
  				c2.addEdge(betterEdge);
  			}
  		}
  		for (int i = 0; i < containingKmers.size(); i++) {
  			Contig c1 = containingKmers.get(i);
  			for (int j = 0; j < containingKmersRC.size(); j++) {
  				Contig c2 = containingKmersRC.get(j);
  				if (c1.equals(c2)) {
  					continue;
  				}
  				if (comparedBefore(c1, c2.getRCContig(), kmer)) {
  					continue;
  				}
  				ContigAlignmentEdge e1 = getContigScore(c1,c2,false);
  				ContigAlignmentEdge e2 = getContigScore(c1,c2,true);
  				ContigAlignmentEdge betterEdge = null; 
  				if (e1.getWeight() >= e2.getWeight()) {
  					if (e1.getWeight() > 0) {
  						betterEdge = e1;
  					}
  				} else {
  					e2.setContig2(c2);
  					betterEdge = e2;
  				}
  				if (betterEdge == null) {
  					continue;
  				}
  				c1.addEdge(betterEdge);
  				c2.addEdge(betterEdge);
  			}
  		}
  	}
  }
  
  private boolean comparedBefore(Contig c1, Contig c2, long kmer) {
  	Vector<Long> c1Kmers = c1.getUniqueKmers();
  	Vector<Long> c2Kmers = c2.getUniqueKmers();
  	int i = 0, j = 0;
  	while (i < c1Kmers.size()) {
  		if (j >= c2Kmers.size()) {
  			break;
  		}
  		long c1kmer = c1Kmers.get(i);
  		long c2kmer = c2Kmers.get(j);
  		if (c1kmer >= kmer || c2kmer >= kmer) {
  			return false;
  		}
  		if (c1kmer > c2kmer) {
  			j++;
  			continue;
  		}
  		if (c1kmer < c2kmer) {
  			i++;
  			continue;
  		}
  		if (c1kmer == c2kmer) {
  			return true;
  		}
  	}
  	return false;
  	
  }
 
  // TOOD: fix logic error, call c2 with .getRCContig => Different node as nonRC
  // 2 ways. Link up the 2 contigs. (RC and non-RC)
  // Find a way such that we only use 1 contig => can preserve other details without dupping
  // Contigs. (but requires changing major logic here and contract)
  public static ContigAlignmentEdge getContigScore(Contig c1, Contig c2, boolean isc2RC) {
  	String c1String = c1.getRead();
  	String c2String = c2.getRead();
  	Contig newc2 = c2;
  	if (isc2RC) {
  		newc2 = c2.getRCContig();
  		c2String = Constants.REVERSE_COMPLEMENT(c2String);
  	}
  	ArrayList<Integer> indexDiffChecked = new ArrayList<Integer>();
  	int i = 0, j = 0;
  	double score = -1;
  	int indexDiff = -1;
  	while (i != c1.getUniqueKmers().size() && j != newc2.getUniqueKmers().size()) {
  		Long c1kmer = c1.getUniqueKmers().get(i);
  		Long c2kmer = newc2.getUniqueKmers().get(j);
  		if (c1kmer < c2kmer) {
  			i++;
  			continue;
  		} else if (c1kmer > c2kmer) {
  			j++;
  			continue;
  		}

  		//Align by said kmer
  		ArrayList<Integer> c1Indexes = new ArrayList<Integer>();
  		c1.find30Mers(new UniqueKmerIndexFinder(c1Indexes, c1kmer));
  		ArrayList<Integer> c2Indexes = new ArrayList<Integer>();
  		newc2.find30Mers(new UniqueKmerIndexFinder(c2Indexes, c2kmer));
  		
  		for (int a = 0; a < c1Indexes.size(); a++) {
  			for (int b = 0; b < c2Indexes.size(); b++) {
  				int index1 = c1Indexes.get(a);
  				int index2 = c2Indexes.get(b);
  				int s1 = 0, s2 = 0;
  				if (index1 > index2) {
  					s1 = index1 - index2;
  					s2 = 0;
  				} else if (index1 < index2) {
  					s1 = 0;
  					s2 = index2 - index1;
  				}

  				// Skip if checked before
  				if (indexDiffChecked.contains(s1 - s2)) {
  					continue;
  				}

  		    int diff = 0;
  				char ch1, ch2;
  				int d = s2;
  				for (int c = s1; c < c1String.length(); c++) {
  					if (d >= c2String.length()) {
  						break;
  					}
  					ch1 = c1String.charAt(c);
  					ch2 = c2String.charAt(d);
  		      if (ch1 != ch2 && ch1 != Constants.AVAILABLE_CHARS[3] && 
  		      		ch2 != Constants.AVAILABLE_CHARS[3]) {
  		        diff++;
  		      }
  		      d++;
  				}
  				int overlapLength = 0;
  				if (index1 > index2) {
  					overlapLength = Math.min(c1String.length() - s1, c2String.length());
  				} else {
  					overlapLength = Math.min(c2String.length() - s2, c1String.length());
  				}
  				
  				double hammingDistance = (double)diff / overlapLength;
  				// Only allow non-negative score if they fulfill criteria
  				if (hammingDistance < Constants.HAMMING_DISTANCE_THRESHOLD &&
  						overlapLength > Constants.MIN_CONTIG_OVERLAP_LENGTH) {
  					if (score < overlapLength * (1 - hammingDistance)) {
  						indexDiff = s1 - s2;
  						score = overlapLength * (1 -  hammingDistance);
  					}
  				}
  				
  				// Prevents rechecking of same coordinates
  				indexDiffChecked.add(s1 - s2);
  			}
  		}
  		i++;
  		j++;
  	}
  	return new ContigAlignmentEdge(c1, c2, score, indexDiff, isc2RC);
  }

  private void contractGraph() {
  	ThreadPoolExecutor exe = 
  			(ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.NUM_THREADS);
  	int exeCount = 0;

    for (String key: nodes.keySet()) {
      Contig thisNode = nodes.get(key);
      try {
      	//TODO: getting nullptr on this, means trace screwing up id that we add?
      	if (thisNode.isVisited()) {
      	  continue;
      	}
      } catch (NullPointerException e) {
      	System.out.println(key);
      	System.exit(0);
      }

      // Standard DFS to obtain connected components while adding all edges
      ArrayList<Contig> nodesConnected = new ArrayList<Contig>();
      Stack<Contig> nodesToVisit = new Stack<Contig>(); 
      thisNode.visited();
      nodesToVisit.push(thisNode);
      while (!nodesToVisit.isEmpty()) {
        Contig nextNode = nodesToVisit.pop();
        nodesConnected.add(nextNode);
        for (ContigAlignmentEdge e: nextNode.getEdges()) {
          Contig neighbour = e.getContig1().equals(nextNode)? 
            e.getContig2() : e.getContig1();
          if (!neighbour.isVisited()) {
            nodesToVisit.push(neighbour);
            neighbour.visited();
          }
        }
      }

      // Identified 1 connect component that is non-singleton 
      if (nodesConnected.size() > 1) {
      	ArrayList<ContigAlignmentEdge> connectingEdges = 
      			new ArrayList<ContigAlignmentEdge>();
      	for (int i = 0; i < nodesConnected.size(); i++) {
      		Contig currentNode = nodesConnected.get(i);
      		for (int j = 0; j < currentNode.getEdges().size(); j++) {
      			if (!connectingEdges.contains(currentNode.getEdges().get(j))) {
      				connectingEdges.add(currentNode.getEdges().get(j));
      			}
      		}
      	}
      	ConnectedContigContractionTask newTask = 
      			new ConnectedContigContractionTask(connectingEdges, u30mers, nodes);
      	System.out.println("executing: " + exeCount);
      	exeCount++;
      	exe.execute(newTask);
      } else {
      	System.out.println("singleton");
      }
    }
    exe.shutdown();
    try {
			while (!exe.awaitTermination(96L, TimeUnit.HOURS)) {
			  System.out.println("Not yet. Still waiting for termination");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  }
  
  private void resetKmers() {
  	// TODO: Free up memory since we don't intend ot use
  	// HOWEVER, not sure if set to null or clear all values.
  	// if clear all values, need to change get and null check to containsKey(..)
  	u30mers = null;
  }
  
  private void printContigs() {
    // print all contigs (from nodes) length > 200?
  	// can use all (after length check) since we remove in contractGraph
  	int nContigs = 0;
  	int maxContigLength = 0;
  	for (String id: nodes.keySet()) {
  		Contig thisContig = nodes.get(id);
  		System.out.println(thisContig.getId());
  		System.out.println(thisContig.getRead());
  		nContigs++;
  		maxContigLength = Math.max(maxContigLength, thisContig.getRead().length());
  	}
  	for (int i = 0; i < trace1.size(); i++) {
  		System.out.println("t1: " + i + " " + trace1.get(i));
  	}
  	System.out.println("trace1: " + trace1String.toString());
  	for (int i = 0; i < trace2.size(); i++) {
  		System.out.println("t2: " + i + " " + trace2.get(i));
  	}
  	System.out.println("trace2: " + trace2String.toString());
  	System.out.println("num output contigs: " + nContigs);
  	System.out.println("max length: " + maxContigLength);
  }
}
