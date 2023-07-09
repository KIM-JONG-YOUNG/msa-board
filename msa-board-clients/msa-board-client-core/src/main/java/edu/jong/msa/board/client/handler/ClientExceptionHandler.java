package edu.jong.msa.board.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import edu.jong.msa.board.client.exception.ClientException;
import edu.jong.msa.board.common.BoardConstants.Packages;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.web.response.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;

@Component
@EnableFeignClients(basePackages = Packages.CLIENT_PACKAGE)
public class ClientExceptionHandler implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
	
		try (BufferedReader bodyReader = new BufferedReader(new InputStreamReader(
				response.body().asInputStream(), StandardCharsets.UTF_8))) {
			
			String body = bodyReader.lines().collect(Collectors.joining("\n"));
			ErrorResponse errorResponse = ObjectMapperUtils.toObject(body, ErrorResponse.class);
			
			return new ClientException(methodKey, 
					errorResponse.getStatus(), 
					errorResponse.getMessageList());

		} catch (IOException e) {

			e.printStackTrace();
		
			return new ClientException(methodKey, 
					HttpStatus.valueOf(response.status()), 
					Arrays.asList(e.getMessage()));
		}
	}

}
