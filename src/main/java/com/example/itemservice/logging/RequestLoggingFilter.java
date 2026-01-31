package com.example.itemservice.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that logs all incoming HTTP requests.
 * 
 * <p>This filter captures and logs details about each incoming request including
 * method, URI, headers, and request body for debugging and monitoring purposes.</p>
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Value("${logging.json-content.enabled:true}")
    private boolean jsonContentLoggingEnabled;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Wrap the request to capture the body
        RequestWrapper requestWrapper = new RequestWrapper(request);
        
        // Capture request details
        String method = requestWrapper.getMethod();
        String uri = requestWrapper.getRequestURI();
        String queryString = requestWrapper.getQueryString();
        String clientIp = getClientIpAddress(requestWrapper);
        String requestBody = requestWrapper.getBody();
        
        // Format the request log with JSON content
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("REQUEST: ").append(method).append(" ").append(uri);
        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }
        logMessage.append(" from ").append(clientIp);
        logMessage.append("\n  Headers: ").append(getHeadersInfo(requestWrapper));
        
        // Add request body if JSON content logging is enabled and body exists
        if (jsonContentLoggingEnabled && requestBody != null && !requestBody.trim().isEmpty()) {
            logMessage.append("\n  Body: ").append(requestBody);
        }
        
        logger.info(logMessage.toString());
        
        // Continue the filter chain with the wrapped request
        filterChain.doFilter(requestWrapper, response);
    }
    
    /**
     * Extracts client IP address from the request, handling proxy headers.
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Extracts relevant headers from the request for logging.
     *
     * @param request the HTTP request
     * @return formatted string of headers
     */
    private String getHeadersInfo(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        
        // Log important headers
        String contentType = request.getContentType();
        if (contentType != null) {
            headers.append("Content-Type: ").append(contentType).append(", ");
        }
        
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            headers.append("User-Agent: ").append(userAgent.substring(0, Math.min(userAgent.length(), 100))).append(", ");
        }
        
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            // Don't log the actual token, just indicate it's present
            headers.append("Authorization: [PRESENT], ");
        }
        
        return headers.toString();
    }
}