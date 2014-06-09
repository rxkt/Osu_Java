import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import javax.sound.sampled.*;

public class SelectScreen extends JPanel implements ActionListener,MouseMotionListener{
    protected Timer timer;
    protected Window w;
    protected Font font;
    protected int mode;//0 denotes titleScreen, 1 denotes song screen.
    protected int mouseX,mouseY;
    //mode 0
    protected ImageIcon desuIcon,playIcon,optionsIcon,exitIcon;
    protected Image desu,play,options,exit;
    protected Background bgPanel;
    protected boolean bgChanged;
    protected int playOverlap,optionsOverlap,exitOverlap;
    //mode 1
    protected List<List<ImageObject>> selectTabs;
    protected ImageIcon blueTabIcon,purpleTabIcon,redTabIcon;
    protected Image blueTab,purpleTab,redTab;
    protected Image darkFilter;

    //current mp3.
    protected MP3 song;
    //current options selected.
    protected boolean video;//some computers just can't handle my 1gig of memory lol
    
    protected File songsDir;
    //lists the directories of the songs
    protected File[] listOfSongs;
    protected File currentSongDir;
    protected int musicIndex;
    protected int beatmapIndex;
    protected ImageObject currentBeatmap;

    //under mouseAdapter
    protected double exitTime;
    public SelectScreen(){
	setFocusable(true);
	setVisible(true);
	bgPanel = new Background("default/titleBG.png");
	bgChanged=false;
	addMouseListener(new MAdapter());
	addKeyListener(new TAdapter());
	addMouseMotionListener(this);
	video=true;
	try{
	    Font font = Font.createFont(Font.TRUETYPE_FONT,new File("default/Exo.ttf"));
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    ge.registerFont(font);
	    font = font.deriveFont(14f);
	    setFont(font);
	}catch(Exception ex){
	    ex.printStackTrace();
	}

	//mode 0 items
	desuIcon = new ImageIcon("default/icon.png");
	desu = desuIcon.getImage();
	playIcon = new ImageIcon("default/play.png");
	play = playIcon.getImage();
	optionsIcon = new ImageIcon("default/options.png");
	options = optionsIcon.getImage();
        exitIcon = new ImageIcon("default/exit.png");
	exit = exitIcon.getImage();
	blueTabIcon = new ImageIcon("default/blueTab.png");
	blueTab = blueTabIcon.getImage();
	redTabIcon = new ImageIcon("default/redTab.png");
	redTab = redTabIcon.getImage();
	purpleTabIcon = new ImageIcon("default/purpleTab.png");
	purpleTab = purpleTabIcon.getImage();
	
	playOverlap=0;
	optionsOverlap=0;
	exitOverlap=0;
	//mode 1 items
	selectTabs= new ArrayList<List<ImageObject>>();
	darkFilter= Toolkit.getDefaultToolkit().createImage("default/black.jpg");

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
	if (song!=null)
	    song.close();
	File dir = listOfSongs[musicIndex];
	this.musicIndex=musicIndex;
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
	if (mode==0){
	    bgPanel.paint(g2d);	
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
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.65f));  
	    bgPanel.paint(g2d);	
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.45f));
	    g2d.drawImage(darkFilter,0,0,null);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
	    //
	    paintTabs(g2d);
	    
	}
	Toolkit.getDefaultToolkit().sync();
	g.dispose();
       
    }
    public void paintTabs(Graphics2D g2d){
	//draw current tab
	
	g2d.setColor(Color.WHITE);
	
	g2d.drawImage(purpleTab,currentBeatmap.x-purpleTabIcon.getIconWidth()/2,
		      currentBeatmap.y-purpleTabIcon.getIconHeight()/2,this);
	ImageObject io = mouseOver();
	//incase we are hovering over this...
	//(we will compare y values, even though the addresses are the same.)
	if (io!= null && currentBeatmap.y==io.y)
	    g2d.setColor(Color.BLACK);
	g2d.drawString(currentBeatmap.data,currentBeatmap.x-260,
		       currentBeatmap.y-10);
	if (io!= null && currentBeatmap.y==io.y)
	    g2d.setColor(Color.WHITE);
	//for description for this loop, see focusTabs()
        for (int j=0;j<selectTabs.size();j++){
	    if (j!= musicIndex){
		ImageObject unselected = selectTabs.get(j).get(0);
		if (unselected.y > -100 && unselected.y < 700){
		    //red
		    g2d.drawImage(redTab, unselected.x-redTabIcon.getIconWidth()/2,
				  unselected.y-redTabIcon.getIconHeight()/2,this);
		    //incase we are hovering over this...
		    if (io!= null && unselected.y==io.y)
			g2d.setColor(Color.BLACK);
		    g2d.drawString(unselected.data,unselected.x-260,
				   unselected.y-10);
		    if (io!= null && unselected.y==io.y)
			g2d.setColor(Color.WHITE);
		}
	    }else{
		List<ImageObject> currentTabs = selectTabs.get(j);
		//we do not include 0 because that is the unselected 'header'
		for (int i=1;i<currentTabs.size();i++){
		    ImageObject selected = currentTabs.get(i);
		    if (i != beatmapIndex){
			if (selected.y > -100 && selected.y < 700){
			    //red
			    g2d.drawImage(blueTab, selected.x-blueTabIcon.getIconWidth()/2,
					  selected.y-blueTabIcon.getIconHeight()/2,this);
			    //incase we are hovering over this...
			    if (io!= null && selected.y==io.y)
				g2d.setColor(Color.BLACK);
			    g2d.drawString(selected.data,selected.x-260,
					   selected.y-10);
			    if (io!= null && selected.y==io.y)
				g2d.setColor(Color.WHITE);
			}
		    }
		}
	    }
	}
	//draw tabs below it.
	
	//draw tabs to the side.
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
	    }
	    if (song!=null){
		if (song.player.isComplete()){
		    song.close();
		    startMusicM(musicIndex);
		}
	    }
	    focusTabs();
	}
	
	repaint();
    }
    //returns the tab that the mouse is hovering over
    //returns null if it isn't hovering over anything.
    public ImageObject mouseOver(){
	for (int j=0;j<selectTabs.size();j++){
	    if (j!= musicIndex){
		ImageObject unselected = selectTabs.get(j).get(0);
		if (unselected.y > -100 && unselected.y < 700){
		    //all tabs are the same. i'ma just use red.
		    if (Math.abs(unselected.y - mouseY) < (redTabIcon.getIconHeight()/2))
			return unselected;
		}
	    }else{
		List<ImageObject> currentTabs = selectTabs.get(j);
		for (int i=1;i<currentTabs.size();i++){
		    ImageObject selected = currentTabs.get(i);
		    if (selected.y > -100 && selected.y < 700){
			if (Math.abs(selected.y - mouseY) < (redTabIcon.getIconHeight()/2))
			    return selected;
		    }
		}
	    }
	}
	return null;
    }
    
    public void focusTabs(){
	//currentbeatmap is always at x,y.
	currentBeatmap.x=800;
	currentBeatmap.y=300;
	for (int j=0;j<selectTabs.size();j++){
	    //if we are not in the same music folder
	    if (j< musicIndex){
		//there will only be index 0 on the screen.
		//the y value will be the #of unselected beatmaps above currentBeatmap + the
		//# of beatmaps in the current directory above currentBeatmap
		ImageObject unselected = selectTabs.get(j).get(0);
	        unselected.y=currentBeatmap.y-(musicIndex-j+beatmapIndex-1)*100;
		unselected.x = 800 +Math.abs(300-unselected.y);
	    }else if(j> musicIndex){
		ImageObject unselected = selectTabs.get(j).get(0);
	        unselected.y=currentBeatmap.y+(selectTabs.get(musicIndex).size()-beatmapIndex+(j-musicIndex-1))*100;
		unselected.x = 800 + Math.abs(300-unselected.y);

	    }else{
		List<ImageObject> currentTabs = selectTabs.get(j);
		//we do not include 0 because that is the unselected 'header'
		for (int i=1;i<currentTabs.size();i++){
		    ImageObject selected = currentTabs.get(i);
		    selected.y = currentBeatmap.y-(beatmapIndex-i)*100;
		    selected.x = 800 + Math.abs(300-selected.y);
		}
	    }
	}
    }
    public void loadSelection(){
	for (File f:listOfSongs){
	    
	    File[] listing = f.listFiles();
	    List<ImageObject> maps = new ArrayList<ImageObject>();
	    maps.add(new ImageObject(0,0,redTab,f.getName()));
	    int numOfDesuFiles=0;
	    for (File file:listing){
		String name = file.getName();
		if (name.contains(".desu")){
		    maps.add(new ImageObject(800,300,blueTab,name.substring(0,name.length()-5)));
		    numOfDesuFiles++;
		}
	    }
	    if (currentSongDir.getPath().contains(f.getName())){
		beatmapIndex = (int)(Math.random()*numOfDesuFiles+1);
		
	    }
	    selectTabs.add(maps);
	}
	currentBeatmap=selectTabs.get(musicIndex).get(beatmapIndex);
	System.out.println(currentBeatmap);
    }
    //this methods assumes you clicked.
    public void interactWithTab(){
	ImageObject hovered = mouseOver();
	System.out.println("Interacted with"+hovered);
	if (hovered!=null){
	    if (hovered.y == currentBeatmap.y){
		if (song!=null&&!song.player.isComplete())
		    song.close();
		w.removePanel(this);
		try{
		    Thread.sleep(3000);
		    w.addPanel(currentSongDir.getPath()+"\\",currentBeatmap.data+".desu",70,video);
		}catch(Exception ex){
		    ex.printStackTrace();
		}
	    }
	    for (int j=0;j<selectTabs.size();j++){
		if (j!= musicIndex){
		    ImageObject unselected = selectTabs.get(j).get(0);
		    if (unselected.y > -100 && unselected.y < 700){
		        if (unselected.y==hovered.y){
			    //open a red tab. current = that now.
			    currentSongDir=listOfSongs[j];
			    musicIndex=j;
			    beatmapIndex=1;
			    currentBeatmap=selectTabs.get(j).get(1);
			    bgChanged=true;
			    song.close();
			    startMusicM(j);
			}
		    }
		}else{
		    List<ImageObject> currentTabs = selectTabs.get(j);
		    for (int i=1;i<currentTabs.size();i++){
			ImageObject selected = currentTabs.get(i);
			if (selected.y > -100 && selected.y < 700){
			    //this is a blue tab.
			    if (selected.y==hovered.y){
				currentBeatmap=selected;
				beatmapIndex=i;
				focusTabs();
				return;
			    }
			}
		    }
		}
	    }
	    //if current && clicked
	    ////window.adds new map.
	    //else if j=musicIndex aka you clicked a blueTab winthin the same folder
	    ////current = blah, beatmapindex=blah.
	    //else, you clicked something from another folder.
	    ////startmusicM(the thing you clicked.)
	    ////beatmapIndex=1 b/c 0 is a closed tab. currentBeatmap = first
	}
    }
    public class TAdapter extends KeyAdapter{
	public void keyReleased(KeyEvent e){
	    int keyNum = e.getKeyCode();
	    if ( mode==1 && keyNum == KeyEvent.VK_ESCAPE){
		//go back to title screen and reset all variables.
		mode=0;
		playOverlap=0;
		optionsOverlap=0;
		exitOverlap=0;
		selectTabs = new ArrayList<List<ImageObject>>();
	       
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
		    loadSelection();
		}
	    }else if (mode==1){
		interactWithTab();
		System.out.println(currentBeatmap);
	    }
	    System.out.println(mouseX+","+mouseY);
	}
    }
	    
}
