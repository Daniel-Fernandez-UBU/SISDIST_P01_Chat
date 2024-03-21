package es.ubu.lsi.client;

import java.io.IOException;

import es.ubu.lsi.common.ChatMessage;

public class ChatClientImpl implements ChatClient{

	/** Atributos de la clase */
	private int id, port;
	private String server, username;
	private boolean carryOn = true;
	
	/**
	 * Constructor de la clase.
	 * @param server
	 * @param port
	 * @param username
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.setPort(port);
		this.setServer(server);
		this.setUsername(username);
	}

	/** Inicio -  Getters y Setters **/
	/**
	 * Devuelve el puerto.
	 * @return port
	 * @see #setPort(int)
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Establece el puerto.
	 * @param port
	 * @see #getPort()
	 */
	private void setPort(int port) {
		this.port = port;
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
	public boolean getCarryOn() {
		return carryOn;
	}

	/**
	 * Establece el estado.
	 * @param carryOn
	 * @see #getcarryOn
	 */
	public void setCarryOn(boolean carryOn) {
		this.carryOn = carryOn;
	}
	/** Fin - Getters y Setters **/
	
	/** Sobreescribimos los métodos de la interfaz con su implementación. */
	
	/**
	 * Inicia el cliente.
	 * @return true or false
	 */
	@Override
	public boolean start() {
		return false;
	}

	/**
	 * Send Message.
	 * @param msg
	 */
	@Override
	public void sendMessage(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Desconecta el cliente.
	 */
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
	
	/**
	 * Método main.
	 * 
	 * Encargado de lanzar los clientes.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		/** Definimos el puerto y servidor por defecto */
		final int port = 1500;
		String server = "localhost";
		String username = null;
		
		/**
		 * Definimos el número de argumentos permitidos
		 * y la forma de uso de la clase.
		 */
        if (args.length == 2) {
        	server = args[0];
        	username = args[1];
        } else if (args.length == 1) {
        	username = args[0];
        } else {
            System.err.println(
                    "Usage: java ChatClientImpl <server> <username>");
            System.err.println(" OR ");
            System.err.println(
                    "Usage: java ChatClientImpl <username>");
                System.exit(1);
        }
        
        //Creamos nuestro cliente
        ChatClientImpl cliente = new ChatClientImpl(server, port, username);
        
        cliente.start();
		
	}

}
