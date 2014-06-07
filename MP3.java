import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class MP3 implements Runnable{
    private String filename;
    protected Player player; 

    public MP3(String filename) {
        this.filename = filename;
    }
    public void close(){
	if(player != null){
	    player.close();
	} 
    }
    public void run(){
	play();
    }
    // play the MP3 file
    public void play(){
        try{
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }catch(Exception e){
		System.out.println("Problem playing file " + filename);
		System.out.println(e);
	    }
	    // run in new thread to play in background swag.
	    new Thread(){
		public void run(){
		    try{
			player.play();
		    }
		    catch(Exception e){
			System.out.println(e);
		    }
		}
	    }.start();
	}
    }
