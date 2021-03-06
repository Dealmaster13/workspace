package uk.ac.cam.oda22.coverage;

import java.awt.geom.Point2D;

import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.environment.VisibilityGraph;
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
public abstract class Coverage {

	public abstract CoverageResult performCoverage(Room room, Robot robot,
			boolean returnToInitialCell);

	/**
	 * Computes the shortest path from the anchor point to every other point in
	 * the room.
	 * 
	 * @param o
	 * @param room
	 * @return saddle line
	 */
	public static ShortestPathGrid computeShortestPaths(Room room, Robot robot,
			double gridSeparation) {
		Point2D anchorPoint = robot.tether.getAnchor();

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
								room.obstacles, initialTC, robot.tether.length,
								robot.radius);

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

}
