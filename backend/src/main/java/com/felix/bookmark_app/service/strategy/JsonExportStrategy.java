package com.felix.bookmark_app.service.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.bookmark_app.model.Collection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("json")
public class JsonExportStrategy implements ExportStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public String exportData(Collection collection) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collection);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error exporting to JSON", e);
        }
    }
}
