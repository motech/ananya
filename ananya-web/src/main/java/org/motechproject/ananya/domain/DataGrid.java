package org.motechproject.ananya.domain;

import java.util.LinkedHashMap;
import java.util.List;

public interface DataGrid {
    LinkedHashMap<String, String> getHeader();
    List<? extends Object> getContent();
}

