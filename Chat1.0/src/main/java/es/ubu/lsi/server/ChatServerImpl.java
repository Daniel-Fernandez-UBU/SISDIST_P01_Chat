package es.ubu.lsi.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer{

	// Puerto por defecto
	final int DEFAULT_PORT = 1500;
	
	// Atributos estáticos
	private static int clientId;
	private static SimpleDateFormat sdf;
	
	// Atributos internos
	private int port;
	private boolean alive;
	
	// Constructor de la clase
	public ChatServerImpl(int port) {
		this.port = port;
	}
	
	// Método Main
	public static void main(String[] args) {
		
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
	public void broadcas(ChatMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		
	}
	
	class ChatServerThreadForClient extends Thread{
		
		private Socket clientSocket;
		
		private int port = ChatServerImpl.this.port;
				
		public ChatServerThreadForClient(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public void run() {
			try {
	            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	        	BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine;
	            
				while ((inputLine = in.readLine()) != null) {
	            	System.out.println(clientSocket.getPort() + ":" + inputLine);
	                out.println(inputLine);
	            }
	        }
	        catch (IOException e) {
	            System.out.println("Exception caught on thread");
	            System.out.println(e.getMessage());
	        }
	      }
		
	}
}
