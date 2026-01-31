package com.example.itemservice.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Wrapper for HttpServletRequest that allows reading the request body multiple times.
 * 
 * <p>HTTP request bodies can only be read once by default. This wrapper reads
 * the request body once and stores it, allowing multiple reads for logging purposes.</p>
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    
    private final String body;
    
    /**
     * Constructs a new RequestWrapper that captures the request body.
     *
     * @param request the original HttpServletRequest
     * @throws IOException if an I/O error occurs while reading the request body
     */
    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = readRequestBody(request);
    }
    
    /**
     * Reads the request body and returns it as a string.
     *
     * @param request the HttpServletRequest
     * @return the request body as a string
     * @throws IOException if an I/O error occurs
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), "UTF-8"))) {
            
            char[] charBuffer = new char[1024];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }
        return stringBuilder.toString();
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Returns a ServletInputStream that reads from the stored request body.</p>
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes("UTF-8"));
        
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented for this use case
            }
            
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Returns a BufferedReader that reads from the stored request body.</p>
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
    }
    
    /**
     * Gets the captured request body.
     *
     * @return the request body as a string
     */
    public String getBody() {
        return body;
    }
}