package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.CertificateCourseLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseContentMeasureService {

    private static final Logger log = LoggerFactory.getLogger(CourseContentMeasureService.class);

    private AllCourseItemMeasures allCourseItemMeasures;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseLogService certificateCourseLogService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;

    public CourseContentMeasureService() {
    }

    @Autowired
    public CourseContentMeasureService(CertificateCourseLogService certificateCourseLogService,
                                       AllTimeDimensions allTimeDimensions,
                                       AllCourseItemDimensions allCourseItemDimensions,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                       AllRegistrationMeasures allRegistrationMeasures,
                                       AllCourseItemMeasures allCourseItemMeasures) {
        this.allCourseItemMeasures = allCourseItemMeasures;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createFor(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(callId);
        if (courseLog == null) {
            log.info(callId + "- courseLog not present");
            return;
        }

        if (courseLog.hasNoItems()) {
            log.info(callId + "- courseLog has no items");
            removeLog(callId, courseLog);
            return;
        }

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(courseLog.callerIdAsLong());
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        TimeDimension timeDimension = allTimeDimensions.getFor(courseLog.time());

        for (CertificationCourseLogItem logItem : courseLog.items()) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(
                    logItem.getContentName(),
                    logItem.getContentType());

            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(timeDimension, courseItemDimension,
                    frontLineWorkerDimension, locationDimension,
                    logItem.getTime(), logItem.giveScore(), logItem.getCourseItemState(), callId);

            allCourseItemMeasures.save(courseItemMeasure);
        }
        log.info(callId + "- courseLog courseItemMeasures added");
        removeLog(callId, courseLog);
    }

    private void removeLog(String callId, CertificationCourseLog courseLog) {
        certificateCourseLogService.remove(courseLog);
        log.info(callId + "- courseLog removed");
    }

}
