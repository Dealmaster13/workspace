package uk.ac.cam.oda22.core.tethers;

import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 *
 */
public class SimpleTetherSegment implements ITetherSegment {

	public final Path path;
	
	public SimpleTetherSegment(Path path) {
		this.path = path;
	}
	
}
