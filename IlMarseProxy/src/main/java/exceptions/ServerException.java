package exceptions;

public class ServerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerException() {
		super();
	}

	public ServerException(final String error) {
		super(error);
	}

}
