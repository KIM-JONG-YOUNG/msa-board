package edu.jong.msa.board.client.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String methodName;

	private final HttpStatus status;

	private final List<String> messageList;

	@Override
	public String getMessage() {
		return String.join("\n", this.getMessageList());
	}
	
}
