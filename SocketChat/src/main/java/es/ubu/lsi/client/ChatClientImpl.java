package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
		return isCarryOn();
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
	class ChatClientListener implements Runnable {

        private BufferedReader in;

        public ChatClientListener(BufferedReader in) {
            this.in = in;
        }

        /**
         * Levanta la conexión en modo escucha para recibir los mensajes del servidor.
         * 
         * Si falla al arrancar el modo escucha, establece en "false" cliente.carryOn.
         */
        @Override
        public void run() {
            try {
                String serverInput;
                while ((serverInput = in.readLine()) != null) {
                    System.out.println("Server: " + serverInput);
                }
            } catch (IOException e) {
                e.printStackTrace();
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

	    try (
	            Socket echoSocket = new Socket(cliente.getServer(), cliente.getPort());
	            PrintWriter out =
	                    new PrintWriter(echoSocket.getOutputStream(), true);
	            BufferedReader in =
	                    new BufferedReader(
	                            new InputStreamReader(echoSocket.getInputStream()));
	            BufferedReader stdIn =
	                    new BufferedReader(
	                            new InputStreamReader(System.in))
	    ) {

	        // Iniciar el ChatClientListener para recibir mensajes del servidor
	        ChatClientListener clientListener = cliente.new ChatClientListener(in);
	        Thread listenerThread = new Thread(clientListener);
	        listenerThread.start();

	        // Leer entrada del usuario y enviar al servidor
	        String userInput;
	        while ((userInput = stdIn.readLine()) != null) {
	            out.println(userInput);
	        }
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host " + cliente.getServer());
	        System.exit(1);
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to " + cliente.getServer());
	        System.exit(1);
	    }

	    if (!cliente.start()) {
	        throw new IOException("El cliente no está preparado para recibir mensajes");
	    }

	}


}
