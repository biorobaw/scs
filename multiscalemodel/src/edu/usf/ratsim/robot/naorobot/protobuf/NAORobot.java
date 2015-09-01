//package edu.usf.ratsim.robot.naorobot.protobuf;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.LinkedList;
//import java.util.List;
//
//import edu.usf.ratsim.experiment.universe.virtual.UniverseFrame;
//import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
//import edu.usf.ratsim.robot.IRobot;
//import edu.usf.ratsim.robot.Landmark;
//import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command;
//import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command.Builder;
//import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Command.CommandType;
//import edu.usf.ratsim.robot.naorobot.protobuf.Connector.Response;
//import edu.usf.ratsim.support.Configuration;
//
//public class NAORobot implements IRobot {
//
//	private Socket protoSocket;
//
//	public NAORobot(String host, int port, ExperimentUniverse world) {
//		if (Configuration.getBoolean("UniverseFrame.display")) {
//			UniverseFrame worldFrame = new UniverseFrame((VirtUniverse) world);
//			worldFrame.setVisible(true);
//		}
//		
//		try {
//			protoSocket = new Socket(host, port);
//			System.out.println("Connection stablished");
//
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			Builder b = Command.newBuilder();
//			b.setType(CommandType.startRobot);
//			Command c = b.build();
//			c.writeTo(protoSocket.getOutputStream());
//
//			Response.parseDelimitedFrom(protoSocket.getInputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void rotate(float degrees) {
//		try {
//			Builder b = Command.newBuilder();
//			b.setType(CommandType.doAction);
//			b.setAngle(degrees);
//			Command c = b.build();
//			c.writeTo(protoSocket.getOutputStream());
//
//			Response.parseDelimitedFrom(protoSocket.getInputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void eat() {
//	}
//
//	@Override
//	public boolean[] getAffordances() {
//		try {
//			Builder b = Command.newBuilder();
//			b.setType(CommandType.getInfo);
//			Command c = b.build();
//			c.writeTo(protoSocket.getOutputStream());
//
//			Response r = Response.parseDelimitedFrom(protoSocket
//					.getInputStream());
//
//			boolean res[] = new boolean[r.getAffs().getAffCount()];
//			for (int i = 0; i < r.getAffs().getAffCount(); i++)
//				res[i] = r.getAffs().getAff(i);
//
//			return res;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public boolean hasFoundFood() {
//		return false;
//	}
//
//	@Override
//	public void startRobot() {
//		try {
//			Builder b = Command.newBuilder();
//			b.setType(CommandType.startRobot);
//			Command c = b.build();
//			c.writeTo(protoSocket.getOutputStream());
//
//			Response.parseDelimitedFrom(protoSocket.getInputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
//	}
//
//	@Override
//	public boolean[] getAffordances(int wallLookahead) {
//		return getAffordances();
//	}
//
//	@Override
//	public List<Landmark> getLandmarks() {
//		try {
//			Builder b = Command.newBuilder();
//			b.setType(CommandType.getInfo);
//			Command c = b.build();
//			c.writeTo(protoSocket.getOutputStream());
//
//			Response r = Response.parseDelimitedFrom(protoSocket
//					.getInputStream());
//
//			List<Landmark> lms = new LinkedList<Landmark>();
//			for (edu.usf.ratsim.robot.naorobot.protobuf.Connector.Landmark lm : r
//					.getLandmarksList())
//				lms.add(new Landmark(lm));
//
//			return lms;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return null;
//	}
//
//	@Override
//	public boolean hasTriedToEat() {
//		return false;
//	}
//
//}
