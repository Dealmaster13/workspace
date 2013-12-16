package uk.ac.cam.oda22.pathplanning;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.oda22.core.ListFunctions;
import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.environment.VisibilityGraphNode;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.robots.Robot;
import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.core.tethers.Tether;

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
		List<List<Point2D>> vList = calculateVisibilitySetChanges(visibility);
		
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

		double w = l;

		boolean complete = false;

		// Compute the visible vertices from equally spaced points on the tether.
		while (!complete) {
			Point2D p = t.getPositionByDistance(w);

			// Compute the visible nodes.
			List<VisibilityGraphNode> visibleNodes = room.getVisibleNodes(new VisibilityGraphNode(p));

			List<Point2D> visibleVertices = new ArrayList<Point2D>();

			// Store the vertices from the nodes.
			for (VisibilityGraphNode node : visibleNodes) {
				visibleVertices.add(node.vertex);
			}

			double nextW = w - interval;

			// Ensure that the last sample point is always at the anchor point.
			if (nextW <= interval / 2) {
				nextW = 0;
			}

			List<Double> wList = new ArrayList<Double>();
			wList.add(w);

			List<Point2D> pList = new ArrayList<Point2D>();
			pList.add(p);

			tetherPointVisibilitySets.add(new TetherPointVisibility(w, nextW, wList, pList, visibleVertices));

			// Finish once the whole tether has been analysed.
			if (w <= 0) {
				complete = true;

				if (w < 0) {
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
		// Iterate over the list of sets, merging any duplicates containing the same visibility set as the previous element.
		for (int i = sets.size() - 2; i >= 0; i--) {
			TetherPointVisibility previous = sets.get(i + 1);
			TetherPointVisibility current = sets.get(i);

			// Merge the sets if they contain the same vertices.
			if (current.isVisibilitySetEqual(previous.visibleVertices)) {
				previous.wList.addAll(current.wList);
				previous.pList.addAll(current.pList);

				sets.remove(current);
			}
		}
	}

	/**
	 * Step 5a.
	 * Calculate the changes in the visibility sets.
	 * 
	 * @param sets
	 */
	private static List<List<Point2D>> calculateVisibilitySetChanges(List<TetherPointVisibility> sets) {
		List<List<Point2D>> vList = new ArrayList<List<Point2D>>();

		List<Point2D> visibleVertices = new ArrayList<Point2D>();
		List<Point2D> vs = sets.get(0).visibleVertices;

		for (int i = 0; i < vs.size(); i++) {
			visibleVertices.add(vs.get(i));
		}

		vList.add(visibleVertices);

		// For each section of unchanging visible vertices, compute 
		// the set of vertices containing each visible vertex which 
		// was not visible in the previous section.
		for (int i = 1; i < sets.size(); i++) {
			List<Point2D> previousVertices = sets.get(i - 1).visibleVertices;
			List<Point2D> currentVertices = sets.get(i).visibleVertices;

			List<Point2D> changes = new ArrayList<Point2D>();
			
			// Add each point which was not in the previous visibility set.
			for (int j = 0; j < currentVertices.size(); j++) {
				Point2D vertex = currentVertices.get(j);
				
				if (!ListFunctions.isPointInList(vertex, previousVertices)) {
					changes.add(vertex);
				}
			}
			
			vList.add(changes);
		}
		
		return vList;
	}

}
