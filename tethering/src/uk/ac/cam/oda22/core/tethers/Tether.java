package uk.ac.cam.oda22.core.tethers;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.oda22.core.MathExtended;
import uk.ac.cam.oda22.core.logging.Log;

/**
 * @author Oliver
 * 
 */
public abstract class Tether {

	/**
	 * The position of the anchor point.
	 */
	protected Point2D anchor;

	/**
	 * The maximum length of the tether.
	 */
	public final double length;

	protected TetherConfiguration configuration;

	public Tether(Point2D anchor, double length,
			TetherConfiguration configuration) throws Exception {
		this.anchor = anchor;
		this.length = length;
		this.configuration = configuration;

		double usedLength = getUsedLength();

		// Check that the tether does not exceed its length restriction.
		if (usedLength > length
				&& !MathExtended.approxEqual(length, usedLength, 0.001, 0)) {
			throw new Exception("Used length (" + usedLength
					+ ") exceeds maximum length (" + length + ").");
		}
	}

	public Point2D getAnchor() {
		return this.anchor;
	}

	/**
	 * Gets the tether configuration which does not include the segment from the
	 * anchor point.
	 * 
	 * @return tether configuration
	 */
	public TetherConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Gets the full tether configuration including the segment from the anchor
	 * point.
	 * 
	 * @return full tether configuration
	 */
	public TetherConfiguration getFullConfiguration() {
		List<Point2D> ps = new ArrayList<Point2D>();
		ps.add(this.anchor);
		ps.addAll(this.configuration.points);

		return new TetherConfiguration(ps);
	}

	public void setConfiguration(TetherConfiguration configuration) {
		this.configuration = configuration;
	}

	public void setFullConfiguration(TetherConfiguration configuration) {
		if (configuration != null && configuration.points.size() > 0) {
			this.anchor = configuration.points.get(0);

			this.configuration = new TetherConfiguration();

			for (int i = 1; i < configuration.points.size(); i++) {
				this.configuration.addPoint(configuration.points.get(i));
			}
		} else {
			Log.error("Invalid tether configuration.");
		}
	}

	public abstract double getUsedLength();

	public abstract Point2D getPositionByDistance(double w);

	public abstract Point2D getLastPoint();

	public abstract ITetherSegment getTetherSegment(double startW, double endW);

}
