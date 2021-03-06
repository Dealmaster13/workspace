package uk.ac.cam.oda22.coverage;

import uk.ac.cam.oda22.pathplanning.TetheredPath;

/**
 * @author Oliver
 * 
 */
public class ShortestPathGridCell {

	public final double x;

	public final double y;

	public final TetheredPath tetheredPath;

	public final double potentialValue;

	public ShortestPathGridCell(double x, double y, TetheredPath tetheredPath) {
		this.x = x;
		this.y = y;
		this.tetheredPath = tetheredPath;

		this.potentialValue = this.tetheredPath != null ? this.tetheredPath.tc
				.length() : Double.POSITIVE_INFINITY;
	}

}
