package org.motechproject.ananya.seed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.service.SMSLogService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.CourseAudioTrackerMeasureService;
import org.motechproject.ananya.service.measure.CourseContentMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.transformers.CalledNumberTransformer;
import org.motechproject.deliverytools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalledNumberCorrectionSeed {

    private static final Logger log = LoggerFactory.getLogger(CalledNumberCorrectionSeed.class);

    private AllCallLogs allCallLogs;
    private CalledNumberTransformer calledNumberTransformer;
    private CallDurationMeasureService callDurationMeasureService;
    private CourseContentMeasureService courseContentMeasureService;
    private CourseAudioTrackerMeasureService courseAudioTrackerMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;
    private SMSLogService smsLogService;
    private SendSMSService sendSMSService;

    @Autowired
    public CalledNumberCorrectionSeed(AllCallLogs allCallLogs,
                                      CalledNumberTransformer calledNumberTransformer,
                                      CallDurationMeasureService callDurationMeasureService,
                                      CourseContentMeasureService courseContentMeasureService,
                                      CourseAudioTrackerMeasureService courseAudioTrackerMeasureService,
                                      JobAidContentMeasureService jobAidContentMeasureService,
                                      SMSLogService smsLogService,
                                      SendSMSService sendSMSService) {
        this.allCallLogs = allCallLogs;
        this.calledNumberTransformer = calledNumberTransformer;
        this.callDurationMeasureService = callDurationMeasureService;
        this.courseContentMeasureService = courseContentMeasureService;
        this.courseAudioTrackerMeasureService = courseAudioTrackerMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.smsLogService = smsLogService;
        this.sendSMSService = sendSMSService;
    }


    @Seed(priority = 0, version = "1.3", comment = "Move alpha-numeric calledNumber callLogs from couchdb to postgres")
    public void migrateInvalidLogsFromCouchToPostgres() {
        List<CallLog> callLogs = allCallLogs.getAll();

        for (CallLog callLog : callLogs) {
            String callId = callLog.getCallId();
            String callerId = callLog.getCallerId();
            String calledNumber = callLog.getCalledNumber();

            if (StringUtils.isNumeric(calledNumber)) continue;

            if (callLog.isForCourse()) {
                CertificateCourseServiceRequest request = new CertificateCourseServiceRequest(callId, callerId, calledNumber);
                calledNumberTransformer.transform(request);
                callLog.setCalledNumber(request.getCalledNumber());
                allCallLogs.update(callLog);

                callDurationMeasureService.createFor(callId);
                courseContentMeasureService.createFor(callId);
                courseAudioTrackerMeasureService.createFor(callId);

                SMSLog smslog = smsLogService.getSMSLogFor(callId);
                if (smslog != null) {
                    sendSMSService.buildAndSendSMS(smslog.getCallerId(), smslog.getLocationId(), smslog.getCourseAttempts(), smslog.getLanguage());
                    smsLogService.deleteFor(smslog);
                }
                log.info("Corrected calledNumber for Course: [" + calledNumber + "=>" + callLog.getCalledNumber() + "]");

            } else {
                JobAidServiceRequest request = new JobAidServiceRequest(callId, callerId, calledNumber);
                calledNumberTransformer.transform(request);
                callLog.setCalledNumber(request.getCalledNumber());
                allCallLogs.update(callLog);

                callDurationMeasureService.createFor(callId);
                jobAidContentMeasureService.createFor(callId);
                log.info("Corrected calledNumber for JobAid: [" + calledNumber + "=>" + callLog.getCalledNumber() + "]");
            }
        }
    }

}
