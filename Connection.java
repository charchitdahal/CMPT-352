/**
 * This is the separate thread that services each
 * incoming s request. The actual logic
 * is implemented in the process() method of
 * the Handler class.
 *
 * @author Greg Gagne
 */

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.logging.*;

public class Connection implements Runnable
{
	public static final int BUFFER_SIZE = 1024;
	private Socket	s;
	byte[] buffer = new byte[BUFFER_SIZE];
	Socket servSocket;
	BufferedReader froms = null;
	BufferedOutputStream tos = null;
	String resource = null;
	String line = null;
	String result;
	String file;
	String path;
	String HTTPStatus;
	String HTTPStatusCode;
	int format = 0;
	Configuration config;

 Date date = new Date();

	public Connection(Socket s, Configuration config) {
		this.s = s;
		this.config = config;
	}

	public void run() {
		try {
			DataOutputStream tos = new DataOutputStream(s.getOutputStream());
			try {
				froms = new BufferedReader(new InputStreamReader(s.getInputStream()));
				line = froms.readLine();


				getResource();
				String file = path;

				// Checks to see if the file is valid.
				if (!(new File(file).isFile())) {
					HTTPStatus = getHTTPStatus(file);
					HTTPStatusCode = getHTTPStatusCode(file);
					file = config.getFourOhFourDocument();
				} else {
					HTTPStatus = getHTTPStatus(file);
					HTTPStatusCode = getHTTPStatusCode(file);
				}

				FileInputStream fileInput = new FileInputStream(file);

				result = HTTPStatus + getDate() +  getServer() + endsWith() + getContentLength(fileInput) + getConnectionClose();
				System.out.println(result);
				System.out.println("===================================================================");
				logFile(fileInput, file);


				tos.writeBytes(result);

				DataOutputStream outTos = tos;
				int bytesRead = 0;
				while ((bytesRead = fileInput.read(buffer)) > 0) {
					outTos.write(buffer, 0, bytesRead);
				}
			} finally {
				tos.close();
			}
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}


	// Method to get the resource the user enters. If it is left empty, it defauls to the index.html
	private void getResource() {
		format = line.indexOf('/');
		resource = line.substring(format + 1, line.indexOf(" HTTP/1.1"));

		if (resource.length() > 1) {
			path = config.getDocumentRoot() + resource;
		} else {
			path = config.getDefaultDocument();
		}
	}


	// A method to get the Status of the HTTP, with the "HTTP/1.1"
	private String getHTTPStatus(String fileName) {
		String httpStatus;
		String file = fileName;
		if (line.startsWith("GET")) {
			if(new File(file).isFile()) {
				httpStatus = "HTTP/1.1 200 OK\r\n";
			}
			else {
				httpStatus = "HTTP/1.1 404 Not Found\r\n";
			}
		} else {
			httpStatus = "HTTP/1.1 404 Not Found\r\n";
		}
		return httpStatus;
	}


	// Method to get the status of the HTTP, but only the status code for the logging.
	private String getHTTPStatusCode(String fileName) {
		String httpStatus;
		String file = fileName;
		if (line.startsWith("GET")) {
			if(new File(file).isFile()) {
				httpStatus = "200 ";
			}
			else {
				httpStatus = "404 ";
			}
		} else {
			httpStatus = "404 ";
		}
		return httpStatus;
	}


	// Method to get the date.
	private String getDate() {
		String todayDate = "Date: " + date + "\r\n";
		return todayDate;
	}


	// Method to get the server name.
	private String getServer() {
		String servName = "Server: " + config.getServerName() + "\r\n";
		return servName;
	}


	//Method to get the content type
	private String endsWith() {
		String contentType = resource;
		if (resource.endsWith(".html")) {
			contentType = "Content-Type: text/html\r\n";
		}
		else if (resource.endsWith(".gif")) {
			contentType = "Content-Type: image/gif\r\n";
		}
		else if (resource.endsWith(".jpg") || resource.endsWith(".jpeg")) {
			contentType = "Content-Type: image/jpeg\r\n";
		}
		else if (resource.endsWith(".png")) {
			contentType = "Content-Type: image/png\r\n";
		}
		else if (resource.endsWith(".txt")) {
			contentType = "Content-Type: text/plain\r\n";
		}
		else if (resource.endsWith(".css")) {
			contentType = "Content-Type: text/css\r\n";
		}
		return contentType;
	}


	private String getContentLength(FileInputStream file) throws IOException {
		String contentLength = "Content-length: " + file.available() + "\r\n";
		return contentLength;
	}


	private String getConnectionClose() {
		String connectionClose = "Connection: close\r\n\r\n";
		return connectionClose;
	}


	private void logFile(FileInputStream fileInput, String fileName) throws IOException {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			// Gets logging information
			String log = s.getInetAddress()+ " [" + date + "] " + "\"" + line + "\" " + HTTPStatusCode + fileInput.available() + "\r\n";
			File file = new File(config.getLogFile());

		//check to see if the file exists
			if (!file.exists()) {
				file.createNewFile();
			}

			// Appends to the log file, making sure to keep previous entries.
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			bw.write(log);


		}
		finally {
			if (bw != null) {
				bw.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}


	// Method to decode the URL.
	private String decodeFile(String fileName) throws UnsupportedEncodingException {
		String result = URLDecoder.decode(fileName, "UTF-8");
		return result;
	}
}
