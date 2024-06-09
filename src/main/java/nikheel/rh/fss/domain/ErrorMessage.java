package nikheel.rh.fss.domain;

import java.util.Date;

public class ErrorMessage {
	private Date errorOccurTime;
	private String errorCode;
	private String errorMessage;
	private String errorDetails;

	public ErrorMessage() {
	}

	public ErrorMessage(Date errorOccurTime, String errorMessage) {
		this.errorOccurTime = errorOccurTime;
		this.errorMessage = errorMessage;
	}

	public ErrorMessage(Date errorOccurTime, String errorCode, String errorMessage, String errorDetails) {
		this.errorOccurTime = errorOccurTime;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorDetails = errorDetails;
	}

	public ErrorMessage(Date errorOccurTime, String errorCode, String errorMessage) {
		this(errorOccurTime, errorCode, errorMessage, null);
	}

	public Date getErrorOccurTime() {
		return errorOccurTime;
	}

	public void setErrorOccurTime(Date errorOccurTime) {
		this.errorOccurTime = errorOccurTime;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}

	@Override
	public String toString() {
		return "ErrorMessage [errorOccurTime=" + errorOccurTime + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}

}
