package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public class VisibilityGraphNode {

	public final Point2D vertex;
	
	public VisibilityGraphNode(Point2D node) {
		this.vertex = node;
	}
	
	public VisibilityGraphNode(VisibilityGraphNode node) {
		this.vertex = new Point2D.Double(node.vertex.getX(), node.vertex.getY());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		VisibilityGraphNode n = (VisibilityGraphNode) o;
		
		return this.vertex.equals(n.vertex);
	}
	
}
