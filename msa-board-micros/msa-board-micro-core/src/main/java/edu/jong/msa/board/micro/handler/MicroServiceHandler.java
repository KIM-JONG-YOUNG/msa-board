package edu.jong.msa.board.micro.handler;

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
	
}
