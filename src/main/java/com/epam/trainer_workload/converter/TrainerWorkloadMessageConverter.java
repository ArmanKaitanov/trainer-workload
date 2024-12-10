package com.epam.trainer_workload.converter;

import com.epam.trainer_workload.dto.request.TrainerWorkloadUpdateRequestDto;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageConverter implements MessageConverter {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadMessageConverter.class);

    @Override
    @NonNull
    public Message toMessage(@NonNull Object object,@NonNull Session session) throws JMSException, MessageConversionException {
        if(object instanceof TrainerWorkloadUpdateRequestDto) {
            return session.createObjectMessage((TrainerWorkloadUpdateRequestDto) object);
        }
        String errorMessage = "Object is not of expected type TrainerWorkloadUpdateRequestDto";
        logger.error(errorMessage);
        throw new MessageConversionException(errorMessage);
    }

    @Override
    @NonNull
    public Object fromMessage(@NonNull Message message) throws JMSException, MessageConversionException {
        if(message instanceof ObjectMessage) {
            return ((ObjectMessage) message).getObject();
        }
        String errorMessage = "Message is not of expected type ObjectMessage";
        logger.error(errorMessage);
        throw new MessageConversionException(errorMessage);
    }
}
