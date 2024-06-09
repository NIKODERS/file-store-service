package nikheel.rh.fss.exception.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import nikheel.rh.fss.domain.ErrorMessage;
import nikheel.rh.fss.exception.BadFileException;
import nikheel.rh.fss.exception.FSSFileNotFoundException;
import nikheel.rh.fss.exception.FileStorageException;

@ControllerAdvice
public class FSSExceptionHandler {

	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorMessage> handleFileStorageException(FileStorageException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), "FILE_STORAGE_ERROR", ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(FSSFileNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleFileNotFoundException(FSSFileNotFoundException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), "FILE_NOT_FOUND", ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadFileException.class)
	public ResponseEntity<ErrorMessage> handleBadFileException(BadFileException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), "BAD_FILE_TYPE_ERROR", ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	// Handle global exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
		ErrorMessage errorDetails = new ErrorMessage(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
