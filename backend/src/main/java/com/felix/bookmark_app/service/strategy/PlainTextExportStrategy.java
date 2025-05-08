package com.felix.bookmark_app.service.strategy;

import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Collection;
import org.springframework.stereotype.Component;

@Component("plaintext")
public class PlainTextExportStrategy implements ExportStrategy {

    @Override
    public String exportData(Collection collection) {
        StringBuilder sb = new StringBuilder();

        sb.append("Collection Info:\n");
        sb.append("ID: ").append(collection.getId()).append("\n")
                .append("Name: ").append(collection.getName()).append("\n")
                .append("Category: ").append(collection.getCategory() != null ? collection.getCategory().toString() : "").append("\n")
                .append("Description: ").append(collection.getDescription()).append("\n")
                .append("Creator: ").append(collection.getCreator()).append("\n")
                .append("Visible: ").append(collection.isVisible()).append("\n")
                .append("-------------------------\n");

        sb.append("Bookmarks:\n");
        for (Bookmark bookmark : collection.getBookmarks()) {
            sb.append("ID: ").append(bookmark.getId()).append("\n")
                    .append("Title: ").append(bookmark.getTitle()).append("\n")
                    .append("URL: ").append(bookmark.getLink() != null ? bookmark.getLink().getUrl() : "").append("\n")
                    .append("-------------------------\n");
        }
        return sb.toString();
    }
}
