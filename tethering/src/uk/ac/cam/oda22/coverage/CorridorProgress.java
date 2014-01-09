package uk.ac.cam.oda22.coverage;

/**
 * @author Oliver
 *
 */
public class CorridorProgress {

	private int currentPotential;
	
	private CorridorProgress leftCorridor;
	
	private CorridorProgress rightCorridor;
	
	public CorridorProgress(int initialPotential) {
		this.currentPotential = initialPotential;
		
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
		this.leftCorridor = new CorridorProgress(this.currentPotential);
		this.rightCorridor = new CorridorProgress(this.currentPotential);
	}
	
}
