package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateCourseDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateCourseDataHandler.class);
    private CourseItemMeasureService courseItemMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private CallDurationMeasureService callDurationMeasureService;

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService,
                                        RegistrationMeasureService registrationMeasureService,
                                        CallDurationMeasureService callDurationMeasureService) {
        this.courseItemMeasureService = courseItemMeasureService;
        this.registrationMeasureService = registrationMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY})
    public void handleCertificateCourseData(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();
            LOG.info("CallId is: " + callId);
            registrationMeasureService.createRegistrationMeasure((LogData)log);
            callDurationMeasureService.createCallDurationMeasure(callId);
            this.courseItemMeasureService.createCourseItemMeasure(callId);
        }
    }
}
