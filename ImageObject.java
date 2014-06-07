import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ImageObject extends PrintableObject{
    protected Image image;
    protected int spd;
    protected double angle;
    public ImageObject(Image i){
	image = i;
    }
    //x,y vars already in printable object.
    public void move(){
	//move according to spd*sin(angle) and spd*cos(angle)
    }
    //not bothering with acceleration
}
