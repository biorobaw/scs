package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import edu.usf.experiment.Globals;

public class CanvasRecorder {
	
	
	public java.awt.Panel panel;
	
	int id = 0;
	int episode;
	
	public CanvasRecorder(java.awt.Panel _panel,String _saveFile){
		panel = _panel;

		
	}
	
	public void startRecording(){
		
	
	}
	
	public void stopRecording(){
		
	
	}
	
	public void record(){
		
		episode = (int)Globals.getInstance().get("episode");
		int id = (int)Globals.getInstance().get("cycle");
		
		String filename = "im/img"+episode+"-"+id+".png";
		
		
		

		UniverseFrame vu = VirtUniverse.getInstance().frame;
		
		
		
		
		
		
		
		// render to off screen
//        BufferedImage bImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
//        ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
//        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
//        VirtUniverse.getInstance().frame.topViewCanvas.setOffScreenBuffer(buffer);
//        VirtUniverse.getInstance().frame.topViewCanvas.renderOffScreenBuffer();
//        VirtUniverse.getInstance().frame.topViewCanvas.waitForOffScreenRendering();
//
//        // write to file
//        File output = new File(filen);
//        try {
//            ImageIO.write(VirtUniverse.getInstance().frame.topViewCanvas.getScreen .getRenderedImage(), "png", output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		
		
		

		
		
		try {
			VirtUniverse.getInstance().frame.repaint();
			FileOutputStream fileOut = new FileOutputStream(filename);
		    java.awt.Robot r = new java.awt.Robot();
		    BufferedImage bi = r.createScreenCapture(new java.awt.Rectangle(
		            (int) panel.getLocationOnScreen().getX(), (int) panel
		                    .getLocationOnScreen().getY(), panel.getBounds().width,
		                    panel.getBounds().height));
		    
			ImageIO.write(bi, "jpeg", fileOut);
			
			fileOut.flush();
		    fileOut.close();
		    
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	

}
