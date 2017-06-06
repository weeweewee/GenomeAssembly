package data;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import entry.ConnectedComponent;

public class ConnectedComponentContractionTask implements Runnable {
	
	private ArrayList<RecordEdge> edgesToContract;
	private ArrayList<RecordNode> newNodes;
	private ConcurrentHashMap<Long, ArrayList<Read>> u30mers;
  private ConcurrentHashMap<String, RecordNode> nodes;
	
	public ConnectedComponentContractionTask(ArrayList<RecordEdge> edges,
			ConcurrentHashMap<Long, ArrayList<Read>> existing30mers,
			ConcurrentHashMap<String, RecordNode> allNodes) {
		edgesToContract = edges;
		newNodes = new ArrayList<RecordNode>();
		u30mers = existing30mers;
		nodes = allNodes;
	}
	
	// Adds all new nodes to nodes. Useful for determining what's left after merging
	private void addAllNewUnmergedNodes() {
		for (int i = 0; i < newNodes.size(); i++) {
			RecordNode newNode = newNodes.get(i);
			if (!newNode.isMerged()) {
				nodes.put(newNode.getId(), newNode);
			}
		}
	}

	public void run() {
		System.out.println("StartContract");
  	while (true) {
  		System.out.println("edges left: " + edgesToContract.size());
  		// IF we always merge heaviest den no need to sort
  		// However, thats if we want to allow edges to be made with that intention
  		// (ie. edge may exist BUT not merged. Currently, all edges can be merged)
  		// (since we check hamming distance and overlap length)

  		// TODO: switch to sorted DS with O(logn) insert to reduce runtime
  		// Sort only once before while loop and insert OR just use a TreeSet
  		//Collections.sort(edges);
  		// TODO: quickfix can be done by instead of removing extra step, just
  		// remove here while iterating
  		double maxWeight = -1;
  		RecordEdge nextEdge = null;
  		for (int i = 0; i < edgesToContract.size(); i++) {
  			RecordEdge thisEdge = edgesToContract.get(i);
  			if (thisEdge.getWeight() > maxWeight) {
  				nextEdge = thisEdge;
  				maxWeight = thisEdge.getWeight();
  			}
  		}

  		//TODO: maybe need to be above min weight
  		if (maxWeight == -1) {
  			// This connected component can no longer be contracted
  			break;
  		}
  		
  		// Merge
  		RecordEdgeAlignment align1 = nextEdge.getAlignOne();
  		RecordEdgeAlignment align2 = nextEdge.getAlignTwo();

  		String mergedId = nextEdge.getFirstNode().getId() +
  				nextEdge.getSecondNode().getId() + "J";
  		String mergedReadS1 = combineOneRead(align1, nextEdge.getFirstNode(),
  				nextEdge.getSecondNode());
  		String mergedReadS2 = combineOneRead(align2, nextEdge.getFirstNode(),
  				nextEdge.getSecondNode());
  		RecordNode mergedNode = new RecordNode(mergedId, mergedReadS1, mergedReadS2);
  		
  		// Remove all edges from neighbors of merged nodes 
  		removeFromNeighboursAndEdgeList(nextEdge.getFirstNode());
  		removeFromNeighboursAndEdgeList(nextEdge.getSecondNode());

  		Read mergedRead1 = new Read(mergedNode, 1);
  		mergedNode.setRead1(mergedRead1);
  		mergedRead1.find30Mers(new UniqueKmerInReadFinder(false, u30mers));
  		Read mergedRead2 = new Read(mergedNode, 2);
  		mergedNode.setRead2(mergedRead2);
  		mergedRead2.find30Mers(new UniqueKmerInReadFinder(false, u30mers));
  		newNodes.add(mergedNode);
  		nextEdge.getFirstNode().merged();
  		nextEdge.getSecondNode().merged();
  		
  		// Update all edges to neighbors (still have references to them)
  		updateEdgesToNeighboursAndEdgeList(nextEdge, mergedNode);

  		// [optional: to free up memory]Remove 2 nodes from list of nodes, buckets of kmers 
  		// Or can just set flag that is merged so that we don't print
  		// Find a way to remove older nodes? To free up memory
  	}
  	addAllNewUnmergedNodes();
	}
		
