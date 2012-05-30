package connection;

public interface ConnectionHandler {

	public Connection getConnection();

	public void free(Connection connection);

	public void drop(Connection connection);
}
