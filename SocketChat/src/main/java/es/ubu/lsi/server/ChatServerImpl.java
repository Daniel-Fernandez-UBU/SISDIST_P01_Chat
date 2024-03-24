package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerImpl {

    private List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());
    private List<PrintWriter> writers = Collections.synchronizedList(new ArrayList<>());

    class ChatServerThreadForClient extends Thread {
        private Socket socket;
        private PrintWriter writer;

        public ChatServerThreadForClient(Socket clientSocket) {
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
                    sendMessageToAllClients(inputLine);
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

    public synchronized void sendMessageToAllClients(String message) {
        for (PrintWriter writer : writers) {
            writer.println(message);
        }
    }

    public static void main(String[] args) {
        int port = 1500;

        if (args.length != 0) {
            System.err.println("Usage: java ChatServerImpl");
            System.exit(1);
        }

        ChatServerImpl chatServer = new ChatServerImpl();

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

