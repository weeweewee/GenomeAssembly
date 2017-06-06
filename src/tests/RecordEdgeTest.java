package tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import data.RecordEdge;
import data.RecordEdgeAlignment;

public class RecordEdgeTest {

	@Test
	public void testSorting() {
		RecordEdgeAlignment a1 = new RecordEdgeAlignment(true, true, 24.5, 0);
		RecordEdgeAlignment a2 = new RecordEdgeAlignment(true, true, 25.5, 0);
		RecordEdgeAlignment a3 = new RecordEdgeAlignment(true, true, 10, 0);
		RecordEdgeAlignment a4 = new RecordEdgeAlignment(true, true, 17.5, 0);
		RecordEdgeAlignment a5 = new RecordEdgeAlignment(true, true, 33.9, 0);
		RecordEdgeAlignment a6 = new RecordEdgeAlignment(true, true, 32.9, 0);
		RecordEdge e1 = new RecordEdge(null, null, a1, a2);
		RecordEdge e2 = new RecordEdge(null, null, a3, a4);
		RecordEdge e3 = new RecordEdge(null, null, a5, a6);
		
		ArrayList<RecordEdge> edges = new ArrayList<RecordEdge>();
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
		Collections.sort(edges);

		assertEquals(33.4, edges.get(0).getWeight(), 0.01);
		assertEquals(25, edges.get(1).getWeight(), 0.01);
		assertEquals(13.75, edges.get(2).getWeight(), 0.01);
	}

}
