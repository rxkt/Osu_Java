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
    protected List<Point> pathOfPoints;

    //denotes when the sliderBall will approach the end of the slider.
    //use this for be'zier curve.
    //protected float sliderBallTime;
    protected int numOfSteps;

    public Slider(int x, int y, int orderNum, double time
		  , ArrayList<Point> points,int numOfSteps){
	this.x = x ;
	this.y=y;
	this.orderNum = orderNum;
	this.time = time;
	this.points = points;
	//sliderBallTime=0;
    }
    public void findPoints(int numOfSteps){
	float time=0;
	for (;time<=1;time+=(1.0f/numOfSteps)){
	    addPointAt(time);
	}
	
    }
    public void addPointAt(float time){
	int x=0,y=0;
	int aLength=points.size();
	for (int i =0;i<aLength;i++){
	    //current point
	    Point p = points.get(i);
	    x+=p.getX()*Math.pow(1-time,aLength-1-i)*
		(Math.pow(time,i))*combination(aLength-1,i);
	    y+=p.getY()*Math.pow(1-time,aLength-1-i)*
		(Math.pow(time,i))*combination(aLength-1,i);
	}
	pathOfPoints.add(new Point(x,y));	    
    }
    public int combination(int n, int r){
	//in format nCr -> n!/(n-r)!r!, all creds to eric for 
	//writing this method even tho it's a sugoi/different loop format
	int ans =1;
	for (;n>r;n--){
	    ans*=n;
	}
	for (;r>1;r--){
	    ans/=r;
	}
	return ans;
    }
    
}
