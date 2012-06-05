package connection;

public interface EndPointConnectionHandler {

	public Connection getConnection();

	public void free(Connection connection);

	public void drop(final Connection connection);

}
