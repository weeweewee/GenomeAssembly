package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import data.Read;
import data.RecordEdgeAlignment;
import data.RecordNode;
import entry.Constants;
import entry.ConnectedComponent;

public class ConnectedComponentsTest {

	//TODO: test the case where 1 unique kmer at more than 1 position
	//TODO: test the case where got more than 1 unique kmer at diff position 
	//TODO: test the case where got N's

	@Test
	public void testGetScoreLongReads() {
		RecordNode n1 = new RecordNode("id1", "GGTTGAAAGAGGCAGAATTAAAACCTCGTAAATTG"
				+ "AAATATATATTGATGTAGTGAATGTATCTTAGGTAAATAATATATATTATTTATTTAACGATTTATAG"
				+ "CAACTCAATATTAGCCTCTCGTATAAATACACATTAGGTGATAGATTAACCTTCGCTATTTTCTCACT"
				+ "CTGTGTCGAATATATTTATTTCCTGAATAATTAATCATGGCAAAAAGAACCAAAGCCGAAGCTCTGAA"
				+ "GACCCGGCAAGAACTGATTGAA", "");

		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAATTGAAATATATATTGATGTAGTGAATG"));
		
		RecordNode n2 = new RecordNode("id2", "CTCCCTTATCGTTACAACCGGCGATTAAAGCCGCGGA"
				+ "GATCAGAATAAAGGAGGGCAGGAGGAAAAACCTGGCATGTTTCGTCATTACTATTCCTCAAAAAACCAAA"
				+ "AGCGCGTTATTTACCCAAAAAGGCAACGCGTTAACTCGCAGAAAGAAAAATACAGTTCGCTATCCTACAA"
				+ "ATTATCATTCGTCGATGTAAGGAATAGTTATGAATACAGGCATCTCAAGGCACATAAACACAAAAAAAGA"
				+ "TTAATATTCTACTGTTTTATTTTGACGCGGGTTGAAAGAGGCAGAATTAAAACCTCGTAAATTGAAATAT"
				+ "ATATTGATGTAGTGAATGTATCTTAGGTAAATAATATAT", "");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAATTGAAATATATATTGATGTAGTGAATG"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(-276, alignmentScore.getAlignment());
	}

	@Test
	public void testGetScoreShareOneUnique() {
		RecordNode n1 = new RecordNode("id1", "GTAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("GTAAAAAAAAAAAAAAAAAAAACAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "TAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAACA", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAACAAAAAAAAAAGT"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(37L, alignmentScore.getWeight(), 0.001);
		assertEquals(true, alignmentScore.isFirstNodeReadOne());
		assertEquals(true, alignmentScore.isSecondNodeReadOne());
		assertEquals(1, alignmentScore.getAlignment());
	}

	@Test
	public void testGetScoreShareMoreThanOneUnique() {
		RecordNode n1 = new RecordNode("id1", "GTAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("TAAAAAAAAAAAAAAAAAAAACAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("GTAAAAAAAAAAAAAAAAAAAACAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "TAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAACA", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		r2.addIdentifier(Constants.CALCULATE_HASH("TAAAAAAAAAAAAAAAAAAAACAAAAAAAA"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(37L, alignmentScore.getWeight(), 0.001);
		assertEquals(true, alignmentScore.isFirstNodeReadOne());
		assertEquals(true, alignmentScore.isSecondNodeReadOne());
		assertEquals(1, alignmentScore.getAlignment());
	}

	@Test
	public void testGetScoreExact() {
		RecordNode n1 = new RecordNode("id1", "AAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "", 
																	 "AAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC");
		Read r2 = new Read(n2, 2);
		n2.setRead2(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(36L, alignmentScore.getWeight(), 0.001);
		assertEquals(true, alignmentScore.isFirstNodeReadOne());
		assertEquals(false, alignmentScore.isSecondNodeReadOne());
		assertEquals(0, alignmentScore.getAlignment());
	}

	@Test
	public void testGetScorePadded() {
		RecordNode n1 = new RecordNode("id1", "GGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "CGGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAA", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(37L, alignmentScore.getWeight(), 0.001);
		assertEquals(true, alignmentScore.isFirstNodeReadOne());
		assertEquals(true, alignmentScore.isSecondNodeReadOne());
		assertEquals(-1, alignmentScore.getAlignment());
	}

	@Test
	public void testGetScoreInsufficientOverlap() {
		RecordNode n1 = new RecordNode("id1", "GGAGTTAAAAAAAAAAAAAAAAAAAACAAAAAAAAA", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "AAAAAAAAAAAAAAAAAAAACAAAAAAAAAGCAGGT", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(-1L, alignmentScore.getWeight(), 0.001);
	}

	@Test
	public void testGetScoreTooMuchHammingDistance() {
		RecordNode n1 = new RecordNode("id1", "CGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTGAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "CGGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAA", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		RecordEdgeAlignment alignmentScore = ConnectedComponent.getScore(r1, r2);
		assertEquals(-1L, alignmentScore.getWeight(), 0.001);
	}

	@Test
	public void testComparedBefore() {
		ConnectedComponent cc = new ConnectedComponent();

		RecordNode n1 = new RecordNode("id1", "CGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTGAC", 
																	"");
		Read r1 = new Read(n1, 1);
		n1.setRead1(r1);
		//Technically, invalid because kmer doesnt exist, but just for testing
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("GAAAAAAAAAAAAAAAAAAAACAAAAAAAA"));

		RecordNode n2 = new RecordNode("id1", "CGGAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAGTAA", 
																	"");
		Read r2 = new Read(n2, 1);
		n2.setRead1(r2);
		r1.addIdentifier(Constants.CALCULATE_HASH("GAAAAAAAAAAAAAAAAAAAACAAAAAAAA"));
		r2.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA"));

		assertFalse(cc.comparedBefore(r1, r2, 
				Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAACAAAAAAAAA")));
		assertTrue(cc.comparedBefore(r1, r2, 
				Constants.CALCULATE_HASH("GAAAAAAAAAAAAAAAAAAAACAAAAAAAA")));
	}
}
