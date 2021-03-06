package uk.ac.cam.oda22.coverage;

/**
 * @author Oliver
 * 
 */
public class CorridorProgress {

	private int currentPotential;

	/**
	 * Stores whether or not the robot is searching the corridor rightwards.
	 */
	private boolean rightSearch;

	private CorridorProgress leftCorridor;

	private CorridorProgress rightCorridor;

	public CorridorProgress(int initialPotential, boolean rightSearch) {
		this.currentPotential = initialPotential;
		this.rightSearch = rightSearch;
		
		this.leftCorridor = null;
		this.rightCorridor = null;
	}

	public CorridorProgress getLeftCorridor() {
		return this.leftCorridor;
	}

	public CorridorProgress getRightCorridor() {
		return this.rightCorridor;
	}

	public int incrementPotential() {
		return ++currentPotential;
	}

	public void splitCorridor() {
		this.leftCorridor = new CorridorProgress(this.currentPotential, false);
		this.rightCorridor = new CorridorProgress(this.currentPotential, true);
	}

}
