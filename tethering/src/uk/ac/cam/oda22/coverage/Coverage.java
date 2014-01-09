package uk.ac.cam.oda22.coverage;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import uk.ac.cam.oda22.core.environment.Obstacle;
import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.robots.Robot;

/**
 * @author Oliver
 *
 */
public final class Coverage {

	public static CoverageResult performCoverage(Room room, Robot robot) {
		/**
		 * TODO: Perform input sanitisation.
		 * Note that the robot must have a positive (non-zero) area for coverage to be possible.
		 */
		
		if (robot.radius <= 0) {
			Log.error("Robot must have a positive area to perform coverage.");
			
			return null;
		}
		
		/**
		 * TODO: Determine which obstacles are internal and compute the saddle lines.
		 * Treat the saddle lines as obstacles.
		 * Initialise a split point stack S.
		 */
		
		// Find the internal obstacles.
		List<Obstacle> internalObstacles = getInternalObstacles(room);
		
		List<SaddleLine> saddleLines = new LinkedList<SaddleLine>();
		
		// Compute the saddle lines.
		for (Obstacle o : internalObstacles) {
			saddleLines.add(computeSaddleLine(o, room));
		}
		
		Stack<Point2D> splitPoints = new Stack<Point2D>();
		
		CorridorProgress corridorProgress = new CorridorProgress(0);
		
		/**
		 * TODO: Sweep around the anchor point until an obstacle is hit which splits the potential in two directions along the obstacle.
		 * Call this the primary split point and add it to S.
		 */
		
		/**
		 * TODO: Explore the right-hand corridor.
		 * Move sufficiently far along the obstacle edge such that the robot is at the edge of the covered area.
		 * Note that this new position should be the appropriate position for the current potential level.
		 */
		
		return null;
	}
	
	/**
	 * Gets the internal obstacles in a room.
	 * 
	 * @param obstacles
	 * @param room
	 * @return internal obstacle list
	 */
	private static List<Obstacle> getInternalObstacles(Room room) {
		List<Obstacle> internalObstacles = new LinkedList<Obstacle>();
		
		for (Obstacle o : room.obstacles) {
			if (room.isObstacleInternal(o)) {
				internalObstacles.add(o);
			}
		}
		
		return internalObstacles;
	}
	
	/**
	 * Computes the saddle line for an internal obstacle.
	 * 
	 * @param o
	 * @param room
	 * @return saddle line
	 */
	private static SaddleLine computeSaddleLine(Obstacle o, Room room) {
		// TODO: Implement.
		return null;
	}
	
	/**
	 * Returns whether or not a sweep segment intersects a saddle line.
	 * 
	 * @param sweepSegment
	 * @param saddleLine
	 * @return true if intersection, and false otherwise.
	 */
	private static boolean sweepSegmentIntersectsSaddleLine(ISweepSegment sweepSegment, SaddleLine saddleLine) {
		// TODO: Implement.
		return false;
	}
	
}
