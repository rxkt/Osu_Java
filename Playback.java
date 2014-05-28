import com.xuggle.xuggler.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Playback extends JPanel implements Runnable{

    String whatToPlay;
    private final ImageComponent imageComponent;
    private boolean mantainRatio = true;
    private int videoWidth;
    private int videoHeight;
    private int vWidth;
    private int vHeight;
    public boolean firstTime = false;
    private BufferedImage currentImage = null;
    //private Playback curObj;
    long starttime;
    long curtime;
    boolean isPaused = false;
    boolean isClosed;
    IStreamCoder videoCoder;
    IContainer container;
    boolean flagForSleep;
    String dir;
    final long millisecondsTolerance = 50;
    long millisecondsStreamTimeSinceStartOfVideo;
    IVideoPicture newPic;
    IVideoPicture picture;

    public Playback(String pardir, String fileName) {
	super();
	setDoubleBuffered(true);
	dir = pardir;
	firstTime = true;
	whatToPlay = dir + fileName;
	this.setBackground(Color.BLACK);
	imageComponent = new ImageComponent();

	this.setLayout(new OverlayLayout(this));


	this.add(imageComponent);
	this.setVisible(true);
	//curObj = this;
	setOpaque(false);
    }
    public void run(){
	startPlayback();
    }

    public boolean isFullScreen() {
	return isFullScreen();
    }

    private void resize() {
	if (currentImage == null) {
	    return;
	}

	if (mantainRatio == true) {
	    double imgW = currentImage.getWidth(this);
	    double imgH = currentImage.getHeight(this);

	    double ratio1 = (double) getWidth() / imgW;
	    double ratio2 = (double) getHeight() / imgH;

	    double scaleFactor = Math.min(ratio1, ratio2);
	    videoWidth = (int) (currentImage.getWidth(this) * scaleFactor);
	    videoHeight = (int) (currentImage.getHeight(this) * scaleFactor);
	} else {
	    videoWidth = getWidth();
	    videoHeight = getHeight();
	}
	vWidth = (getWidth() - videoWidth) / 2;
	vHeight = (getHeight() - videoHeight) / 2;
    }

    public void setImage() {
	if (firstTime) {
	    this.resize();
	    firstTime = false;
	}
	//imageComponent.setImage();
    }

    public void setMantainRatio(boolean mantainRatio) {
	this.mantainRatio = mantainRatio;
    }

    public class ImageComponent extends JComponent {
	public ImageComponent(){
	    super();
	    
	}
	public void setImage() {
	    //repaint();
	    // SwingUtilities.invokeLater(new ImageRunnable(image));
	}

	private class ImageRunnable implements Runnable {

	    public ImageRunnable() {
		super();
	    }

	    public void run() {
		//repaint();
	    }
	}

	public synchronized void paint(Graphics g) {
	    Graphics2D g2d = (Graphics2D)g;
	    if (currentImage != null){
		g2d.drawImage(currentImage, vWidth, vHeight, videoWidth, videoHeight, this);
		/*Graphics2D g2d = (Graphics2D)g;
		  g2d.setColor(Color.red);
		  BasicStroke stroke = new BasicStroke(2.0f);
		  g2d.setStroke(stroke);
		  drawCrossHair(g2d);*/
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	    }
	}
    }

    public void drawCrossHair(Graphics g2d) {
	/*
	//Left top
	g2d.drawLine((getWidth() / 2) - 220, (getHeight() / 2) - 300, (getWidth() / 2) - 160, (getHeight() / 2) - 300);
	g2d.drawLine((getWidth() / 2) - 210, (getHeight() / 2) - 310, (getWidth() / 2) - 210, (getHeight() / 2) - 250);
	//Left bottom
	g2d.drawLine((getWidth() / 2) - 220, (getHeight() / 2) + 300, (getWidth() / 2) - 160, (getHeight() / 2) + 300);
	g2d.drawLine((getWidth() / 2) - 210, (getHeight() / 2) + 310, (getWidth() / 2) - 210, (getHeight() / 2) + 250);
	//Right top
	g2d.drawLine((getWidth() / 2) + 220, (getHeight() / 2) - 300, (getWidth() / 2) + 160, (getHeight() / 2) - 300);
	g2d.drawLine((getWidth() / 2) + 210, (getHeight() / 2) - 310, (getWidth() / 2) + 210, (getHeight() / 2) - 250);
	//Right bottom
	g2d.drawLine((getWidth() / 2) + 220, (getHeight() / 2) + 300, (getWidth() / 2) + 160, (getHeight() / 2) + 300);
	g2d.drawLine((getWidth() / 2) + 210, (getHeight() / 2) + 310, (getWidth() / 2) + 210, (getHeight() / 2) + 250);*/
    }
    IPacket packet;
    long firstTimestampInStream = Global.NO_PTS;
    long systemClockStartTime = 0;
    long systemClockCurrentTime;
    long millisecondsClockTimeSinceStartofVideo;
    public void startPlayback() {
	IRational ira = IRational.make(30, 1);
	IRational irb = IRational.make(1, 30);

	if (!IVideoResampler.isSupported(
					 IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
	    throw new RuntimeException("you must install the GPL version"
				       + 
				       " of Xuggler (with IVideoResampler support) for "
				       + "this demo to work");
	}
	isClosed = false;
	container = IContainer.make();

	int vWidth = 1392;
	int vHeight = 768;

	container.setForcedAudioCodec(ICodec.ID.CODEC_ID_MPEG4);
	if (container.open(whatToPlay, IContainer.Type.READ, null) < 0) {
	    throw new IllegalArgumentException("could not open file: " + whatToPlay);
	}
	int numStreams = container.getNumStreams();
	int videoStreamId = -1;
	for (int i = 0; i < numStreams; i++) {
	    // Find the stream object
	    IStream stream = container.getStream(i);
	    // Get the pre-configured decoder that can decode this stream;
	    IStreamCoder coder = stream.getStreamCoder();

	    if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
		videoStreamId = i;
		videoCoder = coder;
		break;
	    }
	}
	if (videoStreamId == -1) {
	    throw new RuntimeException("could not find video stream in container: "
				       + whatToPlay);
	}
	if (videoCoder.open() < 0) {
	    throw new RuntimeException("could not open video decoder for container: " + whatToPlay);
	}

	IVideoResampler resampler = null;
	if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
	    resampler = IVideoResampler.make(videoCoder.getWidth(),
					     videoCoder.getHeight(), IPixelFormat.Type.YUV420P,
					     videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
	    if (resampler == null) {
		throw new RuntimeException("could not create color space "
					   + "resampler for: " + whatToPlay);
	    }
	}
	packet = IPacket.make();
	int n = 0, m = 0;
	while (!isClosed) {
	    if (isPaused == true) {
		try {
		    Thread.sleep(200);
		    continue;
		} catch (InterruptedException ex) {
		}
	    }
	    if (container.readNextPacket(packet) >= 0) {
		if (packet.getStreamIndex() == videoStreamId) {
		    picture = IVideoPicture.make(videoCoder.getPixelType(),
							       videoCoder.getWidth(), videoCoder.getHeight());

		    int offset = 0;
		    while (offset < packet.getSize()) {
			/*
			 * Now, we decode the video, checking for any errors.
			 *
			 */
			int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
			if (bytesDecoded < 0) {
			    throw new RuntimeException("got error decoding video in: "
						       + whatToPlay);
			}
			offset += bytesDecoded;
			if (picture.isComplete()) {
			    newPic = picture;
			    if (resampler != null) {
				newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
							    picture.getWidth(), picture.getHeight());
				if (resampler.resample(newPic, picture) < 0) {
				    throw new RuntimeException("could not resample video from: "
							       + whatToPlay);
				}
			    }
			    if (firstTimestampInStream == Global.NO_PTS) {
				// This is our first time through
				firstTimestampInStream = picture.getTimeStamp();
				systemClockStartTime = System.currentTimeMillis();
			    } else {
				systemClockCurrentTime = System.currentTimeMillis();
			        millisecondsClockTimeSinceStartofVideo =
				    systemClockCurrentTime - systemClockStartTime;
			        millisecondsStreamTimeSinceStartOfVideo =
				    (picture.getTimeStamp() - firstTimestampInStream) / 1000;
				final long millisecondsToSleep =
				    (millisecondsStreamTimeSinceStartOfVideo
				     - (millisecondsClockTimeSinceStartofVideo
					+ millisecondsTolerance));
				if (millisecondsToSleep > 0) {
				    try {
					Thread.sleep(millisecondsToSleep);
				    } catch (InterruptedException e) {
					// we might get this when the user closes the dialog box, so
					// just return from the method.
					return;
				    }
				}
			    }
			    currentImage = Utils.videoPictureToImage(newPic);
			    setImage();
			    
			}
		    }
		}
	    } else {
		try {
		    Thread.sleep(500);
		} catch (Exception e) {
		}
		isClosed = true;
	    }
	}
	if (videoCoder != null) {
	    videoCoder.close();
	    videoCoder = null;
	}
	if (container != null) {
	    container.close();
	    container = null;
	}
	//return;
	/*
	 * Technically since we're exiting anyway, these will be cleaned up by
	 * the garbage collector... but because we're nice people and want
	 * to be invited places for Christmas, we're going to show how to clean up.
	 */
    }
    long firstTimestampInStream1 = Global.NO_PTS;
    long systemClockStartTime1 = 0;

    public void delay(IVideoPicture picture) {
	if (firstTimestampInStream1 == Global.NO_PTS) {
	    // This is our first time through
	    firstTimestampInStream1 = picture.getTimeStamp();
	    // get the starting clock time so we can hold up frames
	    // until the right time.
	    systemClockStartTime1 = System.currentTimeMillis();
	} else {
	    long systemClockCurrentTime = System.currentTimeMillis();
	    long millisecondsClockTimeSinceStartofVideo =
		systemClockCurrentTime - systemClockStartTime1;
	    // compute how long for this frame since the first frame in the
	    // stream.
	    // remember that IVideoPicture and IAudioSamples timestamps are
	    // always in MICROSECONDS,
	    // so we divide by 1000 to get milliseconds.
	    millisecondsStreamTimeSinceStartOfVideo =
		(picture.getTimeStamp() - firstTimestampInStream1) / 1000;
	    
	    final long millisecondsToSleep =
		(millisecondsStreamTimeSinceStartOfVideo
		 - (millisecondsClockTimeSinceStartofVideo
		    + millisecondsTolerance));
	    if (millisecondsToSleep > 0) {
		try {
		    System.out.println(millisecondsToSleep);
		    Thread.sleep(millisecondsToSleep);
		} catch (InterruptedException e) {
		    // we might get this when the user closes the dialog box, so
		    // just return from the method.
		    return;
		}
	    }
	}
    }

    public void jumpToFrame(long framecount) {
	try {
	    if (container != null) {
		container.seekKeyFrame(0, framecount, IContainer.SEEK_FLAG_BYTE);
	    }
	} catch (Exception e) {
	    isClosed = true;
	    e.printStackTrace();
	}
	firstTimestampInStream = Global.NO_PTS;

	//container.seekKeyFrame(0,0,framecount,container.getDuration(),IContainer.SEEK_FLAG_ANY);
    }

    public void pause() {
	isPaused = true;
    }

    public void unpause() {
	isPaused = false;
	firstTimestampInStream = Global.NO_PTS;
    }

    public long selectFrame() {
	isPaused = true;
	firstTimestampInStream = Global.NO_PTS;
	return packet.getPosition();
    }

    public void stopPlayback() {
	isClosed = true;
    }
}
