import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Circle{
    private String imageSrc;
    //load image in the board, pass it down to circle
    //for optimization on speed
    private int x,y,order;
    private Image image;
    private double time;
    
    public Circle(int x, int y, Image image,int orderNum){
	this.image = image;
	this.x=x;
	this.y=y;
        order = orderNum;
    }
    public int getX(){
	return x;
    }
    public int getY(){
	return y;
    }
    public Image getImage(){
	return image;
    }
    public double getTime(){
	return time;
    }
    
}
