package data;

import entry.Constants;

public class ReadsJoiner {
	private String read1;
	private String read2;

	public ReadsJoiner(String r1, String r2) {
		this.read1 = r1;
		this.read2 = r2;
	}
	
	public String joinByAlignmentIndex(int align) {
  	StringBuilder out = new StringBuilder();
  	int s1index = 0, s2index = 0;
  	if (align > 0) {
  		out.append(read1.substring(0, align));
  		s1index = align;
  	} else if (align < 0) {
  		out.append(read2.substring(0, -align));
  		s2index = -align;
  	}
  	
  	for (; s1index < read1.length() && s2index < read2.length();
  			s1index++, s2index++) {
  		char s1char = read1.charAt(s1index);
  		char s2char = read2.charAt(s2index);
  		//Defaults to s1char (in the case that s1char = s2char this will be toput)
  		char toPut = s1char;
  		if (s1char != s2char) {
  			if (s1char == Constants.AVAILABLE_CHARS[3]) {
  				toPut = s2char;
  			} else if (s2char == Constants.AVAILABLE_CHARS[3]) {
  				toPut = s1char;
  			} else {
  				// Both chars are not equal AND none of them are N's. Randomly choose.
  				// TODO: fix this by converting all usage of string to profiles
  				toPut = Math.random() < 0.5? s1char: s2char;
  			}
  		}
  		out.append(toPut);
  	}

  	if (s1index != read1.length()) {
  		out.append(read1.substring(s1index));
  	} else if (s2index != read2.length()) {
  		out.append(read2.substring(s2index));
  	}
  	return out.toString();
	}

}
