package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.service.CertificateCourseLogService;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CertificateCourseItemSynchroniser implements Synchroniser {

    private CertificateCourseLogService certificateCourseLogService;
    private CourseItemMeasureService courseItemMeasureService;

    @Autowired
    public CertificateCourseItemSynchroniser(CertificateCourseLogService certificateCourseLogService,
                                             CourseItemMeasureService courseItemMeasureService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.courseItemMeasureService = courseItemMeasureService;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        SynchroniserLog synchroniserLog = new SynchroniserLog("CertificateCourseItem");
        List<CertificationCourseLog> courseLogs = certificateCourseLogService.getAll();
        for (CertificationCourseLog courseLog : courseLogs) {
            try {
                courseItemMeasureService.createCourseItemMeasure(courseLog.getCallId());
                synchroniserLog.add(courseLog.getCallId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(courseLog.getCallId(), "Error:" + e.getMessage());
            }
        }
        certificateCourseLogService.removeAll();
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.low;
    }
}
