package es.ubu.lsi.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.List;

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
	private List<String> clientesAct;
	private List<String> clientesBan;
	
	// Constructor de la clase
	public ChatServerImpl() {
		this.port = DEFAULT_PORT;

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
		
		private List<Socket> clientSockets;
				
		public ChatServerThreadForClient(Socket clientSocket) {
			this.clientSockets.add(clientSocket);
		}

		public void run() {
			try {
				for (Socket clientSocket : this.clientSockets) {
		            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		        	BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					String inputLine;
		            
					while ((inputLine = in.readLine()) != null) {
		            	System.out.println(clientSocket.getPort() + ":" + inputLine);
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
