
package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz Chat Server.
 * 
 * @author Daniel Fernandez Barrientos
 * @version 1.0
 */
public interface ChatServer {

	/**
	 * Startup.
	 */
	public void startup();
	
	/**
	 * Shutdown.
	 */
	public void shutdown();
	
	/**
	 * Broadcast.
	 *
	 * @param msg the msg
	 */
	public void broadcast(ChatMessage msg);
	
	/**
	 * Removes the.
	 *
	 * @param id the id
	 */
	public void remove(int id);
	
}
