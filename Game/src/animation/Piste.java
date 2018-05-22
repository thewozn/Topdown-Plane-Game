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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piste {
	BufferedImage image;
	BufferedImage invalid;
	Area preLanding;
	Area postLanding;
	Point2D location;
	double angle;
	double scale;
	private boolean ouvert = true;
	boolean useHelicopter;
	
	/**
	 * @return the ouvert
	 */
	public boolean isOuvert() {
		return ouvert;
	}

	/**
	 * @param ouvert the ouvert to set
	 */
	public void setOuvert(boolean ouvert) {
		this.ouvert = ouvert;
	}
	
	public Piste(String imgPath, double x, double y, double scale, double angle){
		try {
			this.invalid = ImageIO.read(new File("images/invalid.png"));
			this.image =  ImageIO.read(new File(imgPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.location = new Point2D.Double(x, y);
		this.angle = angle;
		this.scale = scale;
		this.useHelicopter = false;
		this.preLanding = new Area(new Rectangle2D.Double(0, 0, image.getWidth(), (int)image.getHeight()/10));
		this.postLanding = new Area(new Rectangle2D.Double(0, image.getHeight()/10, image.getWidth(), (int)image.getHeight()/6));
	}
	
	public Piste(Aeroport a){
		//AffineTransform at = a.getAffineTransform();
		try {
			this.invalid = ImageIO.read(new File("images/invalid.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.location = new Point2D.Double(a.location.getX(), a.location.getY());
		this.angle = a.angle;
		this.scale = a.scale;
		this.useHelicopter = true;
		this.preLanding = new Area(new Ellipse2D.Double(120, 264 , 270, 270));
	}
	
	public boolean isIn(Plane p) {
		p.preLanding = false;
		p.postLanding = false;
		if(p.getPath() == null) return false;
		AffineTransform at = getAffineTransform();
		for(Point2D po : p.getPath().points){
			
			if( preLanding.createTransformedArea(at).getBounds().contains(po)){
				p.preLanding = true;
			}
			else if(!preLanding.createTransformedArea(at).getBounds().contains(po) && !postLanding.createTransformedArea(at).getBounds().contains(po)){
				p.preLanding = false;
				p.postLanding = false;
			}
			else if( postLanding.createTransformedArea(at).getBounds().contains(po) && p.preLanding){
				p.postLanding = true;
			}
		}
		if(p.preLanding && p.postLanding){return true;}
		else return false;
	}
	
	public boolean isInH(Plane p) {
		p.preLanding = false;
		if(p.getPath() == null) return false;
		AffineTransform at = getAffineTransform();

		if( preLanding.createTransformedArea(at).getBounds().contains(p.getPath().points.get(p.getPath().points.size()-1))){
			p.preLanding = true;
		}
		if(p.preLanding){return true;}
		else return false;
	}
	
	public void draw(Graphics2D g2d) {
		AffineTransform at = getAffineTransform();
		g2d.drawImage(image, at, null);
		g2d.setColor(new Color(60, 170, 50, 90));
		g2d.fill(preLanding.createTransformedArea(at));
		if(!this.useHelicopter){
			g2d.setColor(new Color(254, 38, 27, 90));
			g2d.fill(postLanding.createTransformedArea(at));
		}
		if(!this.ouvert){
			if(!this.useHelicopter){
				g2d.drawImage(invalid, at, null);
			}
			else{
				
				g2d.drawImage(invalid, preLanding.createTransformedArea(at).getBounds().x, preLanding.createTransformedArea(at).getBounds().y, null);
			}
		}
	}
	
	public AffineTransform getAffineTransform() {
		AffineTransform at = new AffineTransform();
		// Translate
		at.translate(location.getX(), location.getY());
		// Rotate
		at.rotate(angle);
		// Scale
		at.scale(scale, scale);
		// Center
		//at.translate(-image.getWidth() / 2f, -image.getHeight() / 2f);
		return at;
	}
}
