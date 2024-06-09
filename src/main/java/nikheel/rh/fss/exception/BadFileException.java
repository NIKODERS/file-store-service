package nikheel.rh.fss.exception;

public class BadFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadFileException(String message) {
		super(message);
	}

	public BadFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
