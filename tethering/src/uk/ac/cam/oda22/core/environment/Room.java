package uk.ac.cam.oda22.core.environment;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver
 *
 */
public abstract class Room {

	public final double width;

	public final double height;
	
	public final List<Obstacle> obstacles;

	public Room(double width, double height, List<Obstacle> obstacles) {
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		
		this.addRoomEdges(width, height);
	}

	/**
	 * Expand all of the obstacles in the room by a given radius.
	 * 
	 * @param radius
	 * @return expanded obstacles
	 */
	public List<Obstacle> getExpandedObstacles(double radius) {
		List<Obstacle> l = new ArrayList<Obstacle>();

		for (int i = 0; i < this.obstacles.size(); i++) {
			l.add(this.obstacles.get(i).expandObstacle(radius));
		}

		return l;
	}

	/**
	 * Adds the four room edges as four obstacles with no width.
	 */
	private void addRoomEdges(double width, double height) {
		Point2D p1 = new Point2D.Double(0, 0);
		Point2D p2 = new Point2D.Double(width, 0);
		Point2D p3 = new Point2D.Double(0, height);
		Point2D p4 = new Point2D.Double(width, height);

		List<Point2D> el1 = new ArrayList<Point2D>();
		el1.add(p1);
		el1.add(p2);

		List<Point2D> el2 = new ArrayList<Point2D>();
		el2.add(p2);
		el2.add(p4);

		List<Point2D> el3 = new ArrayList<Point2D>();
		el3.add(p4);
		el3.add(p3);

		List<Point2D> el4 = new ArrayList<Point2D>();
		el4.add(p3);
		el4.add(p1);

		this.obstacles.add(new Obstacle(el1));
		this.obstacles.add(new Obstacle(el2));
		this.obstacles.add(new Obstacle(el3));
		this.obstacles.add(new Obstacle(el4));
	}
	
}
