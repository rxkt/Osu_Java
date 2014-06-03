
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;//arrayList for all printed objects
import java.util.List;
import java.util.Scanner;//scan files beatmap files
import java.util.concurrent.*;//for cyclic barrier
import java.io.*;


public class GameBoard extends JPanel implements ActionListener,MouseMotionListener{
    protected double systemTime;//update every action performed, remove timer1
    protected Timer timer;
    protected double time,currentTime,startTime,lastHitTime;
    //scans notes, etc.
    protected String dir;
    protected Scanner mapInput;
    protected String nextLine;
    //game mechanics vars
    protected boolean mouseDown,mouseClicked,key1down,key2down;
    protected int combo,score;
    protected int circleSize;//the size of each note.
    //----create boolean to prevent repeated inputs with 1 input
    protected boolean key1used,key2used;
    protected int mouseX, mouseY;
    //printable objects
    protected Image approachCircle;
    protected Image hitCircle;
    protected Image hit300,hit300g,hit300k,hit100,hit100k,hit50,hit0;
	
    protected List<ApproachCircle> approachCircles;
    protected List<PrintableObject> objects;
    protected List<HitNum> hitNums;
    //unite all circles and sliders into 1 list<object>
    protected Window w;
    protected String songPath;
    protected MP3 song;
    protected String videoPath;
    protected Playback v;
    //might replace playback with a videoworker that accepts the dir.
    protected VideoWorker vw;
    protected Thread t1;


