/**
 * An proxy server listening on port 8080.
 *
 * This services each request in a separate thread.
 *
 * @author - Greg Gagne.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class webserver extends Thread
{
	public static final int DEFAULT_PORT = 8080;

	private static final Executor exec = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: java webserver [configuration file]");
			System.exit(0);

		}


		ServerSocket sock = null;
    System.out.println("Proudly serving at port 8080");

  	try {
			sock = new ServerSocket(DEFAULT_PORT);
		//	config1 = new Configuration(args[0]);
			//String lolo =  config1.getDefaultDocument();
			Configuration configFile = new Configuration(args[0]);


			while (true) {
				Runnable task = new Connection(sock.accept(), configFile);
				exec.execute(task);
			}
		}
		catch (ConfigurationException ce) {
			System.out.println(ce);

		}
	}
}
