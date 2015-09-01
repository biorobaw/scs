//package edu.usf.ratsim.robot.romina;
//
//import java.awt.geom.Point2D.Float;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.LinkedList;
//import java.util.List;
//
//import javax.vecmath.Point3f;
//
//import edu.usf.ratsim.experiment.universe.virtual.UniverseFrame;
//import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
//import edu.usf.ratsim.robot.IRobot;
//import edu.usf.ratsim.robot.Landmark;
//import edu.usf.ratsim.robot.romina.protobuf.Connector.Command;
//import edu.usf.ratsim.robot.romina.protobuf.Connector.Command.Builder;
//import edu.usf.ratsim.robot.romina.protobuf.Connector.Command.CommandType;
//import edu.usf.ratsim.robot.romina.protobuf.Connector.Position;
//import edu.usf.ratsim.robot.romina.protobuf.Connector.Response;
//import edu.usf.ratsim.support.Configuration;
//import edu.usf.ratsim.support.Debug;
//
//public class Romina implements IRobot {
//
//	private static final float CLOSE_TO_FOOD_THRS = Configuration
//			.getFloat("VirtualUniverse.closeToFood");
//
//	private static Romina romina;
//
//	private Socket protoSocket;
//	private boolean validResponse;
//	private Response r;
//	private ExperimentUniverse world;
//
//	private String host;
//	private int port;
//
//	public Romina(String host, int port, ExperimentUniverse world) {
//		if (Configuration.getBoolean("UniverseFrame.display")) {
//			UniverseFrame worldFrame = new UniverseFrame(
//					(VirtUniverse) world);
//			worldFrame.setVisible(true);
//		}
//
//		((SLAMUniverse) world).setRominaRobot(this);
//		this.world = world;
//
//		this.host = host;
//		this.port = port;
//		protoSocket = null;
//		establishConnection(host, port);
//
//		if (Configuration.getBoolean("Experiment.startRobot")) 
//			startRobot();
//
//		validResponse = false;
//		romina = this;
//	}
//
//	private void establishConnection(String host, int port) {
//		boolean succeded = false;
//		while (!succeded)
//			try {
//				if (protoSocket != null)
//					protoSocket.close();
//				
//				System.out.println("Trying to connect to " + host + " " + port);
//				protoSocket = new Socket(host, port);
//				protoSocket.setSoTimeout(10000);
//				System.out.println("Connection stablished");
//				succeded = true;
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//			} catch (IOException e) {
////				e.printStackTrace();
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//	}
//
//	@Override
//	public void rotate(float degrees) {
//		Builder b = Command.newBuilder();
//		b.setType(CommandType.doAction);
//		b.setAngle(degrees);
//		Command c = b.build();
//		
//		boolean succeded = false;
//		while (!succeded)
//			try {
//				sendCommnad(c, protoSocket);
//				succeded = true;
//			} catch (Exception e) {
//				establishConnection(host, port);
//			}
//		
//
//		try {
//			getResponse(protoSocket);
//		} catch (Exception e) {
//			establishConnection(host, port);
//		}
//
//		validResponse = false;
//
//	}
//
//	@Override
//	public void eat() {
//		world.robotEat();
//		if (Debug.printTryingToEat)
//			System.out.println("Romina ate");
//
//		stop();
//
//	}
//
//	@Override
//	public boolean[] getAffordances() {
//		r = getInfo();
//
//		boolean res[] = new boolean[r.getAffs().getAffCount()];
//		for (int i = 0; i < r.getAffs().getAffCount(); i++)
//			res[i] = r.getAffs().getAff(i);
//
//		return res;
//	}
//
//	private Response getInfo() {
//		Response resp = null;
//		boolean succeded = false;
//		while (!succeded || resp == null || resp.getRobotPos() == null)
//			try {
//				if (!validResponse) {
//					Builder b = Command.newBuilder();
//					b.setType(CommandType.getInfo);
//					Command c = b.build();
//					sendCommnad(c, protoSocket);
//					resp = getResponse(protoSocket);
//					validResponse = true;
//				} else {
//					resp = r;
//				}
//				succeded = true;
//			} catch (Exception e) {
//				System.err
//						.println("Error getting response, sending command again");
//				System.err.print(e.toString());
//				establishConnection(host, port);
//			}
//
//		return resp;
//	}
//
//	@Override
//	public boolean hasFoundFood() {
//		Point3f robot = getRobotPoint();
//		for (Landmark lm : getLandmarks()) {
//			if (world.isFeederActive(lm.id)
//					&& lm.location.distance(new Point3f()) < CLOSE_TO_FOOD_THRS)
//				return true;
//		}
//
//		return false;
//	}
//
//	@Override
//	public void startRobot() {
//		Builder b = Command.newBuilder();
//		b.setType(CommandType.startRobot);
//		Command c = b.build();
//		
//		boolean succeded = false;
//		while (!succeded)
//			try {
//				sendCommnad(c, protoSocket);
//				succeded = true;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		try {
//			getResponse(protoSocket);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		validResponse = false;
//	}
//
//	@Override
//	public BufferedImage[] getPanoramica() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void forward() {
//		rotate(0);
//		validResponse = false;
//	}
//
//	@Override
//	public boolean[] getAffordances(int wallLookahead) {
//		return getAffordances();
//	}
//
//	@Override
//	public List<Landmark> getLandmarks() {
//		r = getInfo();
//
//		List<Landmark> lms = new LinkedList<Landmark>();
//		for (edu.usf.ratsim.robot.romina.protobuf.Connector.Landmark lm : r
//				.getLandmarksList())
//			lms.add(new Landmark(lm));
//
//		return lms;
//	}
//
//	@Override
//	public boolean hasTriedToEat() {
//		return false;
//	}
//
//	public Point3f getRobotPoint() {
//		r = getInfo();
//
//		return new Point3f(r.getRobotPos().getX(), r.getRobotPos().getY(), 0);
//	}
//
//	private void sendCommnad(Command c, Socket protoSocket) throws Exception {
//		c.writeTo(protoSocket.getOutputStream());
//	}
//
//	public float getRobotOrientation() {
//		r = getInfo();
//
//		return r.getRobotPos().getTheta();
//	}
//
//	private Response getResponse(Socket protoSocket) throws Exception {
//		return Response.parseDelimitedFrom(protoSocket.getInputStream());
//	}
//
//	public boolean isCloseToAFeeder() {
//		Point3f robot = getRobotPoint();
//		for (Landmark lm : getLandmarks()) {
//			// Hack to avoid bug discovered that leads to feeding from wrong feeder
//			if (lm.id == 3 && lm.location.distance(new Point3f()) < CLOSE_TO_FOOD_THRS)
//				return true;
//		}
//
//		return false;
//	}
//
//	public void invalidateResponse() {
//		validResponse = false;
//	}
//
//	public void resetPosition(Float pos, float angle) {
//		boolean succeded = false;
//		while (!succeded)
//			try {
//				Builder b = Command.newBuilder();
//				b.setType(CommandType.resetPosition);
//				edu.usf.ratsim.robot.romina.protobuf.Connector.Position.Builder b2 = Position
//						.newBuilder();
//				b2.setX(pos.x);
//				b2.setY(pos.y);
//				b2.setTheta(angle);
//				Position p = b2.build();
//				b.setPos(p);
//				Command c = b.build();
//				sendCommnad(c, protoSocket);
//
//				getResponse(protoSocket);
//
//				// Sleep to wait for update to propagate
//				Thread.sleep(3000);
//
//				// Invalidate response object
//				validResponse = false;
//				succeded = true;
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				System.err
//						.println("Did not get response, reseting position again");
//				establishConnection(host, port);
//			}
//	}
//
//	public static Romina getRomina() {
//		return romina;
//	}
//
//	public void stop() {
//		Builder b = Command.newBuilder();
//		b.setType(CommandType.doAction);
//		b.setStop(true);
//		b.setAngle(0);
//		Command c = b.build();
//		boolean succeded = false;
//		while (!succeded)
//			try {
//				sendCommnad(c, protoSocket);
//				succeded = true;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		try {
//			getResponse(protoSocket);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		validResponse = false;
//	}
//
//}
