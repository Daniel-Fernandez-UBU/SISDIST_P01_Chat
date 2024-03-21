package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

/**
 * Client en el Chat.
 * 
 * @author Daniel Fernandez Barrientos
 * @version 1.0
 */
public interface ChatClient {

	public boolean start();
	public void sendMessage(ChatMessage msg);
	public void disconect();
	
}
