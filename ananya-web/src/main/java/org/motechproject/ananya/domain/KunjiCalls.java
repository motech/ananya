package org.motechproject.ananya.domain;

import java.util.LinkedHashMap;
import java.util.List;

public class KunjiCalls implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<AcademyKunjiCallContent> content;

    private KunjiCalls(List<AcademyKunjiCallContent> content) {
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

    public static KunjiCalls forContent(List<AcademyKunjiCallContent> content) {
        return new KunjiCalls(content);
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<AcademyKunjiCallContent> getContent() {
        return content;
    }
}
