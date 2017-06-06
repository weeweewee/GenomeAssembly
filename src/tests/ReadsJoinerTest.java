package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import data.ReadsJoiner;

public class ReadsJoinerTest {

	@Test
	public void testJoiningNoIndentation() {
		ReadsJoiner j = new ReadsJoiner("ACC","ACCG");
		assertEquals("ACCG", j.joinByAlignmentIndex(0));
	}

	@Test
	public void testJoiningRandom() {
		ReadsJoiner j = new ReadsJoiner("ACCA","ACCG");
		String joined = j.joinByAlignmentIndex(0);
		assertTrue(joined.equals("ACCG") || joined.equals("ACCA"));
	}

	@Test
	public void testJoiningPositiveIndentation() {
		ReadsJoiner j = new ReadsJoiner("GAACC","ACCGGA");
		assertEquals("GAACCGGA", j.joinByAlignmentIndex(2));
	}

	@Test
	public void testJoiningNegativeIndentation() {
		ReadsJoiner j = new ReadsJoiner("CCG","ACCGGA");
		assertEquals("ACCGGA", j.joinByAlignmentIndex(-1));
	}
}
