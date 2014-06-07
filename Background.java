import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Background extends JPanel{
    // The Image to store the background image in.
    protected Image img;
    public Background(String path){
        // Loads the background image and stores in img object.
        img = Toolkit.getDefaultToolkit().createImage(path);
	setOpaque(true);
    }
    public void loadImage(String path){
        img = Toolkit.getDefaultToolkit().createImage(path);
    }
    public void paint(Graphics g){
        // Draws the img to the BackgroundPanel.
        g.drawImage(img, 0, 0,1000,600, null);
    }
}
