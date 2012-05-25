package org.motechproject.ananya.dataSources;

import org.motechproject.ananya.dataSources.mappers.FrontLineReportDataMapper;
import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.FrontLineWorkerDimensionService;
import org.motechproject.export.annotation.Report;
import org.motechproject.export.annotation.ReportGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ReportGroup(name = "FLW")
public class FlwDataSource {

    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private CourseItemMeasureService courseItemMeasureService;

    @Autowired
    public FlwDataSource(FrontLineWorkerDimensionService frontLineWorkerDimensionService, CourseItemMeasureService courseItemMeasureService) {
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.courseItemMeasureService = courseItemMeasureService;
    }

    @Report
    public List<FlwReportData> queryReport() {
        List<Long> allFrontLineWorkerMsisdnsBetween = courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(null, null);
        List<FrontLineWorkerDimension> filteredFLW = frontLineWorkerDimensionService.getFilteredFLW(allFrontLineWorkerMsisdnsBetween, null, null, null, null, null);

        ArrayList<FlwReportData> flwReportDatas = new ArrayList<FlwReportData>();
        FrontLineReportDataMapper frontLineReportDataMapper = new FrontLineReportDataMapper();
        for(FrontLineWorkerDimension frontLineWorkerDimension : filteredFLW) {
            flwReportDatas.add(frontLineReportDataMapper.mapFrom(frontLineWorkerDimension));
        }
        return flwReportDatas;
    }
}
