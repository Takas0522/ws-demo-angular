package com.example.bffservice.util;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for HTTP header operations
 */
public class HeaderUtil {

    /**
     * Extract headers from the incoming HTTP request
     * 
     * @param request the HTTP servlet request
     * @return HttpHeaders containing all headers from the request
     */
    public static HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private HeaderUtil() {
        // Utility class, prevent instantiation
    }
}
