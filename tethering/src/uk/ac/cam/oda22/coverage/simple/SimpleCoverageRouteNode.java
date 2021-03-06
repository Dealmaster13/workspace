package uk.ac.cam.oda22.coverage.simple;

import uk.ac.cam.oda22.core.tethers.TetherConfiguration;

/**
 * @author Oliver
 * 
 */
public class SimpleCoverageRouteNode implements
		Comparable<SimpleCoverageRouteNode> {

	/**
	 * The room cell index of the node.
	 */
	public final RoomCellIndex index;

	/**
	 * The node which comes before this node.
	 */
	public final SimpleCoverageRouteNode previousNode;

	/**
	 * The length of the path.
	 */
	public final int pathLength;

	/**
	 * The re-coverage count which corresponds to the previous node list. This
	 * is the total number of times any cell has been re-covered, rather than
	 * the number of unique cells which have been re-covered.
	 */
	public final int recoverageCount;

	/**
	 * The status of whether or not the robot is re-covering tiles at this
	 * stage.
	 */
	public final boolean recovering;
	
	public final TetherConfiguration tc;

	public SimpleCoverageRouteNode(RoomCellIndex index,
			SimpleCoverageRouteNode previousNode, TetherConfiguration tc) {
		this.index = index;
		this.previousNode = previousNode;
		this.tc = tc;

		this.pathLength = previousNode != null ? previousNode.pathLength + 1 : 0;

		// Get the re-coverage count of the previous node.
		int previousRecoverageCount = previousNode != null ? previousNode.recoverageCount
				: 0;

		// Check if this node has already been visited.
		this.recovering = isRevisited(this.index, this.previousNode);

		// Increment the recoverage count if the node has been revisited.
		this.recoverageCount = previousRecoverageCount
				+ (this.recovering ? 1 : 0);
	}

	/**
	 * Gets the number of unique cells which have been covered.
	 * 
	 * @return covered cell count
	 */
	public int getCoveredCellCount() {
		// Note that path length starts at 0 for the initial node.
		return pathLength + 1 - recoverageCount;
	}

	/**
	 * Checks whether or not a room cell is in one of the nodes which has
	 * recently been re-covered (i.e. during the period of being in the
	 * 're-covering' state).
	 * 
	 * @param c
	 * @return
	 */
	public boolean isCellInRecoveringState(RoomCellIndex c) {
		// Stop if this node is not in the recovering state.
		if (this.recovering == false) {
			return false;
		}

		if (this.index.equals(c)) {
			return true;
		}

		// Check if any previous 're-covering' nodes are at cell c.
		return previousNode.isCellInRecoveringState(c);
	}

	/**
	 * Checks whether or not a room cell has been revisited.
	 * 
	 * @param index
	 * @param previousNode
	 * @return true if the cell has been revisited, false otherwise
	 */
	private static boolean isRevisited(RoomCellIndex index,
			SimpleCoverageRouteNode previousNode) {
		SimpleCoverageRouteNode n = previousNode;

		while (n != null) {
			if (n.index.equals(index)) {
				return true;
			}
			
			n = n.previousNode;
		}

		return false;
	}

	@Override
	public int compareTo(SimpleCoverageRouteNode n) {
		// This node has higher value if it has a higher re-coverage count. Note
		// that lower is better in terms of prioritising which node to search
		// next.
		if (this.recoverageCount > n.recoverageCount) {
			return 1;
		} else if (this.recoverageCount < n.recoverageCount) {
			return -1;
		}

		// When the re-coverage counts are equal, compare against the total
		// number of cells covered. Note that higher is better, and the number
		// of unique cells covered is just the total number of cells minus the
		// re-coverage count.
		Integer pl1 = this.pathLength;
		Integer pl2 = n.pathLength;
		return pl2.compareTo(pl1);
	}
}
