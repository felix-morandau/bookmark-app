package com.felix.bookmark_app.service.strategy;

import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Collection;
import org.springframework.stereotype.Component;

@Component("csv")
public class CsvExportStrategy implements ExportStrategy {

    @Override
    public String exportData(Collection collection) {
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("Collection Info:\n");
        csvBuilder.append("ID,Name,Category,Description,Creator,Visible\n");
        csvBuilder.append(collection.getId()).append(",")
                .append(collection.getName()).append(",")
                .append(collection.getCategory() != null ? collection.getCategory().toString() : "").append(",")
                .append(collection.getDescription()).append(",")
                .append(collection.getCreator()).append(",")
                .append(collection.isVisible())
                .append("\n\n");

        csvBuilder.append("Bookmarks:\n");
        csvBuilder.append("id,title,url\n");
        for (Bookmark bookmark : collection.getBookmarks()) {
            csvBuilder.append(bookmark.getId()).append(",")
                    .append(bookmark.getTitle()).append(",")
                    .append(bookmark.getLink() != null ? bookmark.getLink().getUrl() : "")
                    .append("\n");
        }
        return csvBuilder.toString();
    }
}
