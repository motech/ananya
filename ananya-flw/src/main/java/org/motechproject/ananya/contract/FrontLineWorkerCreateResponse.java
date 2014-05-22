package org.motechproject.ananya.contract;


import org.motechproject.ananya.domain.FrontLineWorker;

public class FrontLineWorkerCreateResponse {

    private FrontLineWorker frontLineWorker;
    private boolean isModified;

    public FrontLineWorkerCreateResponse(FrontLineWorker frontLineWorker, boolean isModified) {
        this.frontLineWorker = frontLineWorker;
        this.isModified = isModified;
    }

    public boolean isModified() {
        return isModified;
    }

    public FrontLineWorker getFrontLineWorker() {
        return frontLineWorker;
    }
}
