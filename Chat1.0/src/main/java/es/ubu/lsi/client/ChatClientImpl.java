package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

public class ChatClientImpl implements ChatClient {
	
	private String server;
	private int port;
	private String username;
	
	/** Constructor de la clase. 
	 * 
	 * Se quita el puerto de la definición porque siempre es el 1500
	 * 
	 * @param server - servidor al que conectarse
	 * @param username - nombre de usuario
	 */
	public ChatClientImpl(String server, String username) {
		this.server = server;
		this.username = username;
		this.port = 1500;
	}
	
	/**
	 * Constructor alternativo si no se indica servidor.
	 * 
	 * Se toma como servidor "localhost"
	 * 
	 * @param username - nombre de usuario
	 */
	public ChatClientImpl(String username) {
		this.server = "localhost";
		this.username = username;
		this.port = 1500;
	}

	/**
	 * Método main.
	 * 
	 * Arranca el hilo principal de ejecución del cliente.
	 * Instancia el cliente y arranca un hilo adicional (método start) a través de Chat ClientListener.
	 * 
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendMessage(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Clase interna ChatClientListener.
	 * 
	 */
	public class ChatClientListener implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}


}
