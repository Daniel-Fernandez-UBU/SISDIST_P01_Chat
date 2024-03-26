package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer {
	
	private static final int DEFAULT_PORT = 1500;
	private int port;
	private boolean alive = true;
	ServerSocket servidorSock;
	
	/** Mapas para guardar todas las listas */
	private HashMap<Integer,ObjectOutputStream> listadoFlujosSalida = new HashMap<>();
	private HashMap<Integer,ObjectInputStream> listadoFlujosEntrada = new HashMap<>();
	private HashMap<Integer,Socket> listadoSockets = new HashMap<>();
	private HashMap<String,Integer> listadoIds = new HashMap<>();
	private HashMap<Integer,String> listadoUsernames = new HashMap<>();
	private List<String> clientesBaneados = new ArrayList<>();
	
    
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
	 * por un "BAN"
	 */
	@Override
	public void remove(int id) {
		
		// Cerramos todas las conexiones abiertas
		try {
			if (!listadoFlujosSalida.isEmpty()) {
				listadoFlujosSalida.get(id).close();
			}
			if (!listadoFlujosEntrada.isEmpty()) {
				listadoFlujosEntrada.get(id).close();
			}
			if (!listadoSockets.isEmpty()) {
				listadoSockets.get(id).close();
			}
		} catch (IOException e) {
			System.out.println("remove: IOException: " + e.getMessage());
		}
		
		// Borramos el cliente de todas las listas
        listadoFlujosSalida.remove(id);
        listadoSockets.remove(id);
        listadoIds.remove(listadoUsernames.get(id));
        listadoUsernames.remove(id);
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
    
    /**
     * Clientes baneados.
     * @param username
     */
    private void banClient(String username) {
    	clientesBaneados.add(username);
    	
    }
    
    /**
     * Clientes no baneados.
     * @param username
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
                	
                	// Se comprueba si no está baneado para reenviar su mensaje.
                	if (!clientesBaneados.contains(this.username)) {
                		// Añadimos los clientes a los listados
	                	storeClient();
	                	
	                	System.out.println("Mensaje recibido en el servidor: " + recibido.getMessage());
	                	
	                	if (recibido.getType().equals(ChatMessage.MessageType.LOGOUT)) {
	                		remove(this.id);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.BAN)) {
	                		banClient(this.username);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.UNBAN)) {
	                		unBanClient(this.username);
	                	} else if (recibido.getType().equals(ChatMessage.MessageType.SHUTDOWN)) {
	                		shutdown();
	                	}
	                	
	                    // Imprimir el mensaje en el área de texto
	                    System.out.println("\n" + this.username + ": " + messageParts[1]);
	
	                    // Reenviar el mensaje a todos los clientes
	                    for (Map.Entry<Integer, ObjectOutputStream> entry : listadoFlujosSalida.entrySet()) {
	                        Integer id = entry.getKey();
	                        ObjectOutputStream flujo = entry.getValue();
	                        
	                        try {
	                            // Se manda solo a los clientes no baneados.
	                        	if (!clientesBaneados.contains(listadoUsernames.get(id))) {
	                            	flujo.writeObject(recibido);
	                                flujo.flush();
	                            }
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                    }
                	}
            	}

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
        }
        
       /**
        * Solo se guarda la primera vez
        */
        private void storeClient() {
        	
        	if (listadoUsernames.containsKey(this.id)) {
        		
        	} else {
 
                listadoFlujosSalida.put(this.id, this.flujoSalida);
                listadoSockets.put(this.id, this.socket);
                listadoIds.put(this.username, this.id);
                listadoUsernames.put(this.id, this.username);
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

