package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseItemMeasureService {
    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseLogService certificateCourseLogService;
    private AllRegistrationMeasures allRegistrationMeasures;

    public CourseItemMeasureService() {
    }

    @Autowired
    public CourseItemMeasureService(ReportDB reportDB,
                                    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                    AllTimeDimensions allTimeDimensions,
                                    AllCourseItemDimensions allCourseItemDimensions,
                                    CertificateCourseLogService certificateCourseLogService,
                                    AllRegistrationMeasures allRegistrationMeasures) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createCourseItemMeasure(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(callId);
        if (courseLog == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(courseLog.callerIdAsLong());
        List<CertificationCourseLogItem> courseLogItems = courseLog.getCourseLogItems();

        for (CertificationCourseLogItem logItem : courseLogItems) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(
                    logItem.getContentName(),
                    logItem.getContentType());

            TimeDimension timeDimension = allTimeDimensions.getFor(logItem.getTime());
            RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
            LocationDimension locationDimension = registrationMeasure.getLocationDimension();
            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(
                    timeDimension,
                    courseItemDimension,
                    frontLineWorkerDimension,
                    locationDimension,
                    logItem.giveScore(),
                    logItem.getCourseItemState());

            reportDB.add(courseItemMeasure);
        }
        certificateCourseLogService.deleteCertificateCourseLogsFor(callId);
        log.info("Added CourseItemMeasures for CallId="+callId);
    }

}
