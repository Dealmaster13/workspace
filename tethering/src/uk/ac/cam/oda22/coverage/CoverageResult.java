package uk.ac.cam.oda22.coverage;

import java.util.List;

import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.pathplanning.Path;

/**
 * @author Oliver
 *
 */
public class CoverageResult {

	public final List<IRobotAction> actions;

	public final Path path;

	public CoverageResult(List<IRobotAction> actions, Path path) {
		this.actions = actions;
		this.path = path;
	}

}
