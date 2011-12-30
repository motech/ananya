package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FrontLineWorker'")
public class FrontLineWorker extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "FrontLineWorker";
    @JsonProperty
    private String name;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private FLWStatus status;

    public FrontLineWorker() {
    }

    public FrontLineWorker(String msisdn) {
        this.msisdn = msisdn;
    }

    public String msisdn() {
        return msisdn;
    }

    public FrontLineWorker status(FLWStatus status) {
        this.status = status;
        return this;
    }

    public FLWStatus status() {
        return status;
    }

    public FrontLineWorker name(String name) {
        this.name = name;
        return this;
    }

    public String getMsisdn() {
        return msisdn;
    }
}
