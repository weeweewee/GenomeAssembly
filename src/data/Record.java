package data;

public class Record {
  private String id;
  private String readString1;
  private String readString2;
  private Read read1;
  private Read read2;

  public Record(String id, String r1, String r2) {
    this.id = id;
    this.readString1 = r1;
    this.readString2 = r2;
  }

  public String getId() {
    return id;
  }

  public String getReadString1() {
    return readString1;
  }

  public void setReadString1(String newRead1) {
  	readString1 = newRead1;
  }

  public String getReadString2() {
    return readString2;
  }

  public void setReadString2(String newRead2) {
  	readString2 = newRead2;
  }
  
  public void setRead1(Read r1) {
  	read1 = r1;
  }

  public Read getRead1() {
  	return read1;
  }

  public void setRead2(Read r2) {
  	read2 = r2;
  }

  public Read getRead2() {
  	return read2;
  }
  
  @Override
  public String toString() {
  	StringBuilder sb = new StringBuilder();
  	sb.append(id);
  	sb.append(System.getProperty("line.separator"));
  	sb.append(readString1);
  	sb.append(System.getProperty("line.separator"));
  	sb.append(id);
  	sb.append(System.getProperty("line.separator"));
  	sb.append(readString2);
  	
  	return sb.toString();
  }
}