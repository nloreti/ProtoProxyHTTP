package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class FilterHandler implements ConnectionHandler {

	/**
	 * @uml.property name="rf"
	 * @uml.associationEnd readOnly="true"
	 */
	RequestFilter rf;
	private DinamicProxyConfiguration configuration = DinamicProxyConfiguration
			.getInstance();

	private String user;
	private String pass;

	public void handle(final Socket s) throws IOException {
		// InputStream in = s.getInputStream();
		// OutputStream out = s.getOutputStream();
		final BufferedReader fromClient = new BufferedReader(
				new InputStreamReader(s.getInputStream()));
		final PrintWriter toClient = new PrintWriter(s.getOutputStream(), true);
		// byte[] receiveBuf = new byte[BUFSIZE]; // Receive buffer
		String response;
		this.user = this.configuration.getUsername();
		this.pass = this.configuration.getPassword();
		boolean auth = false;
		do {
			toClient.println("USER:");
			response = fromClient.readLine();
			if (response.contains(this.user)) {
				toClient.println("PASS:");
				response = fromClient.readLine();
				if (response.contains(this.pass)) {
					auth = true;
				}
			}
		} while (!response.equals("BYE!") && !auth);
		// Receive until client closes connection
		do {
			response = this.parse(fromClient.readLine());
			toClient.println(response);
		} while (!response.equals("BYE!"));
		s.close();
	}

	private String parse(final String request) {
		final RequestFilter rf = RequestFilter.getInstance();
		if (request.equals("HELP")) {
			return "Manual for usage:\64Type any command from the following list:\64\tBLOCK ACCESS blocks every access from the proxy.\64\tUNLOCK ACCESS grants access\64\tL33T ON turns l33t mode on\64\tL33T OFF turns l33t mode off\64\tBLOCK IP [ip] blocks the given [ip] or group of ip's\64\tUNLOCK IP [ip] unlocks the given [ip] or group of ip's\64\tBLOCK URI [uri] blocks the given [uri] or regular expression for uri\64\tUNLOCK URI [uri] unlocks the given uri or regular expression for uri's\64\tSET MAXSIZE [size] sets a max quantity of bytes that can pass throught the proxy, set on 0 for unlimited amount\64\tIMAGES ON turns on the flipping for images\64\tIMAGES OFF turns off the flipping for images\64\tBLOCK MEDIATYPE [media type] blocks the given [media type]\64\tUNLOCK MEDIATYPE [mediatype] unlocks the given [media type]\64End\64";
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
			if (!ip.matches("%d.%d.%d.%d")) {
				return "INVALID IP";
			}
			if (!rf.blockIP(ip)) {
				return ip + " IS ALREADY BLOCKED";
			}
			return ip + " BLOCKED";
		} else if (request.startsWith("UNLOCK IP ")) {
			final String ip = request.substring(10);
			if (!ip.matches("%d.%d.%d.%d")) {
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
			try {
				if (!rf.blockUri(new URI(uri))) {
					return uri + " IS ALREADY BLOCKED";
				}
			} catch (final URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri + " HAS BEEN BLOCKED";
		} else if (request.startsWith("UNLOCK URI ")) {
			final String uri = request.substring(11);
			// urivalidator!
			if (false) {
				return uri + "IS NOT A VALID URI";
			}
			try {
				if (!rf.unlockUri(new URI(uri))) {
					return uri + "NOT BLOCKED";
				}
			} catch (final URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		} else if (request.startsWith("GET BLOCKS")) { // A mi gusto tenemos que
														// hacer un solo
														// servidor que provea
														// todo, por eso lo puse
														// aca.
			return "TOTAL SITE BLOCKS:"
					+ Statistics.getInstance().getSiteBlocks()
					+ "\r\nTOTAL IP BLOCKS: "
					+ Statistics.getInstance().getIpBlocks() + "\r\nTOTAL"
					+ " MEDIATYPE BLOCKS: "
					+ Statistics.getInstance().getContentBlocks()
					+ "\r\nTOTAL SIZE BLOCKS: "
					+ Statistics.getInstance().getSizeBlocks();
		} else if (request.startsWith("GET OPEN CONNECTIONS")) {
			return "TOTAL OPEN CONNECTIONS:"
					+ Statistics.getInstance().getOpenConnections();
		} else if (request.startsWith("GET CLIENT BYTES TRANSMITED")) {
			return "TOTAL CLIENT BYTES TRANSMITED:"
					+ Statistics.getInstance().getProxyClientBytes();
		} else if (request.startsWith("GET SERVERS BYTES TRANSMITED")) {
			return "TOTAL SERVERS BYTES TRANSMITED:"
					+ Statistics.getInstance().getProxyServerBytes();
		} else if (request.startsWith("GET TRANSFORMATIONS")) {
			return "TOTAL TRANSFORMATIONS:"
					+ Statistics.getInstance().getTransformations();
		} else if (request.startsWith("GET TOTAL BYTES TRANSMITED")) {
			return "TOTAL BYTES TRANSMITED:"
					+ String.valueOf(Statistics.getInstance()
							.getProxyClientBytes()
							+ Statistics.getInstance().getProxyServerBytes());
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

