/** 
 * A simple program demonstrating client-side sockets.
 *
 * @author Greg Gagne.
 */

import java.net.*;

public class SimpleClient
{
	public static final int PORT = 6500;
	
	public static void main(String[] args) throws java.io.IOException {
		if (args.length != 1) {
			System.err.println("usage: java SimpleClient <host>");
			System.exit(0);
		}
		
		/**
		 * Open a socket connection on specified host at port 2500
		 * host may be either IP name or IP address.
		 */
		Socket sock = new Socket(args[0], PORT);
		
		/**
		 * At this point we could get an stream (input and/or output)
		 * to communicate with the other end of the socket.
		 */
		
		// close the socket
		sock.close();
	}
}
