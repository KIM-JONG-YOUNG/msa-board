package edu.jong.msa.board.web.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import edu.jong.msa.board.web.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ErrorResponseHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		log.debug("Exception Class : {}", ex.getClass());
		log.debug("Exception Message : {}", ex.getMessage());
		
		return ResponseEntity.status(status)
				.body(ErrorResponse.builder()
						.status(status)
						.message(ex.getMessage())
						.build());
	}

}
