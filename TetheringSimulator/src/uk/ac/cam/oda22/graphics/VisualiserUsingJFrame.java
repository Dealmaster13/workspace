package uk.ac.cam.oda22.graphics;

import javax.swing.JFrame;

import uk.ac.cam.oda22.graphics.shapes.Circle;
import uk.ac.cam.oda22.graphics.shapes.Line;

/**
 * @author Oliver Allbless
 *
 */
public class VisualiserUsingJFrame extends JFrame implements IVisualiser {

	private static final long serialVersionUID = 2748836618770659263L;

	private VisualiserJPanel panel;
	
	public VisualiserUsingJFrame() {
		this.init();
	}

	public void init() {
		this.setTitle("Simulation");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.panel = new VisualiserJPanel();
		this.add(panel);

		this.pack();
		this.setVisible(true);

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
	}

	@Override
	public void drawLine(Line line) {
		this.panel.drawLine(line);
	}

	@Override
	public void drawLines(Line[] lines) {
		this.panel.drawLines(lines);
	}

	@Override
	public void drawCircle(Circle circle) {
		this.panel.drawCircle(circle);
	}

}
