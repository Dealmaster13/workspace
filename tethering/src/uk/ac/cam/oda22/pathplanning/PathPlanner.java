package uk.ac.cam.oda22.pathplanning;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.oda22.core.ListFunctions;
import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.astar.AStarEdge;
import uk.ac.cam.oda22.core.astar.AStarNode;
import uk.ac.cam.oda22.core.astar.AStarPathfinding;
import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.environment.VisibilityGraph;
import uk.ac.cam.oda22.core.environment.VisibilityGraphEdge;
import uk.ac.cam.oda22.core.environment.VisibilityGraphNode;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.robots.Robot;
import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.core.tethers.SimpleTether;
import uk.ac.cam.oda22.core.tethers.SimpleTetherSegment;
import uk.ac.cam.oda22.core.tethers.Tether;
import uk.ac.cam.oda22.core.tethers.TetherPoint;

/**
 * @author Oliver
 *
 */
public final class PathPlanner {

	public static List<IRobotAction> performPathPlanning(Room room, Robot robot, Point2D goal, int tetherSegments) {
		/*
		 * Note that Step 1 (triangulate the environment) is carried 
		 * out by default in the Room class.
		 * Note that Step 2 (compute the visibility graph) is carried 
		 * out by default in the Room class.
		 */

		if (!room.isPointInEmptySpace(goal)) {
			Log.error("Goal (" + goal.getX() + ", " + goal.getY() + ") is not reachable.");

			return null;
		}

		List<IRobotAction> actions = new ArrayList<IRobotAction>();

		VisibilityGraphNode startNode = new VisibilityGraphNode(robot.getPosition());

		/*
		 * Step 3: Find the vertices visible from the start position 
		 * and order them by their angle relative to s.
		 */
		List<VisibilityGraphNode> l = room.getVisibleNodes(startNode);
		sortNodesByAngle(startNode, l);

		/*
		 * Step 4: Compute the changes that occur in the visible 
		 * vertices of the reverse path.
		 */
		List<TetherPointVisibility> visibility = getTetherPointVisibilitySets(robot.tether, tetherSegments, room);

		/*
		 * Step 5: 
		 */
		List<VisibilityChangeList> vList = calculateVisibilitySetChanges(visibility);
		
		return actions;
	}

	/**
	 * Step 3 (second half).
	 * Sort the neighbouring nodes of a particular start node by their 
	 * angle relative to the start node.
	 * 
	 * @param startNode
	 * @param neighbours
	 */
	private static void sortNodesByAngle(VisibilityGraphNode startNode, List<VisibilityGraphNode> neighbours) {
		List<Double> angles = new ArrayList<Double>();

		for (int i = 0; i < neighbours.size(); i++) {
			Point2D p = neighbours.get(i).vertex;

			double angle = Math.atan2(startNode.vertex.getY() - p.getY(), startNode.vertex.getX() - p.getX());

			angles.add(angle);
		}

		List<VisibilityGraphNode> sortedNeighbours = new ArrayList<VisibilityGraphNode>();

		for (int i = angles.size() - 1; i >= 0; i--) {
			double min = Double.MAX_VALUE;
			int minIndex = -1;

			for (int j = 0; j < angles.size(); j++) {
				if (angles.get(j) < min) {
					min = angles.get(j);
					minIndex = j;
				}
			}

			sortedNeighbours.add(neighbours.get(minIndex));
			angles.set(minIndex, Double.MAX_VALUE);
		}

		neighbours = sortedNeighbours;
	}

