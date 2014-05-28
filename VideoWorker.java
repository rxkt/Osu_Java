import javax.swing.*;
import java.util.*;
public class VideoWorker extends SwingWorker<Boolean,Void>{
    public Playback p;
    

    public VideoWorker(GameBoard board,String dir, String fileName){
	p = new Playback(dir,fileName);
	board.add(p);
    }	
    
    public Boolean doInBackground() throws Exception{
        Thread.sleep(900);
	p.startPlayback();
	
	//after done, return true.
	return true;
    }
}
