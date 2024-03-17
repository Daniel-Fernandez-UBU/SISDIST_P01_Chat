package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz ChatServer
 * 
 * Contiene la definicion básica de los métodos
 * 
 */

public interface ChatServer {

	// Inicio del servidor
	public void startup();
	
	// Apagado del servidor
	public void shutdown();
	
	// Mensaje a todos los clientes
	public void broadcas(ChatMessage message);
	
	// Eliminar un cliente
	public void remove (int id);
	
}
