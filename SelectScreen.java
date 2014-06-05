import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class SelectScreen extends JPanel implements ActionListener{
    protected Timer timer;
    protected File thisDir;
    public SelectScreen(){
	thisDir = new File("./");
	
	timer = new Timer(20,this);
	timer.start();
    }
    public void paint(Graphics g){
	super.paint(g);
	Graphics2D g2d = (Graphics2D)g;
    }
    public void actionPerformed(ActionEvent e){
	
	repaint();
    }

}
