package edu.usf.vlwsim.display;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.microcrowd.loader.java3d.max3ds.Loader3DS;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.universe.SimpleUniverse;

import edu.usf.experiment.utils.ElementWrapper;

public class RobotNode extends ExpUniverseNode {

	public static final int NUM_ROBOT_VIEWS = 5;
	public static final float CAMERA_HEIGHT = 0.025f;
	// Off-screen image sizes
	public static final int IMAGE_HEIGHT = 80;
	public static final int IMAGE_WIDTH = 80;

	private TransformGroup tg;
	private View[] robotViews = new View[NUM_ROBOT_VIEWS];
	private Canvas3D[] offScreenCanvas;
	private ImageComponent2D[] offScreenImages;

	public RobotNode(ElementWrapper params, boolean display) {
		float x = params.getChildFloat("x");
		float y = params.getChildFloat("y");
		float z = params.getChildFloat("z");
		float theta = params.getChildFloat("theta");

		// Initialize the transform group
		// Keep it public to move the robot in the future
		Transform3D rPos = new Transform3D();
		rPos.setTranslation(new Vector3f(x, y, z));
		rPos.setRotation(new AxisAngle4d(new Vector3d(0, 0, 1), theta));
		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setTransform(rPos);
		// Add the transform group to the world branch group
		addChild(tg);

		// Create the robot itself
		BranchGroup robotBG = new BranchGroup();
		tg.addChild(robotBG);

		Loader3DS myOBJ = new Loader3DS();
		Scene myOBJScene = null;

		// Attempt to load in the OBJ content using ObjectFile.
		try {
			if (! new File("./rat.3ds"	).exists())
				System.out.println("File for rat model missing");
			myOBJScene = myOBJ.load("./rat.3ds");
			// myOBJScene = myOBJ.load("C:\\workspace\\Java3D\\troll.obj");
		} catch (FileNotFoundException e) {
			System.out.println("Could not open OBJ file...exiting");
			System.exit(1);
		}

		Transform3D scale = new Transform3D();
		scale.setScale(.001);
		scale.setRotation(new AxisAngle4d(new Vector3d(1, 0, 0), Math.PI / 2));
		TransformGroup scaleTG = new TransformGroup();
		scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scaleTG.setTransform(scale);
		tg.addChild(scaleTG);
		scaleTG.addChild(myOBJScene.getSceneGroup());

		// Transforms
		Vector3f robotCameraOffset = new Vector3f(0.1f, 0, CAMERA_HEIGHT);
		TransformGroup camTG = new TransformGroup();
		Transform3D camT = new Transform3D();
		camT.setTranslation(robotCameraOffset);
		Transform3D camRot = new Transform3D();
		camRot.rotX(Math.PI / 2);
		camT.mul(camRot);
		camRot = new Transform3D();
		camRot.rotY(-Math.PI / 2);
		camT.mul(camRot);
		camTG.setTransform(camT);
		robotBG.addChild(camTG);

		// Add on-screen views
		for (int i = 0; i < NUM_ROBOT_VIEWS; i++) {
			TransformGroup vTG = new TransformGroup();
			Transform3D vT = new Transform3D();
			vT.rotY(Math.toRadians(90 - i * 45));
			vTG.setTransform(vT);
			CameraView cv = new CameraView(true);
			robotViews[i] = cv.getView();
			vTG.addChild(cv.getRootBG());
			camTG.addChild(vTG);
		}

		// Add off-screen views
		// Create off-screen canvas to see through the robots views
		if (display) {
			offScreenCanvas = new Canvas3D[NUM_ROBOT_VIEWS];
			offScreenImages = new ImageComponent2D[NUM_ROBOT_VIEWS];
			GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
			for (int i = 0; i < NUM_ROBOT_VIEWS; i++) {
				offScreenCanvas[i] = new Canvas3D(config, true);
				offScreenImages[i] = new ImageComponent2D(ImageComponent2D.FORMAT_RGB,
						new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB));
				offScreenImages[i].setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
				robotViews[i].addCanvas3D(offScreenCanvas[i]);
				offScreenCanvas[i].setOffScreenBuffer(offScreenImages[i]);
				offScreenCanvas[i].getScreen3D().setPhysicalScreenWidth(0.0254d / 90.0 * IMAGE_WIDTH);
				offScreenCanvas[i].getScreen3D().setPhysicalScreenHeight(0.0254d / 90.0 * IMAGE_HEIGHT);
				offScreenCanvas[i].getScreen3D().setSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
			}
		}
	}

	public TransformGroup getTransformGroup() {
		return tg;
	}

	public View[] getRobotViews() {
		return robotViews;
	}

	public static int getImageHeight() {
		return IMAGE_HEIGHT;
	}

	public static int getImageWidth() {
		return IMAGE_WIDTH;
	}

	public Canvas3D[] getOffScreenCanvas() {
		return offScreenCanvas;
	}

	public ImageComponent2D[] getOffScreenImages() {
		return offScreenImages;
	}
}
