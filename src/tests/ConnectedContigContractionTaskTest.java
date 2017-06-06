package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import data.ConnectedContigContractionTask;
import data.Contig;
import data.ContigAlignmentEdge;
import entry.Constants;
import entry.ContigManager;

public class ConnectedContigContractionTaskTest {

	//NOTE: test fails due to update in MIN_OVERLAP_LENGTH (intended for 35)
	@Test
	public void testUpdateEdges() {
		Contig merged = new Contig("merged", "CCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGTTTTTTTTTT");
		merged.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));
		merged.addUniqueKmer(Constants.CALCULATE_HASH("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG"));

		Contig c1 = new Contig("id1", "CCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGG");
		c1.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));
		c1.addUniqueKmer(Constants.CALCULATE_HASH("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG"));

		Contig c2 = new Contig("id2", "AAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGTTTTTTTTTT");
		c2.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));

		Contig c3 = new Contig("id3", "CCCCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGG");
		c3.addUniqueKmer(Constants.CALCULATE_HASH("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG"));
		
		ContigAlignmentEdge mergedEdge = ContigManager.getContigScore(c1, c2, false);
		c1.addEdge(mergedEdge);
		c2.addEdge(mergedEdge);

		ContigAlignmentEdge otherEdge = ContigManager.getContigScore(c1, c3, false);
		c1.addEdge(otherEdge);
		//Not added, since in algorithm, we remove prior to running update edges
		//c3.addEdge(otherEdge);

		ArrayList<ContigAlignmentEdge> listToVerify = new ArrayList<ContigAlignmentEdge>();
		ConnectedContigContractionTask task = 
				new ConnectedContigContractionTask(listToVerify, null, null);
		task.updateEdges(c1, merged, mergedEdge);

		assertEquals(1, merged.getEdges().size());
		assertEquals(c3, merged.getEdges().get(0).getContig2());
		assertFalse(merged.getEdges().get(0).isContig2RC());
		assertEquals(1, listToVerify.size());
	}

	//NOTE: Test fails due to update in MIN OVERLAP LENGTH
	@Test
	public void testUpdateEdgesRC() {
		Contig merged = new Contig("merged", "CCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGTTTTTTTTTT");
		merged.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));
		merged.addUniqueKmer(Constants.CALCULATE_HASH("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG"));

		Contig c1 = new Contig("id1", "CCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGG");
		c1.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));
		c1.addUniqueKmer(Constants.CALCULATE_HASH("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG"));

		Contig c2 = new Contig("id2", "AAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGTTTTTTTTTT");
		c2.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAGGGGGGGGGGGGGGG"));

		Contig c3 = new Contig("id3", 
				Constants.REVERSE_COMPLEMENT("CCCCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAAAAAGGGGGGGGGG"));
		c3.addUniqueKmer(Constants.CALCULATE_HASH(Constants.REVERSE_COMPLEMENT("CCCCCAAAAAAAAAAAAAAAAAAAAGGGGG")));
		
		ContigAlignmentEdge mergedEdge = ContigManager.getContigScore(c1, c2, false);
		c1.addEdge(mergedEdge);
		c2.addEdge(mergedEdge);

		ContigAlignmentEdge otherEdge = ContigManager.getContigScore(c1, c3, true);
		c1.addEdge(otherEdge);
		//Not added, since in algorithm, we remove prior to running update edges
		//c3.addEdge(otherEdge);

		ArrayList<ContigAlignmentEdge> listToVerify = new ArrayList<ContigAlignmentEdge>();
		ConnectedContigContractionTask task = 
				new ConnectedContigContractionTask(listToVerify, null, null);
		task.updateEdges(c1, merged, mergedEdge);

		assertEquals(1, merged.getEdges().size());
		// Compares id because edge is tied to c3.RC. In code, this contig2 is overwritten
		assertEquals("id3", merged.getEdges().get(0).getContig2().getId());
		assertTrue(merged.getEdges().get(0).isContig2RC());
		assertEquals(1, listToVerify.size());
	}
}