    public GameBoard(String dir,String filename,int circleSize){
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
	//test.txt must follow the format, otherwise it crashes on you
	if (nextLine.equals("song")){
	    songPath=mapInput.nextLine();
	}else{
	    throw new RuntimeException();
	}
	song = new MP3(dir+songPath);
	//load all images into memory	
	approachCircle = new ImageIcon(dir+"approachCircle.png").getImage();
	hitCircle = new ImageIcon(dir+"hitcircle.png").getImage();
	hit300 = new ImageIcon(dir+"hit300.png").getImage();
	hit300g= new ImageIcon(dir+"hit300g.png").getImage();
	hit300k= new ImageIcon(dir+"hit300k.png").getImage();
	hit100= new ImageIcon(dir+"hit100.png").getImage();
	hit100k= new ImageIcon(dir+"hit100k.png").getImage();
	hit50= new ImageIcon(dir+"hit50.png").getImage();
	hit0= new ImageIcon(dir+"hit0.png").getImage();

	lastHitTime=0;
	this.circleSize = circleSize;
	approachCircles = new ArrayList<ApproachCircle>();
	objects=new ArrayList<PrintableObject>();
	hitNums = new ArrayList<HitNum>();
        nextLine = mapInput.nextLine();
	if (nextLine.equals("video")){
	    videoPath=mapInput.nextLine();
	    nextLine=mapInput.nextLine();    
	    // ^^^^to align both cases of having both video and no video
	}
	//else{
	//    there is no video. assume the next line is the beginning
	//    of a new note.
	
	//here begins the test.txt file reading.
	while (!nextLine.equals("end")){
	    if (nextLine.equals("C")){
		String s = mapInput.nextLine();
		int x=Integer.parseInt(s);
		int y=Integer.parseInt(mapInput.nextLine());
		double time=Double.parseDouble(mapInput.nextLine());
		int orderNum=Integer.parseInt(mapInput.nextLine());
		objects.add(new Circle(x,y,orderNum,time));
		nextLine=mapInput.nextLine();
		//tie in time references to each object
	    }//add cases for slider lol
	}

	timer = new Timer(20,this);
	timer.start();
	//set cursor w/ toolkit here
    }
    public void streamMedia(){
	t1 = new Thread(song);
	vw = new VideoWorker(this,dir,videoPath);
	add(vw.p);
	vw.execute();
	t1.start();
	repaint();
	revalidate();
	startTime = System.currentTimeMillis()/1000.0;
	System.out.println(startTime);
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
	for (ApproachCircle a:approachCircles){
	    g2d.drawImage(approachCircle,a.getX(),a.getY(),a.getDiameter(),a.getDiameter(),this);
	}
	if (startTime > 0){
	    for (PrintableObject o:objects){
		
		if (startTime+o.time>currentTime+5){
		    break;
		}
		if (o instanceof Circle){
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,o.transparency));
		    g2d.drawImage(hitCircle,o.x-circleSize/2,
				  o.y-circleSize/2,
				  circleSize,circleSize,this);
		}
		g2d.drawImage(approachCircle,o.x-o.aCircleSize/2,
			    o.y-o.aCircleSize/2,
			    o.aCircleSize,o.aCircleSize,this);
		//also draw for sliders lol
	    }
	    for (HitNum h:hitNums){
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,h.transparency));
		if (h.score==300){
		    g2d.drawImage(hit300,h.x-25,h.y-15,70,45,this);
		}else if (h.score==100){
		    g2d.drawImage(hit100,h.x-25,h.y-15,70,45,this);
		}else if (h.score==50){
		    g2d.drawImage(hit50,h.x-25,h.y-15,70,45,this);
		}else if (h.score==0){
		    g2d.drawImage(hit0,h.x-25,h.y-15,50,45,this);
		}
	    }
	}
	Toolkit.getDefaultToolkit().sync();
	g.dispose();
    }
    public void actionPerformed(ActionEvent e){
	repaint();
	time+=0.02;
	//under click->true,set currentTime. if start+o.time>current+5 break
	//compare the x,y of the FIRST OBJECT, as well as timing.
	////////////////////////YOU WERE LAST HERE ERIC////////
	if (mouseClicked){
	    mouseClicked=false;
	    approachCircles.add(new ApproachCircle(mouseX,mouseY));
	    //check first element objects here
	    if (objects.size() > 0){
		PrintableObject o = objects.get(0);
		if (o.aCircleSize<100){
		    if ((o.aCircleSize-70)/70 < .06){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,300));
		    }else if ((o.aCircleSize-70)/70 < .12){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,100));
		    }else if ((o.aCircleSize-70)/70 < .18){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,50));
		    }
		}
	    }
	}/////////////check key1
	if (key1down && key1used==false){
	    approachCircles.add(new ApproachCircle(mouseX,mouseY));
	    key1used=true;
	    //check first element here
	    if (objects.size() > 0){
	        PrintableObject o = objects.get(0);
		if (o.aCircleSize<100){
		    if ((o.aCircleSize-70)/70 < .1){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,300));
		    }else if ((o.aCircleSize-70)/70 < .2){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,100));
		    }else if ((o.aCircleSize-70)/70 < .3){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,50));
		    }
		}
	    }
	}else if(key1used && !key1down){
	    key1used=false;
	}//////////////check key2
	if (key2down && key2used==false){
	    approachCircles.add(new ApproachCircle(mouseX,mouseY));
	    key2used=true;
	    //check first element here
	    if (objects.size() > 0){
	        PrintableObject o = objects.get(0);
		if (o.aCircleSize<100){
		    if ((o.aCircleSize-70)/70 < .1){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,300));
		    }else if ((o.aCircleSize-70)/70 < .2){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,100));
		    }else if ((o.aCircleSize-70)/70 < .3){
			objects.remove(o);
			hitNums.add(new HitNum(o.x,o.y,50));
		    }
		}
	    }
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
	if (startTime>0){//if the game has commenced
	    for (int i=0;i<objects.size();i++){
		PrintableObject o = objects.get(i);
		currentTime = System.currentTimeMillis()/1000.0;
		if (startTime+o.time>currentTime+5){
		    break;//ignore the beats too far ahead in the future.
		}
		if (currentTime-startTime < o.time
		    && o.time+startTime-currentTime<1){
		    if (o.transparency<0.9)
			o.transparency+=0.10;
		    //x = k-kt x =k @ t=0 and x=0 @ t=1
		    //or x = k(c-t) where k is the shrink constant
		    //and c is the time of shrinking.
		    o.aCircleSize=(int)(150*((startTime+o.time-currentTime))+circleSize);
		}else if(currentTime+0.25 > startTime+o.time){
		    //.25 seconds after you MISSED it...
		    objects.remove(o);
		    //insert X image lol
		    hitNums.add(new HitNum(o.x,o.y,0));
		}
	        
	    }
	    for (int i=0;i<hitNums.size();i++){
		HitNum h = hitNums.get(i);
		h.transparency-=0.02;
		if (h.transparency < 0.04){
		    hitNums.remove(h);
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
		System.out.println(mouseX+","+mouseY);
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
