package uk.ac.cam.oda22.core.graphs;

import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public abstract class Node {

	/**
	 * The node's position.
	 */
	public final Point2D p;
	
	public Node(Point2D p) {
		this.p = p;
	}
	
	public double distance(Point2D q) {
		return this.p.distance(q);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		Node n = (Node) o;
		
		return this.p.equals(n.p);
	}
	
}
