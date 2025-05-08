package com.felix.bookmark_app.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.bookmark_app.model.Collection;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportStrategyTest {

    @Test
    void testJsonExportStrategy() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonExportStrategy jsonStrategy = new JsonExportStrategy(mapper);

        Collection collection = new Collection();
        UUID id = UUID.randomUUID();
        collection.setId(id);
        collection.setName("Test Name");
        collection.setCategory(null);
        collection.setDescription("Test Description");
        collection.setCreator("testuser");
        collection.setVisible(true);
        collection.setBookmarks(Collections.emptyList());

        String json = jsonStrategy.exportData(collection);

        JsonNode root = mapper.readTree(json);
        assertEquals(id.toString(), root.get("id").asText());
        assertEquals("Test Name", root.get("name").asText());
        assertEquals("Test Description", root.get("description").asText());
        assertEquals("testuser", root.get("creator").asText());
        assertTrue  (root.get("visible").asBoolean());
        assertTrue  (root.get("bookmarks").isArray());
        assertEquals(0, root.get("bookmarks").size());
    }

    @Test
    void testPlainTextExportStrategy() {
        PlainTextExportStrategy plain = new PlainTextExportStrategy();

        Collection collection = new Collection();
        UUID id = UUID.randomUUID();
        collection.setId(id);
        collection.setName("Test Name");
        collection.setCategory(null);
        collection.setDescription("Test Description");
        collection.setCreator("testuser");
        collection.setVisible(false);
        collection.setBookmarks(Collections.emptyList());

        String out = plain.exportData(collection);

        assertTrue(out.contains("Collection Info:"));
        assertTrue(out.contains("ID: "          + id));
        assertTrue(out.contains("Name: Test Name"));
        assertTrue(out.contains("Category: "));
        assertTrue(out.contains("Description: Test Description"));
        assertTrue(out.contains("Creator: testuser"));
        assertTrue(out.contains("Visible: false"));
        assertTrue(out.contains("-------------------------"));
        assertTrue(out.contains("Bookmarks:"));
    }

    @Test
    void testCsvExportStrategy() {
        CsvExportStrategy csv = new CsvExportStrategy();

        Collection collection = new Collection();
        UUID id = UUID.randomUUID();
        collection.setId(id);
        collection.setName("Test Name");
        collection.setCategory(null);
        collection.setDescription("Test Description");
        collection.setCreator("testuser");
        collection.setVisible(true);
        collection.setBookmarks(Collections.emptyList());

        String out = csv.exportData(collection);

        assertTrue(out.contains("ID,Name,Category,Description,Creator,Visible"));
        assertTrue(out.contains(id + ",Test Name,,Test Description,testuser,true"));
        assertTrue(out.contains("Bookmarks:"));
        assertTrue(out.contains("id,title,url"));
    }
}
