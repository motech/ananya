package org.motechproject.ananya.domain.grid;

import java.util.LinkedHashMap;
import java.util.List;

public class KunjiCallGrid implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<CallContentGridUnit> content;

    public KunjiCallGrid(List<CallContentGridUnit> content) {
        initHeaders();
        this.content = content;
    }

    private void initHeaders() {
        header = new LinkedHashMap<String, String>();
        header.put("name", "Name");
        header.put("msisdn", "MSISDN");
        header.put("callId", "CallId");
        header.put("timeStamp", "TimeStamp");
        header.put("contentName", "Content Name");
        header.put("contentFileName", "Content FileName");
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<CallContentGridUnit> getContent() {
        return content;
    }
}
