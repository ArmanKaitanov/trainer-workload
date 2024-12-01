package com.epam.trainer_workload.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class RestDetailsFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RestDetailsFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String transactionId = (String) httpRequest.getAttribute("transactionId");

        logger.info("{} - Request: {} {}?{}", transactionId, method, uri, queryString);

        chain.doFilter(request, response);

        int status = httpResponse.getStatus();
        String contentType = httpResponse.getContentType();
        logger.info("{} - Response: {} {}", transactionId, status, contentType);
    }
}


