package edu.jong.msa.board.web.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.jong.msa.board.common.BoardConstants.Patterns;
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

	private HttpStatus status;

	@Singular("message")
	private List<String> messageList;

	@Builder.Default
	@JsonFormat(pattern = Patterns.DATE_TIME_FORMAT)
	private LocalDateTime timestamp = LocalDateTime.now();

}
