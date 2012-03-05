package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.ReportPublisherService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateCourseDataHandler {

    private CourseItemMeasureService courseItemMeasureService;

    public CertificateCourseDataHandler() {
    }

    @Autowired
    public CertificateCourseDataHandler(CourseItemMeasureService courseItemMeasureService) {
        this.courseItemMeasureService = courseItemMeasureService;
    }

    @MotechListener(subjects = {ReportPublisherService.SEND_CALL_DURATION_DATA_KEY})
    public void handleCertificateCourseDataDuration(MotechEvent event) {
        for (Object log : event.getParameters().values()) {
            String callId = ((LogData) log).getDataId();
            this.courseItemMeasureService.createCourseItemMeasure(callId);
        }
    }
}
