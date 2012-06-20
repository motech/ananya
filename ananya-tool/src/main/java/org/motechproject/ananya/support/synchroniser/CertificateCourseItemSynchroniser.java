package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.service.CertificateCourseLogService;
import org.motechproject.ananya.service.measure.CourseContentMeasureService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CertificateCourseItemSynchroniser implements Synchroniser {

    private CertificateCourseLogService certificateCourseLogService;
    private CourseContentMeasureService courseContentMeasureService;

    @Autowired
    public CertificateCourseItemSynchroniser(CertificateCourseLogService certificateCourseLogService,
                                             CourseContentMeasureService courseContentMeasureService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.courseContentMeasureService = courseContentMeasureService;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("CertificateCourseItem");
        List<CertificationCourseLog> courseLogs = certificateCourseLogService.getAll();
        for (CertificationCourseLog courseLog : courseLogs) {
            try {
                courseContentMeasureService.createFor(courseLog.getCallId());
                synchroniserLog.add(courseLog.getCallId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(courseLog.getCallId(), "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.low;
    }
}
