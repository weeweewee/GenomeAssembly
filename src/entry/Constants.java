package entry;

public class Constants {

  public static char[] AVAILABLE_CHARS = {'A', 'C', 'G', 'N', 'T'};
  
  public static char[] COMPLEMENT_CHARS = {'T', 'G', 'C', 'A'};

  public static int NINE_MASK = (1 << 18) - 1;

  public static int NUM_THREADS = 24;

  public static long THIRTY_MASK = (1L << 60) - 1;

  public static String SPACE_DELIMS = "\\s+";

  public static int CHAR_TO_INTEGER(char c) {
    switch (c) {
      case 'A':
        return 0;
      case 'C':
        return 1;
      case 'G':
        return 2;
      case 'T':
        return 3;
      default:
        return -1;
    }
  }
  
  public static String REVERSE_COMPLEMENT(String toRC) {
  	StringBuilder rc = new StringBuilder();
  	for (int i = toRC.length()-1; i >= 0; i--) {
  		int index = CHAR_TO_INTEGER(toRC.charAt(i));
  		char toPut = AVAILABLE_CHARS[3];
  		if (index != -1) {
  			toPut = COMPLEMENT_CHARS[index];
  		}
  		rc.append(toPut);
  	}
  	return rc.toString();
  }
  
  public static long REVERSE_COMPLEMENT_HASH_60(long in) {
  	long out = 0l;
  	for (int i = 0; i < 30; i++) {
  		int cur = (int)in & 3;
  		out = out << 2;
  		out |= CHAR_TO_INTEGER(COMPLEMENT_CHARS[cur]);
  		in = in >> 2;
  	}
  	return out;
  }
  
  // Only allows max length 31
  public static long CALCULATE_HASH(String toHash) {
    long result = 0;
    for (int i = 0; i < toHash.length(); i++) {
      result <<= 2;
      if (toHash.charAt(i) == AVAILABLE_CHARS[3]) {
      	return -1;
      }
      result |= (CHAR_TO_INTEGER(toHash.charAt(i)));
    }
    return result;
  }
  
  public static double HAMMING_DISTANCE_THRESHOLD = 0.04f;
  
  // Reason: For overlap of 30mer, min overlap already 30. Hence, require a bit more assurance
  // If all 5 others are different, hamming distance too high => won't join
  public static int MIN_OVERLAP_LENGTH = 35;
 
  public static int MIN_CONTIG_OVERLAP_LENGTH = 120;
}
