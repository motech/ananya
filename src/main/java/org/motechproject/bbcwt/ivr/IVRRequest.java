package org.motechproject.bbcwt.ivr;

import org.apache.commons.lang.StringUtils;

public class IVRRequest {
    private String sid;
    private String cid;
    private String event;
    private String data;

    public IVRRequest() {
    }

    public IVRRequest(String sid, String cid, String event, String data) {
        this.sid = sid;
        this.cid = cid;
        this.event = event;
        this.data = data;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public IVR.Event callEvent() {
        return IVR.Event.keyOf(this.event);
    }

    public boolean hasNoData() {
        return StringUtils.isBlank(this.data);
    }
}
