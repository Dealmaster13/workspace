package uk.ac.cam.oda22.graphics;

import uk.ac.cam.oda22.graphics.shapes.Circle;
import uk.ac.cam.oda22.graphics.shapes.Line;

/**
 * @author Oliver
 *
 */
public interface IVisualiser {

	void drawLine(Line line);
	
	void drawLines(Line[] lines);
	
	void drawCircle(Circle circle);
	
}
