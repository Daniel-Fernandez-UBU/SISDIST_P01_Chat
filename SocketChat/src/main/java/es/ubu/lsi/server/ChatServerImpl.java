package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;


// TODO: Auto-generated Javadoc
/**
 * Clase Chat Server.
 * 
 * @author Daniel Fernandez Barrientos
 * @version 1.0
 */
public class ChatServerImpl implements ChatServer {
	
	/**  Atributos de la clase. */
	private static final int DEFAULT_PORT = 1500;
	
	/** The port. */
	private int port;
	
	/** The alive. */
	private boolean alive = true;
	
	/** The servidor sock. */
	ServerSocket servidorSock;
	
	/**  Mapas para guardar todas las listas. */
	private HashMap<Integer,ObjectOutputStream> listadoFlujosSalida = new HashMap<>();
	
	/** The listado flujos entrada. */
	private HashMap<Integer,ObjectInputStream> listadoFlujosEntrada = new HashMap<>();
	
	/** The listado sockets. */
	private HashMap<Integer,Socket> listadoSockets = new HashMap<>();
	
	/** The listado ids. */
	private HashMap<String,Integer> listadoIds = new HashMap<>();
	
	/** The listado usernames. */
	private HashMap<Integer,String> listadoUsernames = new HashMap<>();
	
	/** The clientes baneados. */
	private List<String> clientesBaneados = new ArrayList<>();
	
    /** Mapa con los threads de clientes. */
    Map<Integer, ServerThreadForClient> listadoThread = new HashMap<>();
	
    
	/**
	 * Instantiates a new chat server impl.
	 *
	 * @param port the port
	 */
	public ChatServerImpl(int port) {
		setPort(port);
	}
	
