package filterManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class FilterManager {
	
	
	public static void main(String[] args) throws IOException {
		
		// args
		if (args.length != 2) // Test for correct # of
			throw new IllegalArgumentException(
					"Parameter(s): <Server> [<Port>]");

		// Server name or IP address
		String server = args[0];

		// Convert argument String to bytes using the default character encoding
		int servPort = Integer.parseInt(args[1]);

		// Create socket that is connected to server on specified port
		Socket socket = new Socket(InetAddress.getByName(server), servPort);
		System.out.println("Connected to server...");

		//InputStream in = socket.getInputStream();
		//OutputStream out = socket.getOutputStream();
		BufferedReader fromServer = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter	toServer = 
				new PrintWriter(socket.getOutputStream(), true);
		String curLine = ""; // Line read from standard in
		System.out.println("Enter a line of text (type 'quit' to exit): ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader termIn = new BufferedReader(converter);
		while (!curLine.equals("EXIT")) {
			curLine = termIn.readLine();
			toServer.println(curLine);
//			// Send the encoded string to the server
//			out.write(curLine.getBytes("UTF-16LE"));
//
//			// Receive the response from the server
//			byte[] data = new byte[50];
//			// Bytes received in last read
//			System.out.println("readin");
//			if (in.read(data) == -1)
//				throw new SocketException("Connection closed prematurely");
			System.out.print(fromServer.readLine().replace('\64', '\n'));
			System.out.println();
		}
		// Close the socket and its streams
		socket.close();
	}
}
