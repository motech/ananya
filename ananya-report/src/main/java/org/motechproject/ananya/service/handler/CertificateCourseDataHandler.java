package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateCourseDataHandler {

    private CourseItemMeasureService courseItemMeasureService;
    private static final Logger logger = LoggerFactory.getLogger(CertificateCourseDataHandler.class);

    public CertificateCourseDataHandler() {
    }

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService) {
        this.courseItemMeasureService = courseItemMeasureService;
    }

    @MotechListener(subjects = {ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY})
    public void handleCertificateCourseData(MotechEvent event) {
        logger.info("Inside Certificate course data handler");
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();
            logger.info("Call Id is: " + callId);
            this.courseItemMeasureService.createCourseItemMeasure(callId);
        }
    }
}
