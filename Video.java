import java.awt.image.*;
import com.xuggle.xuggler.*;

public class Video{
    @SuppressWarnings("deprecation")
    private String filename;
    public Video(String filename){
	this.filename=filename;
	if (!IVideoResampler.isSupported(
					 IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
	    throw new RuntimeException("you must install the GPL version" +
				       " of Xuggler (with IVideoResampler support) for " +
				       "this demo to work");

	// Create a Xuggler container object
	IContainer container = IContainer.make();

	// Open up the container
	if (container.open(filename, IContainer.Type.READ, null) < 0)
	    throw new IllegalArgumentException("could not open file: " + filename);

	// query how many streams the call to open found
	int numStreams = container.getNumStreams();

	// and iterate through the streams to find the first video stream
	int videoStreamId = -1;
	IStreamCoder videoCoder = null;
	for(int i = 0; i < numStreams; i++){
	    // Find the stream object
	    IStream stream = container.getStream(i);
	    // Get the pre-configured decoder that can decode this stream;
	    IStreamCoder coder = stream.getStreamCoder();

	    if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO){
		videoStreamId = i;
		videoCoder = coder;
		break;
	    }
	}
	if (videoStreamId == -1)
	    throw new RuntimeException("could not find video stream in container: "+filename);
	if (videoCoder.open() < 0)
	    throw new RuntimeException("could not open video decoder for container: "+filename);

	IVideoResampler resampler = null;
	if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24){
	    resampler = IVideoResampler.make(videoCoder.getWidth(), 
					     videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					     videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
	    if (resampler == null)
		throw new RuntimeException("could not create color space " +
					   "resampler for: " + filename);
	}
        //open the java window in this frame
	openJavaWindow();

	IPacket packet = IPacket.make();
	long firstTimestampInStream = Global.NO_PTS;
	long systemClockStartTime = 0;
	while(container.readNextPacket(packet) >= 0){
	    if (packet.getStreamIndex() == videoStreamId){
		IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
							   videoCoder.getWidth(), videoCoder.getHeight());

		int offset = 0;
		while(offset < packet.getSize()){
		    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
		    if (bytesDecoded < 0)
			throw new RuntimeException("got error decoding video in: "
						   + filename);
		    offset += bytesDecoded;
		    if (picture.isComplete()){
			IVideoPicture newPic = picture;
			if (resampler != null){
			    // we must resample
			    newPic=IVideoPicture.make(resampler.getOutputPixelFormat(),
						      picture.getWidth(), picture.getHeight());
			    if (resampler.resample(newPic, picture) < 0)
				throw new RuntimeException("could not resample video from: "
							   + filename);
			}
			if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
			    throw new RuntimeException("could not decode video" +
						       " as BGR 24 bit data in: " + filename);
			if (firstTimestampInStream == Global.NO_PTS)
			    {
				// This is our first time through
				firstTimestampInStream = picture.getTimeStamp();
				// get the starting clock time so we can hold up frames
				// until the right time.
				systemClockStartTime = System.currentTimeMillis();
			    } else {
			    long systemClockCurrentTime = System.currentTimeMillis();
			    long millisecondsClockTimeSinceStartofVideo =
				systemClockCurrentTime - systemClockStartTime;
			    // compute how long for this frame since the first frame in the
			    // stream.
			    // remember that IVideoPicture and IAudioSamples timestamps are
			    // always in MICROSECONDS,
			    // so we divide by 1000 to get milliseconds.
			    long millisecondsStreamTimeSinceStartOfVideo =
				(picture.getTimeStamp() - firstTimestampInStream)/1000;
			    final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
			    final long millisecondsToSleep = 
				(millisecondsStreamTimeSinceStartOfVideo -
				 (millisecondsClockTimeSinceStartofVideo +
				  millisecondsTolerance));
			    if (millisecondsToSleep > 0){
				try{
				    Thread.sleep(millisecondsToSleep);
				}catch (InterruptedException e){
				    return;
				}
			    }
			}

			// And finally, convert the BGR24 to an Java buffered image
			BufferedImage javaImage = Utils.videoPictureToImage(newPic);

			// and display it on the Java Swing window
			updateJavaWindow(javaImage);
		    }
		}
	    }else{
		do {} while(false);
	    }

	}
	if (videoCoder != null){
	    videoCoder.close();
	    videoCoder = null;
	}
	if (container !=null){
	    container.close();
	    container = null;
	}
    
    }

    /**
     * The window we'll draw the video on.
     * 
     */
    private static VideoImage mScreen = null;

    private static void updateJavaWindow(BufferedImage javaImage){
	mScreen.setImage(javaImage);
    }
    private static void openJavaWindow(){
	mScreen = new VideoImage();
    }
    public static VideoImage getVideoImage(){
	return mScreen;
    }//add to board while playing
    
    private static void closeJavaWindow(){
	System.exit(0);
    }
}
