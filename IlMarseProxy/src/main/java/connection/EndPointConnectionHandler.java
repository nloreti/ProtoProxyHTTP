package connection;

/**
 * @author  Nloreti
 */
public interface EndPointConnectionHandler {

	/**
	 * @uml.property  name="connection"
	 * @uml.associationEnd  
	 */
	public Connection getConnection();

	public void free(Connection connection);

	public void drop(final Connection connection);

}
