import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class VideoImage extends JFrame{

    private static final long serialVersionUID = -4752966848100689153L;
    private final ImageComponent mOnscreenPicture;

    public VideoImage(){
	super();

	mOnscreenPicture = new ImageComponent();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	getContentPane().add(mOnscreenPicture);
	this.setVisible(false);
	this.pack();
    }
    public void setImage(final BufferedImage aImage){
	mOnscreenPicture.setImage(aImage);
    }

    public class ImageComponent extends JComponent{
	private static final long serialVersionUID = 5584422798735147930L;
	private Image mImage;
	private Dimension mSize;

	public void setImage(Image image){
	    //invokeLater on thread or new runnable onto
	    SwingUtilities.invokeLater(new ImageRunnable(image));
	}
    
	public void setImageSize(Dimension newSize){
	}
    
	private class ImageRunnable implements Runnable{
	    private final Image newImage;

	    public ImageRunnable(Image newImage){
		super();
		this.newImage = newImage;
	    }
	    public void run(){
		ImageComponent.this.mImage = newImage;
		final Dimension newSize = new Dimension(mImage.getWidth(null), mImage.getHeight(null));
		if (!newSize.equals(mSize))
		    {
			ImageComponent.this.mSize = newSize;
			VideoImage.this.setSize(mImage.getWidth(null), mImage.getHeight(null));
			VideoImage.this.setVisible(true);
		    }
		repaint();
	    }
	}
    
	public ImageComponent(){
	    mSize = new Dimension(0, 0);
	    setSize(mSize);
	}

	public synchronized void paint(Graphics g){
	    if (mImage != null)
		g.drawImage(mImage, 0, 0, this);
	}

    }
}
