import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
public class Background extends JPanel{
    // The Image to store the background image in.
    protected Image img;
    public Background(String path){
        // Loads the background image and stores in img object.
        img = Toolkit.getDefaultToolkit().createImage(path);
	setOpaque(true);
    }
    public void loadImage(String path){
	try{
	    img =ImageIO.read(new File(path));
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
    public void paint(Graphics g){
        // Draws the img to the BackgroundPanel.
        g.drawImage(img, 0, 0,1000,600, null);
    }
}