  public String combineOneRead(RecordEdgeAlignment align, RecordNode n1, 
  		RecordNode n2) {
  	Read r1 = align.isFirstNodeReadOne()? n1.getRead1() : n1.getRead2();
  	Read r2 = align.isSecondNodeReadOne()? n2.getRead1() : n2.getRead2();
  	String r1String = r1.getIndexRead() == 1?
  			r1.getNode().getReadString1() : r1.getNode().getReadString2();
  	String r2String = r2.getIndexRead() == 1?
  			r2.getNode().getReadString1() : r2.getNode().getReadString2();

  	//System.out.println("joining: " + r1.getNode().getId());
  	//System.out.println("with: " + r2.getNode().getId());
  	return new ReadsJoiner(r1String, r2String).joinByAlignmentIndex(align.getAlignment());
  }
  
  private void removeFromNeighboursAndEdgeList(RecordNode thisNode) {
  	ArrayList<RecordEdge> neighbouringEdges = thisNode.getEdges();
  	for (int i = 0; i < neighbouringEdges.size(); i++) {
  		RecordEdge thisEdge = neighbouringEdges.get(i);
  		RecordNode otherNode = thisEdge.getFirstNode().equals(thisNode)?
  				thisEdge.getSecondNode() : thisEdge.getFirstNode();
  		otherNode.removeEdge(thisEdge);
  		edgesToContract.remove(thisEdge);
  	}
  }

