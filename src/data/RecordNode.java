package data;

import java.util.*;

public class RecordNode extends Record {
  private ArrayList<RecordNode> neighbours;
  private boolean visited;

  public RecordNode(String id, String r1, String r2) {
    super(id, r1, r2);
    this.visited = false;
    neighbours = new ArrayList<RecordNode>();
  }

  public ArrayList<RecordNode> getNeighbours() {
    return neighbours;
  }

  public boolean isVisited() {
    return visited;
  }

  public void visited() {
    this.visited = true;
  }
}
