package edu.jong.msa.board.micro.exception;

public class AlreadyExistsDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyExistsDataException(String message) {
		super(message);
	}
	
}
