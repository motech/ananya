package org.motechproject.ananya.domain;

import java.util.LinkedHashMap;
import java.util.List;

public class CallDetails implements DataGrid {
    private LinkedHashMap<String, String> header;
    private List<CallDetails.Content> content;

    private CallDetails(List<CallDetails.Content> content) {
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

    public static CallDetails forContent(List<Content> content) {
        return new CallDetails(content);
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }

    public List<CallDetails.Content> getContent() {
        return content;
    }

    public static class Content {
        private String name;
        private String msisdn;
        private String callId;
        private String startTime;
        private String endTime;
        private String duration;
        private String calledNumber;
        private String type;

        public Content(String name, String msisdn, String callId, String startTime, String endTime, String duration, String calledNumber, String type) {
            this.name = name;
            this.msisdn = msisdn;
            this.callId = callId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.calledNumber = calledNumber;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public String getCallId() {
            return callId;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getDuration() {
            return duration;
        }

        public String getCalledNumber() {
            return calledNumber;
        }

        public String getType() {
            return type;
        }
    }
}
