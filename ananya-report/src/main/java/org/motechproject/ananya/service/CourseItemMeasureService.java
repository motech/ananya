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

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseService certificateCourseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseItemMeasureService.class);
    
    @Autowired
    public CourseItemMeasureService(ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                    AllTimeDimensions allTimeDimensions, AllCourseItemDimensions allCourseItemDimensions,
                                    CertificateCourseService certificateCourseService) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseService = certificateCourseService;
    }

    public void createCourseItemMeasure(String callId) {
        CertificationCourseLog courseLog = certificateCourseService.getCertificateCourseLogFor(callId);

        if(courseLog == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(courseLog.getCallerId()),"","","");
        List<CertificationCourseLogItem> courseLogItems = courseLog.getCourseLogItems();
        for(CertificationCourseLogItem logItem:courseLogItems){
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getOrMakeFor(logItem.getContentName(), logItem.getContentId(), logItem.getContentType());
            TimeDimension timeDimension = allTimeDimensions.getFor(logItem.getTime());
            Integer score = courseItemContentIsNullOrEmpty(logItem) ? null :  Integer.valueOf(logItem.getContentData());
            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(timeDimension, courseItemDimension, frontLineWorkerDimension, score, logItem.getCourseItemState());
            reportDB.add(courseItemMeasure);
        }

        certificateCourseService.deleteCertificateCourseLogsFor(callId);
    }

    private boolean courseItemContentIsNullOrEmpty(CertificationCourseLogItem logItem) {
        return logItem.getContentData() == null || logItem.getContentData().isEmpty();
    }
}
