package uk.ac.cam.oda22.tests;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.ac.cam.oda22.core.astar.AStarEdge;
import uk.ac.cam.oda22.core.astar.AStarNode;
import uk.ac.cam.oda22.core.astar.AStarPathfinding;

/**
 * @author Oliver
 *
 */
public class TestAStar {

	private List<AStarNode> nodes;
	
	private AStarNode destination;
	
	private AStarNode[] expectedPath;
	
	private boolean pathFound;
	
	@BeforeClass
	public void oneTimeSetUp() {
		/*
		 * The image representation of the following graph can be 
		 * found here: http://img571.imageshack.us/img571/679/76mm.png
		 */
		
		AStarNode node1 = new AStarNode(new Point2D.Double(5, 6.5));
		AStarNode node2 = new AStarNode(new Point2D.Double(2.2, 20.5));
		AStarNode node3 = new AStarNode(new Point2D.Double(10.8, 12.1));
		AStarNode node4 = new AStarNode(new Point2D.Double(1, 25));
		AStarNode node5 = new AStarNode(new Point2D.Double(6.9, 25));
		AStarNode node6 = new AStarNode(new Point2D.Double(18, 20));
		this.destination = new AStarNode(new Point2D.Double(6.7, 33.2));
		
		this.nodes = new LinkedList<AStarNode>();
		this.nodes.add(node1);
		this.nodes.add(node2);
		this.nodes.add(node3);
		this.nodes.add(node4);
		this.nodes.add(node5);
		this.nodes.add(node6);
		this.nodes.add(this.destination);

		// Give edge weights proportionally similar to the distances between nodes.
		AStarNode.addEdge(new AStarEdge(node1, node2, 10));
		AStarNode.addEdge(new AStarEdge(node1, node3, 5));
		AStarNode.addEdge(new AStarEdge(node2, node4, 4));
		AStarNode.addEdge(new AStarEdge(node2, node5, 5));
		AStarNode.addEdge(new AStarEdge(node3, node6, 8));
		AStarNode.addEdge(new AStarEdge(node4, this.destination, 8));
		AStarNode.addEdge(new AStarEdge(node5, this.destination, 6));
		AStarNode.addEdge(new AStarEdge(node6, this.destination, 16));

		this.expectedPath = new AStarNode[4];
		this.expectedPath[0] = node1;
		this.expectedPath[1] = node2;
		this.expectedPath[2] = node5;
		this.expectedPath[3] = this.destination;
		
		this.pathFound = AStarPathfinding.getShortestPath(node1, this.destination, this.nodes);
	}
	
	@Test
	public void testPathShouldBeFound() {
		Assert.assertTrue(this.pathFound);
	}
	
	@Test
	public void testPathShouldHaveCorrectLength() {
		int nodeCount = 0;
		
		AStarNode current = this.destination;
		
		// Store a set of the nodes which have been seen.
		Set<AStarNode> seen = new HashSet<AStarNode>();
		
		// Count the number of nodes before reaching the source.
		while (current != null) {
			// Assert that the node has not been seen before, which means a cycle exists.
			Assert.assertFalse(seen.contains(current));
			
			seen.add(current);
			
			nodeCount ++;
			
			current = current.predecessor;
		}
		
		Assert.assertEquals(nodeCount, this.expectedPath.length);
	}
	
	@Test
	public void testPathShouldBeCorrect() {
		Assert.assertNotNull(this.destination);
		
		AStarNode current = this.destination;
		
		// Assert that the actual path has the same node list as the expected path.
		for (int i = this.expectedPath.length - 1; i >= 0; i--) {
			Assert.assertEquals(current, this.expectedPath[i]);
			
			current = current.predecessor;
		}
		
		// Assert that the predecessor of the source is null.
		Assert.assertEquals(current, null);
	}
	
}