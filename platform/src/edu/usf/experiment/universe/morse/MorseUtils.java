package edu.usf.experiment.universe.morse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MorseUtils {

	private static HashMap<String, Integer> streamPorts;

	public static void startSimulator() {
			
			String[] envp = { "DISPLAY=:0.0", "MORSE_SILENT_PYTHON_CHECK=1", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games" };
			try {
				// Wait to release the port
				Thread.sleep(500);
				// Execute the process
				Process p = Runtime.getRuntime().exec("killall -9 blender", envp);
				
				while(p.getErrorStream().available() > 0)
				{
			    	System.err.print((char)p.getErrorStream().read());
				}
				
				p = Runtime.getRuntime().exec("morse run morris", envp);
				
				cacheStreamPorts();
				
				while(p.getErrorStream().available() > 0)
				{
			    	System.err.print((char)p.getErrorStream().read());
				}
			    
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("[+] Morse simulator initialized");
	}		
	
	private static void cacheStreamPorts() {
		Socket s = null;
		boolean gotConnection = false;
		while (!gotConnection){
			try {
				s = new Socket("localhost", 4000);
				gotConnection = true;
			} catch (IOException e) {
				System.out.println("[+] Error connecting, waiting");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			w.write("id1 simulation list_streams\n");
			w.flush();
			String line = r.readLine();
			StringTokenizer st = new StringTokenizer(line, " ");
			st.nextToken();

			// Parse stream list and get ports
			streamPorts = new HashMap<String, Integer>();
			if (st.nextToken().equals("SUCCESS")) {
				String listStr = st.nextToken("]");
				listStr = listStr.replaceAll(" \\[", "");
				StringTokenizer streamNames = new StringTokenizer(listStr, ", ");
				int i = 2;
				while (streamNames.hasMoreTokens()) {
					String stream = streamNames.nextToken();
					String rqst = "id" + i + " simulation get_stream_port [" + stream + "]\n";
					w.write(rqst);
					w.flush();
					i++;

					StringTokenizer resp = new StringTokenizer(r.readLine(), " ");
					resp.nextToken();
					if (resp.nextToken().equals("SUCCESS")) {
						streamPorts.put(stream.replaceAll("\"", ""), Integer.parseInt(resp.nextToken()));
					}
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Map<String, Integer> getStreamPorts() {
		return streamPorts;
	}

	public static void main (String[] args){
		startSimulator();
	}
}
