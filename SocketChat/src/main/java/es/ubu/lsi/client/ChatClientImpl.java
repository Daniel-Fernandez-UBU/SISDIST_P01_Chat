package es.ubu.lsi.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

public class ChatClientImpl implements ChatClient{

	/** Atributos de la clase */
	private int id, port;
	private String server, username;
	private boolean carryOn = true;
	
	/** Atributos adicionales */
	private Socket miSocket;
	private ObjectOutputStream data;
	
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
		// Generamos un "id" único para cada cliente
		String uniqueId = getUsername() + System.currentTimeMillis();
		this.id = uniqueId.hashCode();
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
	 * @see #iscarryOn
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
		
		Thread hilo = new Thread(new ChatClientListener());
		hilo.start();
		
		return isCarryOn();
	}

	/**
	 * Send Message.
	 * @param msg
	 */
	@Override
	public void sendMessage(ChatMessage msg) {
		try {
			data.writeObject(msg);
		} catch (IOException e) {
			System.out.println("sendMessage: IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Desconecta el cliente.
	 */
	@Override
	public void disconect() {
		try {
			miSocket.close();
		} catch (IOException e) {
			System.out.println("disconect: IOException: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo que establece la conexión con el servidor.
	 */
	private void establecerConexion() {
		try {
			miSocket = new Socket(this.server,this.port);
			data = new ObjectOutputStream(miSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error al establecer la conexion: " + e.getMessage());
		}
	}
	
	/**
	 * ChatClientListener Class.
	 * 
	 * Clase interna para el flujo de entrada de los mensajes al cliente.
	 *  
	 */
	
	private class ChatClientListener implements Runnable {
        /**
         * Levanta la conexión en modo escucha para recibir los mensajes del servidor.
         * 
         * Si falla al arrancar el modo escucha, establece en "false" cliente.carryOn.
         */
        @Override
        public void run() {
        	
        	try {
        		ObjectInputStream flujoEntrada = new ObjectInputStream(miSocket.getInputStream());
        		while(true) {
        			ChatMessage dataRecibida = (ChatMessage) flujoEntrada.readObject();
        			System.out.println(dataRecibida.getId() + ": " + dataRecibida.getMessage()); //Mostramos el mensaje
        		}
        	//Controlamos los mensajes de las excepciones
        	} catch (EOFException e) {
        		System.out.println("ChatClientListener: EOFException: " + e.getMessage());
        	} catch (IOException i) {
        		System.out.println("ChatClientListener: IOException: " + i.getMessage());
        	} catch (ClassNotFoundException c) {
        		System.out.println("ChatClientListener: ClassNotFoundException: " + c.getMessage());
        	}

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
	    Scanner sc = new Scanner(System.in); //Recoger lo que se escriba por consola
	    String mensaje;
	    ChatMessage datosEnvio;
    	MessageType tipoMens = MessageType.MESSAGE;
	    

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

	    // Creamos nuestro cliente
	    ChatClientImpl cliente = new ChatClientImpl(server, port, username);
	    
	    // Establecemos la conexión con el servidor
	    cliente.establecerConexion();
	    
	    // Iniciar el modo escucha
	    cliente.start();
	    
	    

	    while(cliente.carryOn) {
	    	mensaje = sc.nextLine();
	    	datosEnvio = new ChatMessage(cliente.getId(),tipoMens,mensaje);
	    	if (mensaje.equalsIgnoreCase("logout")) {
	    		break;
	    	}
	    	cliente.sendMessage(datosEnvio); // Enviamos el mensaje
	    }
	    sc.close();
	}


}
