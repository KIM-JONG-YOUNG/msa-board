package edu.jong.msa.board.web.handler;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class QueryStringHandler {

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.initDirectFieldAccess();
	}
	
}
