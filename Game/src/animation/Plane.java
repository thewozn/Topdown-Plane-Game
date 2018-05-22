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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;

public class Plane {
	
	Point2D location;
	double angle;
	double speed;
	PlanePath path;
	double scale;
	Image image;
	Area selectionRing;
	Area collideRing;
	boolean selected;
	boolean collided;
	boolean isHelicopter;
	boolean preLanding = false;
	boolean postLanding = false;
	
	boolean prior = false;
	Image priorImage =  new ImageIcon("images/interface/yellowdot.gif").getImage();
	
	
	Plane(int x, int y) {
		this("images/plane.png", new Point2D.Double(x, y), 0, 0.5d, 2d, false);
	}
	
	Plane(Point2D point) {
		this("images/plane.png", point, 0, 0.5d, 2d, false);
	}
	
	Plane(Point2D point, double angle) {
		this("images/plane.png", point, angle, 0.5d, 2d, false);
	}
	
	Plane(String path, int x, int y) {
		this(path, new Point2D.Double(x, y), 0, 0.5d, 2d, false);
	}
	
	Plane (String imgPath, Point2D point, double angle, double scale, double speed, boolean isHelicopter) {

			this.image =  new ImageIcon(imgPath).getImage();
			this.location = (Point2D) point.clone();
			this.angle = angle;
			this.scale = scale;
			this.speed = speed;
			this.isHelicopter = isHelicopter;
			this.selectionRing = new Area(new Ellipse2D.Double(0, 0, image.getWidth(null), image.getWidth(null)));
			this.collideRing = new Area(new Ellipse2D.Double(0, 0, image.getWidth(null), image.getHeight(null)));
			this.collideRing.subtract(new Area(new Ellipse2D.Double(image.getWidth(null)/6,image.getHeight(null)/6, 2*image.getWidth(null)/3, 2*image.getHeight(null)/3)));
	}
	
	
	public boolean progress() {
		if (path != null) {
			// L'avion avance sur le chemin
			boolean done = path.progressBy(speed);
			location.setLocation(path.getLocation());
			angle = path.getAngle();
			return done;
		} else {
			// L'avion avance dans la direction actuelle
			double x = Math.cos(angle)*speed + location.getX();
			double y = Math.sin(angle)*speed + location.getY();
			location.setLocation(x, y);
			return false;
		}
	}
	
	public void draw(Graphics2D g2d) {
		AffineTransform at = getAffineTransform();
		
		if (prior){
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.8f));
			g2d.drawImage(priorImage, at, null);
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1));
		}
		
		if (selected) {
			g2d.setColor(new Color(66, 164, 244, 95));
			g2d.fill(selectionRing.createTransformedArea(at));
		}
		if (collided) {
			g2d.setColor(new Color(254, 38, 27, 95));
			g2d.fill(collideRing.createTransformedArea(at));
		}
		g2d.drawImage(image, at, null);
	}
	
	public static boolean collide(Plane p, Plane q){
		Point2D pC = p.getLocation();
		Point2D qC = q.getLocation();
		double d = Math.sqrt (Math.pow((qC.getX() - pC.getX()), 2) + Math.pow((qC.getY() - pC.getY()), 2));
		double dmin = p.scale*64 + q.scale*64 + 15;
		if(d > dmin) return false;
		else return true;
	}
	
	public static boolean collapse(Plane p, Plane q){
		Point2D pC = p.getLocation();
		Point2D qC = q.getLocation();
		double d = Math.sqrt (Math.pow((qC.getX() - pC.getX()), 2) + Math.pow((qC.getY() - pC.getY()), 2));
		double dcollide = p.scale*64 + q.scale*64 -40;
		if(d > dcollide) return false;
		else return true;
	}
	
	public Point2D getCenter(){
			double x = (this.image.getWidth(null)*this.scale/2)*Math.cos(this.angle - Math.PI/2) - (this.image.getHeight(null)*this.scale/2)*Math.sin(this.angle - Math.PI/2);
			double y = (this.image.getWidth(null)*this.scale/2)*Math.sin(this.angle - Math.PI/2) + (this.image.getHeight(null)*this.scale/2)*Math.cos(this.angle - Math.PI/2);
			return(new Point2D.Double(x, y));
	}
	
	public boolean contains(Point2D point) {
		return selectionRing.createTransformedArea(getAffineTransform()).contains(point);
	}
	
	public PlanePath getPath() {
		return path;
	}
	
	public void setPath(PlanePath path) {
		this.path = path;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void setCollided(boolean collided) {
		this.collided = collided;
	}
	
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public Point2D getLocation() {
		return location;
	}

	public double getAngle() {
		return angle;
	}

	public double getSpeed() {
		return speed;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public boolean isCollided() {
		return collided;
	}
	
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public AffineTransform getAffineTransform() {
		AffineTransform at = new AffineTransform();
		// Translate
		at.translate(location.getX(), location.getY());
		// Rotate
		at.rotate(angle+Math.PI/2);
		// Scale
		at.scale(scale, scale);
		// Center
		at.translate(-image.getWidth(null) / 2f, -image.getHeight(null) / 2f);
		return at;
	}
}
