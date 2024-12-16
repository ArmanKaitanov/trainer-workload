package com.epam.trainer_workload.activemq;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import com.epam.trainer_workload.exception.AuthenticationException;
import com.epam.trainer_workload.service.TrainerWorkloadService;
import com.epam.trainer_workload.util.JwtUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadListenerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrainerWorkloadListener trainerWorkloadListener;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID_KEY = "transactionId";
    private static final int BEARER_LENGTH = BEARER.length();

    @Test
    void receiveMessage_shouldSuccess() throws Exception {
        String token = BEARER + "some.jwt.token";
        String transactionId = "12345";
        String json = "{\"some\":\"json\"}";
        TrainerWorkloadUpdateRequestDto dto = new TrainerWorkloadUpdateRequestDto();
        TextMessage textMessage = mock(TextMessage.class);
        Claims claims = mock(Claims.class);

        when(claims.get("username", String.class)).thenReturn("testUser");
        when(jwtUtil.parseClaims("some.jwt.token")).thenReturn(claims);
        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(token);
        when(textMessage.getStringProperty(TRANSACTION_ID_KEY)).thenReturn(transactionId);
        when(textMessage.getText()).thenReturn(json);
        when(objectMapper.readValue(json, TrainerWorkloadUpdateRequestDto.class)).thenReturn(dto);

        trainerWorkloadListener.receiveMessage(textMessage);

        verify(jwtUtil).parseClaims("some.jwt.token");
        verify(trainerWorkloadService).updateWorkload(dto);
        verify(textMessage).acknowledge();
    }

    @Test
    public void receiveMessage_shouldThrowJmsException_whenInvalidMessageProvided() throws JMSException {
        Message invalidMessage = mock(Message.class);
        Claims claims = mock(Claims.class);

        when(invalidMessage.getStringProperty(AUTHORIZATION)).thenReturn("Bearer some_token"); // Mock valid token
        when(jwtUtil.parseClaims(anyString())).thenReturn(claims);

        JMSException exception = assertThrows(JMSException.class, () -> trainerWorkloadListener.receiveMessage(invalidMessage));
        assertThat(exception.getMessage()).isEqualTo("Message is not of expected type TextMessage");
    }


    @Test
    void receiveMessage_shouldThrowAuthenticationException_whenTokenCouldNotParse() throws Exception {
        String token = BEARER + "some.jwt.token";
        TextMessage textMessage = mock(TextMessage.class);

        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(token);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> trainerWorkloadListener.receiveMessage(textMessage));
        assertThat(exception.getMessage()).isEqualTo("Invalid JWT token");
    }

    @Test
    void receiveMessage_shouldThrowJmsException_whenJsonProcessingErrorOccurs() throws Exception {
        String token = BEARER + "some.jwt.token";
        String json = "{\"some\":\"json\"}";
        TextMessage textMessage = mock(TextMessage.class);
        Claims claims = mock(Claims.class);

        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(token);
        when(textMessage.getText()).thenReturn(json);
        when(jwtUtil.parseClaims(anyString())).thenReturn(claims);

        doThrow(new JsonProcessingException("JSON processing error") {}).when(objectMapper).readValue(json, TrainerWorkloadUpdateRequestDto.class);

        JMSException exception = assertThrows(JMSException.class, () -> trainerWorkloadListener.receiveMessage(textMessage));
        assertThat(exception.getMessage()).isEqualTo("JSON processing error: JSON processing error");
    }

    @Test
    void receiveMessage_shouldThrowJmsException_whenJsonMappingErrorOccurs() throws Exception {
        String token = BEARER + "some.jwt.token";
        String json = "{\"some\":\"json\"}";
        TextMessage textMessage = mock(TextMessage.class);
        Claims claims = mock(Claims.class);
        JsonParser jsonParser = mock(JsonParser.class);

        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(token);
        when(textMessage.getText()).thenReturn(json);
        when(jwtUtil.parseClaims(anyString())).thenReturn(claims);

        JsonMappingException jsonMappingException = new JsonMappingException(jsonParser, "JSON mapping error");
        doThrow(jsonMappingException).when(objectMapper).readValue(json, TrainerWorkloadUpdateRequestDto.class);

        JMSException exception = assertThrows(JMSException.class, () -> trainerWorkloadListener.receiveMessage(textMessage));
        assertThat(exception.getMessage()).isEqualTo("JSON mapping error: JSON mapping error");
    }

    @Test
    void receiveMessage_shouldThrowAuthenticationException_whenTokenWithoutBearer() throws Exception {
        String token = "some.jwt.token";
        TextMessage textMessage = mock(TextMessage.class);

        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(token);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> trainerWorkloadListener.receiveMessage(textMessage));
        assertThat(exception.getMessage()).isEqualTo("Missing or invalid Authorization token");
    }

    @Test
    void receiveMessage_shouldThrowAuthenticationException_whenTokenIsNull() throws Exception {
        TextMessage textMessage = mock(TextMessage.class);

        when(textMessage.getStringProperty(AUTHORIZATION)).thenReturn(null);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> trainerWorkloadListener.receiveMessage(textMessage));
        assertThat(exception.getMessage()).isEqualTo("Missing or invalid Authorization token");
    }
}
