package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.mapper.CertificateCourseStateFlwRequestMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CertificateCourseService {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseService.class);

    private CertificateCourseLogService certificateCourseLogService;
    private FrontLineWorkerService frontLineWorkerService;
    private DataPublishService dataPublishService;

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    FrontLineWorkerService frontLineWorkerService,
                                    DataPublishService dataPublishService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.dataPublishService = dataPublishService;
    }

    public CertificateCourseCallerDataResponse createCallerData(String msisdn, String operator) {
        log.info("Creating caller data for msisdn: " + msisdn + " for operator " + operator);

        boolean isNewFLW = frontLineWorkerService.isNewFLW(msisdn);
        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, operator);

        if (isNewFLW) {
            dataPublishService.publishNewRegistration(msisdn);
        }
        return new CertificateCourseCallerDataResponse(
                frontLineWorker.bookMark().asJson(),
                frontLineWorker.status().isRegistered(),
                frontLineWorker.reportCard().scoresByChapterIndex());
    }

    public void saveState(CertificationCourseStateRequestList stateRequestList) {
        log.info("State Request List " + stateRequestList);
        if (stateRequestList.isEmpty()) return;

        CertificateCourseStateFlwRequestMapper flwRequestMapper = new CertificateCourseStateFlwRequestMapper();
        for (CertificationCourseStateRequest stateRequest : stateRequestList.all())
            frontLineWorkerService.saveScore(flwRequestMapper.mapFrom(stateRequest));

        saveBookmark(stateRequestList.recentRequest());
        saveCourseCertificateLog(stateRequestList);
    }

    private void saveBookmark(CertificationCourseStateRequest courseStateRequest) {
        final BookMark bookMark = new BookMark(courseStateRequest.getInteractionKey(),
                courseStateRequest.getChapterIndex(),
                courseStateRequest.getLessonOrQuestionIndex());

        log.info("Saving bookmark " + bookMark);
        frontLineWorkerService.addBookMark(courseStateRequest.getCallerId(), bookMark);
    }

    private void saveCourseCertificateLog(CertificationCourseStateRequestList stateRequestList) {
        CertificationCourseStateRequest courseStateRequest = stateRequestList.all().get(0);
        String callId = courseStateRequest.getCallId();
        CertificationCourseLogMapper logMapper = new CertificationCourseLogMapper();
        CertificationCourseLogItemMapper logItemMapper = new CertificationCourseLogItemMapper();

        CertificationCourseLog courseLogDocument = certificateCourseLogService.getCertificateCourseLogFor(callId);
        if (courseLogDocument == null)
            certificateCourseLogService.createNew(logMapper.mapFrom(courseStateRequest, callId));

        for (CertificationCourseStateRequest stateRequest : stateRequestList.all())
            if (stateRequest.hasContentId())
                courseLogDocument.addCourseLogItem(logItemMapper.mapFrom(stateRequest));

        certificateCourseLogService.update(courseLogDocument);
    }
}