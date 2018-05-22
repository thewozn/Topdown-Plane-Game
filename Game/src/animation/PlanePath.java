/**
*
* @author: anthony.woznica
*
* Absolutely ANY USE of this project REQUIRE THIS CREDIT IN THE HEADER OF THE FILE (WOZNICA Anthony).
* There are NO exceptions.
*
*
**/

package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PlanePath {

	Point2D location;
	double angle;
	int index;
	ArrayList<Point2D> points;
	boolean leadsToAirport;
	boolean created;
	boolean selected = false;
	Area pin;

	PlanePath(int x, int y) {
		points = new ArrayList<Point2D>();
		location = new Point2D.Double(x, y);
		addPoint(location);
	}

	public void addPoint(int x, int y) {
		points.add(new Point2D.Double(x, y));
	}

	public void addPoint(Point2D point) {
		points.add(point);
	}

	public double getAngle() {
		return angle;
	}

	/**
	 * @return the points
	 */
	public ArrayList<Point2D> getPoints() {
		return points;
	}

	/**
	 * @param points
	 *            the points to set
	 */
	public void setPoints(ArrayList<Point2D> points) {
		this.points = points;
	}

	public void interpolate(Point2D c, Piste p) {

		this.created = false;
		this.leadsToAirport = true;
		AffineTransform at = p.getAffineTransform();
		Point2D mid = new Point2D.Double(p.postLanding.createTransformedArea(at).getBounds().getX(),
				p.postLanding.createTransformedArea(at).getBounds().getY());

		this.points.add(c);
		this.points.add(mid);
	}

	

	
	/**
	 * Calcule la distance normale entre un point c et le segment formé par les
	 * points a et b
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	protected static double normalDistance(Point2D a, Point2D b, Point2D c) {
		// y = mx + p
		double m = (b.getY() - a.getY()) / (b.getX() - a.getX());
		double p = a.getY() - m * a.getX();

		// d(A, (d)) = |mx - y + p| / sqrt(1 + m²)
		double distance = Math.abs(m * c.getX() - c.getY() + p) / Math.sqrt(1 + m * m);

		return distance;
	}

	protected static ArrayList<Point2D> Concat(ArrayList<Point2D> a, ArrayList<Point2D> b) {
		ArrayList<Point2D> result = new ArrayList<Point2D>(a.size() + b.size());

		for (Point2D p : a) {
			result.add(p);
		}

		for (Point2D p : b) {
			result.add(p);
		}

		return result;
	}

	protected static ArrayList<Point2D> Ramer(ArrayList<Point2D> points, int start, int end, double epsilon) {

		ArrayList<Point2D> results;

		// On trouve le point le plus eloigne du segment
		double dmax = 0;
		int pivot = 0;

		for (int i = 1; i < points.size(); i++) {
			double d = normalDistance(points.get(start), points.get(end - 1), points.get(i));
			if (d > dmax) {
				// System.out.println("i = " + i + " | " + (int)dmax);
				pivot = i;
				dmax = d;
			}
		}

		// Si dmax est superieure au seuil de tolerance, on simplifie
		if (dmax >= epsilon) {
			// Partie inferieure de la liste de points
			ArrayList<Point2D> lower = Ramer(new ArrayList<Point2D>(points.subList(0, pivot)), 0, pivot - 1, epsilon);
			// Partie superieure de la liste de points
			ArrayList<Point2D> upper = Ramer(new ArrayList<Point2D>(points.subList(pivot, end)), 0, end - pivot,
					epsilon);

			results = Concat(lower, upper);
		}

		else {
			results = points;
		}

		return results;

	}

	protected static Point2D moyenne(Point2D a, Point2D b, Point2D c, Point2D d, Point2D e, Point2D f) {
		double moyenne_x = (a.getX() + b.getX() + c.getX() + d.getX() + e.getX() + f.getX()) / 6;
		double moyenne_y = (a.getY() + b.getY() + c.getY() + d.getY() + e.getY() + f.getY()) / 6;
		return new Point2D.Double(moyenne_x, moyenne_y);
	}

	protected void lissageMoyenne() {
		for (int i = 3; i < this.points.size() - 3; i++) {
			this.points.set(i, moyenne(this.points.get(i - 3), this.points.get(i - 2), this.points.get(i - 1),
					this.points.get(i + 1), this.points.get(i + 2), this.points.get(i + 3)));
		}
	}
	/*
	 * private double pow(double b, int i) { if ( i == 0 ) { return 1; } return
	 * ( b * pow(b, i-1)); }
	 */

	/**
	 * @return the created
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(boolean created) {
		this.created = created;
	}

	public Point2D getLocation() {
		return location;
	}

	public void setLeadsToAirport(boolean leadsToAirport) {
		this.leadsToAirport = leadsToAirport;
	}

	public boolean getLeadsToAirport() {
		return leadsToAirport;
	}

	// Avance de "amount" pixels le long du chemin "path" ou, s'il n'est pas
	// spécifié, dans la direction spécifiée par "angle".
	// Retourne "vrai" si le bout du chemin est atteint. Faux sinon.
	public boolean progressBy(double amount) {
		int size = points.size();
		if (size == 0) {
			return false;
		}
		double distance = 0d, rest, progress, totalDistance = 0d;
		Point2D point, lastPoint;
		point = lastPoint = points.get(0);
		int i;
		for (i = index; i < size; i++) {
			point = points.get(i);
			distance = location.distance(point);
			totalDistance += distance;
			if (totalDistance > amount) {
				rest = totalDistance - amount;
				progress = (distance - rest) / distance;
				location.setLocation(lastPoint.getX() + (point.getX() - lastPoint.getX()) * progress,
						lastPoint.getY() + (point.getY() - lastPoint.getY()) * progress);
				index = i;
				angle = Math.atan2(point.getY() - lastPoint.getY(), point.getX() - lastPoint.getX());
				return false;
			} else {
				lastPoint = point;
			}
		}
		if (size > 1) {
			lastPoint = points.get(size - 2);
		}
		location.setLocation(point);
		angle = Math.atan2(point.getY() - lastPoint.getY(), point.getX() - lastPoint.getX());
		index = size - 1;
		return true;
	}

	public boolean contains(Point2D point) {
		if (pin == null)
			return false;
		return this.pin.getBounds().contains(point);
	}

	public void draw(Graphics2D g2d) {
		long size = points.size();
		if (index < size) {
			GeneralPath path = new GeneralPath();
			path.moveTo(location.getX(), location.getY());
			for (int i = index; i < size; i++) {
				Point2D point = points.get(i);
				path.lineTo(point.getX(), point.getY());
			}
			g2d.draw(path);
			if (!this.leadsToAirport && !this.isCreated()) {
				this.pin = new Area(new Ellipse2D.Double(points.get((int) size - 1).getX() - 10,
						points.get((int) size - 1).getY() - 10, 20, 20));
				
				if (Animator.level.equals("images/Desert_level.png"))
					g2d.setColor(new Color(240, 240, 240));
				else if (Animator.level.equals("images/snow_level.jpg"))
					g2d.setColor(new Color(237, 32, 21));
				g2d.fill(pin);
			}
		}
	}
}
