import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;//arrayList for all printed objects
import java.util.Scanner;//scan files beatmap files
import java.io.*;
import javax.sound.sampled.*;//aim for mp3 rather than wav file inputs

public class Board extends JPanel implements ActionListener,MouseMotionListener{
    private Timer timer;
    private double time;
    private boolean mouseDown,mouseClicked,key1down,key2down;
    //create boolean to prevent repeated inputs with 1 input
    private boolean key1used,key2used;
    private int mouseX, mouseY;
    

    private int gameMode;//0:title,,1:game,,
    private boolean mapSelected,mapStarted;
    private Scanner mapInput;
    private int combo,score;
    private double systemTime;//update every action performed, remove timer1
    
    private double lastHitTime;
    private Clip map;
    //printable objects
    private ArrayList<Circle> circles;
    private ArrayList<ApproachCircle> approachCircles;
    private Image approachCircle;
    private ArrayList<Slider> sliders;

    public Board(){
	//set up component, might need more
	setFocusable(true);
	setBackground(Color.BLACK);
	
	addKeyListener(new TAdapter());
	addMouseListener(new MAdapter());
	addMouseMotionListener(this);

	mapInput = new Scanner(System.in);
	timer = new Timer(10,this);
	timer.start();

	//load all images into memory	
	approachCircle = new ImageIcon("approachCircle.png").getImage();
	
	gameMode=1;
	mapSelected=false;
	mapStarted=false;
	lastHitTime=0;
	circles = new ArrayList<Circle>();
	sliders = new ArrayList<Slider>();
	approachCircles = new ArrayList<ApproachCircle>();
	//dummy scanner
	
	//debug mode

    }
    public void paint(Graphics g){
	super.paint(g);
	Graphics2D g2d = (Graphics2D)g;
	//approach circles contain images.
	for (ApproachCircle a:approachCircles){
	    g2d.drawImage(approachCircle,a.getX(),a.getY(),a.getDiameter(),a.getDiameter(),this);
	}
	for (Circle c: circles){
	    g2d.drawImage(approachCircle,c.getX(),c.getY(),this);
	    //g2d.drawString(string, x,y) get its placement # as string
	}
	
	Toolkit.getDefaultToolkit().sync();
	g.dispose();
    }
    public void actionPerformed(ActionEvent e){
	repaint();
	time+=0.01;//may replace with systemTime
	//menu screen
	if (gameMode==0){
	    if (mapSelected){
		mapSelected=false;
		mapStarted=false;
		gameMode=1;
		//take clip and load it into memory
		try{
		    map = AudioSystem.getClip();
		    map.open(AudioSystem.getAudioInputStream(new File("")));
		}catch(FileNotFoundException exception){
		    System.out.println("File not found.");
		}catch(Exception exception){
		    exception.printStackTrace();
		}//take fileName and load it into scanner
		try{
		    mapInput = new Scanner(new File(""));
		}catch(FileNotFoundException exception){
		    exception.printStackTrace();
		}
	    }
	}
	if (gameMode==1){
	    /*
	    if (!mapStarted){
		//play map once
		//record time using system.current time millis
		mapStarted=true;
		//begin processing mapInput
		while(mapInput.hasNext()){
		    //if scanner.next does not begin with a ##, process it
		}
		}*/
	    if (mouseClicked){
		mouseClicked=false;
		approachCircles.add(new ApproachCircle(mouseX,mouseY));
	    }
	    if (key1down && key1used==false){
		approachCircles.add(new ApproachCircle(mouseX,mouseY));
		key1used=true;
	    }else if(key1used && !key1down){
		key1used=false;
	    }
	    if (key2down && key2used==false){
		approachCircles.add(new ApproachCircle(mouseX,mouseY));
		key2used=true;
	    }else if(key2used && !key2down){
		key2used=false;
	    }

	    //do game actions here//
	    for (int i =0;i<approachCircles.size();i++){
		ApproachCircle a = approachCircles.get(i);
		a.grow();
		if (a.getDiameter() > 150){
		    approachCircles.remove(i);
		}
	    }
	}
	
    }
    public void mouseMoved(MouseEvent e){
        mouseX = e.getX();
	mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){
    }

    public class MAdapter extends MouseAdapter{
	public void mouseReleased(MouseEvent e){
	    //
	    if (gameMode==1){
		int keyNum = e.getID();
		if (keyNum == MouseEvent.MOUSE_RELEASED){
		    mouseDown=false;
		}
	    }
	}
	public void mousePressed(MouseEvent e){
	    //
	    if (gameMode==1){
		int keyNum = e.getID();
		if (keyNum == MouseEvent.MOUSE_PRESSED){
		    mouseDown=true;
		}
	    }
	}
	
	public void mouseClicked(MouseEvent e){
	    //
	    if (gameMode==1){
		int keyNum = e.getID();
		if (keyNum == MouseEvent.MOUSE_CLICKED){
		    lastHitTime=time;
		    mouseClicked=true;
		    
		}
	    }
	}
    }
    public class TAdapter extends KeyAdapter{
	public void keyReleased(KeyEvent e){
	    //z + x will operate as buttons 1 and 2
	    if (gameMode==1){
		int keyNum = e.getKeyCode();
		if (keyNum == KeyEvent.VK_Z){
		    key1down = false;
		}else if (keyNum == KeyEvent.VK_X){
		    key2down = false;
		}
	    }
	}
        public void keyPressed(KeyEvent e){
	    //if key, boolean of keyUp = true
	    if (gameMode==1){
		int keyNum = e.getKeyCode();
		if (keyNum == KeyEvent.VK_Z){
		    key1down = true;
		    lastHitTime=time;
		}else if (keyNum == KeyEvent.VK_X){
		    key2down = true;
		    lastHitTime=time;
		}
	    }
	}
    } 
}
