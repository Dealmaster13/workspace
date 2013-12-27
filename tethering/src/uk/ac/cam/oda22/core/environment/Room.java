package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.LineIntersectionResult;

/**
 * @author Oliver
 *
 */
public class Room {

	public final List<Obstacle> obstacles;

	public final VisibilityGraph visibilityGraph;

	private final double width;

	private final double height;

	private final List<EnvironmentTriangle> triangles;

	public Room(double width, double height, List<Obstacle> l) {
		this.width = width;
		this.height = height;

		this.obstacles = new ArrayList<Obstacle>(); 

		for (Obstacle o : l) {
			this.obstacles.add(o);
		}

		this.addRoomEdges();

		this.triangles = this.triangulateRoom();

		this.visibilityGraph = this.generateVisibilityGraph();
	}

	public boolean isPointInEmptySpace(Point2D p) {
		boolean inSpace = false;

		int index = 0;

		while (!inSpace && index < this.triangles.size()) {
			EnvironmentTriangle t = this.triangles.get(index);

			if (!t.isObstacle && t.containsPoint(p)) {
				inSpace = true;
			}

			index ++;
		}

		return inSpace;
	}

	/**
	 * Adds a node, returning a new visibility graph.
	 * 
	 * @param goal
	 * @return a visibility graph with the new node added
	 */
	public VisibilityGraph addNode(VisibilityGraph g1, Point2D goal) {
		VisibilityGraphNode node = new VisibilityGraphNode(goal);
		
		VisibilityGraph g2 = new VisibilityGraph(g1);

		boolean addedNode = g2.addNode(node);

		if (!addedNode) {
			return g2;
		}
		
		for (VisibilityGraphNode n : g2.nodes) {
			this.tryAddEdge(node, n, g2);
		}
		
		return g2;
	}

	public List<VisibilityGraphNode> getVisibleNodes(VisibilityGraphNode node) {
		List<VisibilityGraphNode> l = new ArrayList<VisibilityGraphNode>();

		for (VisibilityGraphNode q : this.visibilityGraph.nodes) {
			// Add node q if it is visible from 'node'.
			if (getVisibility(node, q, this.visibilityGraph).isPartlyVisible()) {
				l.add(q);
			}
		}

		return l;
	}

	/**
	 * Adds the four room edges as four obstacles with no width.
	 */
	private void addRoomEdges() {
		Point2D p1 = new Point2D.Double(0, 0);
		Point2D p2 = new Point2D.Double(width, 0);
		Point2D p3 = new Point2D.Double(0, height);
		Point2D p4 = new Point2D.Double(width, height);

		List<Point2D> el1 = new ArrayList<Point2D>();
		el1.add(p1);
		el1.add(p2);

		List<Point2D> el2 = new ArrayList<Point2D>();
		el2.add(p2);
		el2.add(p4);

		List<Point2D> el3 = new ArrayList<Point2D>();
		el3.add(p4);
		el3.add(p3);

		List<Point2D> el4 = new ArrayList<Point2D>();
		el4.add(p3);
		el4.add(p1);

		this.obstacles.add(new Obstacle(el1));
		this.obstacles.add(new Obstacle(el2));
		this.obstacles.add(new Obstacle(el3));
		this.obstacles.add(new Obstacle(el4));
	}

