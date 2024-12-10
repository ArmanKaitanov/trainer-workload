package com.epam.trainer_workload.activemq;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.exception.ParseTokenException;
import com.epam.trainer_workload.service.TrainerWorkloadService;
import com.epam.trainer_workload.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
@RequiredArgsConstructor
public class TrainerWorkloadListener {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadListener.class);

    private final TrainerWorkloadService trainerWorkloadService;

//    private final JmsTemplate jmsTemplate;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private static final String BEARER = "Bearer ";

    private static final int BEARER_LENGTH = BEARER.length();

    private static final String AUTHORIZATION = "Authorization";

    private static final String TRANSACTION_ID_KEY = "transactionId";

    @JmsListener(destination = "trainer-workload.queue")
    public void receiveMessage(Message message) throws JMSException {
//        try {
//            if(true) {
//                throw new AuthenticationException("Authentication exception");
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
        try {
            tokenAuthentication(message);
            logger.info("Transaction ID: {}", message.getStringProperty(TRANSACTION_ID_KEY));

            if(message instanceof TextMessage textMessage) {
                String json = textMessage.getText();
                TrainerWorkloadUpdateRequestDto dto = objectMapper.readValue(json, TrainerWorkloadUpdateRequestDto.class);
                trainerWorkloadService.updateWorkload(dto);
            } else {
                String errorMessage = "Message is not of expected type TextMessage";
                logger.error(errorMessage);
                throw new JMSException(errorMessage);
            }

//            TrainerWorkloadUpdateRequestDto dto = (TrainerWorkloadUpdateRequestDto) objectMessage.getObject();
//            trainerWorkloadService.updateWorkload(dto);
//            message.acknowledge();
        } catch (AuthenticationException | ParseTokenException e) {
            logger.error("Authentication error: {}", e.getMessage());
            throw e;
        } catch (JMSException e) {
            logger.error("JMS error: {}", e.getMessage());
            throw e;
        } catch (JsonMappingException e) {
            logger.error("JSON mapping error: {}", e.getMessage());
            throw new JMSException("JSON mapping error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage());
            throw new JMSException("JSON processing error: " + e.getMessage());
        }
    }

    private void tokenAuthentication(Message message) throws JMSException {
        String token = message.getStringProperty(AUTHORIZATION);

        if (token != null && token.startsWith(BEARER)) {
            String jwtToken = token.substring(BEARER_LENGTH);
            Claims claims = jwtUtil.parseClaims(jwtToken);

            if (claims != null) {
                String username = claims.get("username", String.class);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("User with username {} successfully authenticated", username);
            } else {
                throw new AuthenticationException("Invalid JWT token");
            }
        } else {
            throw new AuthenticationException("Missing or invalid Authorization token");
        }
    }
}