  public void updateEdgesToNeighboursAndEdgeList(RecordEdge edgeMerged, RecordNode mergedNode) {
  	RecordNode firstNodeMerged = edgeMerged.getFirstNode();
  	RecordNode secondNodeMerged = edgeMerged.getSecondNode();
  	boolean isFirstReadFirstNodeMergedToFirstRead = 
  			edgeMerged.getAlignOne().isFirstNodeReadOne();
  	boolean isFirstReadSecondNodeMergedToFirstRead = 
  			edgeMerged.getAlignOne().isSecondNodeReadOne();
  	
  	//ArrayList<Read> firstNodeUpdatedWithRead = new ArrayList<Read>();
  	
  	// Trying to use the fact that previous edges contain information of
  	// which reads in record node were aligned well. Since we combined one of the
  	// two nodes, we need to (1) locate the new location of joined read. and (2)
  	// pair it with the information in old edge
  	for (int i = 0; i < firstNodeMerged.getEdges().size(); i++) {
  		RecordEdge nextEdge = firstNodeMerged.getEdges().get(i);
  		boolean isMergedFirstNode = nextEdge.getFirstNode().equals(firstNodeMerged);
  		RecordNode neighbour = isMergedFirstNode? nextEdge.getSecondNode() :
  			nextEdge.getFirstNode();
  		if (neighbour.equals(firstNodeMerged) || neighbour.equals(secondNodeMerged)) {
  			// Skip if already merged
  			continue;
  		}
  		
  		Read firstReadAlignOne = null;
  		Read secondReadAlignOne = null;
  		Read firstReadAlignTwo = null;
  		Read secondReadAlignTwo = null;
  		// Assignment works because both reads merged. One merged read uses
      // first node's first read, while the other uses first node's second read
  		// AND isMergedFirstNode never changes over an edge
  		if (isMergedFirstNode) {
  			if (nextEdge.getAlignOne().isFirstNodeReadOne()) {
  				if (isFirstReadFirstNodeMergedToFirstRead) {
  					firstReadAlignOne = mergedNode.getRead1();
  					firstReadAlignTwo = mergedNode.getRead2(); 
  				} else {
  					firstReadAlignOne = mergedNode.getRead2();
  					firstReadAlignTwo = mergedNode.getRead1(); 
  				}
  			} else {
  				if (isFirstReadFirstNodeMergedToFirstRead) {
  					firstReadAlignOne = mergedNode.getRead2();
  					firstReadAlignTwo = mergedNode.getRead1(); 
  				} else {
  					firstReadAlignOne = mergedNode.getRead1();
  					firstReadAlignTwo = mergedNode.getRead2(); 
  				}
  			}
  			secondReadAlignOne = nextEdge.getAlignOne().isSecondNodeReadOne()?
  					neighbour.getRead1() : neighbour.getRead2();
  			secondReadAlignTwo = nextEdge.getAlignOne().isSecondNodeReadOne()?
  					neighbour.getRead2() : neighbour.getRead1();
  		} else {
  			//neighbour is node one
  			if (nextEdge.getAlignOne().isSecondNodeReadOne()) {
  				if (isFirstReadFirstNodeMergedToFirstRead) {
  					secondReadAlignOne = mergedNode.getRead1();
  					secondReadAlignTwo = mergedNode.getRead2();
  				} else {
  					secondReadAlignOne = mergedNode.getRead2();
  					secondReadAlignTwo = mergedNode.getRead1();
  				}
  			} else {
  				if (isFirstReadFirstNodeMergedToFirstRead) {
  					secondReadAlignOne = mergedNode.getRead2();
  					secondReadAlignTwo = mergedNode.getRead1();
  				} else {
  					secondReadAlignOne = mergedNode.getRead1();
  					secondReadAlignTwo = mergedNode.getRead2();
  				}
  			}
  			firstReadAlignOne = nextEdge.getAlignOne().isFirstNodeReadOne()?
  					neighbour.getRead1() : neighbour.getRead2();
  			firstReadAlignTwo = nextEdge.getAlignOne().isFirstNodeReadOne()?
  					neighbour.getRead2() : neighbour.getRead1();
  		}
  		
  		//if (firstNodeUpdatedWithRead.contains(
  		//		isFirstReadFirstNodeMergedWithFirstReadNextNode?
  		//			neighbour.getRead1(): neighbour.getRead2())) {
  		//	// Already updated, skip
  		//	continue;
  		//}

  		RecordEdgeAlignment alignOne = ConnectedComponent.getScore(firstReadAlignOne,
  				secondReadAlignOne);
  		RecordEdgeAlignment alignTwo = ConnectedComponent.getScore(firstReadAlignTwo,
  				secondReadAlignTwo);

  		if (alignOne.getWeight() > 0 && alignTwo.getWeight() > 0) {
  		//	firstNodeUpdatedWithRead.add(isFirstReadFirstNodeMergedWithFirstReadNextNode?
  		//			neighbour.getRead1() : neighbour.getRead2());

  		  RecordEdge updatedEdge = new RecordEdge(isMergedFirstNode? mergedNode:
  		  	neighbour, isMergedFirstNode? neighbour : mergedNode, alignOne, 
  		  			alignTwo);
  		  edgesToContract.add(updatedEdge);
  		  mergedNode.addEdge(updatedEdge);
  		  neighbour.addEdge(updatedEdge);
  		}
  	}

  	//TODO: refactor these. [but try to set up test before]
  	// Basically, 2 almost exact for loops
  	for (int i = 0; i < secondNodeMerged.getEdges().size(); i++) {
  		RecordEdge nextEdge = secondNodeMerged.getEdges().get(i);
  		boolean isMergedFirstNode = nextEdge.getFirstNode().equals(secondNodeMerged);
  		RecordNode neighbour = isMergedFirstNode?
  				nextEdge.getSecondNode() : nextEdge.getFirstNode();
  		
  		if (neighbour.equals(firstNodeMerged) || neighbour.equals(secondNodeMerged)) {
  			// Skip if already merged
  			continue;
  		}
  		
  		Read firstReadAlignOne = null;
  		Read secondReadAlignOne = null;
  		Read firstReadAlignTwo = null;
  		Read secondReadAlignTwo = null;
  		// Assignment works because both reads merged. One merged read uses
      // first node's first read, while the other uses first node's second read
  		// AND isMergedFirstNode never changes over an edge
  		if (isMergedFirstNode) {
  			if (nextEdge.getAlignOne().isFirstNodeReadOne()) {
  				if (isFirstReadSecondNodeMergedToFirstRead) {
  					firstReadAlignOne = mergedNode.getRead1();
  					firstReadAlignTwo = mergedNode.getRead2(); 
  				} else {
  					firstReadAlignOne = mergedNode.getRead2();
  					firstReadAlignTwo = mergedNode.getRead1(); 
  				}
  			} else {
  				if (isFirstReadSecondNodeMergedToFirstRead) {
  					firstReadAlignOne = mergedNode.getRead2();
  					firstReadAlignTwo = mergedNode.getRead1(); 
  				} else {
  					firstReadAlignOne = mergedNode.getRead1();
  					firstReadAlignTwo = mergedNode.getRead2(); 
  				}
  			}
  			secondReadAlignOne = nextEdge.getAlignOne().isSecondNodeReadOne()?
  					neighbour.getRead1() : neighbour.getRead2();
  			secondReadAlignTwo = nextEdge.getAlignOne().isSecondNodeReadOne()?
  					neighbour.getRead2() : neighbour.getRead1();
  		} else {
  			//neighbour is node one
  			if (nextEdge.getAlignOne().isSecondNodeReadOne()) {
  				if (isFirstReadSecondNodeMergedToFirstRead) {
  					secondReadAlignOne = mergedNode.getRead1();
  					secondReadAlignTwo = mergedNode.getRead2();
  				} else {
  					secondReadAlignOne = mergedNode.getRead2();
  					secondReadAlignTwo = mergedNode.getRead1();
  				}
  			} else {
  				if (isFirstReadSecondNodeMergedToFirstRead) {
  					secondReadAlignOne = mergedNode.getRead2();
  					secondReadAlignTwo = mergedNode.getRead1();
  				} else {
  					secondReadAlignOne = mergedNode.getRead1();
  					secondReadAlignTwo = mergedNode.getRead2();
  				}
  			}
  			firstReadAlignOne = nextEdge.getAlignOne().isFirstNodeReadOne()?
  					neighbour.getRead1() : neighbour.getRead2();
  			firstReadAlignTwo = nextEdge.getAlignOne().isFirstNodeReadOne()?
  					neighbour.getRead2() : neighbour.getRead1();
  		}
  		RecordEdgeAlignment alignOne = ConnectedComponent.getScore(firstReadAlignOne, 
  				secondReadAlignOne);
  		RecordEdgeAlignment alignTwo = ConnectedComponent.getScore(firstReadAlignTwo, 
  				secondReadAlignTwo);

  		if (alignOne.getWeight() > 0 && alignTwo.getWeight() > 0) {
  		  RecordEdge updatedEdge = new RecordEdge(isMergedFirstNode? mergedNode:
  		  	neighbour, isMergedFirstNode? neighbour : mergedNode, alignOne, 
  		  			alignTwo);
  		  edgesToContract.add(updatedEdge);
  		  mergedNode.addEdge(updatedEdge);
  		  neighbour.addEdge(updatedEdge);
  		}
  	}

  	//System.out.println("size: " + listToAdd.size());
  	//for (int i = 0; i < listToAdd.size(); i++) {
  	//	System.out.print(listToAdd.get(i).getFirstNode().getId());
  	//	System.out.print(" to ");
  	//	System.out.println(listToAdd.get(i).getSecondNode().getId());
  	//	System.out.println(listToAdd.get(i).getWeight());
  	//}
  }
}
