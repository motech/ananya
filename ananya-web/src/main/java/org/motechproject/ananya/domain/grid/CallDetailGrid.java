package org.motechproject.ananya.domain.grid;

import java.util.LinkedHashMap;
import java.util.List;

public class CallDetailGrid implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<CallDetailGridUnit> content;

    public CallDetailGrid(List<CallDetailGridUnit> content) {
        initHeaders();
        this.content = content;
    }

    private void initHeaders() {
        header = new LinkedHashMap<String, String>();
        header.put("name", "Name");
        header.put("msisdn", "MSISDN");
        header.put("callId", "CallId");
        header.put("startTime", "Start Time");
        header.put("endTime", "End Time");
        header.put("duration", "Duration");
        header.put("calledNumber", "Called Number");
        header.put("type", "Type");
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<CallDetailGridUnit> getContent() {
        return content;
    }


}
