package edu.jong.msa.board.micro.handler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.jong.msa.board.client.exception.ClientException;
import edu.jong.msa.board.web.response.ErrorResponse;

@RestControllerAdvice
public class MicroServiceHandler {

	@ExceptionHandler(ClientException.class)
	ResponseEntity<ErrorResponse> handlerClientException(ClientException e) {
		return ResponseEntity.status(e.getStatus())
				.body(ErrorResponse.builder()
						.status(e.getStatus())
						.messageList(e.getMessageList())
						.build());
	} 

	@ExceptionHandler({
		EntityNotFoundException.class,
		EntityExistsException.class
	})
	ResponseEntity<ErrorResponse> handlerEntityException(Exception e) {
		
		HttpStatus status = null;
		
		if (e instanceof EntityNotFoundException) {
			status = HttpStatus.NOT_FOUND;
		} else if (e instanceof EntityExistsException) {
			status = HttpStatus.CONFLICT;
		} else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return ResponseEntity.status(status)
				.body(ErrorResponse.builder()
						.status(status)
						.message(e.getMessage())
						.build());
	} 
	
}
