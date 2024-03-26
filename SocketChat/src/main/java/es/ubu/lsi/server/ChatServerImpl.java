package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;

public class ChatServerImpl implements ChatServer {
	
	private static final int DEFAULT_PORT = 1500;
	private int port;
	
	private List<ObjectOutputStream> listadoFlujosSalida;

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


	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		
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

