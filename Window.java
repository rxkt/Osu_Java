import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{
    //expand this soon to the titlescreen
    //titlescreen-jpanel
    //current-music-screen-jpanel
    //
    //phases:
    //open jframe -> titlescreen + musicscreen
    //loop thru all open directories in /songs/
    //open a random dir and play the mp3 file.
    //have the musicscreen report back to jframe what song is playing.
    //during the song selection screen, get the song from the titlescreen, open up to the specific song.
    //
    //OR:
    //have a jpanel dedicated to playing music? or container for less workload. but more memory usage.

    GameBoard current;
    public Window(){
	int width =1000,height=600;
	//add((new GameBoard("yanaginagi-Tokohana/","test.txt")));
	
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(width,height);
	setLocationRelativeTo(null);
	//setLayout(null);
	setTitle("Some 1/2-parody-crappy osu made with Java");
	setResizable(true);
	setVisible(true);
    }
    public void addPanel(String dir, String source,int circleSize){
	current = new GameBoard(dir,source,circleSize);
	current.setFrame(this);

	add(current);
	current.streamMedia();
	current.repaint();

	revalidate();
	current.requestFocusInWindow();
    }
    public String toString(){
	return "Window";

    }
    public static void main(String[] args){
	Window w= new Window();
	w.addPanel("songs/yanaginagi-Tokohana/","test.txt",70);
	//change songs to a search using java file doc
    }
}
