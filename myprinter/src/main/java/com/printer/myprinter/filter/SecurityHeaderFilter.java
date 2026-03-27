package com.printer.myprinter.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Prevent browsers from guessing the mime type
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            // Prevent clickjacking by restricting framing
            httpResponse.setHeader("X-Frame-Options", "DENY");
            // Enable browser XSS filtering
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            // Prevent caching of sensitive data (can be refined per endpoint if needed)
            httpResponse.setHeader("Cache-Control", "no-store, max-age=0");
            // Strict Transport Security (HSTS) - Tell browser to only use HTTPS
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        chain.doFilter(request, response);
    }
}
