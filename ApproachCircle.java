import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ApproachCircle{
    private int x,y,diameter;
    private Image image;
    public ApproachCircle(int x, int y){
	this.x=x;
	this.y=y;
	this.image = image;
	diameter = 50;
    }
    public int getX(){
	return x-diameter/2;
    }
    public int getY(){
	return y-diameter/2;
    }
    public int getDiameter(){
	return diameter;
    }
    public void grow(){
	diameter+=2;
    }
}
