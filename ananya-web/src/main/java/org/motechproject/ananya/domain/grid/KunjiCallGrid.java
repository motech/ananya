package org.motechproject.ananya.domain.grid;

import org.motechproject.ananya.support.admin.domain.CallContent;

import java.util.LinkedHashMap;
import java.util.List;

public class KunjiCallGrid implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<CallContent> content;

    public KunjiCallGrid(List<CallContent> content) {
        initHeaders();
        this.content = content;
    }

    private void initHeaders() {
        header = new LinkedHashMap<String, String>();
        header.put("callId", "Call Id");
        header.put("timeStamp", "Time Stamp");
        header.put("contentName", "Content Name");
        header.put("contentFileName", "Content File Name");
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<CallContent> getContent() {
        return content;
    }
}
