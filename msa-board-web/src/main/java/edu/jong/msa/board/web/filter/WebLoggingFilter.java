package edu.jong.msa.board.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ContentLoggingRequestWrapper requestWrapper 	= new ContentLoggingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper 	= new ContentCachingResponseWrapper(response);

        log.info("Request URL: [{}] {}", 
        		requestWrapper.getMethod(), 
        		requestWrapper.getRequestURL().toString());
        
        if (requestWrapper.getContentLength() > 0) {
        	log.info("Request Content Type: {}", requestWrapper.getContentType());
            log.info("Request Content Body:\n{}", new String(requestWrapper.getBody()));
        }

        filterChain.doFilter(requestWrapper, responseWrapper);
        
        responseWrapper.copyBodyToResponse();

        log.info("Response Status: {}", responseWrapper.getStatus());

        if (responseWrapper.containsHeader(HttpHeaders.LOCATION)) {
        	log.info("Response Location: {}", responseWrapper.getHeader(HttpHeaders.LOCATION));
        }
        
        if (responseWrapper.getContentSize() > 0) {
            log.info("Response Content Type: {}", responseWrapper.getContentType());
            log.info("Response Content Body: {}", new String(responseWrapper.getContentAsByteArray()));
        }
	
	}

	@Getter
	public static class ContentLoggingRequestWrapper extends HttpServletRequestWrapper {

		private final String body;
		
		public ContentLoggingRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
	        this.body = new String(StreamUtils.copyToByteArray(request.getInputStream()));
		}
		
		@Override
		public ServletInputStream getInputStream() throws IOException {
			
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(this.body.getBytes());

			return new ServletInputStream() {
				
				@Override
				public int read() throws IOException {
					return byteInputStream.read();
				}
				
				@Override
				public void setReadListener(ReadListener listener) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public boolean isReady() {
					return true;
				}
				
				@Override
				public boolean isFinished() {
		            return byteInputStream.available() == 0;
				}
			};
		}
		
		@Override
		public BufferedReader getReader() throws IOException {
	        return new BufferedReader(new InputStreamReader(getInputStream()));
		}
	}
}
