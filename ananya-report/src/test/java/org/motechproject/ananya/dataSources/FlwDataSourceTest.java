package org.motechproject.ananya.dataSources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.FrontLineWorkerDimensionService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlwDataSourceTest {
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Mock
    private CourseItemMeasureService courseItemMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void ShouldGetReportData() {
        ArrayList<Long> emptyMsisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(null, null)).thenReturn(emptyMsisdnList);
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(1234L, "operator1", "circle1", "name1", "designation1", "status1"));
        when(frontLineWorkerDimensionService.getFilteredFLW(emptyMsisdnList, null, null, null, null, null)).thenReturn(frontLineWorkerDimensions);

        List<FlwReportData> flwReportDatas = new FlwDataSource(frontLineWorkerDimensionService, courseItemMeasureService).queryReport();
        assertEquals(1, flwReportDatas.size());
        assertEquals("1234", flwReportDatas.get(0).getMsisdn());
        assertEquals("name1", flwReportDatas.get(0).getName());
        assertEquals("designation1", flwReportDatas.get(0).getDesignation());
        assertEquals("operator1", flwReportDatas.get(0).getOperator());
        assertEquals("circle1", flwReportDatas.get(0).getCircle());
        assertEquals("status1", flwReportDatas.get(0).getStatus());
    }
}
