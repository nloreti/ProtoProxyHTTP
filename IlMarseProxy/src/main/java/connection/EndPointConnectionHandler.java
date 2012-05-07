package connection;

public interface EndPointConnectionHandler {

	public Connection getConnection();

	public void free(ConnectionImpl connection);

}
