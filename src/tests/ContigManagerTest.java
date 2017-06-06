package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import data.Contig;
import data.ContigAlignmentEdge;
import entry.Constants;
import entry.ContigManager;

public class ContigManagerTest {

	@Test
	public void testGetContigScore() {
		// Refer to ConnectedComponents test for more getScore test (only minor differences)
		Contig c1 = new Contig("id1", "GTAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC");
		c1.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		Contig c2 = new Contig("id2", "TAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAACA");
		c2.addUniqueKmer(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		ContigAlignmentEdge edge = ContigManager.getContigScore(c1, c2, true);
		
		assertEquals(37L, edge.getWeight(), 0.01);
		assertEquals(c1, edge.getContig1());
		assertEquals(c2, edge.getContig2());
		assertTrue(edge.isContig2RC());
	}

}
