package exceptions;

public class CloseException extends RuntimeException {

	public CloseException() {
		super();
	}

	public CloseException(final String message) {
		super(message);
	}
}
