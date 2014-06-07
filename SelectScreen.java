import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import javax.sound.sampled.*;

public class SelectScreen extends JPanel implements ActionListener,MouseMotionListener{
    protected Timer timer;
    protected Window w;
    protected Font font;
    protected int mode;//0 denotes titleScreen, 1 denotes song screen.
    protected int mouseX,mouseY;
    //images?
    protected ImageIcon desuIcon,playIcon,optionsIcon,exitIcon;
    protected Image desu,play,options,exit;
    protected ImageIcon blueBoxIcon,orangeBoxIcon,whiteBoxIcon;
    protected Image blueBox, whiteBox, orangeBox;
    protected Background bgPanel;
    protected boolean bgChanged;
    protected int playOverlap,optionsOverlap,exitOverlap;
    
    //current mp3.
    protected MP3 song;
    //current options selected.
    protected boolean video;//some computers just can't handle my 1gig of memory lol
    
    protected File songsDir;
    //lists the directories of the songs
    protected File[] listOfSongs;
    protected File currentSongDir;
    protected int musicIndex;

    //under mouseAdapter
    protected double exitTime;
    public SelectScreen(boolean video){
	setFocusable(true);
	setVisible(true);
	setBackground(Color.BLACK);
	bgPanel = new Background("default/titleBG.png");
	bgChanged=false;
	addMouseListener(new MAdapter());
	addKeyListener(new TAdapter());
	addMouseMotionListener(this);
	//mode 0 items
	desuIcon = new ImageIcon("default/icon.png");
	desu = desuIcon.getImage();
	playIcon = new ImageIcon("default/play.png");
	play = playIcon.getImage();
	optionsIcon = new ImageIcon("default/options.png");
	options = optionsIcon.getImage();
	exitIcon = new ImageIcon("default/exit.png");
	exit = exitIcon.getImage();
	blueBoxIcon= new ImageIcon("default/blueBox.png");
	blueBox = blueBoxIcon.getImage();
	orangeBoxIcon= new ImageIcon("default/orangeBox.png");
	orangeBox = orangeBoxIcon.getImage();
	whiteBoxIcon= new ImageIcon("default/whiteBox.png");
	orangeBox = orangeBoxIcon.getImage();
	playOverlap=0;
	optionsOverlap=0;
	exitOverlap=0;
	//mode 1 items
	songsDir = new File("./songs/");
	this.video = video;
	listOfSongs = songsDir.listFiles();
	musicIndex = (int)(Math.random()*listOfSongs.length);
	startMusicM(musicIndex);
	mode=0;
	timer = new Timer(10,this);
	timer.start();
    }
    public void menuClick(){
	try{
	    Clip menuclick;
	    menuclick = AudioSystem.getClip();
	    menuclick.open(AudioSystem.getAudioInputStream(new File("./default/menuclick.wav")));
	    menuclick.start();
	}catch(Exception e){
	    e.printStackTrace();
	    
	}
    }
    public void menuBack(){
	try{
	    Clip menuback;
	    menuback = AudioSystem.getClip();
	    menuback.open(AudioSystem.getAudioInputStream(new File("./default/menuback.wav")));
	    menuback.start();
	}catch(Exception e){
	    e.printStackTrace();
	    
	}
    }
    public void menuHit(){
	try{
	    Clip menuhit;
	    menuhit = AudioSystem.getClip();
	    menuhit.open(AudioSystem.getAudioInputStream(new File("./default/menuhit.wav")));
	    menuhit.start();
	}catch(Exception e){
	    e.printStackTrace();
	    
	}
    }
    //start the music from the middle of the song.
    public void startMusicM(int musicIndex){
	File dir = listOfSongs[musicIndex];
	File[] listOfObjects = dir.listFiles();
	for (File f:listOfObjects){
	    if (f.getPath().contains("mp3")){
		song = new MP3(f.getPath());
		song.play();
		System.out.println(f);
		currentSongDir=dir;
	    }
	}
	bgChanged=true;
    }
    //start the music from the beginning of the song.
    
