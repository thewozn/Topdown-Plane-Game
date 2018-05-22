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
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Aeroport {
	BufferedImage image;
	Point2D location;
	double angle;
	double scale;
	
	public Aeroport(String imgPath, int x, int y, double scale, double angle){
		try {
			this.image =  ImageIO.read(new File(imgPath));
			this.location = new Point2D.Double(x, y);
			this.angle = 0;
			this.scale = scale;
			this.angle = angle;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void draw(Graphics2D g2d) {
		AffineTransform at = getAffineTransform();
		g2d.drawImage(image, at, null);
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
