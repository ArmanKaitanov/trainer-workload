package com.epam.trainer_workload.activemq;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.exception.ParseTokenException;
import com.epam.trainer_workload.service.TrainerWorkloadService;
import com.epam.trainer_workload.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadListener {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadListener.class);

    private final TrainerWorkloadService trainerWorkloadService;

    private final JwtUtil jwtUtil;

    private static final String BEARER = "Bearer ";

    private static final int BEARER_LENGTH = BEARER.length();

    private static final String AUTHORIZATION = "Authorization";

    private static final String TRANSACTION_ID_KEY = "transactionId";

    @JmsListener(destination = "trainer-workload.queue")
    public void receiveMessage(Message message) throws JMSException {
        try {
            tokenAuthentication(message);
            logger.info("Transaction ID: {}", message.getStringProperty(TRANSACTION_ID_KEY));

            ObjectMessage objectMessage = (ObjectMessage) message;
            TrainerWorkloadUpdateRequestDto dto = (TrainerWorkloadUpdateRequestDto) objectMessage.getObject();
            trainerWorkloadService.updateWorkload(dto);
        } catch (AuthenticationException | ParseTokenException e) {
            logger.error("Authentication error: {}", e.getMessage());
            throw new JMSException(String.format("Token validation error: %s", e.getMessage()));
        } catch (JMSException e) {
            logger.error("JMS error: {}", e.getMessage());
            throw e;
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
            }
        }
    }
}
