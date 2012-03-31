package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseItemMeasureService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseLogService certificateCourseLogService;

    @Autowired
    public CourseItemMeasureService(ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                    AllTimeDimensions allTimeDimensions, AllCourseItemDimensions allCourseItemDimensions,
                                    CertificateCourseLogService certificateCourseLogService) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
    }

    public void createCourseItemMeasure(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getCertificateCourseLogFor(callId);
        if (courseLog == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(courseLog.callerIdAsLong());
        List<CertificationCourseLogItem> courseLogItems = courseLog.getCourseLogItems();

        for (CertificationCourseLogItem logItem : courseLogItems) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(
                    logItem.getContentName(),
                    logItem.getContentType());

            TimeDimension timeDimension = allTimeDimensions.getFor(logItem.getTime());

            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(
                    timeDimension,
                    courseItemDimension,
                    frontLineWorkerDimension,
                    logItem.giveScore(),
                    logItem.getCourseItemState());

            reportDB.add(courseItemMeasure);
        }
        certificateCourseLogService.deleteCertificateCourseLogsFor(callId);
        LOG.info("Added CourseItemMeasures for CallId="+callId);
    }

}
