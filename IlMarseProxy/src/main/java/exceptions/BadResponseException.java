package exceptions;

public class BadResponseException extends RuntimeException {

	public BadResponseException() {
	}
	
	public BadResponseException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
