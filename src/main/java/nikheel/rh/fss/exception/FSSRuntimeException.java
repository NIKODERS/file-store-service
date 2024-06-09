package nikheel.rh.fss.exception;

public class FSSRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String errorDetails;

	public FSSRuntimeException() {
		super();
	}

	public FSSRuntimeException(String message) {
		super(message);
	}

	public FSSRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FSSRuntimeException(String errorCode, String message, String errorDetails) {
		super(message);
		this.errorCode = errorCode;
		this.errorDetails = errorDetails;
	}

	public FSSRuntimeException(String errorCode, String message, String errorDetails, Throwable cause) {
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
		return "FSSException{" + "errorCode='" + errorCode + '\'' + ", errorDetails='" + errorDetails + '\''
				+ ", message='" + getMessage() + '\'' + '}';
	}
}
