package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.service.CertificateCourseLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseItemMeasureAddAction {

    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private ReportDB reportDB;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseLogService certificateCourseLogService;

    public CourseItemMeasureAddAction() {}

    @Autowired
    public CourseItemMeasureAddAction(ReportDB reportDB,
                                      AllTimeDimensions allTimeDimensions,
                                      AllCourseItemDimensions allCourseItemDimensions,
                                      CertificateCourseLogService certificateCourseLogService) {
        this.reportDB = reportDB;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
    }

    public void process(String callId, CertificationCourseLog courseLog,
                        FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension) {
        if (courseLog == null) return;

        List<CertificationCourseLogItem> courseLogItems = courseLog.getCourseLogItems();
        if (courseLogItems == null || courseLogItems.isEmpty()) {
            certificateCourseLogService.remove(courseLog);
            return;
        }

        TimeDimension timeDimension = allTimeDimensions.getFor(courseLogItems.get(0).getTime());

        for (CertificationCourseLogItem logItem : courseLogItems) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(
                    logItem.getContentName(),
                    logItem.getContentType());

            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(
                    timeDimension,
                    courseItemDimension,
                    frontLineWorkerDimension,
                    locationDimension,
                    logItem.getTime(),
                    logItem.giveScore(),
                    logItem.getCourseItemState(),
                    callId);

            reportDB.add(courseItemMeasure);
        }

        certificateCourseLogService.remove(courseLog);
        log.info("Added Certificate CourseItemMeasures for CallId=" + callId);
    }
}
