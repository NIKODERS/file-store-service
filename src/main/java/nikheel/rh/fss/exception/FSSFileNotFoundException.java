package nikheel.rh.fss.exception;

public class FSSFileNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String errorDetails;

	public FSSFileNotFoundException(String message) {
		super(message);
	}

	public FSSFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public FSSFileNotFoundException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public FSSFileNotFoundException(String errorCode, String message, String errorDetails) {
		super(message);
		this.errorCode = errorCode;
		this.errorDetails = errorDetails;
	}

	public FSSFileNotFoundException(String errorCode, String message, String errorDetails, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.errorDetails = errorDetails;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDetails() {
		return errorDetails;
	}

	@Override
	public String toString() {
		return "FileNotFoundException{" + "errorCode='" + errorCode + '\'' + ", errorDetails='" + errorDetails + '\''
				+ ", message='" + getMessage() + '\'' + '}';
	}
}
