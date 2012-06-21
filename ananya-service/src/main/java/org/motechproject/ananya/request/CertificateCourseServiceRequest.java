package org.motechproject.ananya.request;

import org.motechproject.ananya.domain.TransferData;

public class CertificateCourseServiceRequest extends BaseServiceRequest {

    public CertificateCourseServiceRequest(String callId, String callerId, String calledNumber, String jsonData) {
        super(callId, callerId, calledNumber, jsonData);
    }

    public CertificateCourseStateRequestList getCertificateCourseStateRequestList() {
        CertificateCourseStateRequestList certificateCourseStateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isCCState())
                certificateCourseStateRequestList.add(transferData.getData(), transferData.getToken());
        return certificateCourseStateRequestList;
    }


}
