package uk.ac.cam.oda22.coverage;

import java.awt.geom.Point2D;
import java.util.Stack;

import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.environment.VisibilityGraph;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.pathfinding.astar.TetheredAStarPathfinding;
import uk.ac.cam.oda22.core.pathfinding.astar.TetheredAStarShortestPathResult;
import uk.ac.cam.oda22.core.pathfinding.astar.TetheredAStarSinglePathResult;
import uk.ac.cam.oda22.core.robots.Robot;
import uk.ac.cam.oda22.core.tethers.TetherConfiguration;
import uk.ac.cam.oda22.pathplanning.TetheredPath;

/**
 * @author Oliver
 * 
 */
public final class Coverage {

	public static CoverageResult performCoverage(Room room, Robot robot) {
		/*
		 * TODO: Perform input sanitisation.
		 */

		// Check that the robot is not a point robot (otherwise coverage is not
		// possible).
		if (robot.radius <= 0) {
			Log.error("Robot must have a positive area to perform coverage.");

			return null;
		}

		/*
		 * TODO: Compute the shortest path from the anchor point to every point
		 * in the room, thus providing a discrete notion of 'saddle lines'.
		 */

		ShortestPathGrid shortestPathGrid = computeShortestPaths(room, robot);

		// Initialise a split point stack.
		Stack<Point2D> splitPoints = new Stack<Point2D>();

		// Initialise corridor progress.
		// Note that we initially search rightwards.
		CorridorProgress corridorProgress = new CorridorProgress(0, true);

		/*
		 * TODO: Step 1: Move to the closest uncovered area, in a given
		 * direction (determined by whether we are in a left or right corridor),
		 * along the obstacle edge or saddle line. Note that we should be
		 * touching an obstacle edge or saddle line.
		 */

		/*
		 * TODO: Step 2: Explore the current corridor.
		 */

		/*
		 * TODO: Step 3: Return to the last split point for which a corridor is
		 * unexplored, and then go to step 2. If no such split point exists then
		 * we are finished.
		 */

		return null;
	}

	/**
	 * Computes the shortest path from the anchor point to every other point in
	 * the room.
	 * 
	 * @param o
	 * @param room
	 * @return saddle line
	 */
	public static ShortestPathGrid computeShortestPaths(Room room, Robot robot) {
		Point2D anchorPoint = robot.tether.getAnchor();

		double gridSeparation = robot.radius * 2;

		int horizontalCount = (int) Math.ceil((room.width - gridSeparation / 2)
				/ gridSeparation);
		int verticalCount = (int) Math.ceil((room.height - gridSeparation / 2)
				/ gridSeparation);

		ShortestPathGridCell[][] cells = new ShortestPathGridCell[verticalCount][horizontalCount];

		VisibilityGraph g = new VisibilityGraph(room.obstacles);

		TetherConfiguration initialTC = new TetherConfiguration(anchorPoint);

		for (int i = 0; i < verticalCount; i++) {
			for (int j = 0; j < horizontalCount; j++) {
				double x = (j * gridSeparation) + (gridSeparation / 2);
				double y = (i * gridSeparation) + (gridSeparation / 2);

				Point2D destination = new Point2D.Double(x, y);

				TetheredAStarShortestPathResult shortestPathResult = TetheredAStarPathfinding
						.getShortestPaths(anchorPoint, destination, g,
								initialTC, robot.tether.length, robot.radius);

				TetheredPath shortestPath = null;

				// Set the shortest path.
				if (shortestPathResult != null
						&& shortestPathResult.shortestPathResults.size() > 0) {
					// Get the first path result.
					TetheredAStarSinglePathResult path = shortestPathResult.shortestPathResults
							.get(0);

					shortestPath = new TetheredPath(path.path,
							path.tetherConfiguration);
				}

				cells[i][j] = new ShortestPathGridCell(x, y, shortestPath);
			}
		}

		return new ShortestPathGrid(cells);
	}

	private static ISweepSegment exploreCorridor(Point2D position,
			CorridorProgress corridorProgress) {
		/*
		 * TODO: Step 2a: Check when the robot will first hit a saddle line via
		 * sweeping at its current radius, if one exists.
		 */

		/*
		 * TODO: Step 2b: Check when the robot will first hit an obstacle (before
		 * hitting any saddle lines) via sweeping at its current radius, if one
		 * exists, and check if it would introduce a split point. If a split
		 * point is introduced then add it to the stack, and create the
		 * appropriate corridors.
		 */

		/*
		 * TODO: Step 2c: Perform a sweep until the next saddle line or obstacle.
		 */

		return null;
	}

	private static ISweepSegment performSweep(Point2D position, double rads,
			CorridorProgress corridorProgress) {
		/*
		 * TODO: Step 2c: Perform a sweep of given rotation.
		 */

		return null;
	}

	/**
	 * Returns whether or not a sweep segment intersects a saddle line.
	 * 
	 * @param sweepSegment
	 * @param saddleLine
	 * @return true if intersection, and false otherwise.
	 */
	private static boolean sweepSegmentIntersectsSaddleLine(
			ISweepSegment sweepSegment, SaddleLine saddleLine) {
		// TODO: Implement.
		return false;
	}

}
