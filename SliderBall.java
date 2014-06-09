import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SliderBall extends PrintableObject{
    //x,y
    protected boolean alive;//we say it is alive when it is used for a slider.
    //aka we reuse the same sliderball for EVERY slider to conserve memory
    protected double angle;
    
    public SliderBall(){
	alive=false;
	x=0;
	y=0;
	//the image will be in GameBoard.java for clutterfree-ness
    }
    public void target(Point p){
	double x=p.getX();
	double y=p.getY();
	double dist = Math.sqrt(Math.pow(this.x-x,2)+Math.pow(y-this.y,2));
	angle = (int)Math.toDegrees(Math.asin((1.0*y-this.y)/dist));
	if (x>this.x){
	    if (y>this.y)
		angle*=(-1);
	}else{
	    if (this.y > y)
		angle*=(-1);
	    angle+=180;
	}
	if (this.y>y)
	    angle*=(-1);
    }
    public boolean isCloseTo(Point p,int limit){
	return (Math.pow(x-p.getX(),2) + Math.pow(y-p.getY(),2) < Math.pow(limit,2));
    }
}
