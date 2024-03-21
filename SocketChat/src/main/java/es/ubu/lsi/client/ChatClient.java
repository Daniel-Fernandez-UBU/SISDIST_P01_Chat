package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

/**
 * Client en el Chat.
 * 
 * @author Daniel Fernandez Barrientos
 * @version 1.0
 */
public interface ChatClient {

	/**
	 * Inicia el cliente.
	 * @return true or false
	 */
	public boolean start();
	
	/**
	 * Send Message.
	 * @param msg
	 */
	public void sendMessage(ChatMessage msg);
	
	/**
	 * Desconecta el cliente.
	 */
	public void disconect();
	
}
