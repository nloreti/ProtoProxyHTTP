package exceptions;

public class ResponseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResponseException() {
	}

	public ResponseException(final String error) {
		super(error);
	}

}
