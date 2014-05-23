
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;//arrayList for all printed objects
import java.util.Scanner;//scan files beatmap files
import java.util.concurrent.*;//for cyclic barrier
import java.io.*;

public class GameBoard extends JPanel implements ActionListener,MouseMotionListener{
    private double systemTime;//update every action performed, remove timer1
    private Timer timer;
    private double time;
    private double lastHitTime;
    //scans notes, etc.
    private String dir;
    private Scanner mapInput;
    private String nextLine;
    //game mechanics vars
    private boolean mouseDown,mouseClicked,key1down,key2down;
    private int combo,score;
    //----create boolean to prevent repeated inputs with 1 input
    private boolean key1used,key2used;
    private int mouseX, mouseY;
    //printable objects
    private Image approachCircle;
    private ArrayList<Circle> circles;
    private ArrayList<ApproachCircle> approachCircles;
    private ArrayList<Slider> sliders;
    private Window w;
    private MP3 song;
    private Playback v;
    //might replace playback with a videoworker that accepts the dir.
    private VideoWorker vw;
    private Thread t1,t2;
    //debug
    int numOfClicks;

    public GameBoard(String dir,String filename){
	//set up component, might need more
	nextLine="";
	setFocusable(true);
	setVisible(true);
	setBackground(Color.BLACK);
	this.dir=dir;
	setOpaque(false);
	setLayout(new BorderLayout());
	addKeyListener(new TAdapter());
	addMouseListener(new MAdapter());
	addMouseMotionListener(this);
	//open the scanned file
	try{
            mapInput = new Scanner(new File(dir + filename));
	    while (mapInput.hasNext()){
		nextLine = mapInput.nextLine();
		if (!nextLine.substring(0,1).equals("#")){
		    break;
		}
	    }
	}catch(FileNotFoundException exception){
	    exception.printStackTrace();
	}
	//open the stupid multimedia files here//
	song = new MP3(dir+nextLine);
	//load all images into memory	
	approachCircle = new ImageIcon("approachCircle.png").getImage();
	lastHitTime=0;
	circles = new ArrayList<Circle>();
	sliders = new ArrayList<Slider>();
	approachCircles = new ArrayList<ApproachCircle>();
	timer = new Timer(10,this);
	timer.start();
	//set cursor w/ toolkit here
    }
    public boolean isOptimizedDrawingEnabled(){
	return false;
    }
    public void streamMedia(){
	t1 = new Thread(song);
	nextLine = mapInput.nextLine();
	//vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	//v = new Playback(dir,nextLine);
	//add(v);
	//^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	/* this will be replaced by a VideoWorker that runs on a 
	   different thread handle:
	   >>add a videoworker to each GameBoard as a var
	*/
	vw = new VideoWorker(this,dir,nextLine);
	add(vw.p);
	vw.execute();
	/////////////////comment out stuff inbetween these with VideoWorker
	//t2 = new Thread(v);
	//t2.setPriority(Thread.MAX_PRIORITY);
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	t1.start();
	//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	/*try{
	    t2.sleep(900);
	}catch(Exception e){
	    e.printStackTrace();
	}
	t2.start();*/	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	repaint();
	revalidate();
    }
    public void setFrame(Window w){
	this.w = w;
    }
    public Window getFrame(){
	return w;
    }
    public synchronized void paint(Graphics g){
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
	if (mouseClicked){
	    mouseClicked=false;
	    approachCircles.add(new ApproachCircle(mouseX,mouseY));
	}
	if (key1down && key1used==false){
	    approachCircles.add(new ApproachCircle(mouseX,mouseY));
	    numOfClicks++;
	    System.out.println(numOfClicks);
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

    public void mouseMoved(MouseEvent e){
        mouseX = e.getX();
	mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){
    }

    public class MAdapter extends MouseAdapter{
	public void mouseReleased(MouseEvent e){
	    int keyNum = e.getID();
	    if (keyNum == MouseEvent.MOUSE_RELEASED){
		mouseDown=false;
	    }
	}
	public void mousePressed(MouseEvent e){
	    int keyNum = e.getID();
	    if (keyNum == MouseEvent.MOUSE_PRESSED){
		mouseDown=true;
	    }
	}
	public void mouseClicked(MouseEvent e){
	    int keyNum = e.getID();
	    if (keyNum == MouseEvent.MOUSE_CLICKED){
		lastHitTime=time;
		mouseClicked=true;
	    }
	}
    }
    public class TAdapter extends KeyAdapter{
	public void keyReleased(KeyEvent e){
	    //z + x will operate as buttons 1 and 2
	    int keyNum = e.getKeyCode();
	    if (keyNum == KeyEvent.VK_Z){
		key1down = false;
	    }else if (keyNum == KeyEvent.VK_X){
		key2down = false;
		
	    }
	}
	public void keyPressed(KeyEvent e){
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
