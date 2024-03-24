package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer {
	
	
	
	public int port;

	class ChatServerThreadForClient extends Thread{

		private List<Socket> clientSocket = new ArrayList<>();
		
		private int id;
		private String username;

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

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void broadcast(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		
	}
	
	 public static void main(String[] args) throws IOException {
	        
		int port = 1500;
		 
        if (args.length != 0) {
            System.err.println("Usage: java ChatServerImpl");
            System.exit(1);
        }
        
        ChatServerImpl chatServer = new ChatServerImpl();
    
    	Thread thread = new Thread(() -> {
	        try  (
	            	ServerSocket serverSocket = new ServerSocket(port);
	   		)
	        {
	            while (true){
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Nuevo Cliente: " + clientSocket.getInetAddress() + "/" + clientSocket.getPort());
	            	Thread hilonuevocliente = chatServer.new ChatServerThreadForClient(clientSocket);
	            	hilonuevocliente.start();
	            }
	        	
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port: " + port + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
    	}); 
    	thread.start();
    	
    	}

}
