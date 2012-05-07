package connection;

public interface ConnectionHandler {

	public Connection getConnection();

	public void free(ConnectionImpl connection);

}
