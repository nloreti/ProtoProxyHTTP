package exceptions;

public class ServerTimeOutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerTimeOutException() {
		super();
	}

	public ServerTimeOutException(final String error) {
		super(error);
	}

}
