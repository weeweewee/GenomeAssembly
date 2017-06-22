package entry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.*;
import data.*;
import data.FastaFastqReader.ValidReadMethod;

//TODO: improve manual test case for more than 1 cc
//TODO: what if boths read are the same, or at least share kmer. what happens

//Currently, ukmer and ukmerRC do not group together (due to complications in 
// hamming distance etc.) However, all ukmer are grouped with ukmers, similarly
// with ukmerRCs.
//TODO: fix above issue

//TODO: consider switching ArrayList to vector for threadsafety
// for neighbours of nodes especially [for multithreading in connect nodes]
// Don't seem to need to do Vectors, since each node is only in 1 CC and one thread
// handles that. Will maybe need if handling for ConnectNodes BUT main bottleneck
// is now ContractConnectedComponents

//TODO: adding multithreaded features to speed up process. Takes too long currently
// connecting nodes alone takes 1hr. Do note that it must work well with RCs.
// ie. ConnectNodes multithreaded RCs slightly complicated

//TODO: use read profiles instead of strings!
public class ConnectedComponent {

	//TODO: u30mers unneeded after collecting nodes. Can consider freeing up memory

	// Doesnt need to be sorted keys, even though we combine so, because
	// eventually we'll reach the lowest key and try to connect nodes
  public ConcurrentHashMap<Long, ArrayList<Read>> u30mers;

  // TODO: maybe nodes dun need node [for now, useful for finding CC]
  private ConcurrentHashMap<String, RecordNode> nodes;
  
  public ConnectedComponent() {
  	// Empty constructor for testing member functions
    u30mers = new ConcurrentHashMap<Long, ArrayList<Read>>();
    nodes = new ConcurrentHashMap<String, RecordNode>();
  }

