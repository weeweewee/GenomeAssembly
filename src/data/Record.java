package data;

public class Record {
  private String id;
  private String read1;
  private String read2;

  public Record(String id, String r1, String r2) {
    this.id = id;
    this.read1 = r1;
    this.read2 = r2;
  }

  public String getId() {
    return id;
  }

  public String getRead1() {
    return read1;
  }

  public String getRead2() {
    return read2;
  }
}
