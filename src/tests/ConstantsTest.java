package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import entry.Constants;

public class ConstantsTest {

	@Test
	public void testRCHash1() {
		long hash = 0l;									//00...00 expect 11...11
		assertEquals(1152921504606846975l,Constants.REVERSE_COMPLEMENT_HASH_60(hash));
	}

	@Test
	public void testRCHash2() {
		long hash = 10l; 							  //00..001010 expect 010111..11 
		assertEquals(432345564227567615l,Constants.REVERSE_COMPLEMENT_HASH_60(hash));
	}

	@Test
	public void testRCHash3() {
		long hash = 768614336404564650l; //101010... expect 010101..
		assertEquals(384307168202282325l,Constants.REVERSE_COMPLEMENT_HASH_60(hash));
	}

}
