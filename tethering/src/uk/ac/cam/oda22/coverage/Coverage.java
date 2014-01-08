package uk.ac.cam.oda22.coverage;

import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.robots.Robot;

/**
 * @author Oliver
 *
 */
public final class Coverage {

	public static CoverageResult performCoverage(Room room, Robot robot) {
		/**
		 * Perform input sanitisation.
		 * Note that the robot must have a positive (non-zero) area for coverage to be possible.
		 */
		
		/**
		 * Determine which obstacles are internal and compute the saddle lines.
		 * Treat the saddle lines as obstacles.
		 * Initialise a split point stack S.
		 */
		
		/**
		 * Sweep around the anchor point until an obstacle is hit which splits the potential in two directions along the obstacle.
		 * Call this the primary split point and add it to S.
		 */
		
		/**
		 * Explore the righthand corridor.
		 * Move sufficiently far along the obstacle edge such that the robot is at the edge of the covered area.
		 */
		
		return null;
	}
	
}
