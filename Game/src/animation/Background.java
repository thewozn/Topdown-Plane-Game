/**
*
* @author: anthony.woznica
*
* Absolutely ANY USE of this project REQUIRE THIS CREDIT IN THE HEADER OF THE FILE (WOZNICA Anthony).
* There are NO exceptions.
*
* This file contains the object Chronometer influencing the number of incomming flights.
*
**/

package animation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Background {
	BufferedImage image;
	
	public Background(){
		try {
			this.image =  ImageIO.read(new File(FlightController.getLevels()[FlightController.getIndex()]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2d) {
		g2d.drawImage(image, 0, 0, null);

	}
	
}
