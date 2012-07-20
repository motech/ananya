package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.request.CertificateCourseStateRequest;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScoreAction implements CourseAction {

    private static Logger log = LoggerFactory.getLogger(ScoreAction.class);

    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public ScoreAction(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @Override
    public void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        for (CertificateCourseStateRequest stateRequest : stateRequestList.all()) {
            CourseInteractionAction interactionAction = CourseInteractionAction.findFor(stateRequest.getInteractionKey());
            interactionAction.process(frontLineWorker, stateRequest);
        }
        frontLineWorkerService.updateCertificateCourseState(frontLineWorker);
        log.info(callId + "- updated scores for " + frontLineWorker);
    }
}
