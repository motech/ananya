package org.motechproject.bbcwt.domain;

import org.apache.log4j.Logger;
import org.ektorp.support.CouchDbDocument;

public class BaseCouchEntity extends CouchDbDocument {
    protected String documentType;

    public BaseCouchEntity() {
        this.documentType =  this.getClass().getSimpleName();
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}