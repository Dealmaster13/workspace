package uk.ac.cam.oda22.coverage.simple;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.environment.SimpleRoom;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.pathfinding.astar.TetheredAStarPathfinding;
import uk.ac.cam.oda22.core.robots.Robot;
import uk.ac.cam.oda22.core.tethers.TetherConfiguration;
import uk.ac.cam.oda22.coverage.Coverage;
import uk.ac.cam.oda22.coverage.CoverageResult;
import uk.ac.cam.oda22.coverage.ShortestPathGrid;

/**
 * @author Oliver
 * 
 */
public class SimpleCoverage extends Coverage {

	@Override
	public CoverageResult performCoverage(Room room, Robot robot) {
		if (!(room instanceof SimpleRoom)) {
			Log.error("Coverage unimplemented for non-simple rooms");

			return null;
		}

		SimpleRoom sRoom = (SimpleRoom) room;

		// Get the robot coordinates and corresponding cell positions.
		double robotX = robot.getPosition().getX();
		double robotY = robot.getPosition().getY();
		RoomCellIndex robotCell = new RoomCellIndex(robot.getPosition(),
				sRoom.cellSize);

		// Check if the robot is at the centre of a cell.
		if (robotX != robotCell.x * sRoom.cellSize
				|| robotY != robotCell.y * sRoom.cellSize) {
			Log.warning("Robot is not at the centre of a cell, so it will be repositioned.");
		}

		// Fail if the robot is outside of the room boundaries.
		if (robotCell.x < 0 || robotCell.x >= sRoom.horizontalCellCount
				|| robotCell.y < 0 || robotCell.y >= sRoom.verticalCellCount) {
			Log.error("Robot is outside of the room boundaries.");

			return null;
		}

		RoomCellIndex anchorCell = new RoomCellIndex(robot.tether.getAnchor(),
				sRoom.cellSize);

		// Assert that the anchor point is in the same cell as the robot.
		if (!anchorCell.equals(robotCell)) {
			Log.error("Robot is not in the same cell as its anchor.");
		}

		// Fail if the robot is on an obstacle.
		if (sRoom.obstacleCells[robotCell.y][robotCell.x]) {
			Log.error("Robot is on an obstacle.");

			return null;
		}

		/*
		 * Step 1: Compute the shortest path from the anchor point to every cell
		 * in the room, thus providing a discrete notion of 'saddle lines'.
		 */

		ShortestPathGrid shortestPathGrid = computeShortestPaths(sRoom, robot,
				sRoom.cellSize);

		/*
		 * Step 2: Perform breadth first search to find the optimal coverage
		 * solution which re-covers the minimum number of cells possible.
		 */
		SimpleCoverageRouteNode node = findOptimalCoverageBFS(robotCell, sRoom,
				robot, shortestPathGrid, false);

		return null;
	}

	private static SimpleCoverageRouteNode findOptimalCoverageBFS(
			RoomCellIndex initialRobotCell, SimpleRoom room, Robot robot,
			ShortestPathGrid shortestPathGrid, boolean returnToInitialCell) {
		RoomCellIndex currentRobotCell = new RoomCellIndex(initialRobotCell);

		// This stores the list of nodes which are due to be explored.
		List<SimpleCoverageRouteNode> openList = new ArrayList<SimpleCoverageRouteNode>();

		TetherConfiguration initialTC = new TetherConfiguration(
				robot.tether.getAnchor());
		initialTC.addPoint(robot.getPosition());

		// Add the robot's current cell to the open list.
		SimpleCoverageRouteNode startNode = new SimpleCoverageRouteNode(
				currentRobotCell, null, initialTC);
		openList.add(startNode);

		// Get the number of cells which need to be covered.
		int totalCellCount = getNumberOfCoverableCells(room, shortestPathGrid,
				robot.tether.length);

		while (openList.size() > 0) {
			// Get the next node in the open list with minimal re-covered cells.
			// This ensures that the first solution found will be the optimal
			// one.
			SimpleCoverageRouteNode n = extractMinNode(openList);

			// Check if all cells have been covered.
			if (n.getCoveredCellCount() == totalCellCount) {
				// Check if the robot has returned to the initial cell if
				// necessary.
				if (!returnToInitialCell || n.index.equals(initialRobotCell)) {
					return n;
				}
			}

			// Get the valid cells adjacent to n.
			List<RoomCellIndex> cells = getAdjacentCellChoices(n.index, room);

			// For each valid adjacent cell, add a new node to the open list,
			// only if it doesn't re-cover a tile visited in the 're-covering'
			// phase. This prevents (useless) spurious solutions, since this
			// state can equally be achieved by the associated shorter path.
			for (RoomCellIndex cell : cells) {
				if (!n.isCellInRecoveringState(cell)) {
					// Get the coordinates of the cell.
					Point2D d = new Point2D.Double(cell.x * room.cellSize,
							cell.y * room.cellSize);

					// Get the resultant tether configuration.
					TetherConfiguration nextTC = TetheredAStarPathfinding
							.computeTetherChange(n.tc, robot.tether.length, d,
									room.obstacles, robot.radius);

					/*
					 * Note that no checks are made for crossing saddle lines
					 * due to there already being a check for tether crossing.
					 */

					// Add the node if the tether was not crossed and its length
					// was not exceeded.
					if (nextTC != null) {
						SimpleCoverageRouteNode newNode = new SimpleCoverageRouteNode(
								cell, n, nextTC);

						openList.add(newNode);
					}
				}
			}
		}

		// A solution was not found.
		return null;
	}

	private static List<RoomCellIndex> getAdjacentCellChoices(RoomCellIndex c,
			SimpleRoom room) {
		List<RoomCellIndex> l = new ArrayList<RoomCellIndex>();

		List<RoomCellIndex> adjacentCells = c.getAdjacentCells();

		// For each cell adjacent to c, check if it is a valid cell in open
		// space (not coincident with an obstacle).
		for (RoomCellIndex cell : adjacentCells) {
			if (cell.x >= 0 && cell.x < room.horizontalCellCount && cell.y >= 0
					&& cell.y < room.verticalCellCount) {
				if (!room.obstacleCells[c.y][c.x]) {
					l.add(cell);
				}
			}
		}

		return l;
	}

	private static int getNumberOfCoverableCells(SimpleRoom room,
			ShortestPathGrid shortestPathGrid, double maximumTetherLength) {
		int count = 0;

		for (int y = 0; y < room.verticalCellCount; y++) {
			for (int x = 0; x < room.horizontalCellCount; x++) {
				// Check if the cell is covered by an obstacle.
				if (!room.obstacleCells[y][x]) {
					// Check if the cell cannot be reached given the tether
					// length restrictions.
					if (shortestPathGrid.cells[y][x].potentialValue <= maximumTetherLength) {
						count++;
					}
				}
			}
		}

		return count;
	}

	private static SimpleCoverageRouteNode extractMinNode(
			List<SimpleCoverageRouteNode> openList) {
		SimpleCoverageRouteNode min = null;

		for (SimpleCoverageRouteNode n : openList) {
			if (min == null || n.compareTo(min) < 0) {
				min = n;
			}
		}

		openList.remove(min);

		return min;
	}

}
