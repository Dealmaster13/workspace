package uk.ac.cam.oda22.simulation;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import uk.ac.cam.oda22.core.ShapeFunctions;
import uk.ac.cam.oda22.core.environment.Obstacle;
import uk.ac.cam.oda22.core.environment.Room;
import uk.ac.cam.oda22.core.logging.Log;
import uk.ac.cam.oda22.core.robots.PointRobot;
import uk.ac.cam.oda22.core.robots.Robot;
import uk.ac.cam.oda22.core.robots.actions.IRobotAction;
import uk.ac.cam.oda22.core.tethers.SimpleTether;
import uk.ac.cam.oda22.core.tethers.Tether;
import uk.ac.cam.oda22.graphics.GraphicsFunctions;
import uk.ac.cam.oda22.graphics.IVisualiser;
import uk.ac.cam.oda22.graphics.VisualiserUsingJFrame;
import uk.ac.cam.oda22.graphics.shapes.Circle;
import uk.ac.cam.oda22.graphics.shapes.Line;
import uk.ac.cam.oda22.pathplanning.PathPlanner;

/**
 * @author Oliver
 *
 */
public class Simulator {

	/**
	 * This number represents the number of segments the tether (at maximum length) is split up into for simulation.
	 */
	private static int tetherSegments;
	
	/**
	 * The visualiser.
	 */
	private static IVisualiser visualiser;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// Create the visualiser.
		createVisualiser();

		List<Obstacle> l = new ArrayList<Obstacle>();

		List<Point2D> points = new ArrayList<Point2D>();
		points.add(new Point2D.Double(35, 45));
		points.add(new Point2D.Double(60, 55));
		points.add(new Point2D.Double(50, 15));
		points.add(new Point2D.Double(10, 25));

		Obstacle o = new Obstacle(points);

		l.add(o);

		Room room = new Room(100, 100, l);

		Point2D u = new Point2D.Double(80, 60);

		Point2D anchor = new Point2D.Double(0, 0);

		List<Point2D> X = new ArrayList<Point2D>();
		X.add(new Point2D.Double(50, 15));
		X.add(new Point2D.Double(80, 60));

		Tether t = new SimpleTether(anchor, 150, X);

		Robot robot = new PointRobot(u, 0, t);

		tetherSegments = 1000;
		
		Point2D goal = new Point2D.Double(20, 20);

		testPathPlanning(room, robot, goal);
		
		// Draw the graphics.
		drawRoom(room);
		drawRobot(robot);
		drawGoal(goal);
		drawTether(robot.tether);
		drawAnchor(anchor);
	}

	private static void createVisualiser() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				visualiser = new VisualiserUsingJFrame();
			}
		});
	}
	
	private static void drawRoom(Room room) {
		for (Obstacle o : room.obstacles) {
			for (Line2D edge : o.edges) {
				visualiser.drawLine(new Line(edge, Color.red, 1));
			}
		}
	}
	
	private static void drawRobot(Robot robot) {
		for (Line2D line : robot.getOutline().lines) {
			Line2D newLine = ShapeFunctions.translateShape(line, robot.getPosition().getX(), robot.getPosition().getY());
			
			visualiser.drawLine(new Line(newLine, Color.blue, 1));
		}
	}
	
	private static void drawGoal(Point2D goal) {
		Line2D[] l = ShapeFunctions.getCross(goal, 5);
		Line[] cross = GraphicsFunctions.colourLines(l, Color.green, 1);
		
		visualiser.drawLines(cross);
	}
	
	private static void drawTether(Tether tether) {
		if (tether instanceof SimpleTether) {
			SimpleTether t = (SimpleTether)tether;
			
			Point2D previousPoint = t.getAnchor();
			
			for (Point2D p : t.getFixedPoints()) {
				Line2D l = new Line2D.Double(previousPoint, p);
				
				visualiser.drawLine(new Line(l, Color.gray, 1));
				
				previousPoint = p;
			}
		}
		else {
			Log.error("Unknown tether type.");
		}
	}
	
	private static void drawAnchor(Point2D anchor) {
		visualiser.drawCircle(new Circle(anchor, 2, Color.black, 1));
	}

	private static void testPathPlanning(Room room, Robot robot, Point2D goal) {
		List<IRobotAction> actions = PathPlanner.performPathPlanning(room, robot, goal, tetherSegments);

		for (int i = 0; i < actions.size(); i++) {
			System.out.println(actions.get(i));
		}
	}

}
