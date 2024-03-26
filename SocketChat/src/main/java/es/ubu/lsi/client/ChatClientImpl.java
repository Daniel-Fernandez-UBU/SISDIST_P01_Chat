package es.ubu.lsi.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Clase chat client.
 * 
 * @author Daniel Fernandez Barrientos
 * @version 1.0
 */
public class ChatClientImpl implements ChatClient{

	/**  Atributos de la clase. */
	private int id, port;
	
	/** The username. */
	private String server, username;
	
	/** The carry on. */
	private boolean carryOn = true;
	
	/**  Atributos adicionales. */
	private Socket miSocket;
	
	/** The data. */
	private ObjectOutputStream data;
	
	/** Hilo para escucha. */
	private ChatClientListener listener;
	private Thread hilo;
	
	
	/**
	 * Constructor de la clase.
	 *
	 * @param server the server
	 * @param port the port
	 * @param username the username
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.setPort(port);
		this.setServer(server);
		this.setUsername(username);
		setId();//Generamos Id unico
	}

	/**
	 *  Inicio -  Getters y Setters *.
	 *
	 * @return the port
	 */
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
	 *
	 * @param port the new port
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
	 *
	 * @see #getId()
	 */
	public void setId() {
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
	 *
	 * @param server the new server
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
	 *
	 * @param username the new username
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
	 *
	 * @param carryOn the new carry on
	 * @see #iscarryOn
	 */
	public void setCarryOn(boolean carryOn) {
		this.carryOn = carryOn;
	}
	
	/**
	 *  Fin - Getters y Setters *.
	 *
	 * @return true, if successful
	 */
	
	/** Sobreescribimos los métodos de la interfaz con su implementación. */
	
	/**
	 * Inicia el cliente.
	 * @return true or false
	 */
	@Override
	public boolean start() {
		listener = new ChatClientListener();
		hilo = new Thread(this.listener);
		hilo.start();

		return isCarryOn();
	}

	/**
	 * Send Message.
	 *
	 * @param msg the msg
	 */
	@Override
	public synchronized void sendMessage(ChatMessage msg) {
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
		
		listener.stop();
		
		try {
			miSocket.close();
			setCarryOn(false);
			hilo.join();
			
		} catch (IOException e) {
			System.out.println("disconect: IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @see ChatClientEvent
	 */
	private class ChatClientListener implements Runnable {
		
		private volatile boolean cerrar = false;
		
        /**
         * Levanta la conexión en modo escucha para recibir los mensajes del servidor.
         * 
         * Si falla al arrancar el modo escucha, establece en "false" cliente.carryOn.
         */
        @Override
        public void run() {
        	ObjectInputStream flujoEntrada = null;
        	try {
        		flujoEntrada = new ObjectInputStream(miSocket.getInputStream());
        		while(!cerrar) {
        			ChatMessage dataRecibida = (ChatMessage) flujoEntrada.readObject();
        			if (dataRecibida.getId() == getId()) {
        				System.out.println("Yo: " + dataRecibida.getMessage());
        			} else {
        				System.out.println(dataRecibida.getId() + ": " + dataRecibida.getMessage()); //Mostramos el mensaje
        			}
        		}
        		
        	//Controlamos los mensajes de las excepciones
        	} catch (EOFException e) {
        		System.out.println("ChatClientListener: EOFException: " + e.getMessage());
        	} catch (IOException i) {
        		System.out.println("ChatClientListener: IOException: " + i.getMessage());
        	} catch (ClassNotFoundException c) {
        		System.out.println("ChatClientListener: ClassNotFoundException: " + c.getMessage());
        	} finally {
        		cerrarFlujoEntrada(flujoEntrada);
        	}

        }
        
        /**
         * Cerrar.
         */
        private void stop() {
        	this.cerrar=true;
        }
        
        private void cerrarFlujoEntrada(ObjectInputStream flujoEntrada) {
            if (flujoEntrada != null) {
                try {
                    flujoEntrada.close();
                } catch (IOException e) {
                    // Manejar la excepción de cierre de flujo de entrada
                }
            }
        }
        
    }
		
	/**
	 * Método main.
	 * 
	 * Encargado de lanzar los clientes.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
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
    	MessageType tipoIni = MessageType.INICIO;
	    

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
	    
	    datosEnvio = new ChatMessage(cliente.getId(),tipoIni,username+": inicio chat");
	    cliente.sendMessage(datosEnvio);
	    
	    while(cliente.isCarryOn()) {
	    	mensaje = sc.nextLine();
	    	
	    	// Incorporamos el nombre de usuario en el mensaje final
	    	String mensajeFinal = cliente.getUsername() + ":" + mensaje;
	    	    	
	    	if (mensaje.split(" ")[0].equalsIgnoreCase("ban")) {
	    		datosEnvio = new ChatMessage(cliente.getId(),tipoMens=MessageType.BAN,mensajeFinal);
	    	} else if (mensaje.split(" ")[0].equalsIgnoreCase("unban")) {
	    		datosEnvio = new ChatMessage(cliente.getId(),MessageType.UNBAN,mensajeFinal);
	    	} else if (mensaje.equalsIgnoreCase("logout")) {
	    		datosEnvio = new ChatMessage(cliente.getId(),MessageType.LOGOUT,mensajeFinal);
	    	} else if (mensaje.equalsIgnoreCase("shutdown")) {
	    		datosEnvio = new ChatMessage(cliente.getId(),MessageType.SHUTDOWN,mensajeFinal);
	    	} else {
	    		datosEnvio = new ChatMessage(cliente.getId(),tipoMens,mensajeFinal);
	    	} 
	    	
	    	if (cliente.isCarryOn()) {
		    	cliente.sendMessage(datosEnvio); // Enviamos el mensaje
	    	}
	    	
	    	if (mensaje.equalsIgnoreCase("logout") || mensaje.equalsIgnoreCase("shutdown")) {
	    		// Se cambia a no activo
	    		cliente.disconect();
	    	}
	    	
	    }
	    sc.close();

	}


}
