package edu.usf.ratsim.robot.ssl;

import java.io.IOException;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SSLPilot {

	private static final long FORWARD_SLEEP = 500;
	private static final long TURN_SLEEP = 250;
    private static final long AFTER_SLEEP = 500;
	public final int FWDVEL = 1;
	public final int ROTVEL = 1;


	private OutputStream outStream;
	private SerialPort serialPort;

	public SSLPilot() {
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier
					.getPortIdentifier("/dev/teensy");
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass()
						.getName(), 2000);

				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				outStream = serialPort.getOutputStream();
			}
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendVels(int lf, int lb, int rf, int rb) {
		byte[] pkt = new byte[1 + 4 + 1 + 1];
		pkt[0] = (byte) 250;
		pkt[1] = (byte) (lf + 100);
		pkt[2] = (byte) (lb + 100);
		pkt[3] = (byte) (rf + 100);
		pkt[4] = (byte) (rb + 100);
		pkt[5] = 0;
		pkt[6] = (byte) 255;

		try {
			outStream.write(pkt);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void forward(){
		sendVels(FWDVEL,FWDVEL,FWDVEL,FWDVEL);
	}
	
	public void backward(){
		sendVels(-FWDVEL,-FWDVEL,-FWDVEL,-FWDVEL);
	}
	
	public void left(){
		sendVels(-ROTVEL,-ROTVEL,ROTVEL,ROTVEL);
	}

	public void circleLeft()
	{
		sendVels(-5, 1, 5, -1);
	}

	public void circleLeftConcave()
    {
       sendVels(-1, 5, 1, -5);
    }

	public void circleRight()
	{
		sendVels(5, -1, -5, 1);
	}	

	public void right(){
		sendVels(ROTVEL,ROTVEL,-ROTVEL,-ROTVEL);
	}
	
	public void still() {
		sendVels(0,0,0,0);
	}
	
	// Moves right while facing the same direction
	public void strafeLeft()
	{
		sendVels(-1, 2, 1, -2);
	}
	
	// Moves left while facing the same direction
	public void strafeRight()
	{
		sendVels(1, -2, -1, 2);
	}

	public void stepForward() {
		forward();
		try {
			Thread.sleep(FORWARD_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		still();
		try {
			Thread.sleep(AFTER_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stepRight() {
		right();
		try {
			Thread.sleep(TURN_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		still();
		try {
			Thread.sleep(AFTER_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stepLeft() {
		left();
		try {
			Thread.sleep(TURN_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		still();
		try {
			Thread.sleep(AFTER_SLEEP);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void finalize(){
		still();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SSLPilot p = new SSLPilot();
		
//		p.left();
		while (true){
/*		for (int i = 0; i < 30; i++){
			p.sendVels(1,1,1,1);
		
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		p.sendVels(0, 0, 0,0);
		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
			for (int i = 0; i < 30; i++){
				p.right();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (int i = 0; i < 30; i++){
				p.left();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (int i = 0; i < 30; i++){
				p.forward();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}

	public void close() {
		try {
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serialPort.close();
	}


}
