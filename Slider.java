import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Stack;

//consider using list instead of arrayList
public class Slider{
    private ArrayList<Point> points;//or implement a stack<Point>
    //all points refenced will be in a line.
    // -(midpt)-(arc)-(midpt)-(arc)-(midpt)-?
    //curve the 2 lines conjoined with the arc/midpts as a sector/arc

    private int order;
    private Image image;
    public Slider(ArrayList<Point> points,int order, Image image){
	this.points = points;//preload all points in board
	this.order=order;
	this.image=image;
    }
    public Image getImage(){
	return image;
    }
    public int getOrder(){
	return order;
    }
    public ArrayList<Point> getPoints(){
	return points;
    }
    public void progress(){
	//stack.dequeue
    }
    public void addPoint(Point p){
	//stack.enqueue
    }
}
