package exceptions;

public class EncodingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EncodingException() {
		super();
	}

	public EncodingException(final String encoding) {
		super("Se genero la exception por el encoding: " + encoding);
	}
}
