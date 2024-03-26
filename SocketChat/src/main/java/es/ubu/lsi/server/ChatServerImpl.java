package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer {
	
	private static final int DEFAULT_PORT = 1500;
	private int port;
	private boolean alive = true;
	
	/** Mapas para guardar todas las listas */
	private HashMap<Integer,ObjectOutputStream> listadoFlujosSalida = new HashMap<>();
	private HashMap<Integer,ObjectInputStream> listadoFlujosEntrada = new HashMap<>();
	private HashMap<Integer,Socket> listadoSockets = new HashMap<>();
	private HashMap<String,Integer> listadoIds = new HashMap<>();
	private HashMap<Integer,String> listadoUsernames = new HashMap<>();
	private HashMap<Integer,HashMap<ObjectOutputStream,ObjectInputStream>> clientesBaneados = new HashMap<>();
	
    
	public ChatServerImpl(int port) {
		setPort(port);
	}
	
	/** Inicio - Getters y Setters */
	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}
	
	
	
	/** Fin - Getters y Setters */

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * Bucle con el servidor de sockets, esperando y aceptando peticiones.
	 * Cada peticion genera un nuevo ServerThreadForClient y se arranca el hilo
	 * correspondiente para que cada cliente tenga el suyo independiente asociado con el servidor
	 * Socket, flujo de entrada y flujo de salida. Guardar registro de hilos para el push
	 */
	@Override
	public void startup() {
		
		
		
		ServerSocket servidorSock;
        try {
        	// Iniciamos el server socket con el puerto del servidor
            servidorSock = new ServerSocket(getPort());
            System.out.println("Servidor levantado en el puerto: " + getPort());

            // Se mantiene a la escucha de forma infinita
            while (true) {
                Socket misocket = servidorSock.accept();
                
                ServerThreadForClient clientSocket = new ServerThreadForClient(misocket);
                clientSocket.start();

            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        
		
	}

	/**
	 * Cierra todos los flujos de entrada/salida y el socket de cada cliente.
	 */
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Elimina un cliente de la lista de clientes que reciben el mensaje
	 * por un "BAN"
	 */
	@Override
	public void remove(int id) {
		
		// Cerramos todas las conexiones abiertas
		try {
			listadoFlujosSalida.get(id).close();
			listadoSockets.get(id).close();
			listadoFlujosEntrada.get(id).close();
		} catch (IOException e) {
			System.out.println("remove: IOException: " + e.getMessage());
		}
		
		// Borramos el cliente de todas las listas
        listadoFlujosSalida.remove(id);
        listadoSockets.remove(id);
        listadoClientes.remove(id);
        listadoFlujosEntrada.remove(id);
		
	}
	
    /**
     * Método para enviar el mensaje a todos los clientes.
     * @param args
     */
    public void broadcast(ChatMessage mensaje) {
        for (ObjectOutputStream flujo : listadoFlujosSalida.values()) {
            try {
				flujo.writeObject(mensaje);
				flujo.flush();
			} catch (IOException e) {
				System.out.println("broadcast: IOException: " + e.getMessage());
			} 
         }
    }
    
    private void banClient(String username) {
    	int idBaneado = listadoClientes.

    }

    /**
     * Clase que crea el hilo de escucha para cada cliente.
     * 
     * Recibe los mensajes y los reenvía a todos los clientes conectados.
     */
    private class ServerThreadForClient extends Thread {
    	
    	/** Atributos de cada hilo */
        private Socket socket;
        private ObjectOutputStream flujoSalida;
        private ObjectInputStream flujoEntrada;
        private String username;
        private int id;
        
        /**
         * Constructor de la clase.
         * @param clientSocket
         */
        public ServerThreadForClient(Socket clientSocket) {
            this.socket = clientSocket;
        }

        public void run() {

            try {
            	// Generamos flujo de entrada y salida para cada cliente
            	this.flujoSalida = new ObjectOutputStream(this.socket.getOutputStream());
            	this.flujoEntrada = new ObjectInputStream(this.socket.getInputStream());
            	
            	// Se mantiene activo mientras el servidor está activo
            	while (alive) {
                	ChatMessage recibido = (ChatMessage) flujoEntrada.readObject();
                	this.id = recibido.getId();
                	
                	String[] messageParts = recibido.getMessage().split(":");
                	this.username = messageParts[0];
                	System.out.println("Mensaje recibido en el servidor: " + recibido.getMessage());
                	
                	if (recibido.getType().equals(ChatMessage.MessageType.LOGOUT)) {
                		remove(this.id);
                	}
                	
                    listadoFlujosSalida.put(this.id, this.flujoSalida);
                    listadoSockets.put(this.id, this.socket);
                    listadoClientes.put(this.username, this.id);
                	
                    // Imprimir el mensaje en el área de texto
                    System.out.println("\n" + this.username + ": " + messageParts[1]);

                    // Reenviar el mensaje a todos los clientes
                    for (ObjectOutputStream flujo : listadoFlujosSalida.values()) {
                        flujo.writeObject(recibido);
                        flujo.flush();
                    }
            	}

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
        }
    }



    /**
     * Instancia el servidor y lo arranca en el método startup()
     * @param args
     */
    public static void main(String[] args) {
    	
        if (args.length != 0) {
            System.err.println("Usage: java ChatServerImpl");
            System.exit(1);
        }

        ChatServerImpl chatServer = new ChatServerImpl(DEFAULT_PORT);
        
        chatServer.startup();

    }


}

