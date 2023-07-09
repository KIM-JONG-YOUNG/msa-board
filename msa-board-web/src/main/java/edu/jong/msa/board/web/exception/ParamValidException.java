package edu.jong.msa.board.web.exception;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ParamValidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final List<String> messageList;

	public ParamValidException(String message) {
		this(Arrays.asList(message));
	}

	@Override
	public String getMessage() {
		return String.join("\n", this.getMessageList());
	}	
	
}
