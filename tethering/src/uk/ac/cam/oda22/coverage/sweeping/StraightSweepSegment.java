package uk.ac.cam.oda22.coverage.sweeping;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Oliver
 *
 */
public class StraightSweepSegment implements ISweepSegment {

	public final Line2D line;
	
	public StraightSweepSegment(Line2D line) {
		this.line = line;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		StraightSweepSegment s = (StraightSweepSegment) o;
		
		if (this.line.getP1().equals(s.line.getP1()) && this.line.getP2().equals(s.line.getP2())) {
			return true;
		}
		
		if (this.line.getP1().equals(s.line.getP2()) && this.line.getP2().equals(s.line.getP1())) {
			return true;
		}
		
		return false;
	}

	@Override
	public Point2D getStartPoint() {
		return line.getP1();
	}

	@Override
	public Point2D getEndPoint() {
		return line.getP2();
	}
	
}
