package exceptions;

public class ClientException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientException() {
		super();
	}

	public ClientException(final String error) {
		super(error);
	}

}
