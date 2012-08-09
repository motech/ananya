package org.motechproject.ananya.domain.grid;

import org.motechproject.ananya.support.admin.domain.CallDetail;

import java.util.LinkedHashMap;
import java.util.List;

public class CallDetailGrid implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<CallDetail> content;

    public CallDetailGrid(List<CallDetail> content) {
        initHeaders();
        this.content = content;
    }

    private void initHeaders() {
        header = new LinkedHashMap<String, String>();
        header.put("callId", "Call Id");
        header.put("startTime", "Start Time");
        header.put("endTime", "End Time");
        header.put("duration", "Duration");
        header.put("calledNumber", "Called Number");
        header.put("type", "Type");
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<CallDetail> getContent() {
        return content;
    }


}
