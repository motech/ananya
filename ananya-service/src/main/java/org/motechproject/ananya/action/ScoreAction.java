package org.motechproject.ananya.action;

import org.motechproject.ananya.contract.CertificateCourseStateRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScoreAction implements CourseAction {

    private static Logger log = LoggerFactory.getLogger(ScoreAction.class);

    @Override
    public void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        for (CertificateCourseStateRequest stateRequest : stateRequestList.all()) {
            ScoreActionInteraction scoreActionInteraction = ScoreActionInteraction.findFor(stateRequest.getInteractionKey());
            scoreActionInteraction.process(frontLineWorker, stateRequest);
        }
        log.info(callId + "- updated scores for " + frontLineWorker.getMsisdn());
    }
}
