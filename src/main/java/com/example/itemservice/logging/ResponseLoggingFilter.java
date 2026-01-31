package com.example.itemservice.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Filter that logs all outgoing HTTP responses.
 * 
 * <p>This filter captures and logs details about each outgoing response including
 * status code, response time, and content type for monitoring and performance analysis.</p>
 */
@Component
public class ResponseLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseLoggingFilter.class);
    
    @Value("${logging.json-content.enabled:true}")
    private boolean jsonContentLoggingEnabled;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // Create a response wrapper to capture the response
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        
        try {
            // Continue the filter chain
            filterChain.doFilter(request, responseWrapper);
            
            // Calculate response time
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Log the response details
            logResponse(request, responseWrapper, responseTime);
            
            // Copy the response content to the original response
            responseWrapper.copyBodyToResponse();
            
        } catch (Exception e) {
            // Log any exceptions that occur during processing
            long responseTime = System.currentTimeMillis() - startTime;
            logger.error("RESPONSE ERROR: {} {} - Status: {} - Time: {}ms - Error: {}", 
                        request.getMethod(), 
                        request.getRequestURI(),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        responseTime,
                        e.getMessage());
            throw e;
        }
    }
    
    /**
     * Logs the response details.
     *
     * @param request the HTTP request
     * @param response the HTTP response wrapper
     * @param responseTime the time taken to process the request
     */
    private void logResponse(HttpServletRequest request, ResponseWrapper response, long responseTime) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String contentType = response.getContentType();
        long contentLength = response.getContentLength();
        String responseBody = getResponseBody(response);
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("RESPONSE: ").append(method).append(" ").append(uri);
        logMessage.append(" - Status: ").append(status);
        logMessage.append(" - Time: ").append(responseTime).append("ms");
        logMessage.append(" - Content-Type: ").append(contentType != null ? contentType : "N/A");
        logMessage.append(" - Size: ").append(contentLength).append(" bytes");
        
        // Add response body if JSON content logging is enabled and body exists
        if (jsonContentLoggingEnabled && responseBody != null && !responseBody.trim().isEmpty()) {
            logMessage.append("\nBody: ").append(responseBody);
        }
        
        logger.info(logMessage.toString());
    }
    
    /**
     * Extracts the response body from the response wrapper.
     *
     * @param response the response wrapper
     * @return the response body as a string, or null if not available
     */
    private String getResponseBody(ResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content != null && content.length > 0) {
                return new String(content);
            }
        } catch (Exception e) {
            // If we can't read the response body, return null
            return null;
        }
        return null;
    }
    
    /**
     * Custom HttpServletResponse wrapper to capture response content.
     */
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        
        private final ByteArrayOutputStream content = new ByteArrayOutputStream();
        private final HttpServletResponse originalResponse;
        
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
            this.originalResponse = response;
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setWriteListener(WriteListener listener) {
                    // Not implemented for this use case
                }
                
                @Override
                public void write(int b) throws IOException {
                    content.write(b);
                }
            };
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(new OutputStreamWriter(content, getCharacterEncoding()));
        }
        
        @Override
        public String getCharacterEncoding() {
            return originalResponse.getCharacterEncoding();
        }
        
        @Override
        public void setContentLength(int len) {
            // Ignore - we'll set it after capturing content
        }
        
        @Override
        public void setContentLengthLong(long len) {
            // Ignore - we'll set it after capturing content
        }
        
        @Override
        public void setContentType(String type) {
            originalResponse.setContentType(type);
        }
        
        @Override
        public String getContentType() {
            return originalResponse.getContentType();
        }
        
        @Override
        public int getStatus() {
            return originalResponse.getStatus();
        }
        
        public long getContentLength() {
            return content.size();
        }
        
        public byte[] getContentAsByteArray() {
            return content.toByteArray();
        }
        
        public void copyBodyToResponse() throws IOException {
            byte[] contentBytes = getContentAsByteArray();
            originalResponse.setContentLengthLong(contentBytes.length);
            
            ServletOutputStream outputStream = originalResponse.getOutputStream();
            outputStream.write(contentBytes);
            outputStream.flush();
        }
    }
}