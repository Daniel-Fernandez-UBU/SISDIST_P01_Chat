package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
		setServer(server);
		setUsername(username);
		setPort(1500);
	}
	
	/**
	 * Constructor alternativo si no se indica servidor.
	 * 
	 * Se toma como servidor "localhost"
	 * 
	 * @param username - nombre de usuario
	 */
	public ChatClientImpl(String username) {
		setServer("localhost");
		setUsername(username);
		setPort(1500);
	}
	
	/** Inicio - Getters y setters. */
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	/** Fin - Getters y setters. */
	
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
		
		
		ChatClientImpl chatClient = null;
		
		// Si se ejecuta con servidor y nombre de usuario
        if (args.length == 2) {
        	chatClient = new ChatClientImpl(args[0], args[1]);
        
        // Si se ejecuta SOLO con nombre de usuario
        } else if (args.length == 1) {
        	chatClient = new ChatClientImpl(args[0]);
        
        // Si no tiene argumentos o tiene más de 2
        } else {
            System.err.println(
                    "Usage: java ChatClientImpl <server> <username>");
            System.err.println(
                    "Or Usage: java ChatClientImpl <username>");
                System.exit(1);
        }
        
        try (
                Socket echoSocket = new Socket(chatClient.getServer(), chatClient.getPort());
                PrintWriter out =
                    new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                    new BufferedReader(
                        new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                    new BufferedReader(
                        new InputStreamReader(System.in))
                
            ) {
        		// Arrancamos el hilo de escucha
    			chatClient.start();
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                	out.println(userInput);
                    System.out.println("echo: " + in.readLine());
                }
                
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + chatClient.getServer());
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + chatClient.getServer()); 
                System.exit(1);
            } 
		
	}
	
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void sendMessage(ChatMessage msg) {
		ChatMessage chatMsg = new ChatMessage(msg.getId(), msg.getType(), msg.getMessage());
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	private int getId() {
		int id = this.username.hashCode(); //Id para encapsular en el mensaje.
		return id;
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
