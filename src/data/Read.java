package data;

//TODO: remember to add list of unique kmers this read has. (use get/set)
public class Read {
  private RecordNode node;
  private int readIndex;

  public Read(RecordNode n, int readI) {
    node = n;
    readIndex = readI;
  }

  public RecordNode getNode() {
    return node;
  }

  public int getIndexRead() {
    return readIndex;
  }
}
