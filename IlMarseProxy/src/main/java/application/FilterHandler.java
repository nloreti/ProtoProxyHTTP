package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FilterHandler implements ConnectionHandler {

	RequestFilter rf;

	public void handle(final Socket s) throws IOException {
		// InputStream in = s.getInputStream();
		// OutputStream out = s.getOutputStream();
		final BufferedReader fromClient = new BufferedReader(
				new InputStreamReader(s.getInputStream()));
		final PrintWriter toClient = new PrintWriter(s.getOutputStream(), true);
		// byte[] receiveBuf = new byte[BUFSIZE]; // Receive buffer
		String response;
		// Receive until client closes connection
		do {
			response = this.parse(fromClient.readLine());
			toClient.println(response);
			// int recvMsgSize = 0;
			// int totalSize = 0;
			// while ((recvMsgSize = in.read(receiveBuf, totalSize, BUFSIZE
			// - totalSize)) != -1) {
			// totalSize += recvMsgSize;
			// }
			// response = parse(new String(receiveBuf));
			// System.out.println("asd");
			// out.write(response.getBytes("UTF-16LE"), 0,
			// response.getBytes("UTF-16LE").length);
			//
		} while (!response.equals("BYE!"));
		s.close(); // Close the socket. We are done with this client!
	}

	private String parse(final String request) {
		final RequestFilter rf = RequestFilter.getInstance();
		if (request.equals("HELP")) {
			return "Manual for usage:\nType any command from the following list:\n\tBLOCK ACCESS blocks every access from the proxy.\n\tUNLOCK ACCESS grants access\n\tL33T ON turns l33t mode on\n\tL33T OFF turns l33t mode off\n\tBLOCK IP [ip] blocks the given [ip] or group of ip's\n\tUNLOCK IP [ip] unlocks the given [ip] or group of ip's\n\tBLOCK URI [uri] blocks the given [uri] or regular expression for uri\n\tUNLOCK URI [uri] unlocks the given uri or regular expression for uri's\n\tMAXSIZE [size] sets a max quantity of bytes that can pass throught the proxy, set on 0 for unlimited amount\n\tIMAGES ON turns on the flipping for images\n\tIMAGES OFF turns off the flipping for images\n\tBLOCK MEDIATYPE [media type] blocks the given [media type]\n\tUNLOCK MEDIATYPE [mediatype] unlocks the given [media type]\nEnd\n";
		} else if (request.equals("BLOCK ACCESS")) {
			if (!rf.access()) {
				return "ACCESS IS ALREADY BLOCKED";
			}
			rf.accessOff();
			return "ACCESS BLOCKED";
		} else if (request.equals("UNLOCK ACCESS")) {
			if (rf.access()) {
				return "ACCESS IS ALREADY UNLOCKED";
			}
			rf.accessOn();
			return "ACCESS UNLOCKED";
		} else if (request.equals("L33T ON")) {
			if (rf.leet()) {
				return "L33T IS ALREADY ON";
			}
			rf.leetOn();
			return "L33T IS NOW ON";
		} else if (request.equals("L33T OFF")) {
			if (!rf.leet()) {
				return "L33T IS ALREADY OFF";
			}
			rf.leetOff();
			return "L33T IS NOW OFF";
		} else if (request.equals("IMAGES OFF")) {
			if (!rf.images()) {
				return "IMAGES ARE ALREADY OFF";
			}
			rf.imagesOff();
			return "IMAGES WILL STOP ROTATING";
		} else if (request.equals("IMAGES ON")) {
			if (rf.images()) {
				return "IMAGES ARE ALREADY ON";
			}
			rf.imagesOn();
			return "IMAGES WILL NOW ROTATE";
		} else if (request.startsWith("BLOCK IP ")) {
			final String ip = request.substring(9);
			if (!ip.matches("%d.%d.%d")) {
				return "INVALID IP";
			}
			if (!rf.blockIP(ip)) {
				return ip + " IS ALREADY BLOCKED";
			}
			return ip + " BLOCKED";
		} else if (request.startsWith("UNLOCK IP ")) {
			final String ip = request.substring(10);
			if (!ip.matches("%d.%d.%d")) {
				return "INVALID IP";
			}
			if (rf.unlockIP(ip)) {
				return ip + " NOT BLOCKED";
			}
			return ip + " UNLOCKED";
		} else if (request.startsWith("BLOCK URI ")) {
			final String uri = request.substring(10);
			// urivalidator!
			if (false) {
				return uri + " IS NOT A VALID URI";
			}
			if (!rf.blockUri(uri)) {
				return uri + " IS ALREADY BLOCKED";
			}
			return uri + " HAS BEEN BLOCKED";
		} else if (request.startsWith("UNLOCK URI ")) {
			final String uri = request.substring(11);
			// urivalidator!
			if (false) {
				return uri + "IS NOT A VALID URI";
			}
			if (!rf.unlockUri(uri)) {
				return uri + "NOT BLOCKED";
			}
			return uri + "UNLOCKED";
		} else if (request.startsWith("BLOCK MEDIATYPE ")) {
			final String mediaType = request.substring(16);
			if (!rf.blockMediaType(mediaType)) {
				return mediaType + " IS ALREADY BLOCKED";
			}
			return mediaType + " BLOCKED";
		} else if (request.startsWith("UNLOCK MEDIATYPE ")) {
			final String mediaType = request.substring(17);
			if (!rf.unlockMediaType(mediaType)) {
				return mediaType + " NOT BLOCKED";
			}
			return mediaType + " UNLOCKED";
		} else if (request.startsWith("SET MAXSIZE ")) {
			final String maxSize = request.substring(12);
			int ms;
			try {
				ms = Integer.valueOf(maxSize);
			} catch (final Exception e) {
				return "NOT A VALID INTEGER";
			}
			if (rf.setMaxSize(ms)) {
				return "MAXSIZE SET TO " + maxSize;
			}
			return "MAXSIZE IS NOW OFF";
		} else if (request.equals("EXIT")) {
			return "BYE!";
		} else {
			return "INVALID COMMAND, TYPE HELP FOR A LIST OF COMMANDS";
		}
	}
}
// o bloquear accesos a recursos muy grandes: No permitir descargas
// mayores a cierta cantidad de bytes. (Tener en cuenta el header
// Content-Lenght, y en su ausencia simplemente los bytes
// transferidos)

