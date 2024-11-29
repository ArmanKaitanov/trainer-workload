package com.epam.trainer_workload.filter;

import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.exception.ParseTokenException;
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

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            int statusCode = determineStatusCode(e);
            logger.error("{} - Error: {}", transactionId, e.getMessage());
            sendErrorMessage(httpResponse, e.getMessage(), statusCode);

            return;
        }

        int status = httpResponse.getStatus();
        String contentType = httpResponse.getContentType();
        logger.info("{} - Response: {} {}", transactionId, status, contentType);
    }

    private int determineStatusCode(Exception e) {
        if (e instanceof AuthenticationException || e instanceof ParseTokenException) {
            return HttpServletResponse.SC_UNAUTHORIZED;
        } else {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    private void sendErrorMessage(HttpServletResponse response, String errorMessage, int statusCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
    }
}

