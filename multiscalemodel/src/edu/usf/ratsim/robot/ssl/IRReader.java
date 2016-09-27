package edu.usf.ratsim.robot.ssl;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Class that polls a program that sends three IR sensor data from arduino.
 * 
 * Needs a compiled so file for the library, apt-get install librxtx-java,
 * locate librxtxSerial.so and add it in build path.
 * 
 * @author Martin Llofriu
 * 
 */
public class IRReader extends Thread {

	private static final float CLOSETHRS = 5.0f;
	float leftIR, rightIR, frontIR;
	private Scanner scanner;

	public IRReader() {
		leftIR = 0;
		rightIR = 0;
		frontIR = 0;

		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier
					.getPortIdentifier("/dev/uno");
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass()
						.getName(), 2000);

				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				scanner = new Scanner(in);
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

	public void run() {
		while (true) {
			synchronized (this) {
				leftIR = scanner.nextFloat();
				frontIR = scanner.nextFloat();
				rightIR = scanner.nextFloat();
			//	System.out.println(leftIR + " " + frontIR + " " + rightIR);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized float getLeftIR() {
		return leftIR;
	}

	public synchronized float getRightIR() {
		return rightIR;
	}

	public synchronized float getFrontIR() {
		return frontIR;
	}
	
	public synchronized boolean somethingClose(){
		return leftIR < CLOSETHRS || rightIR < CLOSETHRS || frontIR < CLOSETHRS;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IRReader irreader = new IRReader();
		irreader.start();

		while (true) {
			System.out.print(irreader.getLeftIR() + " ");
			System.out.print(irreader.getFrontIR() + " ");
			System.out.print(irreader.getRightIR() + " ");
			System.out.println();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
