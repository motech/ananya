package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FrontLineWorker'")
public class FrontLineWorker extends MotechBaseDataObject {
    @JsonProperty
    private String name;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private BookMark bookmark;
    @JsonProperty
    private FrontLineWorkerStatus status = FrontLineWorkerStatus.UNREGISTERED;

    public FrontLineWorker() {
    }

    public FrontLineWorker(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public BookMark getBookmark() {
        return bookmark;
    }

    public FrontLineWorker status(FrontLineWorkerStatus status) {
        this.status = status;
        return this;
    }

    public FrontLineWorkerStatus status() {
        return status;
    }

    public FrontLineWorker name(String name) {
        this.name = name;
        return this;
    }

    public void addBookMark(BookMark bookMark) {
        this.bookmark = bookMark;
    }
}
