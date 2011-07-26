package org.motechproject.bbcwt.domain;

import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'HealthWorker'")
public class HealthWorker extends CouchDbDocument {
    private String callerId;

    private String documentType;

    public HealthWorker() {
        this.documentType = this.getClass().getSimpleName();
    }

    public HealthWorker(String callerId) {
        this();
        this.callerId = callerId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
