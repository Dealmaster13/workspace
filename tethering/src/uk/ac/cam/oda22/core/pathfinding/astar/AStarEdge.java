package uk.ac.cam.oda22.core.pathfinding.astar;

/**
 * @author Oliver
 *
 */
public class AStarEdge {

	public final AStarNode p;

	public final AStarNode q;
	
	public final double cost;
	
	public AStarEdge(AStarNode p, AStarNode q, double cost) {
		this.p = p;
		this.q = q;
		this.cost = cost;

		this.p.edges.add(this);
		this.q.edges.add(this);
	}
	
}
