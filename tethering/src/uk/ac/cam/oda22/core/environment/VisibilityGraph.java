package uk.ac.cam.oda22.core.environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver
 *
 */
public class VisibilityGraph {

	public final List<VisibilityGraphNode> nodes;
	
	public final List<VisibilityGraphEdge> edges;
	
	public VisibilityGraph() {
		this.nodes = new ArrayList<VisibilityGraphNode>();
		this.edges = new ArrayList<VisibilityGraphEdge>();
	}
	
	public VisibilityGraph(VisibilityGraph g) {
		this.nodes = new ArrayList<VisibilityGraphNode>();
		this.edges = new ArrayList<VisibilityGraphEdge>();
		
		for (VisibilityGraphNode node : g.nodes) {
			this.nodes.add(new VisibilityGraphNode(node));
		}
		
		for (VisibilityGraphEdge edge : g.edges) {
			this.edges.add(new VisibilityGraphEdge(edge));
		}
	}
	
	public boolean addNode(VisibilityGraphNode node) {
		// If the node already exists (by location) then do not add it.
		for (VisibilityGraphNode n : this.nodes) {
			if (n.equals(node)) {
				return false;
			}
		}
		
		this.nodes.add(node);
		
		return true;
	}
	
	public boolean addEdge(VisibilityGraphEdge edge) {
		// If the edge already exists (by its nodes) then do not add it.
		for (VisibilityGraphEdge e : this.edges) {
			if (e.equals(edge)) {
				return false;
			}
		}
		
		this.edges.add(edge);
		
		return true;
	}
	
}
