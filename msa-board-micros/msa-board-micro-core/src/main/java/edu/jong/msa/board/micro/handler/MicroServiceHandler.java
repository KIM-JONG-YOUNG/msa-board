package edu.jong.msa.board.micro.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.jong.msa.board.client.exception.ClientException;
import edu.jong.msa.board.micro.exception.AlreadyExistsDataException;
import edu.jong.msa.board.micro.exception.NotFoundDataException;
import edu.jong.msa.board.web.exception.ParamValidException;
import edu.jong.msa.board.web.handler.ErrorResponseHandler;
import edu.jong.msa.board.web.response.ErrorResponse;

@RestControllerAdvice
@Order(Integer.MIN_VALUE)
public class MicroServiceHandler extends ErrorResponseHandler {

	@ExceptionHandler(ParamValidException.class)
	protected ResponseEntity<Object> handleParamValidException(ParamValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.builder()
						.status(HttpStatus.BAD_REQUEST)
						.messageList(e.getMessageList())
						.build());
	}
	
	@ExceptionHandler(ClientException.class)
	ResponseEntity<ErrorResponse> handleClientException(ClientException e) {
		return ResponseEntity.status(e.getStatus())
				.body(ErrorResponse.builder()
						.status(e.getStatus())
						.messageList(e.getMessageList())
						.build());
	} 
	
	@ExceptionHandler({
		NotFoundDataException.class,
		AlreadyExistsDataException.class
	})
	ResponseEntity<ErrorResponse> handleDataException(Exception e) {
		
		HttpStatus status = null;
		
		if (e instanceof NotFoundDataException) {
			status = HttpStatus.NOT_FOUND;
		} else if (e instanceof AlreadyExistsDataException) {
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
