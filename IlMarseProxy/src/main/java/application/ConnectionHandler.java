package application;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionHandler {
    void handle(Socket s) throws IOException;
}
