/**
 * This program is a very simple Web server.  When it receives a HTTP request
 * it sends the request back as the reply.  This can be of interest when
 * you want to see just what a Web client is requesting, or what data is
 * being sent when a form is submitted, for example.
 *
 * Usage:
 * 	java HttpMirror <port>
 *
 * The idea for this is motivated from "Java Examples in a NutShell".
 *
 */

import java.io.*;
import java.net.*;

public class HttpMirror {
	// Get the port to listen on
	static final int PORT = 1500;

	public static void main(String args[]) throws IOException {
		try {
			// Create a ServerSocket to listen on that port.
			ServerSocket ss = new ServerSocket(PORT);
			System.out.println("Server bound at port " + ss.getLocalPort());

			// Now enter an infinite loop, waiting for connections and handling them.
			for(;;) {
				// Wait for a client to connect.  The method will block, and when it
				// returns the socket will be already connected to the client
				Socket client = ss.accept();

				// Get input and output streams to talk to the client from the socket
				BufferedReader in =
				new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out =
				new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				// Start sending our reply, using the HTTP 1.0 protocol
				out.println("HTTP/1.1 200 ");              // Version & status code
				out.println("Content-Type: text/plain");   // The type of data we send
				out.println();                             // End of response headers
				out.flush();

				// Now, read the HTTP request from the client, and send it right
				// back to the client as part of the body of our response.
				// The client doesn't disconnect, so we never get an EOF.
				// It does sends an empty line at the end of the headers, though.
				// So when we see the empty line, we stop reading.  This means we
				// don't mirror the contents of POST requests, for example.
				String line;
				while((line = in.readLine()) != null) {
					if (line.length() == 0) break;
					out.println(line);
				}

				// Close the streams and socket, breaking the connection to the client
				out.close();
				in.close();
				client.close();
			} // Loop again, waiting for the next connection
		}
		// If anything goes wrong, print an error message
		catch (Exception e) {
			System.err.println(e);

		}
	}
}
