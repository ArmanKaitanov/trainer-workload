package com.epam.trainer_workload.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomBooleanDeserializer extends JsonDeserializer<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(CustomBooleanDeserializer.class);

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();

        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            logger.error("Invalid boolean value: {}", value);
            throw new InvalidFormatException(parser, String.format("Invalid boolean value: %s", value), value, Boolean.class);
        }
    }
}
