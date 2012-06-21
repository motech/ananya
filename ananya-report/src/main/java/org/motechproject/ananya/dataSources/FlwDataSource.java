package org.motechproject.ananya.dataSources;

import org.apache.commons.collections.ListUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.dataSources.mappers.FrontLineReportDataMapper;
import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.export.annotation.Report;
import org.motechproject.export.annotation.ReportGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@ReportGroup(name = "FRONTLINEWORKER")
public class FlwDataSource {

    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private CourseItemMeasureService courseItemMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;

    @Autowired
    public FlwDataSource(FrontLineWorkerDimensionService frontLineWorkerDimensionService, CourseItemMeasureService courseItemMeasureService, JobAidContentMeasureService jobAidContentMeasureService) {
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
    }

    @Report
    public List<FlwReportData> queryReport(HashMap<String, String> criteria) {
        if(criteria == null)
            criteria = new HashMap<String, String>();

        String activityStartDate = criteria.get("activitystartdate");
        String activityEndDate = criteria.get("activityenddate");
        String msisdn = criteria.get("msisdn");
        List<Long> allFilteredMsisdns = new ArrayList<Long>();

        if (activityStartDate != null && activityEndDate != null) {
            List<Long> filteredMsisdnsFromJobAid = jobAidContentMeasureService.getAllFrontLineWorkerMsisdnsBetween(DateTime.parse(activityStartDate).toDate(), DateTime.parse(activityEndDate).toDate());
            List<Long> filteredMsisdnsFromCertificateCourse = courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(DateTime.parse(activityStartDate).toDate(), DateTime.parse(activityEndDate).toDate());
            allFilteredMsisdns = ListUtils.union(filteredMsisdnsFromJobAid, filteredMsisdnsFromCertificateCourse);

            if (allFilteredMsisdns.isEmpty()) {
                return new ArrayList<FlwReportData>();
            }
        }

        Long msisdnInLong = null;
        if (msisdn != null) msisdnInLong = Long.parseLong(msisdn);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getFilteredFLW(allFilteredMsisdns, msisdnInLong, criteria.get("name"), criteria.get("status"), criteria.get("designation"), criteria.get("operator"), criteria.get("circle"));

        ArrayList<FlwReportData> flwReportDatas = new ArrayList<FlwReportData>();
        FrontLineReportDataMapper frontLineReportDataMapper = new FrontLineReportDataMapper();
        for(FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            flwReportDatas.add(frontLineReportDataMapper.mapFrom(frontLineWorkerDimension));
        }
        return flwReportDatas;
    }
}
