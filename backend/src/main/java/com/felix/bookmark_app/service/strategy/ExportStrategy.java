package com.felix.bookmark_app.service.strategy;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Collection;

import java.util.List;

public interface ExportStrategy {

    String exportData(Collection collection);
}
