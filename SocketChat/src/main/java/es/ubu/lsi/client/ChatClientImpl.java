package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

public class ChatClientImpl implements ChatClient{

	/** El puerto es 1500 en todos los casos */
	private int port = 1500;
	
	/** Atributos de la clase */
	private int id;
	private String server, username;
	private boolean carryOn = true;
	
	/**
	 * Devuelve el puerto.
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Devuelve el Id.
	 * @return id
	 * @see #setId(int)
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Establece el Id.
	 * @param id
	 * @see #getId()
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Devuelve el servidor.
	 * @return server
	 * @see #setServer(String server)
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Establece el servidor.
	 * @param server
	 * @see #getServer()
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Devuelve el nombre de usuario.
	 * @return username
	 * @see #setUsername(String)
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario.
	 * @param username
	 * @see #getUsername()
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Establece si está activo.
	 * @return carryOn
	 * @see #setCarryOn(boolean)
	 */
	public boolean isCarryOn() {
		return carryOn;
	}

	/**
	 * Establece el estado.
	 * @param carryOn
	 * @see #carryOn
	 */
	public void setCarryOn(boolean carryOn) {
		this.carryOn = carryOn;
	}

	
	/** Sobreescribimos los métodos de la interfaz con su implementación. */
	
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
	public void disconect() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * ChatClientListener Class.
	 * 
	 * Clase interna para el flujo de entrada de los mensajes al cliente.
	 *  
	 */
	class ChatClientListener implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
