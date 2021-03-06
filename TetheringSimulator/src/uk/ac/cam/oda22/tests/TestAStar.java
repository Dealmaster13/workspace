package uk.ac.cam.oda22.tests;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.ac.cam.oda22.core.environment.Obstacle;
import uk.ac.cam.oda22.core.pathfinding.astar.AStarEdge;
import uk.ac.cam.oda22.core.pathfinding.astar.AStarGraph;
import uk.ac.cam.oda22.core.pathfinding.astar.AStarNode;
import uk.ac.cam.oda22.core.pathfinding.astar.TetheredAStarPathfinding;
import uk.ac.cam.oda22.core.tethers.TetherConfiguration;

/**
 * @author Oliver
 * 
 */
public class TestAStar {

	private List<AStarNode> nodes;

	private AStarNode destinationNode;

	private AStarNode[] expectedPath;

	private boolean pathFound;

	@BeforeClass
	public void oneTimeSetUp() {
		/*
		 * The image representation of the following graph can be found here:
		 * http://img571.imageshack.us/img571/679/76mm.png
		 */

		AStarNode sourceNode = new AStarNode(new Point2D.Double(5, 6.5));
		AStarNode node2 = new AStarNode(new Point2D.Double(2.2, 20.5));
		AStarNode node3 = new AStarNode(new Point2D.Double(10.8, 12.1));
		AStarNode node4 = new AStarNode(new Point2D.Double(1, 25));
		AStarNode node5 = new AStarNode(new Point2D.Double(6.9, 25));
		AStarNode node6 = new AStarNode(new Point2D.Double(18, 20));
		this.destinationNode = new AStarNode(new Point2D.Double(6.7, 33.2));

		this.nodes = new LinkedList<AStarNode>();
		this.nodes.add(sourceNode);
		this.nodes.add(node2);
		this.nodes.add(node3);
		this.nodes.add(node4);
		this.nodes.add(node5);
		this.nodes.add(node6);
		this.nodes.add(this.destinationNode);

		List<AStarEdge> edges = new ArrayList<AStarEdge>();

		// Give edge weights proportionally similar to the distances between
		// nodes.
		edges.add(new AStarEdge(sourceNode, node2, 10));
		edges.add(new AStarEdge(sourceNode, node3, 5));
		edges.add(new AStarEdge(node2, node4, 4));
		edges.add(new AStarEdge(node2, node5, 5));
		edges.add(new AStarEdge(node3, node6, 8));
		edges.add(new AStarEdge(node4, this.destinationNode, 8));
		edges.add(new AStarEdge(node5, this.destinationNode, 6));
		edges.add(new AStarEdge(node6, this.destinationNode, 16));

		List<Obstacle> obstacles = new ArrayList<Obstacle>();

		// Create the A* graph without any obstacles.
		AStarGraph g = new AStarGraph(nodes, edges, obstacles);

		this.expectedPath = new AStarNode[4];
		this.expectedPath[0] = sourceNode;
		this.expectedPath[1] = node2;
		this.expectedPath[2] = node5;
		this.expectedPath[3] = this.destinationNode;

		// Create an tether configuration with just the source.
		TetherConfiguration tc = new TetherConfiguration(sourceNode.p);

		// Use a large tether length limit.
		double maxTetherLength = 100;

		this.pathFound = TetheredAStarPathfinding.getShortestPath(g,
				sourceNode, this.destinationNode, obstacles, tc,
				maxTetherLength, 0);
	}

	@Test
	public void testPathShouldBeFound() {
		Assert.assertTrue(this.pathFound);
	}

	@Test
	public void testPathShouldHaveCorrectLength() {
		int nodeCount = 0;

		AStarNode current = this.destinationNode;

		// Store a set of the nodes which have been seen.
		Set<AStarNode> seen = new HashSet<AStarNode>();

		// Count the number of nodes before reaching the source.
		while (current != null) {
			// Assert that the node has not been seen before, which means a
			// cycle exists.
			Assert.assertFalse(seen.contains(current));

			seen.add(current);

			nodeCount++;

			current = current.subPaths.size() != 0 ? current.subPaths.get(0).predecessorList
					.get(0) : null;
		}

		Assert.assertEquals(nodeCount, this.expectedPath.length);
	}

	@Test
	public void testPathShouldBeCorrect() {
		Assert.assertNotNull(this.destinationNode);

		AStarNode current = this.destinationNode;

		// Assert that the actual path has the same node list as the expected
		// path.
		for (int i = this.expectedPath.length - 1; i >= 0; i--) {
			Assert.assertEquals(current, this.expectedPath[i]);

			current = current.subPaths.size() != 0 ? current.subPaths.get(0).predecessorList
					.get(0) : null;
		}

		// Assert that the predecessor of the source is null.
		Assert.assertEquals(current, null);
	}

	@Test
	public void testNodesShouldHaveAtMostOneSubPath() {
		// Assert that all nodes have at most one predecessor.
		// This means that there are no equivalently optimal sub-paths.
		for (AStarNode node : this.nodes) {
			Assert.assertTrue(node.subPaths.size() <= 1);
		}
	}

	@Test
	public void testNodesShouldHaveAtMostOnePredecessor() {
		// Assert that all nodes have at most one predecessor.
		// This means that there are no equivalently optimal sub-paths.
		for (AStarNode node : this.nodes) {
			Assert.assertTrue(node.subPaths.get(0).predecessorList.size() <= 1);
		}
	}

}
