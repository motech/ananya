package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.contract.CertificateCourseStateRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.CertificateCourseLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseLogAction implements CourseAction {

    private static Logger log = LoggerFactory.getLogger(CourseLogAction.class);

    private CertificateCourseLogService certificateCourseLogService;

    @Autowired
    public CourseLogAction(CertificateCourseLogService certificateCourseLogService) {
        this.certificateCourseLogService = certificateCourseLogService;
    }

    @Override
    public void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        String callerId = stateRequestList.getCallerId();

        CertificationCourseLogMapper logMapper = new CertificationCourseLogMapper();
        CertificationCourseLogItemMapper logItemMapper = new CertificationCourseLogItemMapper();
        CertificationCourseLog courseLog = logMapper.mapFrom(stateRequestList.firstRequest());

        for (CertificateCourseStateRequest stateRequest : stateRequestList.all())
            if (stateRequest.hasContentId())
                courseLog.addCourseLogItem(logItemMapper.mapFrom(stateRequest));

        certificateCourseLogService.createNew(courseLog);
        log.info(callId + "- courseLog saved for " + callerId);
    }
}
    