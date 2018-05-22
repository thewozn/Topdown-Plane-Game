/**
*
* @author: anthony.woznica
*
* Absolutely ANY USE of this project REQUIRE THIS CREDIT IN THE HEADER OF THE FILE (WOZNICA Anthony).
* There are NO exceptions.
*
* This file contains the Controller part of our MVC model.
*
**/

package animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Classe de la fen�tre, controlleur
 */
public class FlightController extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * 
	 * VARIABLES
	 * 
	 */
	
		//VARIABLES DE NIVEAU
		private static String[] levels = { "images/Desert_level.png", "images/snow_level.jpg"};
		private static ImageIcon a = new ImageIcon("images/main_menu/difficulty.png");
		private static ImageIcon bc = new ImageIcon("images/main_menu/levels.png");
		private static int index = 0;
		private static JSlider acceleration;
		private static JSlider planeSpeed;
		
		//VARIABLES DE DIFFICULTE
		private static Object[] difficulty =
	        {
	            a,
	            new ImageIcon("images/main_menu/easy.png"),
	            new ImageIcon("images/main_menu/normal.png"),
	            new ImageIcon("images/main_menu/hard.png"),
	            new ImageIcon("images/main_menu/impossible.png")
	        };
		
		private static Object[] level =
	        {
			 	bc,
				new ImageIcon("images/main_menu/desert.png"),
				new ImageIcon("images/main_menu/winter.png")
	        };
		
		private static final JComboBox<Object> combo = new JComboBox<Object>(difficulty);
		private static final JComboBox<Object> combo2 = new JComboBox<Object>(level);
		public static String currentDiff = "Facile";
		
		//VARIABLES GENERALES
		private static JLabel background;
	    private static int pos = 0;
	    public static Timer SimpleTimer;
	    public static Object[] numList = 
					        {
					    	new ImageIcon("images/numbers/0.png"),
					    	new ImageIcon("images/numbers/1.png"),
					    	new ImageIcon("images/numbers/2.png"),
					    	new ImageIcon("images/numbers/3.png"),
					    	new ImageIcon("images/numbers/4.png"),
					    	new ImageIcon("images/numbers/5.png"),
					    	new ImageIcon("images/numbers/6.png"),
					    	new ImageIcon("images/numbers/7.png"),
					    	new ImageIcon("images/numbers/8.png"),
					    	new ImageIcon("images/numbers/9.png"),
				        };
		Animator animator;
		final Container content;
   
	
	/*
	 * 
	 * GETTERS AND SETTERS
	 * 
	 */
	
	/**
	 * Retourne la liste de niveaux
	 * @return levels liste de niveaux
	 */
	public static String[] getLevels() {
		return levels;
	}

	
	/**
	 * Retourne le num�ro du niveau
	 * @return index Num�ro du niveau
	 */
	public static int getIndex() {
		return index%(levels.length);
	}

	public static void setSlider(int v){
		acceleration.setValue(1);
	}

	public static void setPlaneSpeed(double v){
		planeSpeed.setValue((int)(v*10));
	}


	public void init(){
		background=new JLabel(new ImageIcon("images/menubg.jpg"));
        background.setBounds(0, 0, 3842, 720);
        content.add(background);
        background.setLayout(null);
        
        final JLabel logo=new JLabel(new ImageIcon("images/main_menu.png"));
        logo.setBounds(0, 0, 1280, 720);
        background.add(logo);
        
        // Creating Button & stuff 

        final JButton info=new JButton();
        info.setBorderPainted( false );
        info.setContentAreaFilled( false );
        info.setBounds(1280-90, 20, 70, 70);
        info.setIcon(new ImageIcon("images/main_menu/ask.png"));
        info.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	if(Desktop.isDesktopSupported())
            	{
            	  try {
					Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/anthony-woznica-55b719150/"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
            	}
            }
        });
        background.add(info);
        
        final JButton b=new JButton();
        b.setBounds(1280/2-140, 720/2, 300, 30);
        b.setIcon(new ImageIcon("images/main_menu/newgame.png"));
        background.add(b);
        
       
        combo.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	combo.removeItem(a);
                currentDiff = (combo.getSelectedItem().toString().equals("images/main_menu/easy.png"))
                		? "Facile" : (combo.getSelectedItem().toString().equals("images/main_menu/normal.png"))
                		? "Normal" : (combo.getSelectedItem().toString().equals("images/main_menu/hard.png"))
                		? "Difficile" : "Impossible";
            }
        });
        combo.setForeground(new Color(70, 59, 49));
        combo.setBounds(1280/2-140, 720/2 + 45, 300, 40);
        background.add(combo);
        

        
        
        combo2.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	combo2.removeItem(bc);
                index = (combo2.getSelectedItem().toString().equals("images/main_menu/desert.png")) ? 0 : 1;
            }
        });
        combo2.setForeground(new Color(70, 59, 49));
        combo2.setBounds(1280/2-140, 720/2 + 45, 300, 40);
        background.add(combo2);
        
		
        final JButton quit = new JButton();
        quit.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	dispose();
            }
        });
        
        
        quit.setBounds(1280/2-140, 720/2 + 155, 300, 30);
        quit.setIcon(new ImageIcon("images/main_menu/quitter.png"));
        background.add(quit);
        
        
        SimpleTimer = new Timer(60, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	pos = (pos+1)%2561;
            	background.setBounds(-pos, 0, 3842, 720);
            	logo.setBounds(pos, 0, 1280, 720);
            	info.setBounds(1280-90 + pos, 20, 70, 70);
            	b.setBounds(1280/2-140 + pos, 720/2, 300, 40);
                combo2.setBounds(1280/2-140 + pos, 720/2 + 55, 300, 40);
                combo.setBounds(1280/2-140 + pos, 720/2 + 110, 300, 40);
                quit.setBounds(1280/2-140 + pos, 720/2 + 165, 300, 40);
            	content.repaint();
            }
        });
        SimpleTimer.start();
        
        /*
         * 
         * START  GAME
         * 
         */
        b.addActionListener(new ActionListener() {
			
			/**
			 * D�clenche le d�but de la partie en cliquand sur Nouvelle Partie
			 */
        	@Override
			public void actionPerformed(ActionEvent event) {
					SimpleTimer.stop();
					content.removeAll();
					content.repaint();
					content.setLayout(new BorderLayout());
					content.setBackground(Color.GRAY);
					
					
					// Zone de contrôle des avions
					animator = new Animator();
					content.add(animator, BorderLayout.CENTER);
					
					SimpleTimer = new Timer(100, new ActionListener(){
			            @Override
			            public void actionPerformed(ActionEvent e) {

			            	if(animator.anim == false){
			            		animator.anim = true;
			            		content.removeAll();
			            		
			            		JLabel bg=new JLabel(new ImageIcon("images/gameover/gameover.png"));
			                    bg.setBounds(0, 0, 1280, 720);
			                    content.add(bg);
			                    bg.setLayout(null);
			                    int i = 0;
			                    do{
			                    	JLabel score =new JLabel();
			                    	score.setIcon((Icon)numList[animator.avionsAtterris % 10]);
			                    	animator.avionsAtterris = (int)(animator.avionsAtterris  / 10);
			                    	score.setBounds(754-54*i, 255, 64, 73);
			                    	bg.add(score);
			                    	i++;
			                    }while(i < 4);
			                    
			                    b.setBounds(1280/2-140, 720/2, 300, 40);
			                    bg.add(b);
			                    
			                    combo.setBounds(1280/2-140, 720/2 + 55, 300, 40);
			                    bg.add(combo);
			                    
			                    combo2.setBounds(1280/2-140, 720/2 + 110, 300, 40);
			                    bg.add(combo2);
			                    
			                    quit.setBounds(1280/2-140, 720/2 + 165, 300, 40);
			                    bg.add(quit);
			                    
								content.repaint();
			            	}
			            }
			        });
					
			        
					
					// Boutons de contrôle
					JPanel menu = new JPanel();
					menu.setLayout(new BoxLayout(menu, BoxLayout.LINE_AXIS));
					
					
					// Bouton de retour au menu
					JButton buttonMenu = new JButton();
					buttonMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							SimpleTimer.stop();
							content.removeAll();
							content.repaint();
							init();
						}
					});
					
					buttonMenu.setIcon(new ImageIcon("images/interface/back.png"));
					menu.add(buttonMenu);
					
					JButton buttonPause = new JButton();
					buttonPause.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							JButton button = (JButton) event.getSource();
							System.out.println(button.getIcon());
							if (button.getIcon().toString().equals("images/interface/pause.png")) {
								animator.stop();
								button.setIcon(new ImageIcon("images/interface/continue.png"));
							}else {
								animator.start();
								button.setIcon(new ImageIcon("images/interface/pause.png"));
							}
						}
					});
					buttonPause.setIcon(new ImageIcon("images/interface/pause.png"));
					menu.add(buttonPause);
					

					
					
					acceleration = new JSlider();
					acceleration.setMinimum(1);
					acceleration.setMaximum(10);
					acceleration.setMinorTickSpacing(1);
					acceleration.setMajorTickSpacing(1);
					acceleration.setValue(1);
					acceleration.setPaintTicks(true);
					acceleration.setPaintLabels(true);
					acceleration.setSnapToTicks(true);
					
					acceleration.addChangeListener(
							new ChangeListener() {
								@Override
								public void stateChanged(ChangeEvent e) {
										animator.setSpeed(acceleration.getValue());
								}
							});
					
					menu.add(acceleration);
					
					
					final JLabel thumb = new JLabel();
					thumb.setIcon(new ImageIcon("images/interface/speed2.png"));
					menu.add(thumb);
					
					JPanel menu2 = new JPanel();
					menu2.setLayout(new BoxLayout(menu2, BoxLayout.LINE_AXIS));
					menu2.setOpaque(false);
					planeSpeed = new JSlider(JSlider.VERTICAL, 0, 30, 1);
					
					Hashtable<Integer,JLabel> labelTable = new java.util.Hashtable<Integer,JLabel>();
				    labelTable.put(new Integer(30), new JLabel("3.0"));
				    labelTable.put(new Integer(20), new JLabel("2.0"));
				    labelTable.put(new Integer(10), new JLabel("1.0"));
				    labelTable.put(new Integer(5), new JLabel("0.5"));
				    labelTable.put(new Integer(2), new JLabel("0.25"));
				    labelTable.put(new Integer(0), new JLabel("0.0"));
				    planeSpeed.setLabelTable( labelTable );
				    planeSpeed.setPaintLabels(true);
					
					planeSpeed.addChangeListener(
							new ChangeListener() {
								@Override
								public void stateChanged(ChangeEvent e) {
										for(Plane p : animator.getPlanes()){
											if(p.isSelected()) p.setSpeed((float)planeSpeed.getValue()/10);
										}
								}
							});

					menu2.add(planeSpeed);
					content.add(menu2, BorderLayout.EAST);
					content.add(menu, BorderLayout.SOUTH);
					animator.start();
					setVisible(true);	
			}
		});
	}
	
	
	/*
	 * 
	 * CONSTRUCTEUR
	 * 
	 */
	/**
	 * Constructeur de la frame
	 */
	public FlightController(){  
        /* JFrame is a top level container (window)
         * where we would be adding our button
         */
		super("Simple Flight Control");
		this.setSize(new Dimension(1280, 720));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		content = getContentPane();
		content.setLayout(null);
        content.setSize(2561,720);    
        this.init();
        this.setResizable(false);
        this.setVisible(true);
                  
    }
	
	
	/*
	 * 
	 * MAIN
	 * 
	 */
	
	/**
	 * Instancie la classe FlightController
	 * @param args
	 */
    public static void main(String[] args) {  
            new FlightController();  
     }  
}
