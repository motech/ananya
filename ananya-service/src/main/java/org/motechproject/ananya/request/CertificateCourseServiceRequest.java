package org.motechproject.ananya.request;

import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;

public class CertificateCourseServiceRequest extends BaseServiceRequest {

    public CertificateCourseServiceRequest(String callId, String callerId, String calledNumber) {
        super(callId, callerId, calledNumber);
    }

    public CertificateCourseServiceRequest(String callId, String callerId) {
        super(callId, callerId);
    }

    public CertificateCourseStateRequestList getCertificateCourseStateRequestList() {
        CertificateCourseStateRequestList certificateCourseStateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isCCState())
                certificateCourseStateRequestList.add(transferData.getData(), transferData.getToken());
        return certificateCourseStateRequestList;
    }

    public CertificateCourseServiceRequest withJson(String json) {
        this.transferDataList = new TransferDataList(json);
        return this;
    }

    public CertificateCourseServiceRequest withCircle(String circle) {
        this.circle = circle;
        return this;
    }

    public CertificateCourseServiceRequest withOperator(String operator) {
        this.operator = operator;
        return this;
    }

}
