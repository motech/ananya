package org.motechproject.ananya.request;


import org.motechproject.ananya.domain.FrontLineWorker;

public class CertificateCourseStateFlwRequest {

    private FrontLineWorker frontLineWorker;
    private boolean sendSMS;

    public CertificateCourseStateFlwRequest(FrontLineWorker frontLineWorker, boolean sendSMS) {

        this.frontLineWorker = frontLineWorker;
        this.sendSMS = sendSMS;
    }

    public boolean shouldSendSMS() {
        return sendSMS;
    }

    public FrontLineWorker getFrontLineWorker() {
        return frontLineWorker;
    }
}
