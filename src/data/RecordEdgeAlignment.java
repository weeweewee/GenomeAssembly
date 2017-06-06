package data;

public class RecordEdgeAlignment {
	private boolean isFirstNodeRead1;
	private boolean isSecondNodeRead1;
	private double weight;
	private int alignmentIndex;
	
	public RecordEdgeAlignment (boolean isFirstNodeR1, boolean isSecondNodeR1,
			double w, int aIndex) {
		isFirstNodeRead1 = isFirstNodeR1;
		isSecondNodeRead1 = isSecondNodeR1;
		weight = w;
		alignmentIndex = aIndex;
	}
	
	public boolean isFirstNodeReadOne() {
		return isFirstNodeRead1;
	}

	public boolean isSecondNodeReadOne() {
		return isSecondNodeRead1;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public int getAlignment() {
		return alignmentIndex;
	}
}
