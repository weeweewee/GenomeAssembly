package data;

import entry.Constants;
import entry.ContigManager;

public class ContigAlignmentEdge {

	private double weight;
	private Contig contig1;
	private Contig contig2;
	private int alignmentIndex;
	private boolean isContig2RC;
	
	public ContigAlignmentEdge(Contig c1, Contig c2, double w, int aIndex, boolean isC2RC) {
		contig1 = c1;
		contig2 = c2;
		weight = w;
		alignmentIndex = aIndex;
		isContig2RC = isC2RC;
	}
	
	// Used to overwrite contig2
	public void setContig2(Contig newContig2) {
		contig2 = newContig2;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public boolean isContig2RC() {
		return isContig2RC;
	}
	
	public Contig getContig1() {
		return contig1;
	}

	public Contig getContig2() {
		return contig2;
	}
	
	public Contig contract(int trace1) {
		//String newId = contig1.getId() + contig2.getId() + "C";
		String newId = ContigManager.getNewContigId();
		String newRead = new ReadsJoiner(contig1.getRead(), isContig2RC? Constants.REVERSE_COMPLEMENT(contig2.getRead()):
																		contig2.getRead()).joinByAlignmentIndex(alignmentIndex);
		if (trace1 == 1) {
			ContigManager.addToTrace1(null, "\n read1 id: " + contig1.getId());
			ContigManager.addToTrace1(null, "\n read1: " + contig1.getRead());
			ContigManager.addToTrace1(null, "\n read2 id: " + contig2.getId());
			if (isContig2RC) {
				ContigManager.addToTrace1(null, "\n read2RC: " + Constants.REVERSE_COMPLEMENT(contig2.getRead()));
			} else {
				ContigManager.addToTrace1(null, "\n read2: " + contig2.getRead());
			}
			ContigManager.addToTrace1(null, "\n index: " + alignmentIndex);
			ContigManager.addToTrace1(null, "\n weight: " + weight);
		} else if (trace1 == 2) {
			ContigManager.addToTrace2(null, "\n read1 id: " + contig1.getId());
			ContigManager.addToTrace2(null, "\n read1: " + contig1.getRead());
			ContigManager.addToTrace2(null, "\n read2 id: " + contig2.getId());
			if (isContig2RC) {
				ContigManager.addToTrace2(null, "\n read2RC: " + Constants.REVERSE_COMPLEMENT(contig2.getRead()));
			} else {
				ContigManager.addToTrace2(null, "\n read2: " + contig2.getRead());
			}
			ContigManager.addToTrace2(null, "\n index: " + alignmentIndex);
			ContigManager.addToTrace2(null, "\n weight: " + weight);
		}

		return new Contig(newId, newRead);
	}
	
	//@Override
	//public boolean equals(Object otherEdge) {
	//	if (!otherEdge.getClass().equals(this.getClass())) {
	//		return false;
	//	}
	//	
	//	ContigAlignmentEdge edge2 = (ContigAlignmentEdge)otherEdge;
	//	if ((edge2.getContig1().getId().equals(getContig1().getId()) && 
	//			edge2.getContig2().getId().equals(getContig2().getId())) || 
	//			(edge2.getContig2().getId().equals(getContig1().getId()) && 
	//			edge2.getContig1().getId().equals(getContig2().getId()))) {
	//		return true;
	//	}
	//	return false;
	//}
}
