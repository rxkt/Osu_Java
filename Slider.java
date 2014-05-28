import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

//http://en.wikipedia.org/wiki/B%C3%A9zier_curve for osu sliders.
//consider using list instead of arrayList
public class Slider extends PrintableObject{
    //arraylist of key points for the slider
    
    //points used for be'zier curve
    protected List<Point> points;

    //denotes when the sliderBall will approach the end of the slider.
    //use this for be'zier curve.
    protected float sliderBallTime;
    public Slider(int x, int y, int orderNum, double time
		  , ArrayList<Point> points){
	this.x = x ;
	this.y=y;
	this.orderNum = orderNum;
	this.time = time;
	this.points = points;
	sliderBallTime=0;
    }

    
}
