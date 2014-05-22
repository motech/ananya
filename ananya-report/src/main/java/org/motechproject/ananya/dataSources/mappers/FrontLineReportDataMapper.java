package org.motechproject.ananya.dataSources.mappers;

import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;

public class FrontLineReportDataMapper {

    public FlwReportData mapFrom(FrontLineWorkerDimension frontLineWorkerDimension) {
        FlwReportData flwReportData = new FlwReportData(frontLineWorkerDimension.getMsisdn(), frontLineWorkerDimension.getName(), frontLineWorkerDimension.getStatus(), frontLineWorkerDimension.getDesignation(), frontLineWorkerDimension.getOperator(), frontLineWorkerDimension.getCircle());
        return flwReportData;
    }
}
