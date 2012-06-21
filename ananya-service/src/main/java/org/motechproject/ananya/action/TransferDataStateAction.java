package org.motechproject.ananya.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataState;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;


public enum TransferDataStateAction {

    CertificateCourseState(TransferDataState.TYPE_CC_STATE) {
        @Override
        public void addToRequest(TransferData transferData,
                                 CertificateCourseStateRequestList certificationCourseStateRequestList,
                                 AudioTrackerRequestList audioTrackerRequestList,
                                 CallDurationList callDurationList) {
            certificationCourseStateRequestList.add(transferData.getData(), transferData.getToken());
        }
    },
    AudioTrackerState(TransferDataState.TYPE_AUDIO_TRACKER) {
        @Override
        public void addToRequest(TransferData transferData,
                                 CertificateCourseStateRequestList certificationCourseStateRequestList,
                                 AudioTrackerRequestList audioTrackerRequestList,
                                 CallDurationList callDurationList) {
            audioTrackerRequestList.add(transferData.getData(), transferData.getToken());
        }
    },
    CallDurationState(TransferDataState.TYPE_CALL_DURATION) {
        @Override
        public void addToRequest(TransferData transferData,
                                 CertificateCourseStateRequestList certificationCourseStateRequestList,
                                 AudioTrackerRequestList audioTrackerRequestList,
                                 CallDurationList callDurationList) {
            callDurationList.add(transferData.getData());
        }
    },
    Default("") {
        @Override
        public void addToRequest(TransferData transferData,
                                 CertificateCourseStateRequestList certificationCourseStateRequestList,
                                 AudioTrackerRequestList audioTrackerRequestList,
                                 CallDurationList callDurationList) {
            //Do Nothing
        }
    };

    private final String transferDataState;

    public abstract void addToRequest(TransferData transferData,
                                      CertificateCourseStateRequestList certificationCourseStateRequestList,
                                      AudioTrackerRequestList audioTrackerRequestList,
                                      CallDurationList callDurationList);

    public void addToRequest(TransferData transferData,
                             AudioTrackerRequestList audioTrackerRequestList,
                             CallDurationList callDurationList) {
        addToRequest(transferData, null, audioTrackerRequestList, callDurationList);
    }

    private TransferDataStateAction(String transferDataState) {
        this.transferDataState = transferDataState;
    }

    public static TransferDataStateAction getFor(String transferDataState) {
        for (TransferDataStateAction state : TransferDataStateAction.values()) {
            if (StringUtils.equalsIgnoreCase(state.transferDataState, transferDataState))
                return state;
        }
        return Default;
    }
}