  public ConnectedComponent(String f30mers, String fasta) {
    u30mers = new ConcurrentHashMap<Long, ArrayList<Read>>();
    nodes = new ConcurrentHashMap<String, RecordNode>();

    try {
    	long startTime = System.currentTimeMillis();
      BufferedReader kmerReader = new BufferedReader(new FileReader(f30mers));
      hash30mers(kmerReader);
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println("hashing unique kmers took: " + totalTime/60000);
      
    	startTime = System.currentTimeMillis();
      createNodes(fasta);
      endTime = System.currentTimeMillis();
      totalTime = endTime - startTime;
      System.out.println("creating nodes took: " + totalTime/60000);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
  
  public void joinRecords() {
    long startTime = System.currentTimeMillis();
    connectNodes();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("connecting nodes took: " + totalTime/60000);
    
    //printStats();
    
    //clearAllVisitedNodes();
    
    // For decreasing order of weights above a certain amount, [OR just highest]
    // 	combine 2 nodes & remove nodes from buckets based on kmer 
    //  for new node found, recalculate the kmers AND all edges
    //  add all created DS to existing DS and repeat step 1.
    // When combined 2. Remember to remove nodes AND edges from everywhere 
    // this include neighbours
    startTime = System.currentTimeMillis();
    contractGraph();
    endTime = System.currentTimeMillis();
    totalTime = endTime - startTime;
    System.out.println("contracting graph took: " + totalTime/60000);

    //clearAllVisitedNodes();

    //printStats();

  	System.out.println("Printing all nodes");
    printAllNodes();
  }
  
  private void printAllNodes() {
  	for (String id: nodes.keySet()) {
  		RecordNode thisNode = nodes.get(id);
  		if (!thisNode.isMerged()) {
  			System.out.println(thisNode.toString());
  		}
  	}
  }
  
  private void clearAllVisitedNodes() {
  	for (RecordNode n : nodes.values()) {
  		n.clearVisited();
  	}
  }

  private void contractGraph() {
    // Find all connected components 
  	ThreadPoolExecutor exe = 
  			(ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.NUM_THREADS);
  	//ArrayList<ConnectedComponentContractionTask> allTasks =
  	//		new ArrayList<ConnectedComponentContractionTask>();
    for (String key: nodes.keySet()) {
      RecordNode thisNode = nodes.get(key);
      if (thisNode.isVisited()) {
        continue;
      }

      // Standard BFS to obtain connected components while adding all edges
      ArrayList<RecordNode> nodesConnected = new ArrayList<RecordNode>();
      Stack<RecordNode> nodesToVisit = new Stack<RecordNode>(); 
      thisNode.visited();
      nodesToVisit.push(thisNode);
      while (!nodesToVisit.isEmpty()) {
        RecordNode nextNode = nodesToVisit.pop();
        nodesConnected.add(nextNode);
        for (RecordEdge e: nextNode.getEdges()) {
          RecordNode neighbour = e.getFirstNode().equals(nextNode)? 
            e.getSecondNode() : e.getFirstNode();
          if (!neighbour.isVisited()) {
            nodesToVisit.push(neighbour);
            neighbour.visited();
          }
        }
      }

      // Identified 1 connect component that is non-singleton 
      if (nodesConnected.size() > 1) {
      	ArrayList<RecordEdge> connectingEdges = new ArrayList<RecordEdge>();
      	for (int i = 0; i < nodesConnected.size(); i++) {
      		RecordNode currentNode = nodesConnected.get(i);
      		for (int j = 0; j < currentNode.getEdges().size(); j++) {
      			if (!connectingEdges.contains(currentNode.getEdges().get(j))) {
      				connectingEdges.add(currentNode.getEdges().get(j));
      			}
      		}
      	}
      	System.out.println("contracting: " + connectingEdges.size());
      	ConnectedComponentContractionTask newTask = 
      			new ConnectedComponentContractionTask(connectingEdges, u30mers, nodes);
      	//allTasks.add(newTask);
      	exe.execute(newTask);
      }
    }
    exe.shutdown();
    try {
			while (!exe.awaitTermination(48L, TimeUnit.HOURS)) {
			  System.out.println("Not yet. Still waiting for termination");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  }
  
  private void printStats() {
    System.out.println("num nodes: " + nodes.size());

    int maxLengthRead = -1;
    int numComponents = 0;
    ArrayList<Integer> componentSizes = new ArrayList<Integer>();
    for (String key: nodes.keySet()) {
      RecordNode thisNode = nodes.get(key);
      if (thisNode.isVisited() || thisNode.isMerged()) {
        continue;
      }
      System.out.println("component " + numComponents);
      int thisComponentSize = 1;
      Stack<RecordNode> nodesToVisit = new Stack<RecordNode>(); 
      thisNode.visited();
      nodesToVisit.push(thisNode);
      while (!nodesToVisit.isEmpty()) {
        RecordNode nextNode = nodesToVisit.pop();
        
        System.out.println("id " + nextNode.getId());
        System.out.println("r1: " + nextNode.getReadString1());
        System.out.println("r2: " + nextNode.getReadString2());
        
        maxLengthRead = Math.max(maxLengthRead, Math.max(nextNode.getReadString1().length(),nextNode.getReadString2().length()));
        for (RecordEdge edge: nextNode.getEdges()) {
        	RecordNode neighbour = edge.getFirstNode().equals(nextNode) ?
        			edge.getSecondNode() : edge.getFirstNode();
          if (!neighbour.isVisited() && !neighbour.isMerged()) {
            nodesToVisit.push(neighbour);
            neighbour.visited();
            thisComponentSize++;
          }
        }
      }
      componentSizes.add(thisComponentSize);
      numComponents++;
    }
    System.out.println("Num components = " + numComponents);
    int maxSize = 0;
    int nonSingleton = 0;
    for (int i = 1; i <= numComponents; i++) {
      System.out.println("Component "+i+": size = "+componentSizes.get(i-1));
      maxSize = Math.max(maxSize, componentSizes.get(i-1));
      if (componentSizes.get(i-1) > 1) {
	      nonSingleton += componentSizes.get(i-1);
      }
    }
    System.out.println("Non-singletons= " + nonSingleton);
    System.out.println("Max Size = " + maxSize);
    System.out.println("longest read= " + maxLengthRead);
  }

  private void connectNodes() {
  	//int index = 0;
  	// For long operations to see if they are still runing 
  	//System.out.println("num unique 30mers: " + u30mers.size());
  	for (Long kmer : u30mers.keySet()) {
  		//index++;
  		ArrayList<Read> containingKmers = u30mers.get(kmer);
  		//System.out.println("index: " + index + " bucket size: " + containingKmers.size());
  		for (int i = 0; i < containingKmers.size(); i++) {
  			Read currentRead = containingKmers.get(i);
  			Read currentReadMate = currentRead.getIndexRead() == 1? 
  					currentRead.getNode().getRead2() : currentRead.getNode().getRead1();
  			for (int j = i + 1; j < containingKmers.size(); j++) {
  				Read nextRead = containingKmers.get(j);
  				Read nextReadMate = nextRead.getIndexRead() == 1? 
  					nextRead.getNode().getRead2() : nextRead.getNode().getRead1();

  				// Skip if they both contain a kmer smaller than current.
  				// That implies that it's been checked before
  				if (comparedBefore(currentRead, nextRead, kmer) || 
  						comparedBefore(currentReadMate, nextReadMate, kmer)) {
  					continue;
  				}

  				RecordEdgeAlignment align1 = getScore(currentRead, nextRead);
  			  RecordEdgeAlignment align2 = getScore(currentReadMate, nextReadMate);
  				if (align1.getWeight() > 0 && align2.getWeight() > 0) {
  					//Average out the scores to assign as weight of edge
  					//TODO: can consider average * (% overlap) to favor balanced overlap
  					RecordEdge edge = new RecordEdge(currentRead.getNode(), 
  							nextRead.getNode(), align1, align2);
  					currentRead.getNode().addEdge(edge);
  					nextRead.getNode().addEdge(edge);
  				}
  			}
  		}
  	}
  }
  
  public boolean comparedBefore(Read r1, Read r2, long kmer) {
  	ArrayList<Long> r1Kmers = r1.getUniqueKmers();
  	ArrayList<Long> r2Kmers = r2.getUniqueKmers();
  	int i = 0, j = 0;
  	while (i < r1Kmers.size()) {
  		if (j >= r2Kmers.size()) {
  			break;
  		}
  		long r1kmer = r1Kmers.get(i);
  		long r2kmer = r2Kmers.get(j);
  		if (r1kmer >= kmer || r2kmer >= kmer) {
  			return false;
  		}
  		if (r1kmer > r2kmer) {
  			j++;
  			continue;
  		}
  		if (r1kmer < r2kmer) {
  			i++;
  			continue;
  		}
  		if (r1kmer == r2kmer) {
  			return true;
  		}
  	}
  	return false;
  }
  
  // Returns #overlap * (1 - hamming distance) only if hamming distance < threshold AND
  // overlap > min_required else returns -1
  public static RecordEdgeAlignment getScore(Read r1, Read r2) {
  	String r1String = r1.getIndexRead() == 1?
  			r1.getNode().getReadString1() : r1.getNode().getReadString2();
  	String r2String = r2.getIndexRead() == 1?
  			r2.getNode().getReadString1() : r2.getNode().getReadString2();
  	ArrayList<Integer> indexDiffChecked = new ArrayList<Integer>();
  	int i = 0, j = 0;
  	double score = -1;
  	int indexDiff = -1;
  	while (i != r1.getUniqueKmers().size() && j != r2.getUniqueKmers().size()) {
  		Long r1kmer = r1.getUniqueKmers().get(i);
  		Long r2kmer = r2.getUniqueKmers().get(j);
  		if (r1kmer < r2kmer) {
  			i++;
  			continue;
  		} else if (r1kmer > r2kmer) {
  			j++;
  			continue;
  		}

  		//Align by said kmer
  		ArrayList<Integer> r1Indexes = new ArrayList<Integer>();
  		r1.find30Mers(new UniqueKmerIndexFinder(r1Indexes, r1kmer));
  		ArrayList<Integer> r2Indexes = new ArrayList<Integer>();
  		r2.find30Mers(new UniqueKmerIndexFinder(r2Indexes, r2kmer));
  		
  		for (int a = 0; a < r1Indexes.size(); a++) {
  			for (int b = 0; b < r2Indexes.size(); b++) {
  				int index1 = r1Indexes.get(a);
  				int index2 = r2Indexes.get(b);
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
  				char c1, c2;
  				int d = s2;
  				for (int c = s1; c < r1String.length(); c++) {
  					if (d >= r2String.length()) {
  						break;
  					}
  					c1 = r1String.charAt(c);
  					c2 = r2String.charAt(d);
  		      if (c1 != c2 && c1 != Constants.AVAILABLE_CHARS[3] && 
  		      		c2 != Constants.AVAILABLE_CHARS[3]) {
  		        diff++;
  		      }
  		      d++;
  				}
  				int overlapLength = 0;
  				if (index1 > index2) {
  					overlapLength = Math.min(r1String.length() - s1, r2String.length());
  				} else {
  					overlapLength = Math.min(r2String.length() - s2, r1String.length());
  				}

  				double hammingDistance = (double)diff / overlapLength;
  				// Only allow non-negative score if they fulfill criteria
  				if (hammingDistance < Constants.HAMMING_DISTANCE_THRESHOLD &&
  						overlapLength > Constants.MIN_OVERLAP_LENGTH) {
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
  	return new RecordEdgeAlignment(r1.getIndexRead() == 1, r2.getIndexRead() == 1,
  			score, indexDiff);
  }

  public void createNodes(String fasta) {
  	//TODO: remove hardcoded true here for bypassing
  	FastaFastqReader ir = new FastaFastqReader(fasta, false);

  	ir.Read(new ValidReadMethod() {
  		public void RunFunction(String id, String read1, String read2) {
  			RecordNode node = new RecordNode(id, read1, read2);
  			nodes.put(id, node);
  			
  			// Run rolling hash to add all Unique Kmers in each read.
  			// Add to grouping by Unique Kmers as well as Unique Kmers to read itself
  			Read r1 = new Read(node, 1);
  			node.setRead1(r1);
  			r1.find30Mers(new UniqueKmerInReadFinder(true, u30mers));
  			Read r2 = new Read(node, 2);
  			node.setRead2(r2);
  			r2.find30Mers(new UniqueKmerInReadFinder(true, u30mers));
  		}
  	});
  }
  
  private void hash30mers(BufferedReader kmerReader) throws IOException {
    String line;
    while ((line = kmerReader.readLine()) != null) {
      String[] lineArgs = line.split(Constants.SPACE_DELIMS);
      u30mers.put(Constants.CALCULATE_HASH(lineArgs[0]), new ArrayList<Read>());
      // Adds the RC of 30mer as well.
      u30mers.put(Constants.CALCULATE_HASH(Constants.REVERSE_COMPLEMENT(lineArgs[0])),
      		new ArrayList<Read>());
    }
  }
  
  public Set<Long> getUniqueKmers() {
  	return u30mers.keySet();
  }
  
  public void initContigManager(ContigManager cm, boolean onlyJoined) {
  	for (String id: nodes.keySet()) {
  		RecordNode thisNode = nodes.get(id);
  		
  		if (!thisNode.isMerged()) {
  			if (onlyJoined) {
  				if (thisNode.getId().contains("J")) {
  					cm.addContig(thisNode.getId(), thisNode.getReadString1());
  					cm.addContig(thisNode.getId(), thisNode.getReadString2());
  				}
  			} else {
  				cm.addContig(thisNode.getId(), thisNode.getReadString1());
  				cm.addContig(thisNode.getId(), thisNode.getReadString2());
  			}
  		}
  	}
  }
  
  // Entry Point
  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
  	ConnectedComponent cc = new ConnectedComponent(args[0], args[1]);
  	// Comment out line below for immediate processing of contigs (ie. single end instead
  	// of paired end)
  	cc.joinRecords();
  	ContigManager cm = new ContigManager(cc.getUniqueKmers());
  	cc.initContigManager(cm, true);
  	// Frees up memory
  	cc = null;
  	cm.joinContigs();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("total time taken: " + totalTime/60000);
  }
}
