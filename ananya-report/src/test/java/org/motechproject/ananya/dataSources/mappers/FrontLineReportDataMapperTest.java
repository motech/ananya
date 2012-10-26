package org.motechproject.ananya.dataSources.mappers;

import org.junit.Test;
import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FrontLineReportDataMapperTest {
    @Test
    public void shouldMapFromFrontLineWorkerDimension() {
        FlwReportData flwReportData = new FrontLineReportDataMapper().mapFrom(new FrontLineWorkerDimension(1234L, "operator1", "circle1", "name1", "designation1", "status1", UUID.randomUUID()));
        assertEquals("1234", flwReportData.getMsisdn());
        assertEquals("name1", flwReportData.getName());
        assertEquals("designation1", flwReportData.getDesignation());
        assertEquals("status1", flwReportData.getStatus());
        assertEquals("operator1", flwReportData.getOperator());
        assertEquals("circle1", flwReportData.getCircle());
    }
}
