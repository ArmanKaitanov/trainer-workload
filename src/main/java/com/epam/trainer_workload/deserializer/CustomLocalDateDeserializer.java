//package com.epam.trainer_workload.deserializer;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomLocalDateDeserializer.class);
//
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//    @Override
//    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
//        String date = parser.getText();
//
//        logger.info("Received date string: {}", date);
//
//        try {
////            return LocalDate.parse(date, formatter);
//            LocalDate parsedDate = LocalDate.parse(date, formatter);
//            logger.info("Parsed date: {}", parsedDate);
//            return parsedDate;
//        } catch (Exception e) {
//            logger.error("Invalid date format: {}", date);
//            throw new InvalidFormatException(parser, String.format("Invalid date format: %s", date), date, LocalDate.class);
//        }
//    }
//}
