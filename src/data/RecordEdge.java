package data;

public class RecordEdge implements Comparable<RecordEdge> {
  private RecordNode node1;
  private RecordNode node2;
  private RecordEdgeAlignment align1;
  private RecordEdgeAlignment align2;

  public RecordEdge(RecordNode n1, RecordNode n2, RecordEdgeAlignment a1,
  		RecordEdgeAlignment a2) {
    node1 = n1;
    node2 = n2;
    align1 = a1;
    align2 = a2;
  }

  public RecordNode getFirstNode() {
    return node1;
  }

  public RecordNode getSecondNode() {
    return node2;
  }
  
  public double getWeight() {
  	return (align1.getWeight() + align2.getWeight())/2;
  }
  
  public RecordEdgeAlignment getAlignOne() {
  	return align1;
  }
  
  public RecordEdgeAlignment getAlignTwo() {
  	return align2;
  }

  @Override
  public int compareTo(RecordEdge other) {
    // Sorts in descending order of weight based on alignment score
    return (int)(other.getWeight() - getWeight());
  }
}
