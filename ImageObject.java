import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;

public class ImageObject extends PrintableObject{
    //protected Image image;
    protected Image image;
    protected int spd;
    protected double angle;
    protected String data;//incase it has
    public ImageObject(Image i){
	image = i;
    }
    public ImageObject(int x, int y,Image i,String data){
	image = i;
	this.x=x;
	this.y=y;
	this.data=data;
    }
    //x,y vars already in printable object.
    public void move(){
	//move according to spd*sin(angle) and spd*cos(angle)
	
    }
    public String toString(){
	return data;
    }
    //not bothering with acceleration
}
