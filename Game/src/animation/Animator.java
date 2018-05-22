package animation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Animator extends JPanel implements ActionListener {
	
	/*
	 * 
	 * VARIABLES
	 * 
	 */
		
		//Variables de programme
		public int avionsAtterris = 0;
		private PlanePath path;
		private ArrayList<PlanePath> paths;
		private ArrayList<Plane> planes;
		private ArrayList<Piste> pistes = new ArrayList<Piste>(30);
		private Hashtable<Plane, Integer>incomingPlanes = new Hashtable<Plane, Integer>(40);
		
		//Variables de fonctionnement
		private Timer timer;
		public boolean anim = true;
		private int intervalle = 0;
		
		//Variables de niveau
		private int delay, difficulty;
		static String level = FlightController.getLevels()[FlightController.getIndex()];
		Chronometer time;
		
		//Variables d'image
		private BufferedImage rain; //Image de l'intemperie
		Background bg;
		Warning wn;
		Aeroport apt;
		BufferedImage score;
		BufferedImage shade;
		
		//Variables d'options graphiques
		private float opacityIndex = 0.01f;
		private boolean ferme = false; //S'il y a au moins une piste fermee
		
		
		
	
		
	/*
	 * 
	 * GETTERS AND SETTERS
	 * 
	 */

	/**
	 * @return planes Liste d'avions
	 */
	public ArrayList<Plane> getPlanes() {
		return planes;
	}
		
	/**
	 * @return planes Liste d'avions
	 */
	public static String getLevel() {
		return level;
	}
	
	/*
	 * 
	 * CONSTRUCTEURS
	 * 
	 */	
	/**
	 * CONSTRUCTEUR
	 */
	Animator() {
		this(30);
	}

	/**
	 * CONSTRUCTEUR
	 * @param delay Frames per second
	 */
	Animator(int delay) {
		super();
		
		/*
		 * Initialisation des composantes statiques
		 * Image.IO est utilis� pour les images statiques en raison de sa vitesse (10% plus rapide)
		 * https://books.google.fr/books?isbn=0596007302
		 */
		this.bg = new Background();
		this.wn = new Warning("images/warning.gif");
		
		if(level.equals("images/Desert_level.png")){
			this.apt = new Aeroport("images/airports/aeroport.png", 1188, -287, 0.37d, Math.PI/3);
			pistes.add(new Piste("images/pistes/p1.png", 650, 318, 0.8, -2*Math.PI/3));
			pistes.add(new Piste("images/pistes/p1.png", 713, 348, 0.8, -2*Math.PI/3));
			pistes.add(new Piste("images/pistes/p1.png", 713 + 63, 378, 0.8, -2*Math.PI/3));
			
			try {
				rain = ImageIO.read(new File("images/layers/rain.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if(level.equals("images/snow_level.jpg")){
			this.apt = new Aeroport("images/airports/aeroport_snow.png", 1150, 150, 0.3d, Math.PI/2);
			pistes.add(new Piste("images/pistes/p2.png", 350, 400, 1 ,-Math.PI/2));
			pistes.add(new Piste(apt));
			try {
				rain = ImageIO.read(new File("images/layers/snow.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		try {
			this.score = ImageIO.read(new File("images/interface/avatt.png"));
			this.shade = ImageIO.read(new File("images/planes/shade.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		/*
		 * Initialisation de la difficulte et du chronometre
		 */
		difficulty = (FlightController.currentDiff.equals("Facile")) ? 1 
				: (FlightController.currentDiff.equals("Normal")) ? 2 
				: (FlightController.currentDiff.equals("Difficile")) ? 3 : 6;
		
		time = new Chronometer();
		
		// Timer
		this.delay = delay;
		timer = new Timer(delay, this);
		
		// Chemins et avions
		paths = new ArrayList<PlanePath>();
		planes = new ArrayList<Plane>();
		
		/*
		 * LISTENERS
		 */
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				boolean planeSelected = false;
				for (Plane p : planes) {
					p.setSelected(false);
					if (p.contains(event.getPoint()) && p.prior == false) {
						p.setSelected(true);
						planeSelected = true;
						FlightController.setPlaneSpeed(p.speed);
						path = new PlanePath(event.getX(), event.getY());
						
						repaint();
					}
				}
				
				if(!planeSelected == true){
					for (PlanePath pa: paths) {
						pa.selected = false;
						if(!pa.leadsToAirport){
						if (pa.contains(event.getPoint())) {
							pa.selected = true;
						}}
					}
				}
				
			}

			public void mouseReleased(MouseEvent event) {
				for (Plane p : planes) {

					if (p.isSelected()) {
						if (p.getPath() != null) {
							paths.remove(p.getPath());
							p.getPath().lissageMoyenne();
							p.getPath().setPoints(
									PlanePath.Ramer(p.getPath().getPoints(), 0,
											p.getPath().getPoints().size(),
											0.001d));
						}
						paths.add(p.getPath());
						path.setCreated(false);
					}
				}
				
				for (PlanePath pa: paths) {
					if(pa != null){
						if (pa.selected) {
							pa.lissageMoyenne();
							pa.setPoints(
									PlanePath.Ramer(pa.getPoints(), 0,
											pa.getPoints().size(),
											0.001d));
							pa.addPoint(event.getX(), event.getY());
						}
						pa.selected = false;
						pa.created = false;
					}
				}
				
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent event) {
				for (Plane p : planes) {
					if (p.isSelected()) {
						paths.add(path);
						paths.remove(p.getPath());
						path.addPoint(event.getX(), event.getY());
						p.setPath(path);
						path.setCreated(true);
					}
					
				}
				
				for (PlanePath pa: paths) {
					if (pa.selected) {
						pa.addPoint(event.getX(), event.getY());
						pa.setCreated(true);
					}
					
				}

				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent event) {
				// Rien si la souris bouge sans bouton enfoncé
			}
		});
	}

	
	
	
	
	
	/*
	 * 
	 * FONCTIONS
	 * 
	 */
	
	/**
	 * Detecte les collisions, se stoppe s'il y a une collision
	 */
	public void collisionDetect() {
		for (int i = 0; i < planes.size(); i++) {
			for (int j = 0; j < planes.size(); j++) {
				//Si deux avions sont proches d'entrer en collision
				if (Plane.collide(planes.get(i), planes.get(j))
						&& planes.get(i) != planes.get(j)
						&& !planes.get(i).isCollided()
						&& !planes.get(j).isCollided()) {
					planes.get(i).setCollided(true);
					planes.get(j).setCollided(true);
					this.setSpeed(1);
					FlightController.setSlider(1);
				}
				if (!Plane.collide(planes.get(i), planes.get(j))
						&& planes.get(i) != planes.get(j)
						&& planes.get(i).isCollided()
						&& planes.get(j).isCollided()) {
					planes.get(i).setCollided(false);
					planes.get(j).setCollided(false);
				}
				
				//Si il y a une collision
				if (Plane.collapse(planes.get(i), planes.get(j))
						&& planes.get(i) != planes.get(j)) {
					anim=false;
					FlightController.SimpleTimer.start();
					this.stop();
				}		
			}
		}
	}

	
	
	
	/*
	 * 
	 * AFFICHAGE
	 * 
	 */
	
	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void setSpeed(int factor) {
		timer.setDelay(delay / factor);
	}

	public void paint(Graphics g) {
		Point2D warn = new Point2D.Double(this.getWidth() / 2,
				this.getHeight() / 2);

		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		// Couleur de trait pour les chemins
		Stroke dashed = new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
		Stroke continuous = new BasicStroke(4f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND);


		/*
		 * Ajout du contenu statique
		 */
		bg.draw(g2d);

		for (Piste pt : pistes) {
			if(!pt.useHelicopter)
				pt.draw(g2d);
		}

		apt.draw(g2d);
		
		for (Piste pt : pistes) {
			if(pt.useHelicopter)
				pt.draw(g2d);
		}
		
		/*
		 * Ajout du contenu dynamique
		 */
		// Dessin des chemins
		for (PlanePath path : paths) {

			if (path != null) {
				if (!path.isCreated())
					g2d.setColor( (level.equals("images/Desert_level.png")) ? new Color(240, 240, 240, 97) : new Color(237, 32, 21, 95));
				else
					g2d.setColor( (level.equals("images/Desert_level.png")) ? Color.WHITE : new Color(237, 32, 21));
				g2d.setStroke(path.isCreated() ? dashed : continuous);
				path.draw(g2d);
			}
		}
		
		// Dessin de l'arrivee des avions
		for(Plane plane : incomingPlanes.keySet()){

			if (plane.getLocation().getX() == 0) {
				warn.setLocation(0, plane.getLocation().getY());
			}
			if (plane.getLocation().getY() == 0) {
				warn.setLocation(plane.getLocation().getX(), 0);
			}
			if (plane.getLocation().getX() == this.getWidth()) {
				warn.setLocation(this.getWidth() - 52, plane.getLocation()
						.getY());
			}
			if (plane.getLocation().getY() == this.getHeight()) {
				warn.setLocation(plane.getLocation().getX(),
						this.getHeight() - 52);
			}
			if (!warn.equals(new Point2D.Double(this.getWidth() / 2, this
					.getHeight() / 2))) {
				wn.draw(g2d, (int) warn.getX(), (int) warn.getY());
			}
		}
		
		// Dessin des ombres
		for (Plane plane : planes) {
			AffineTransform tr= new AffineTransform();
			
			tr.translate(plane.getLocation().getX() - plane.getCenter().getX(),
					plane.getLocation().getY() - plane.getCenter().getY() + 20);
			
			tr.rotate( plane.getAngle() - Math.PI/2);
		
			tr.scale((0.8+0.2*plane.getLocation().getY()/this.getHeight()), (0.8+0.1*plane.getLocation().getY()/this.getHeight()));
	
			
			g2d.drawImage(shade, tr, null);
		}
		
		// Dessin des avions
		for (Plane plane : planes) {
			if (plane.path != null) {
				if (plane.path.getLeadsToAirport()) {
					AlphaComposite ac = AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 0.6f);
					g2d.setComposite(ac);
				}
			}

			plane.draw(g2d);
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1));
		}

		
		// Intemperies
		ferme = false;
		for (Piste pt : pistes) {
			if (!pt.isOuvert()) {
				ferme = true;
				try {
					opacityIndex = (opacityIndex >= 0.99f) ? 1.0f : opacityIndex + 0.01f;
					AlphaComposite ac = AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, opacityIndex);
					g2d.setComposite(ac);
					g2d.drawImage(rain, 0, 0, null);
					g2d.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 1));
					g2d.drawImage(ImageIO.read(new File("images/alert.png")),
							370, 10, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(ferme == false) opacityIndex = 0;
		

		g2d.drawImage(score, 3,
				this.getHeight() - 60, null);

		int i = 0;
		int copyVar = this.avionsAtterris;
        do{
        	Image image =  ((ImageIcon)(FlightController.numList[copyVar % 10])).getImage();
        	copyVar = (int)(copyVar  / 10);
        	g2d.drawImage(image, 394-54*i, this.getHeight() - 80, null);
        	i++;
        }while(i < 4);
		//Toolkit.getDefaultToolkit().sync();
        

		i = 0;
		if(time.getHh() != 17){
				AlphaComposite ac = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.6f);
				g2d.setComposite(ac);
		}
		copyVar = time.getMin();
        do{
        	Image image =  ((ImageIcon)(FlightController.numList[copyVar % 10])).getImage();
        	copyVar = (int)(copyVar  / 10);
        	g2d.drawImage(image, 1180-50*i, 10, null);
        	i++;
        }while(i < 2);
        g2d.drawImage(((ImageIcon)(new ImageIcon("images/numbers/deuxpts.png"))).getImage(), 1100, 15, null);
        
        copyVar = time.getHh();
        do{
        	Image image =  ((ImageIcon)(FlightController.numList[copyVar % 10])).getImage();
        	copyVar = (int)(copyVar  / 10);
        	g2d.drawImage(image, 1150-50*i, 10, null);
        	i++;
        }while(i < 4);
        g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1));
	}
	
	
	
	
	
	
	
	/*
	 * 
	 * ACTION PERFORMED
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	@Override
	public void actionPerformed(ActionEvent event) {
		ArrayList<Plane> arrivedPlanes = new ArrayList<Plane>();
		
		
		/*
		 *  On crée des avions, deux fois plus si on est en Heure de pointe
		 *  et que le nombre d'avions actuellement en vol est inf�rieur � 9
		 */
		if (new Random().nextInt(300) <  ((time.getHh() == 17 && planes.size() < 9) ? (difficulty+1) : 1*((difficulty+1)/2))) {

			int posx = new Random().nextInt(this.getWidth());
			int posy = new Random().nextInt(this.getHeight());

			if (new Random().nextBoolean()) {
				posx = (new Random().nextBoolean() ? 0 : this.getWidth());
			} else {
				posy = (new Random().nextBoolean() ? 0 : this.getHeight());
			}
			if (new Random().nextInt(6) > 1) {
				if(new Random().nextInt(20) == 1){
					Plane p = new Plane("images/planes.png", new Point2D.Double(
							posx, posy), 0, 0.7, 1, false);
					p.prior = true;
					p.path = new PlanePath(posx, posy);
					p.path.interpolate(new Point2D.Double(posx, posy), pistes.get(0));
					incomingPlanes.put(p, 200);
				}
				
				else{
				incomingPlanes.put(new Plane("images/planes.png", new Point2D.Double(
						posx, posy), 0, 0.7, 1, false), 200);
				}
			}
			else{
				if(level.equals("images/Desert_level.png")){
					incomingPlanes.put(new Plane("images/preview.png", new Point2D.Double(
							posx, posy), 0, 0.7, 2.1, false), 200);
				}
				else if(level.equals("images/snow_level.jpg")){
					incomingPlanes.put(new Plane("images/helicopter.gif", new Point2D.Double(
							posx, posy), 0, 0.7, 0.8, true), 200);
				}

			}
		}
		
		for(Plane p : incomingPlanes.keySet()){
			incomingPlanes.put(p, incomingPlanes.get(p) - 1);
			if(incomingPlanes.get(p) <= 0){
				incomingPlanes.remove(p);
				planes.add(p);
				break;
			}
			
		}
		
		// Mise à jour des avions
		for (Plane plane : planes) {
			if (plane.progress()) {
				if (plane.getPath().getLeadsToAirport()) {
					// L'avion a atterri
					arrivedPlanes.add(plane);
				}

				paths.remove(plane.getPath());
				plane.setPath(null);
			}

			if (plane.getLocation().getX() <= 0) {
				plane.setAngle((new Random().nextBoolean() ? new Random()
						.nextFloat() * Math.PI / 2 : new Random().nextFloat()
						* -Math.PI / 2));
			}

			if (plane.getLocation().getY() <= 0) {
				plane.setAngle(new Random().nextFloat() * Math.PI);
			}

			if (plane.getLocation().getX() >= this.getWidth()) {
				plane.setAngle((new Random().nextBoolean() ? new Random()
						.nextFloat() * Math.PI / 2 + Math.PI / 2 : new Random()
						.nextFloat() * -Math.PI / 2 - Math.PI / 2));
			}

			if (plane.getLocation().getY() >= this.getHeight()) {
				plane.setAngle(-new Random().nextFloat() * Math.PI);
			}
			// Detection de collisions
			collisionDetect();
		}

		
		//Desactivation des paths li�s aux pistes ferm�es
		for (Plane p : planes) {
			for (Piste pt : pistes) {
				
				if(!pt.useHelicopter && !p.isHelicopter){
					if (pt.isIn(p) && pt.isOuvert())
						p.path.setLeadsToAirport(true);
					else if (pt.isIn(p) && !pt.isOuvert() && !p.prior) {
						p.path.setLeadsToAirport(false);
					}
				}
				else if(pt.useHelicopter && p.isHelicopter){
					if (pt.isInH(p) && pt.isOuvert())
						p.path.setLeadsToAirport(true);
					else if (pt.isInH(p) && !pt.isOuvert() && !p.prior) {
						p.path.setLeadsToAirport(false);
					}
				}
			}
		}

		// Fermeture de pistes
		if (new Random().nextInt(2500) < 1*((difficulty+1)/2)) {
			pistes.get(new Random().nextInt(pistes.size())).setOuvert(false);
		}
		for (Piste p : pistes) {
			if (!p.isOuvert() && new Random().nextInt(700*((difficulty+1)/2)) == 1)
				p.setOuvert(true);
		}

		// Mise à jour de l'affichage
		repaint();
		// Suppression des avions atterri
		for (Plane plane : arrivedPlanes) {
			planes.remove(plane);
			this.avionsAtterris++;
		}
		
		intervalle++;
		if(intervalle % 30 == 0){
			time.update();
		}
	}
}
