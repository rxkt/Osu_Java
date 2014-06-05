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
    protected int turnArounds;
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
	this.numOfSteps = numOfSteps;
	pathOfPoints = new ArrayList<Point>();
	//sliderBallTime=0;
    }
    public void findPoints(){
	double time=0;
	for (;time<=1;time+=(1.0/numOfSteps)){
	    addPointAt(time);
	}
	
    }
    public void addPointAt(double time){
	double x=0,y=0;
	int aLength=points.size();
	for (int i =0;i<aLength;i++){
	    //current point
	    Point p = points.get(i);
	    x+=(p.getX()*Math.pow(1-time,(double)(aLength-1-i))*
		Math.pow(time,(double)i)*combination(aLength-1,i));
	    y+=(p.getY()*Math.pow(1-time,(double)(aLength-1-i))*
		Math.pow(time,(double)i)*combination(aLength-1,i));
	}
	System.out.println(x+","+y+","+(1-time));
	pathOfPoints.add(new Point((int)x,(int)y));	    
    }
    public static int factorial(int n){
	int ans = 1;
	if (n==0)
	    return ans;
	else
	    while (n > 1){
		ans*=n;
		n--;
	    }
	return ans;
    }
    public static int combination(int n, int r){
	return factorial(n)/(factorial(r)*factorial(n-r));
    }
    public static void main(String[] args){
	
	System.out.println(combination(2,0));
	System.out.println(combination(2,1));
	System.out.println(combination(2,2));

	System.out.println(combination(3,0));
	System.out.println(combination(3,1));
	System.out.println(combination(3,2));
	System.out.println(combination(3,3));
    }    
}
