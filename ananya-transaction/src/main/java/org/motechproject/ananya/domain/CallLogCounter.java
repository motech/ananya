package org.motechproject.ananya.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;


@TypeDiscriminator("doc.type === 'CallLogCounter'")
public class CallLogCounter extends MotechBaseDataObject {

    @JsonProperty
    private String callId;

    @JsonProperty
    private Integer token;

    public String getCallId() {
        return callId;
    }

    public Integer getToken() {
        return token;
    }
    
    public void setToken(int token) {
        this.token = token;
    }
   
    public CallLogCounter() {
    }
    
    public CallLogCounter(String callId, Integer token) {
        this.callId = callId;
        this.token = token;
    }
}