	/**
	 * Step 4.
	 * Compute which vertices are visible from different points on the 
	 * tether, from the robot to the anchor point.
	 * 
	 * @param t
	 * @param tetherSegments
	 * @param room
	 * @return tether point visibility sets
	 */
	// TODO: Replace algorithm with continuous solution.
	private static List<TetherPointVisibility> getTetherPointVisibilitySets(Tether t, int tetherSegments, Room room) {
		List<TetherPointVisibility> tetherPointVisibilitySets = new LinkedList<TetherPointVisibility>();

		double l = t.getUsedLength();

		double interval = l / tetherSegments;

		// w is the distance along the tether from the robot to the anchor point.
		double w = 0;

		boolean complete = false;

		// Compute the visible vertices from equally spaced points on the tether.
		while (!complete) {
			// Get the position starting from the robot, so use the reverse distance.
			Point2D p = t.getPositionByDistance(l - w);

			// Compute the visible nodes.
			List<VisibilityGraphNode> visibleNodes = room.getVisibleNodes(new VisibilityGraphNode(p));

			List<Point2D> visibleVertices = new ArrayList<Point2D>();

			// Store the vertices from the nodes.
			for (VisibilityGraphNode node : visibleNodes) {
				visibleVertices.add(node.vertex);
			}

			double nextW = w + interval;

			// Ensure that the last sample point is always at the anchor point.
			if (nextW >= l - (interval / 2)) {
				nextW = l;
			}

			List<TetherPoint> pList = new ArrayList<TetherPoint>();
			pList.add(new TetherPoint(p, w));

			tetherPointVisibilitySets.add(new TetherPointVisibility(w, nextW, pList, visibleVertices));

			// Finish once the whole tether has been analysed.
			if (w >= l) {
				complete = true;

				if (w > 0) {
					Log.error("Tether position is out of range.");
				}
			}
			else {
				w = nextW;
			}
		}

		// Merge any duplicate sets.
		mergeDuplicateVisibilitySets(tetherPointVisibilitySets);

		return tetherPointVisibilitySets;
	}

	/**
	 * Step 4 (second half).
	 * Merge the visibility sets.
	 * 
	 * @param sets
	 */
	private static void mergeDuplicateVisibilitySets(List<TetherPointVisibility> sets) {
		// Iterate backwards over the list of sets, merging any duplicates containing the same visibility set as the previous element.
		for (int i = sets.size() - 2; i >= 0; i--) {
			TetherPointVisibility previous = sets.get(i + 1);
			TetherPointVisibility current = sets.get(i);

			// Merge the sets if they contain the same vertices.
			if (current.isVisibilitySetEqual(previous.visibleVertices)) {
				previous.tetherPoints.addAll(current.tetherPoints);

				sets.remove(current);
			}
		}
	}

	/**
	 * Step 5a.
	 * Calculate the changes in the visibility sets.
	 * 
	 * @param s
	 */
	private static List<VisibilityChangeList> calculateVisibilitySetChanges(List<TetherPointVisibility> s) {
		List<VisibilityChangeList> v = new ArrayList<VisibilityChangeList>();

		// Keep all visible vertices for the first section.
		List<Point2D> visibleVertices = new ArrayList<Point2D>();
		List<Point2D> vs = s.get(0).visibleVertices;

		for (int i = 0; i < vs.size(); i++) {
			visibleVertices.add(vs.get(i));
		}

		VisibilityChangeList v_0 = new VisibilityChangeList(visibleVertices, s.get(0).tetherPoints);
		
		v.add(v_0);

		// For each section of unchanging visible vertices, compute 
		// the set of vertices containing each visible vertex which 
		// was not visible in the previous section.
		for (int i = 1; i < s.size(); i++) {
			List<Point2D> previousVertices = s.get(i - 1).visibleVertices;
			List<Point2D> currentVertices = s.get(i).visibleVertices;

			List<Point2D> changes = new ArrayList<Point2D>();
			
			// Add each point which was not in the previous visibility set.
			for (int j = 0; j < currentVertices.size(); j++) {
				Point2D vertex = currentVertices.get(j);
				
				if (!ListFunctions.isPointInList(vertex, previousVertices)) {
					changes.add(vertex);
				}
			}

			VisibilityChangeList v_i = new VisibilityChangeList(changes, s.get(i).tetherPoints);
			
			v.add(v_i);
		}
		
		return v;
	}
	
