package com.github.biorobaw.scs.gui.utils;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverterFactory;

public class VideoRecorder {

	// JAVA AWT ROBOT TO GET SCREEN CAPTURES
	Robot awt_robot = null;
	
	// Default recording format values:
	int width = 800, height = 800; // 1920x1080, 1280x720, 720x480
	static final Rational framerate = Rational.make(1, 30);
	static final PixelFormat.Type pixel_format = PixelFormat.Type.PIX_FMT_YUV420P;
	static final String default_codec = "libx264"; // if null, will choose automatically based on format
	static final String default_format = null; // if null, will choose automatically based on file name
	// Encoder and muxer to create output video file
//	{
//		for(var c : Codec.getInstalledCodecs())
//			System.out.println("codec : " + c);
//		for(var m : MuxerFormat.getFormats())
//			System.out.println("muxer: " + m);
//	}
	Encoder encoder;
	Muxer muxer;
	
	
	// Variables for adding frames:
	MediaPicture picture;
	MediaPacket packet = MediaPacket.make();
	long frame_id = 0;
	
	
	
	Thread recorder_thread;
	boolean exit_flag = false;
	boolean paused = false;
	boolean recorder_is_waiting = false;
	Rectangle bounds = null;
	private Semaphore waitSemaphore = new Semaphore(0);
	
	public VideoRecorder(String output_file, int width, int height) {
				
		try {
			awt_robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		 
		// create muxer to store video, get format and codec
		muxer = Muxer.make(output_file, null, default_format);
		var format = muxer.getFormat();
		var codec = Codec.findEncodingCodecByName(default_codec);
		if(codec == null) {
			codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
			System.out.println("WARNINNG: output video codec could not be found, switching to default value for the format: " + codec);
		}

		// CREATE ENCODER, OPEN IT AND ADD IT TO MUXER
		encoder = Encoder.make(codec);
		encoder.setWidth(width);
		encoder.setHeight(height);
		encoder.setPixelFormat(pixel_format);
		encoder.setTimeBase(framerate);
		if( format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER) )
			encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);	
		encoder.open(null, null);
		muxer.addNewStream(encoder);
		
		// OPEN MUXER
		try {
			muxer.open(null,null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(-1);
		} 
		
		// CREATE OUTPUT PICTURE
		picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixel_format);
		this.width = encoder.getWidth();
		this.height = encoder.getHeight();
		System.out.println("[+] Recording resolution: " + width +" x " + height);
		picture.setTimeBase(framerate); 
		
		
		recorder_thread = new Thread(()->{
			recorderThread();
		});
		recorder_thread.start();
		
	}
	
	public void update_bounds(Rectangle bounds) {
		if(muxer == null) return;
		
		// Code removed from here and move to recorder thread
		
		if( this.bounds == null ||
			this.bounds.x != bounds.x ||
			this.bounds.y != bounds.y ||
			this.bounds.width != bounds.width ||
			this.bounds.height != bounds.height ) {
			
			// bounds change, update bounds:
			var r = (Rectangle)bounds.clone();
			synchronized (this) {
				this.bounds = r;
			}	
		}
	}
	
	public void update_bounds(int x, int y, int w, int h) {
		update_bounds(new Rectangle(x,y,w,h));
	}
	
	
	public void endRecording() {
		System.out.println("Closing recoding...");
		synchronized(this) {
			exit_flag = true;
			if(recorder_is_waiting) waitSemaphore.release();
		}
		
		try {
			recorder_thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
	     * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
	     * input until the output is not complete.
	     */
	    do {
	        encoder.encode(packet, null);
	        if (packet.isComplete())
	          muxer.write(packet,  false);
	      } while (packet.isComplete());
	      
	      /** Finally, let's clean up after ourselves. */
	      muxer.close();
	      muxer = null;
	      System.out.println("Closed video");
	}
	
	public BufferedImage format_frame(BufferedImage img) {

		final var required_format = BufferedImage.TYPE_3BYTE_BGR;
		
		// image to be copied
		Image copy_src = img;

		// check if resize is necessary:
		if(img.getWidth()!=width || img.getHeight()!=height) 
			copy_src = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		else if(img.getType() == required_format) return img; // don't need to do anything
		
		// convert image type:
	    var result = new BufferedImage(width, height, required_format);
	    result.getGraphics().drawImage(copy_src, 0, 0, null);

	    return result;
	}
	
	
	void recorderThread() {
		
		// wait until bounds have been set:
		while(bounds == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// start recording process
		Rectangle local_bounds;
		System.err.println("Starting to record");
		while(true) {
			
			// try to get bounds for the image
			local_bounds = null;
			synchronized(this) {
				if(exit_flag) return; // if exit flag, return
				if(paused) recorder_is_waiting = true; // if paused, signal wait
				else local_bounds = (Rectangle)bounds.clone(); // else get bounds
			}
			
			// if I couldnt get the bounds it is because the recording is paused
			// wait until pause is removed and then start over
			if(local_bounds == null) {
				try {
					System.err.println("rocorder waiting: " + paused);
					waitSemaphore.acquire();
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Record image
//			var tic = Debug.tic();

			// get capture
			var capture = awt_robot.createScreenCapture(local_bounds);
			
			// GOT AN IMAGE, NOW PROCESS IT
			var screen = format_frame(capture);
			
			// Convert media
			MediaPictureConverterFactory.createConverter(screen, picture)
										.toPicture(picture, screen, frame_id++);
			
			// encode and write image
			do {
				encoder.encode(packet,  picture);
				if(packet.isComplete()) muxer.write(packet,	false);
			} while (packet.isComplete());
			
//			System.out.println("record frame processing time: " + (Debug.toc(tic)));
			
		}
	}

	public void pauseRecording(boolean value) {
		synchronized(this) {
			if(paused == value) return;
			paused=value;
			
			if(!paused && recorder_is_waiting) {
				recorder_is_waiting = false;
				waitSemaphore.release();
			}
			
		}
	}
	
}

