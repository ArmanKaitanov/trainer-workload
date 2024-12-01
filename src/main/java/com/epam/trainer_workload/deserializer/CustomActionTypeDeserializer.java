package com.epam.trainer_workload.deserializer;

import com.epam.trainer_workload.model.enumeration.ActionType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomActionTypeDeserializer extends JsonDeserializer<ActionType> {

    private static final Logger logger = LoggerFactory.getLogger(CustomActionTypeDeserializer.class);

    @Override
    public ActionType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText().toUpperCase();

        try {
            return ActionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid action type value: {}", value);
            throw new InvalidFormatException(parser, String.format("Invalid action type value: %s", parser.getText()), value, ActionType.class);
        }
    }
}
