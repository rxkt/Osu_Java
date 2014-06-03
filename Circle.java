import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Circle extends PrintableObject{
    private String imageSrc;
    //load image in the board, pass it down to circle
    //for optimization on speed
    
    public Circle(int x, int y, int orderNum, double time){
	//all circles will have the same image
	this.x=x;
	this.y=y;
        this.orderNum = orderNum;
	this.time=time;
	aCircleSize=150;
    }
    //ALL GET METHODS ARE USELESS WITH PROTECTED
    
}
