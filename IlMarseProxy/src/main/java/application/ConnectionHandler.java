package application;

public interface ConnectionHandler {

	public ConnectionImpl getConnection();

	public void free(ConnectionImpl connection);

}
