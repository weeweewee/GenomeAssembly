package data;

import java.util.*;

public class RecordNode extends Record {
  private ArrayList<RecordEdge> edges;
  private boolean visited;
  private boolean merged;

  public RecordNode(String id, String r1, String r2) {
    super(id, r1, r2);
    visited = false;
    merged = false;
    edges = new ArrayList<RecordEdge>();
  }
  
  public boolean isMerged() {
  	return merged;
  }
  
  public void merged() {
  	merged = true;
  	// Clears reads to free up some memory
  	super.setReadString1("");
  	super.setReadString2("");
  }

  public ArrayList<RecordEdge> getEdges() {
    return edges;
  }
  
  public void removeEdge(RecordEdge edge) {
  	edges.remove(edge);
  }
  
  public void addEdge(RecordEdge e) {
  	edges.add(e);
  }

  public boolean isVisited() {
    return visited;
  }

  public void clearVisited() {
    visited = false;
  }

  public void visited() {
    this.visited = true;
  }
}
