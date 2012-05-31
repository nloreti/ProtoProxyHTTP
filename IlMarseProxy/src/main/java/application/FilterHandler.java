package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FilterHandler implements ConnectionHandler {


	RequestFilter rf;
	@Override
	public void handle(Socket s) throws IOException {
		// InputStream in = s.getInputStream();
		// OutputStream out = s.getOutputStream();
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		PrintWriter toClient = new PrintWriter(s.getOutputStream(), true);
		//byte[] receiveBuf = new byte[BUFSIZE]; // Receive buffer
		String response;
		// Receive until client closes connection
		do {
			response = parse(fromClient.readLine());
			toClient.println(response);
//			int recvMsgSize = 0;
//			int totalSize = 0;
//			while ((recvMsgSize = in.read(receiveBuf, totalSize, BUFSIZE
//					- totalSize)) != -1) {
//				totalSize += recvMsgSize;
//			}
//			response = parse(new String(receiveBuf));
//			System.out.println("asd");
//			out.write(response.getBytes("UTF-16LE"), 0,
//					response.getBytes("UTF-16LE").length);
//
		} while (!response.equals("BYE!"));
		s.close(); // Close the socket. We are done with this client!
	}

	private String parse(String request) {
		RequestFilter rf = RequestFilter.getInstance();
		if( request.equals("IMAGES OFF")){
			if( !rf.images() )
				return "IMAGES ARE ALREADY OFF";
			rf.setImages(false);
			return "IMAGES WILL STOP ROTATING";
		}else if( request.equals("IMAGES ON")){
			if( rf.images() )
				return "IMAGES ARE ALREADY ON";
			rf.setImages(true);
			return "IMAGES WILL NOW ROTATE";
		}else if( request.equals("EXIT")){
			return "BYE!";
		}else{
			return "INVALID COMMAND, TYPE HELP FOR A LIST OF COMMANDS";
		}
	}
}
