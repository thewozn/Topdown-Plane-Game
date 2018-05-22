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
import java.awt.Image;
import javax.swing.ImageIcon;



public class Warning {
	Image image;
	
	public Warning(String imgPath){
			this.image =  new ImageIcon(imgPath).getImage();
	}
	
	public void draw(Graphics2D g2d, int posx, int posy) {
		g2d.drawImage(image, posx, posy, null);

	}
	
}
