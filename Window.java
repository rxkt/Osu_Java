import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{
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
    public void addPanel(String dir, String source){
	current = new GameBoard(dir,source);
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
	w.addPanel("yanaginagi-Tokohana/","test.txt");
	//w.pack();
    }
}
