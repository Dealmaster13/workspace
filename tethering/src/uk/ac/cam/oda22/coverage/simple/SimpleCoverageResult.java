package uk.ac.cam.oda22.coverage.simple;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.coverage.CoverageResult;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 * 
 */
public class SimpleCoverageResult extends CoverageResult {

	public SimpleCoverageResult(List<IRobotAction> actions, Path path) {
		super(actions, path);
	}

	public static List<RoomCellIndex> getCellList(SimpleCoverageRouteNode finalNode) {
		List<RoomCellIndex> reverseCellList = new ArrayList<RoomCellIndex>();

		SimpleCoverageRouteNode currentNode = finalNode;

		while (currentNode != null) {
			reverseCellList.add(currentNode.index);

			currentNode = currentNode.previousNode;
		}

		List<RoomCellIndex> cellList = new ArrayList<RoomCellIndex>();

		for (int i = reverseCellList.size() - 1; i >= 0; i--) {
			cellList.add(reverseCellList.get(i));
		}
		
		return cellList;
	}

	public static Path getPath(List<RoomCellIndex> cellList, double cellSize) {
		Path path = new Path();

		for (int i = 0; i < cellList.size(); i++) {
			RoomCellIndex cell = cellList.get(i);

			Point2D point = cell.getPosition(cellSize);

			path.addPoint(point);
		}

		return path;
	}

}
