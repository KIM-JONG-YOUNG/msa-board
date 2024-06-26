package com.jong.msa.board.core.web.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Getter
@Builder 
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private String errorCode;
	
	private String errorMessage;
	
	@Singular("errorDetails")
	private List<Details> errorDetailsList;
	
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	@Getter
	@Builder
	@ToString 
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Details {

		private String field;
		private String message;
		
	}
	
}
