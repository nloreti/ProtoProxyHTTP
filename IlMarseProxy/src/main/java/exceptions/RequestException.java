package exceptions;

public class RequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestException() {
		super();
	}

	public RequestException(final String string) {
		super(string);
	}

}
