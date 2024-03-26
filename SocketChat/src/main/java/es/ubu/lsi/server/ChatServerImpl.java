package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer {
	
	private static final int DEFAULT_PORT = 1500;
	private int port;
	
	/** Mapas para guardar todas las listas */
	private Map<Integer,ObjectOutputStream> listadoFlujosSalida;
	private Map<Integer,ObjectInputStream> listadoFlujosEntrada;
	private Map<Integer,Socket> listadoSockets;
	private Map<Integer,String> listadoClientes;

    private List<Object> clientSockets = new ArrayList<>();
    private List<Object> writers = new ArrayList<>();
    
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


	/**
	 * Bucle con el servidor de sockets, esperando y aceptando peticiones.
	 * Cada peticion genera un nuevo ServerThreadForClient y se arranca el hilo
	 * correspondiente para que cada cliente tenga el suyo independiente asociado con el servidor
	 * Socket, flujo de entrada y flujo de salida. Guardar registro de hilos para el push
	 */
	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Método para enviar el mensaje a todos los clientes.
     * @param args
     */
    public void broadcast(ChatMessage mensaje) {
        for (ObjectOutputStream flujo : listadoFlujosSalida) {
            try {
				flujo.writeObject(mensaje);
				flujo.flush();
			} catch (IOException e) {
				System.out.println("broadcast: IOException: " + e.getMessage());
			} 
         }
    }

    private class ServerThreadForClient extends Thread {
        private Socket socket;
        private PrintWriter writer;

        public ServerThreadForClient(Socket clientSocket) {
            this.socket = clientSocket;
            try {
                this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientSockets.add(clientSocket);
            writers.add(writer);
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(socket.getPort() + ": " + inputLine);
                    broadcast(socket.getPort() + ": " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientSockets.remove(socket);
                writers.remove(writer);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        Thread clientListenerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nuevo Cliente: " + clientSocket.getInetAddress() + "/" + clientSocket.getPort());
                    Thread clientThread = chatServer.new ChatServerThreadForClient(clientSocket);
                    clientThread.start();
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port: " + port + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        });
        clientListenerThread.start();
    }


}

