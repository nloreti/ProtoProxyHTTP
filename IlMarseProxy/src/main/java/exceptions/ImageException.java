package exceptions;

public class ImageException extends Exception {

	public ImageException() {
		super();
	}

	public ImageException(final String error) {
		super("error");
	}

}
