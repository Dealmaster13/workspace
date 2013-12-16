package uk.ac.cam.oda22.core.environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver
 *
 */
public class VisibilityGraph {

	List<VisibilityGraphNode> nodes;
	
	List<VisibilityGraphEdge> edges;
	
	public VisibilityGraph() {
		this.nodes = new ArrayList<VisibilityGraphNode>();
		this.edges = new ArrayList<VisibilityGraphEdge>();
	}
	
	public boolean addNode(VisibilityGraphNode node) {
		for (VisibilityGraphNode n : this.nodes) {
			if (n.equals(node)) {
				return false;
			}
		}
		
		this.nodes.add(node);
		
		return true;
	}
	
	public boolean addEdge(VisibilityGraphEdge edge) {
		for (VisibilityGraphEdge e : this.edges) {
			if (e.equals(edge)) {
				return false;
			}
		}
		
		this.edges.add(edge);
		
		return true;
	}
	
}
