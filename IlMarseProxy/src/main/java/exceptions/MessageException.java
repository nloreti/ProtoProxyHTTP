package exceptions;

public class MessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MessageException() {
		super();
	}

	public MessageException(final String error) {
		super(error);
	}
}