	/**
	 * Step 5b.
	 * Compute the optimal path using the visibility change lists.
	 * 
	 * @param v
	 * @return
	 */
	private static Path computePath(List<VisibilityChangeList> v, Tether t, Room room, Point2D goal) {
		Path optimalPath = null;
		Path currentPath;
		
		// For each vertex list in the visibility change list.
		for (int i = 0; i < v.size(); i++) {
			VisibilityChangeList v_i = v.get(i);
			
			// For each visible vertex.
			for (int j = 0; j < v_i.vertices.size(); j++) {
				currentPath = new Path();
				
				Point2D vertex = v_i.vertices.get(j);
				
				TetherPoint x = v_i.getClosestPoint(true);
				
				if (t instanceof SimpleTether) {
					SimpleTether tether = (SimpleTether) t;
					
					SimpleTetherSegment tetherSegment = (SimpleTetherSegment) tether.getTetherSegment(0, t.length - x.w);
					
					// Ensure that the tether segment ends at the correct position.
					if (!MathExtended.ApproxEqual(x.x, ListFunctions.getLast(tetherSegment.path.points), 0.0001, 0.0001)) {
						Log.error("Malformed tether segment.");
						
						return null;
					}
					
					// Compute the shortest path from vertex to the goal.
					Path vToG = getShortestPath(vertex, goal, room);
					
					// If the path is null or empty then fail.
					if (vToG == null || vToG.isEmpty()) {
						Log.error("The shortest path from v to g was not found.");
						
						return null;
					}

					// If the path does not contain vertex then fail.
					if (!vToG.contains(vertex)) {
						Log.error("The shortest path from v to g does not contain v.");
						
						return null;
					}
					
					// The path is the tether up to x, concatenated with the direct path from x to vertex, 
					// concatenated with the shortest path from vertex to the goal.
					// Note that vertex is contained in vToG.
					Path q = new Path();
					q.addPoints(tetherSegment.path.points);
					q.addPoints(vToG.points);
				}
				else {
					Log.error("Unsupported tether type.");
					
					return null;
				}
				
			}
		}
		
		return optimalPath;
	}
	
	private static Path getShortestPath(Point2D source, Point2D destination, Room room) {
		// If the points are equal then return the empty path.
		if (source.equals(destination)) {
			return new Path();
		}
		
		// Create a visibility graph including the source and destination nodes.
		// Note that if either vertex already exists then a new node will not be added.
		VisibilityGraph g = room.addNode(room.visibilityGraph, source);
		g = room.addNode(g, destination);
		
		VisibilityGraphNode sourceNode = null;
		VisibilityGraphNode destinationNode = null;
		
		int index = 0;
		
		// Find the source and destination nodes.
		while ((sourceNode == null || destinationNode == null) && index < g.nodes.size()) {
			VisibilityGraphNode node = g.nodes.get(index);
			
			if (node.vertex.equals(source)) {
				sourceNode = node;
			}
			
			if (node.vertex.equals(destination)) {
				destinationNode = node;
			}
		}
		
		// If either the source or destination nodes were not found then fail.
		if (sourceNode == null || destinationNode == null) {
			Log.warning("Source or destination node not found.");
			
			return null;
		}
		
		List<AStarNode> aStarNodes = new LinkedList<AStarNode>();
		
		Hashtable<VisibilityGraphNode, AStarNode> nodeMapping = new Hashtable<VisibilityGraphNode, AStarNode>();
		
		// Create all of the A* nodes.
		for (VisibilityGraphNode node : g.nodes) {
			AStarNode aStarNode = new AStarNode(node.vertex);
			
			aStarNodes.add(aStarNode);
			
			nodeMapping.put(node, aStarNode);
		}
		
		// Create all of the A* edges.
		for (VisibilityGraphEdge edge : g.edges) {
			AStarNode aStarNode1 = nodeMapping.get(edge.startNode);
			AStarNode aStarNode2 = nodeMapping.get(edge.endNode);
			
			AStarEdge aStarEdge = new AStarEdge(aStarNode1, aStarNode2, aStarNode1.distance(aStarNode2.p));
			AStarNode.addEdge(aStarEdge);
		}

		AStarNode aStarSource = nodeMapping.get(sourceNode);
		AStarNode aStarDestination = nodeMapping.get(destinationNode);
		
		boolean pathFound = AStarPathfinding.getShortestPath(aStarSource, aStarDestination, aStarNodes);
		
		// Fail if no path was found.
		if (!pathFound) {
			return null;
		}
		
		return AStarPathfinding.retrievePath(aStarDestination);
	}

}
