package data;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import entry.ContigManager;

public class ConnectedContigContractionTask implements Runnable {
	
	private ConcurrentHashMap<Long, ArrayList<Contig>> u30mers;
	private ConcurrentHashMap<String, Contig> nodes;
	//TODO: consider using set instead of arraylist to prevent same edges.
	// IIRC i need to override the .equals function
	//private HashSet<ContigAlignemntEdge> edgesToContract2;
	private ArrayList<ContigAlignmentEdge> edgesToContract;
	
	public ConnectedContigContractionTask(ArrayList<ContigAlignmentEdge> edges,
			ConcurrentHashMap<Long, ArrayList<Contig>> existing30mers,
			ConcurrentHashMap<String, Contig> allNodes) {
		u30mers = existing30mers;
		nodes = allNodes;
		edgesToContract = edges;
	}
	
	public void run() {
		System.out.println("StartContract");
  	while (true) {
  		System.out.println("edges left: " + edgesToContract.size());

  		double maxWeight = -1;
  		ContigAlignmentEdge nextEdge = null;
  		for (int i = 0; i < edgesToContract.size(); i++) {
  			ContigAlignmentEdge thisEdge = edgesToContract.get(i);
  			if (thisEdge.getWeight() > maxWeight) {
  				nextEdge = thisEdge;
  				maxWeight = thisEdge.getWeight();
  			}
  		}
  		
  		if (maxWeight == -1) {
  			// This connected component can no longer be contracted
  			break;
  		}
  		
  		//System.out.println(nextEdge.getContig1().getId() + ": " + nextEdge.getContig1().getRead());
  		//System.out.println(nextEdge.getContig2().getId() + ": " + nextEdge.getContig2().getRead());
  		
  		Contig merged = null;

  		boolean alreadyMerged = false;
  		if (ContigManager.trace1.contains(nextEdge.getContig1().getId()) || 
  				ContigManager.trace1.contains(nextEdge.getContig2().getId())) {
  			// Merge
  			merged = nextEdge.contract(1);
  			ContigManager.addToTrace1(merged.getId(), "\n merged to: " + merged.getId());
  			alreadyMerged = true;
  		} 
  		if (ContigManager.trace2.contains(nextEdge.getContig1().getId()) || 
  				ContigManager.trace2.contains(nextEdge.getContig2().getId())) {
  			// Merge
  			if (!alreadyMerged) {
  				merged = nextEdge.contract(2);
  				alreadyMerged = true;
  			}
  			ContigManager.addToTrace2(merged.getId(), "\n merged to: " + merged.getId());
  		}
  			
  		if (!alreadyMerged) {
  			merged = nextEdge.contract(0);
  		}
  		
  		//System.out.println("Merged: " + nextEdge.getContig1().getId() + " " + nextEdge.getContig2().getId());
  		//System.out.println("to: " + merged.getId());

  		merged.find30Mers(new UniqueKmerInContigFinder(false, u30mers));

  		//Remove all edges for contracted nodes
  		removeSelectedEdge(nextEdge);
  		removeFromNeighboursAndEdgeList(nextEdge.getContig1());
  		removeFromNeighboursAndEdgeList(nextEdge.getContig2());
  		
  		//Recreate all edges
  		updateEdges(nextEdge.getContig1(), merged, nextEdge);
  		updateEdges(nextEdge.getContig2(), merged, nextEdge);

  		//Remove all edges on merged nodes. Helps with memory
  		nextEdge.getContig1().resetEdges();
  		nextEdge.getContig2().resetEdges();
  		
  		//Edit global DS on nodes
  		nodes.put(merged.getId(), merged);
  		nodes.remove(nextEdge.getContig1().getId());
  		nodes.remove(nextEdge.getContig2().getId());
  	}
	}
	
	public void updateEdges(Contig contig, Contig newContig, ContigAlignmentEdge mergedEdge) {
		boolean isContigMergedFirst = mergedEdge.getContig1().equals(contig);
		boolean isSecondContigMergedRC = mergedEdge.isContig2RC();

  	Vector<ContigAlignmentEdge> neighbouringEdges = contig.getEdges();
		for (int i = 0; i < neighbouringEdges.size(); i++) {
  		ContigAlignmentEdge thisEdge = neighbouringEdges.get(i);
  		Contig otherContig = thisEdge.getContig1().equals(contig)?
  				thisEdge.getContig2() : thisEdge.getContig1();
  		if (otherContig.equals(mergedEdge.getContig1()) ||
  				otherContig.equals(mergedEdge.getContig2())) {
  			continue;
  		}
  		boolean isSecondContigRC = thisEdge.isContig2RC();
  		ContigAlignmentEdge newEdge = null;

  		// Previous contig was RCed. Therefore any existing edges must factor this into consideration
  		if (!isContigMergedFirst && isSecondContigMergedRC) {
  			// Since we know that this contig was RCed, just invert RC status on previous edge
  			newEdge = ContigManager.getContigScore(newContig, otherContig, !isSecondContigRC);
  		} else {
  			newEdge = ContigManager.getContigScore(newContig, otherContig, isSecondContigRC);
  		}

  		if (newEdge.getWeight() > -1) {
  			otherContig.addEdge(newEdge);
  			newContig.addEdge(newEdge);
  			edgesToContract.add(newEdge);
  		}
		}
	}
	
	private void removeFromNeighboursAndEdgeList(Contig contig) {
  	Vector<ContigAlignmentEdge> neighbouringEdges = contig.getEdges();
		for (int i = 0; i < neighbouringEdges.size(); i++) {
  		ContigAlignmentEdge thisEdge = neighbouringEdges.get(i);
  		Contig otherContig = thisEdge.getContig1().equals(contig)?
  				thisEdge.getContig2() : thisEdge.getContig1();
			
  		//TODO: looks like some edges are not removed. Maybe due to Contig.RC
  		//instead of contig (ie. both kinds of nodes?)
  		while (otherContig.removeEdge(thisEdge)) {
  		}
  		while (edgesToContract.remove(thisEdge)) {
  		}
		}
	}
	
	private void removeSelectedEdge(ContigAlignmentEdge e) {
		while (edgesToContract.remove(e)) {
		}
	}
}
