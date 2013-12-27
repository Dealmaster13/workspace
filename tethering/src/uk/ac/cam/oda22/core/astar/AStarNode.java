package uk.ac.cam.oda22.core.astar;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Oliver
 *
 */
public class AStarNode {
	
	/**
	 * The node's position.
	 */
	public final Point2D p;
	
	/**
	 * The neighbouring edges.
	 */
	public final List<AStarEdge> edges;
	
	/**
	 * The node's predecessor for the shortest path.
	 */
	public AStarNode predecessor;
	
	/**
	 * Whether or not the node has been discovered.
	 */
	public boolean discovered;
	
	/**
	 * To cost from the source node to this node.
	 */
	public double f;
	
	/**
	 * The heuristic cost from this node to the destination node.
	 */
	public double g;
	
	public AStarNode(Point2D p) {
		this.p = p;
		
		this.edges = new LinkedList<AStarEdge>();
		this.predecessor = null;
		this.discovered = false;
		this.f = Double.POSITIVE_INFINITY;
		this.g = Double.POSITIVE_INFINITY;
	}
	
	public static void addEdge(AStarEdge e) {
		e.p.edges.add(e);
		e.q.edges.add(e);
	}
	
	public double distance(Point2D q) {
		return p.distance(q);
	}
	
}
