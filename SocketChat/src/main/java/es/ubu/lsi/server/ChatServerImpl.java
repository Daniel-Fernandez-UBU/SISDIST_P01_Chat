package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerImpl {
	
	
	
	
	
	
	
	public int port;

	class ChatServerThreadForClient extends Thread{

		private List<Socket> clientSocket = new ArrayList<>();

		public ChatServerThreadForClient(Socket clientSocket) {
			this.clientSocket.add(clientSocket);
		}

		public void run() {
			try {
				for (Socket sock : clientSocket) {
					
		            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
		        	BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					String inputLine;

					while ((inputLine = in.readLine()) != null) {
		            	System.out.println(sock.getPort() + ":" + inputLine);
		                out.println(inputLine);
		            }
				}

	        }
	        catch (IOException e) {
	            System.out.println("Exception caught on thread");
	            System.out.println(e.getMessage());
	        }
	      }
	}

}
