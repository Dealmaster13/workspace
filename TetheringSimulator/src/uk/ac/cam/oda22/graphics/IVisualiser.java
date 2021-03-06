package uk.ac.cam.oda22.graphics;

import uk.ac.cam.oda22.graphics.shapes.DisplayShape;

/**
 * @author Oliver
 *
 */
public interface IVisualiser {

	void drawShape(DisplayShape s);

	void drawShapes(DisplayShape[] ss);
	
}
