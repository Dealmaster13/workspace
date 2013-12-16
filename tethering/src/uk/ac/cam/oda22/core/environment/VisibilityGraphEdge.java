package uk.ac.cam.oda22.core.environment;

/**
 * @author Oliver
 *
 */
public class VisibilityGraphEdge {

	public final VisibilityGraphNode startNode;
	
	public final VisibilityGraphNode endNode;
	
	public final double weight;

	public VisibilityGraphEdge(VisibilityGraphNode startNode, VisibilityGraphNode endNode, double weight) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		VisibilityGraphEdge e = (VisibilityGraphEdge)o;
		
		if (this.startNode.equals(e.startNode) && this.endNode.equals(e.endNode)) {
			return true;
		}
		
		if (this.startNode.equals(e.endNode) && this.endNode.equals(e.startNode)) {
			return true;
		}
		
		return false;
	}
	
}
