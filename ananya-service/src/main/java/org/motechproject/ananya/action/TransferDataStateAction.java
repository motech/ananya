package org.motechproject.ananya.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataStates;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;


public enum TransferDataStateAction {

    CertificateCourseState(TransferDataStates.TYPE_CC_STATE) {
        @Override
        public void addToRequest(TransferData transferData) {
            certificationCourseStateRequestList.add(transferData.getData(), transferData.getToken());
        }
    },
    AudioTrackerState(TransferDataStates.TYPE_AUDIO_TRACKER) {
        @Override
        public void addToRequest(TransferData transferData) {
            audioTrackerRequestList.add(transferData.getData(), transferData.getToken());
        }
    },
    CallDurationState(TransferDataStates.TYPE_CALL_DURATION) {
        @Override
        public void addToRequest(TransferData transferData) {
            callDurationList.add(transferData.getData());
        }
    },
    Default("") {
        @Override
        public void addToRequest(TransferData transferData) {
            //Do Nothing
        }
    };

    private static CertificationCourseStateRequestList certificationCourseStateRequestList;
    private static AudioTrackerRequestList audioTrackerRequestList;
    private static CallDurationList callDurationList;

    private final String transferDataState;

    public abstract void addToRequest(TransferData transferData);

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

    public static void init(CertificationCourseStateRequestList certificationCourseStateRequestList,
                            AudioTrackerRequestList audioTrackerRequestList,
                            CallDurationList callDurationList) {

        TransferDataStateAction.certificationCourseStateRequestList = certificationCourseStateRequestList;
        TransferDataStateAction.audioTrackerRequestList = audioTrackerRequestList;
        TransferDataStateAction.callDurationList = callDurationList;
    }

    public static void init(AudioTrackerRequestList audioTrackerList,
                            CallDurationList callDurationList) {
        init(null, audioTrackerList, callDurationList);
    }
}
