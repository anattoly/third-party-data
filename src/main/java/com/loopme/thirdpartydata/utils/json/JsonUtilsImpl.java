package com.loopme.thirdpartydata.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopme.thirdpartydata.service.amazon.S3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonUtilsImpl implements JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Override
    public String getFileName(String requestBody) {
        String fileName = "";

        try {
            Map<String, String> request = MAPPER.readValue(requestBody, Map.class);
            JsonNode messages = MAPPER.readTree(request.get("Message"));
            for (JsonNode records : messages) {
                if (records.isArray()) {
                    for (JsonNode events : records) {
                        JsonNode s3 = events.path("s3");
                        for (JsonNode object : s3) {
                            fileName = object.path("key").asText();
                        }
                    }
                }
            }

        } catch (JsonProcessingException jpe) {
            logger.info("Error Message:    " + jpe.getMessage());
        }

        return fileName;
    }

    @Override
    public Map<String, String> getConfirmField(String requestBody) {
        Map<String, String> confirmFields = new HashMap<>();

        try {
            confirmFields = MAPPER.readValue(requestBody, Map.class);
        } catch (JsonProcessingException jpe) {
            logger.info("Error Message:    " + jpe.getMessage());
        }

        return confirmFields;
    }

    @Override
    public LocalDateTime getTimeRequest(String requestBody) {
        Map<String, String> request = new HashMap<>();

        try {
            request = MAPPER.readValue(requestBody, Map.class);
        } catch (JsonProcessingException jpe) {
            logger.info("Error Message:    " + jpe.getMessage());
        }

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .optionalStart()
                .appendPattern(".SSS")
                .optionalEnd()
                .optionalStart()
                .appendZoneOrOffsetId()
                .optionalEnd()
                .optionalStart()
                .appendOffset("+HHMM", "0000")
                .optionalEnd()
                .toFormatter();

        return LocalDateTime.parse(request.get("Timestamp"), dateTimeFormatter);
    }
}