	private List<EnvironmentTriangle> triangulateRoom() {
		List<EnvironmentTriangle> t = new ArrayList<EnvironmentTriangle>();

		// Add all 'obstacle' triangles.
		for (Obstacle o : obstacles) {
			if (o.points.size() > 0) {
				// Add a triangle if obstacle is a point.
				if (o.points.size() == 1) {
					t.add(new EnvironmentTriangle(true, o.points.get(0), o.points.get(0), o.points.get(0)));
				}
				// Add a triangle if obstacle is a line.
				else if (o.points.size() == 2) {
					t.add(new EnvironmentTriangle(true, o.points.get(0), o.points.get(0), o.points.get(1)));
				}
				else {
					// Add a triangle with one point always being the first point.
					for (int i = 1; i < o.points.size() - 1; i++) {
						t.add(new EnvironmentTriangle(true, o.points.get(0), o.points.get(i), o.points.get(i + 1)));
					}
				}
			}
		}

		// Add all 'freespace' triangles.
		for (Obstacle p : obstacles) {
			for (Obstacle q : obstacles) {
				if (p != q) {
					// Try to form a triangle with one of p's edges and a point from q.
					for (int i = 0; i < p.points.size(); i++) {
						if (p.points.size() >= 2) {
							Point2D point1 = p.points.get(i);
							Point2D point2 = p.points.get((i + 1) % p.points.size());

							for (int j = 0; j < q.points.size(); j++) {
								boolean cross = false;

								int index = 0;

								Point2D point3 = q.points.get(j);

								Line2D l1 = new Line2D.Double();
								l1.setLine(point1, q.points.get(j));

								Line2D l2 = new Line2D.Double();
								l2.setLine(point2, q.points.get(j));

								// Do not add the triangle if its edges cross any other triangles.
								while (!cross && index < t.size()) {
									if (t.get(index).looseIntersectsLine(l1) || t.get(index).looseIntersectsLine(l2)) {
										cross = true;
									}

									index ++;
								}

								EnvironmentTriangle s = new EnvironmentTriangle(false, point1, point2, point3);

								boolean unique = true;

								int index2 = 0;

								while (unique && index2 < t.size()) {
									if (s.equals(t.get(index2))) {
										unique = false;
									}

									index2 ++;
								}

								if (!cross && unique) {
									t.add(s);
								}
							}
						}
					}
				}
			}
		}

		return t;
	}

	private VisibilityGraph generateVisibilityGraph() {
		VisibilityGraph g = new VisibilityGraph();

		for (Obstacle obstacle : this.obstacles) {
			for (Point2D p : obstacle.points) {
				g.addNode(new VisibilityGraphNode(p));
			}
		}

		for (VisibilityGraphNode p : g.nodes) {
			for (VisibilityGraphNode q : g.nodes) {
				this.tryAddEdge(p, q, g);
			}
		}

		return g;
	}

	private boolean tryAddEdge(VisibilityGraphNode p, VisibilityGraphNode q, VisibilityGraph g) {
		NodeVisibility visibility = this.getVisibility(p, q, g);
		
		// Add the edge if q is at least partly visible from p.
		if (visibility.isPartlyVisible()) {
			double weight = p.vertex.distance(q.vertex);

			boolean isObstacleEdge = visibility == NodeVisibility.ALONG_OBSTACLE_EDGE;
			
			g.addEdge(new VisibilityGraphEdge(p, q, weight, isObstacleEdge));

			return true;
		}

		return false;
	}

	/**
	 * Calculates the visibility between two points.
	 * 
	 * @param p
	 * @param q
	 * @param g
	 * @return point-to-point visibility
	 */
	private NodeVisibility getVisibility(VisibilityGraphNode p, VisibilityGraphNode q, VisibilityGraph g) {
		// If the points are not equal then check for visibility.
		if (!p.equals(q)) {
			Line2D l = new Line2D.Double();
			l.setLine(p.vertex, q.vertex);

			// Initialise visibility to 'fully visible.
			NodeVisibility visibility = NodeVisibility.FULLY_VISIBLE;

			int index = 0;

			// For each obstacle, check if it blocks visibility.
			while (visibility != NodeVisibility.NOT_VISIBLE && index < this.obstacles.size()) {
				Obstacle o = this.obstacles.get(index);

				// Calculate if the line formed between the two points intersects with the obstacle.
				LineIntersectionResult intersection = o.intersectsLine(l);

				switch (intersection) {
				case CROSSED:
					visibility = NodeVisibility.NOT_VISIBLE;
					break;

				case EQUAL_LINES:
					visibility = NodeVisibility.ALONG_OBSTACLE_EDGE;
					break;

				default:
					break;
				}

				index ++;
			}

			return visibility;
		}

		return NodeVisibility.SAME_POINT;
	}

}
