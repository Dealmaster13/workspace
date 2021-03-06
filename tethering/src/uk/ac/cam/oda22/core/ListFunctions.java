package uk.ac.cam.oda22.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver
 * 
 */
public final class ListFunctions {

	public static boolean contains(Point2D point, List<Point2D> list,
			double fractionalError, double absoluteError) {
		for (Point2D p : list) {
			if (MathExtended.approxEqual(point, p, fractionalError,
					absoluteError)) {
				return true;
			}
		}

		return false;
	}

	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static <T> T removeLast(List<T> list) {
		return list.remove(list.size() - 1);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> addAll(T[]... lists) {
		List<T> l = new ArrayList<T>();

		for (T[] list : lists) {
			for (T e : list) {
				l.add(e);
			}
		}

		return l;
	}

	/**
	 * Performs bubble sort.
	 * 
	 * @param l
	 * @return sorted list
	 */
	public static List<Double> getSortedList(List<Double> l) {
		List<Double> lS = new ArrayList<Double>();
		lS.addAll(l);

		boolean swapMade = true;

		while (swapMade) {
			swapMade = false;

			for (int i = 0; i < lS.size() - 1; i++) {
				// Swap the two numbers if they are in the wrong order.
				if (lS.get(i) < lS.get(i + 1)) {
					double temp = lS.get(i);
					lS.set(i, lS.get(i + 1));
					lS.set(i + 1, temp);

					swapMade = true;
				}
			}
		}

		return lS;
	}

}
