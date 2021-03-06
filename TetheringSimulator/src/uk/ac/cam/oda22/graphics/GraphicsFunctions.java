package uk.ac.cam.oda22.graphics;

import java.awt.Color;
import java.awt.geom.Line2D;

import uk.ac.cam.oda22.graphics.shapes.Line;

/**
 * @author Oliver
 *
 */
public class GraphicsFunctions {

	public static Line colourLine(Line2D lines, Color colour, float thickness) {
		return new Line(lines, colour, thickness);
	}

	public static Line[] colourLines(Line2D[] lines, Color colour, float thickness) {
		Line[] newLines = new Line[lines.length];
		
		for (int i = 0; i < lines.length; i++) {
			newLines[i] = colourLine(lines[i], colour, thickness);
		}
		
		return newLines;
	}
	
}