    public void checkOverlap(){
	if (Math.abs(mouseX-560)<playIcon.getIconWidth()/2 &&
	    Math.abs(mouseY-200)<playIcon.getIconHeight()/2){
	    if(playOverlap==0){
		playOverlap=40;
		menuClick();
		bgChanged=true;
	    }
	}else{
	    playOverlap=0;
	}
	if ( Math.abs(mouseX-600)<optionsIcon.getIconWidth()/2 &&
	    Math.abs(mouseY-300)<optionsIcon.getIconHeight()/2){
	    if(optionsOverlap== 0){
		optionsOverlap=40;
		menuClick();
	    }
	}else{
	    optionsOverlap=0;
	}
	if (Math.abs(mouseX-560)<exitIcon.getIconWidth()/2 &&
	    Math.abs(mouseY-400)<exitIcon.getIconHeight()/2){
	    if(exitOverlap==0){
		exitOverlap=40;
		menuClick();
	    }
	}else{
	    exitOverlap=0;
	}
    }
    public void paint(Graphics g){
	super.paint(g);

	Graphics2D g2d = (Graphics2D)g;
	bgPanel.paint(g2d);	
	if (mode==0){
	    //move tabs over 40 if mouse is on top of this.
	    g2d.drawImage(play,560-playIcon.getIconWidth()/2+playOverlap,
			  200-playIcon.getIconHeight()/2,this);
	    g2d.drawImage(options,600-optionsIcon.getIconWidth()/2+optionsOverlap,
			  300-optionsIcon.getIconHeight()/2,this);
	    g2d.drawImage(exit,560-exitIcon.getIconWidth()/2+exitOverlap,
			  400-exitIcon.getIconHeight()/2,this);
	    g2d.drawImage(desu,300-desuIcon.getIconWidth()/2,
			  300-desuIcon.getIconHeight()/2,this);
	    
	}else if (mode==1){
	    
	    //find background.png, put it into bgPanel.
	    //paint.
	    //loop thru all .desu files, paint a blue box.
	    //orange boxes denote other dirs
	}
    }
    public void actionPerformed(ActionEvent e){
	
	if (mode ==0){
	    checkOverlap();
	    //if exitTime > 0, then exit was called. exit after time t.
	    if (exitTime >0){
		double timeNow = System.currentTimeMillis()/1000;
		if (timeNow-exitTime>1)
		    System.exit(0);
	    }
	    if (song!=null){
		if (song.player.isComplete()){
		    song.close();
		    musicIndex=(musicIndex+1)%listOfSongs.length;
		    startMusicM(musicIndex);
		}
	    }
	}else if (mode==1){
	    if (bgChanged){
		//if bg is changed then we know the song is changed.
		if (currentSongDir!=null){
		    File[] files = currentSongDir.listFiles();
		    for (File f:files){
			if (f.getPath().contains("ackground")){
			    bgPanel.loadImage(f.getPath());
			    bgChanged=false;
			}
		    }
		}
		//perform song change actions
		//
		//here^^.
	    }
	    //do stuff in mode 1 below
	}
		
	    
	
	repaint();
    }
    public class TAdapter extends KeyAdapter{
	public void keyReleased(KeyEvent e){
	    int keyNum = e.getKeyCode();
	    if ( mode==1 && keyNum == KeyEvent.VK_ESCAPE){
		//go back to title screen
		mode=0;
		menuBack();
		bgPanel.loadImage("default/titleBG.png");
	    }
	}
    }
    public void mouseMoved(MouseEvent e){
        mouseX = e.getX();
	mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
	mouseY = e.getY();
    }
    public class MAdapter extends MouseAdapter{
	public void mouseReleased(MouseEvent e){
	    int keyNum = e.getID();
	}
	public void mouseClicked(MouseEvent e){
	    if (mode==0){
		if (exitOverlap!=0){
		    menuHit();
		    exitTime=System.currentTimeMillis()/1000;
		}else if(playOverlap!=0){
		    menuHit();
		    mode=1;
		}
	    }
	}
    }
	    
}