	/**
	 *  Get the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}



	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	
	
	/**
	 *  Get alive.
	 *
	 * @return true, if is alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Sets the alive.
	 *
	 * @param alive the new alive
	 */
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
		
		
        try {
        	// Iniciamos el server socket con el puerto del servidor
            this.servidorSock = new ServerSocket(getPort());
            System.out.println("Servidor levantado en el puerto: " + getPort());

            // Se mantiene a la escucha de forma infinita
            while (true) {
                Socket misocket = this.servidorSock.accept();
                
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

		// Cierra todos los clientes
		
		for (ServerThreadForClient actual : listadoThread.values()) {
			actual.closeClient();
		}
		
		for (Integer id : listadoIds.values()) {
			remove(id);
		}
		
		if (this.servidorSock.isBound()) {
			try {
				this.servidorSock.close();
			} catch (IOException e) {
				System.out.println("shutdown: IOException: " + e.getMessage());
			}
		}
	
	}

	/**
	 * Elimina un cliente de la lista de clientes que reciben el mensaje
	 * por un "BAN".
	 *
	 * @param id the id
	 */
	@Override
	public void remove(int id) {
			
		// Borramos el cliente de todas las listas
        listadoFlujosSalida.remove(id);
        listadoSockets.remove(id);
        listadoIds.remove(listadoUsernames.get(id));
        listadoUsernames.remove(id);
        listadoFlujosEntrada.remove(id);
		
	}
	
    /**
     * Método para enviar el mensaje a todos los clientes.
     *
     * @param mensaje the mensaje
     */
    public void broadcast(ChatMessage mensaje) {

        for (Map.Entry<Integer, ObjectOutputStream> entry : listadoFlujosSalida.entrySet()) {
            Integer id = entry.getKey();
            ObjectOutputStream flujo = entry.getValue();
            
            
            
            try {
                // Se manda solo a los clientes no baneados.
            	if (!clientesBaneados.contains(listadoUsernames.get(id))) {
                	flujo.writeObject(mensaje);
                    flujo.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }
    
    /**
     * Clientes baneados.
     *
     * @param username the username
     */
    private void banClient(String username) {
    	clientesBaneados.add(username);
    	
    }
    
    /**
     * Clientes no baneados.
     *
     * @param username the username
     */
    private void unBanClient(String username) {
    	clientesBaneados.remove(username);
    }
    

    /**
     * Clase que crea el hilo de escucha para cada cliente.
     * 
     * Recibe los mensajes y los reenvía a todos los clientes conectados.
     */
    private class ServerThreadForClient extends Thread {
    	
    	/**  Atributos de cada hilo. */
        private Socket socket;
        
        /** The flujo salida. */
        private ObjectOutputStream flujoSalida;
        
        /** The flujo entrada. */
        private ObjectInputStream flujoEntrada;
        
        /** The username. */
        private String username;
        
        /** The id. */
        private int id;
        
        private boolean clientAlive = true;
        
        /**
         * Constructor de la clase.
         *
         * @param clientSocket the client socket
         */
        public ServerThreadForClient(Socket clientSocket) {
            this.socket = clientSocket;
        }

        /**
         * Run.
         */
        public void run() {
        	
        	alive = true;
            try {
            	// Generamos flujo de entrada y salida para cada cliente
            	this.flujoSalida = new ObjectOutputStream(this.socket.getOutputStream());
            	this.flujoEntrada = new ObjectInputStream(this.socket.getInputStream());
            	
            	// Se mantiene activo mientras el servidor está activo
            	while (clientAlive) {
                	            		
            		ChatMessage recibido = (ChatMessage) flujoEntrada.readObject();
                	this.id = recibido.getId();
                	
                	String[] messageParts = recibido.getMessage().split(":");
                	this.username = messageParts[0];
                	
                	
                	// Se comprueba si no está baneado para reenviar su mensaje.
                	if (!clientesBaneados.contains(this.username)) {
                		
                		// Añadimos los clientes a los listados
	                	storeClient();
	                	
	                	System.out.println("Mensaje recibido en el servidor: " + recibido.getMessage());
	                	
	                	if (recibido.getType().equals(ChatMessage.MessageType.LOGOUT)) {
	                	    //ChatMessage confirmacionLogout = new ChatMessage(this.id,ChatMessage.MessageType.LOGOUT, "logout confirmado");
	                	    //flujoSalida.writeObject(confirmacionLogout);
	                	    //flujoSalida.flush();
	                	    closeClient();
	                	    clientAlive=false;
	                		remove(this.id);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.BAN)) {
	                		String userban = messageParts[1].split(" ")[1];
	                		banClient(userban);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.UNBAN)) {
	                		String userban = messageParts[1].split(" ")[1];
	                		unBanClient(userban);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.SHUTDOWN)) {
	                		shutdown();
	                		setAlive(false);
	                		break; //Terminamos
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.INICIO)) {
	                		recibido.setMessage("Nuevo cliente (" + username + ") conectado");
	                	} 
	                	
	                    // Imprimir el mensaje en el área de texto
	                    System.out.println("\n" + this.username + ": " + messageParts[1]);
	                    
	                    broadcast(recibido);
	
                	}
            	}

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
        }
        
       /**
        * Solo se guarda la primera vez.
        */
        private void storeClient() {
        	
        	if (listadoUsernames.containsKey(this.id)) {
        		
        	} else {
        		listadoThread.put(this.id,this);
                listadoFlujosSalida.put(this.id, this.flujoSalida);
                listadoSockets.put(this.id, this.socket);
                listadoIds.put(this.username, this.id);
                listadoUsernames.put(this.id, this.username);
        	}
        }
        
        /**
         * Cierra la conexion del cliente
         */
        public void closeClient() {
        	clientAlive = false;
            try {
                if (listadoFlujosEntrada.get(id) != null)
                	listadoFlujosEntrada.get(id).close();
                if (listadoFlujosSalida.get(id) != null)
                	listadoFlujosSalida.get(id).close();
                if (listadoSockets.get(id) != null)
                	listadoSockets.get(id).close();
            } catch (IOException e) {
                System.err.println("Error closing the connection to client " + e.getMessage());
            }
        }
    }



    /**
     * Instancia el servidor y lo arranca en el método startup().
     *
     * @param args the arguments
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

