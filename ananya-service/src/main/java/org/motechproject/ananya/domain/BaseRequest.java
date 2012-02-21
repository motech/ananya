package org.motechproject.ananya.domain;

import org.motechproject.ananya.request.ILogBase;

/**
 * Created by IntelliJ IDEA.
 * User: imdadah
 * Date: 21/02/12
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseRequest implements ILogBase{
    private String callerId;
    private String calledNumber;

    protected BaseRequest(String callerId, String callerNumber) {
        this.callerId = callerId;
        this.calledNumber = callerNumber;
    }

    @Override
    public String callerId() {
        return this.callerId;
    }

    @Override
    public String calledNumber() {
        return this.calledNumber;
    }
}
