package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Line2D;

/**
 * @author Oliver
 * 
 */
public class VisibilityGraphEdge {

	public final VisibilityGraphNode startNode;

	public final VisibilityGraphNode endNode;

	public final double weight;

	public final boolean isObstacleEdge;

	public VisibilityGraphEdge(VisibilityGraphNode startNode,
			VisibilityGraphNode endNode, double weight, boolean isObstacleEdge) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.weight = weight;
		this.isObstacleEdge = isObstacleEdge;
	}

	public VisibilityGraphEdge(VisibilityGraphEdge edge) {
		this.startNode = new VisibilityGraphNode(edge.startNode);
		this.endNode = new VisibilityGraphNode(edge.endNode);
		this.weight = edge.weight;
		this.isObstacleEdge = edge.isObstacleEdge;
	}

	public Line2D getLine() {
		return new Line2D.Double(this.startNode.p.getX(),
				this.startNode.p.getY(), this.endNode.p.getX(),
				this.endNode.p.getY());
	}
	
	public boolean containsNode(VisibilityGraphNode node) {
		return startNode.equals(node) || endNode.equals(node);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		VisibilityGraphEdge e = (VisibilityGraphEdge) o;

		if (this.startNode.equals(e.startNode)
				&& this.endNode.equals(e.endNode)) {
			return true;
		}

		if (this.startNode.equals(e.endNode)
				&& this.endNode.equals(e.startNode)) {
			return true;
		}

		return false;
	}

}
