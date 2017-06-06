package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import data.ConnectedComponentContractionTask;
import data.Read;
import data.RecordEdge;
import data.RecordNode;
import entry.Constants;
import entry.ConnectedComponent;

public class ConnectedComponentContractionTaskTest {


	@Test
	public void testUpdateEdgesFromMerging() {
		// Testing the update edges of after merging.
		// Merge n1 - n2 => mergedNode
		// n1 - n3, n2 - n4. Output should have 2 edges mergedNode - n3, mergedNode - n4
		RecordNode mergedNode = new RecordNode("merged", 
				"GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		Read r1 = new Read(mergedNode, 1);
		Read r2 = new Read(mergedNode, 2);
		mergedNode.setRead1(r1);
		mergedNode.setRead2(r2);
		r1.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"));
		r2.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r2.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));
		r2.addIdentifier(Constants.CALCULATE_HASH("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"));

		RecordNode node1 = new RecordNode("id1", 
				"GGGGGGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTT");
		Read r11 = new Read(node1, 1);
		Read r12 = new Read(node1, 2);
		node1.setRead1(r11);
		node1.setRead2(r12);
		r11.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r11.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r12.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r12.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));
		
		RecordNode node2 = new RecordNode("id2", 
				"GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		Read r21 = new Read(node2, 1);
		Read r22 = new Read(node2, 2);
		node2.setRead1(r21);
		node2.setRead2(r22);
		r21.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r21.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"));
		r22.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r22.addIdentifier(Constants.CALCULATE_HASH("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"));

		RecordNode node3 = new RecordNode("id3", 
				"GGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCTTTTT");
		Read r31 = new Read(node3, 1);
		Read r32 = new Read(node3, 2);
		node3.setRead1(r31);
		node3.setRead2(r32);
		r31.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r32.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));

		RecordNode node4 = new RecordNode("id4", 
				"GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAA",
				"CCCCCTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		Read r41 = new Read(node4, 1);
		Read r42 = new Read(node4, 2);
		node4.setRead1(r41);
		node4.setRead2(r42);
		r41.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"));
		r42.addIdentifier(Constants.CALCULATE_HASH("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"));

		// 1 - 2
		RecordEdge mergedEdge = new RecordEdge(node1, node2, 
				ConnectedComponent.getScore(r11, r21), 
				ConnectedComponent.getScore(r12, r22));
		//Assume removed as in program requirements.
		//node1.addEdge(mergedEdge);
		//node2.addEdge(mergedEdge);

		// 1 - 3 
		RecordEdge otherEdge = new RecordEdge(node1, node3,
				ConnectedComponent.getScore(r11, r31),
				ConnectedComponent.getScore(r12, r32));
		node1.addEdge(otherEdge);
		node3.addEdge(otherEdge);

		// 2 - 4 
		RecordEdge otherEdge2 = new RecordEdge(node2, node4,
				ConnectedComponent.getScore(r21, r41),
				ConnectedComponent.getScore(r22, r42));
		node2.addEdge(otherEdge2);
		node4.addEdge(otherEdge2);

		ArrayList<RecordEdge> listToVerify = new ArrayList<RecordEdge>();

		ConnectedComponentContractionTask task = 
				new ConnectedComponentContractionTask(listToVerify, null, null);
		task.updateEdgesToNeighboursAndEdgeList(mergedEdge, mergedNode);

		assertEquals(2, listToVerify.size());
	}

	@Test
	public void testUpdateEdgesFromMergingShared() {
		// Testing the update edges of after merging.
		// Merge n1 - n2 => mergedNode
		// n1 - n3, n2 - n3. Output should have 2 edges mergedNode - n3, mergedNode - n3
		RecordNode mergedNode = new RecordNode("merged", 
				"GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		Read r1 = new Read(mergedNode, 1);
		Read r2 = new Read(mergedNode, 2);
		mergedNode.setRead1(r1);
		mergedNode.setRead2(r2);
		r1.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r1.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"));
		r2.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r2.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));
		r2.addIdentifier(Constants.CALCULATE_HASH("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"));

		RecordNode node1 = new RecordNode("id1", 
				"GGGGGGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTT");
		Read r11 = new Read(node1, 1);
		Read r12 = new Read(node1, 2);
		node1.setRead1(r11);
		node1.setRead2(r12);
		r11.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r11.addIdentifier(Constants.CALCULATE_HASH("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		r12.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r12.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));
		
		RecordNode node2 = new RecordNode("id2", 
				"GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGAAAAAAAAAAAAA",
				"CCCCCCCCCCCCCCCTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		Read r21 = new Read(node2, 1);
		Read r22 = new Read(node2, 2);
		node2.setRead1(r21);
		node2.setRead2(r22);
		r21.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r21.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"));
		r22.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));
		r22.addIdentifier(Constants.CALCULATE_HASH("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"));

		RecordNode node3 = new RecordNode("id3", 
				"GGGGGGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAA", 
				"CCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTT");
		Read r31 = new Read(node3, 1);
		Read r32 = new Read(node3, 2);
		node3.setRead1(r31);
		node3.setRead2(r32);
		r31.addIdentifier(Constants.CALCULATE_HASH("GGGGGGGGGGGGGGGAAAAAAAAAAAAAAA"));
		r32.addIdentifier(Constants.CALCULATE_HASH("CCCCCCCCCCCCCCCTTTTTTTTTTTTTTT"));

		// 1 - 2
		RecordEdge mergedEdge = new RecordEdge(node1, node2, 
				ConnectedComponent.getScore(r11, r21), 
				ConnectedComponent.getScore(r12, r22));
		//Assume removed as in program requirements.
		//node1.addEdge(mergedEdge);
		//node2.addEdge(mergedEdge);

		// 1 - 3 
		RecordEdge otherEdge = new RecordEdge(node1, node3, 
				ConnectedComponent.getScore(r11, r31),
				ConnectedComponent.getScore(r12, r32));
		node1.addEdge(otherEdge);
		node3.addEdge(otherEdge);

		// 2 - 3 
		RecordEdge otherEdge2 = new RecordEdge(node2, node3,
				ConnectedComponent.getScore(r21, r31),
				ConnectedComponent.getScore(r22, r32));
		node2.addEdge(otherEdge2);
		node3.addEdge(otherEdge2);

		ArrayList<RecordEdge> listToVerify = new ArrayList<RecordEdge>();

		ConnectedComponentContractionTask task = 
				new ConnectedComponentContractionTask(listToVerify, null, null);
		task.updateEdgesToNeighboursAndEdgeList(mergedEdge, mergedNode);

		assertEquals(2, listToVerify.size());
	}
}
