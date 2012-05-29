package org.motechproject.ananya.dataSources;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.dataSources.reportData.FlwReportData;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.FrontLineWorkerDimensionService;
import org.motechproject.export.annotation.ReportGroup;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlwDataSourceTest {
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    private FlwDataSource flwDataSource;

    @Before
    public void setUp() {
        initMocks(this);
        flwDataSource = new FlwDataSource(frontLineWorkerDimensionService, courseItemMeasureService);
    }

    @Test
    public void shouldVerifyThatItBelongsToLocationReportGroup() {
        assertEquals("FLW", flwDataSource.getClass().getAnnotation(ReportGroup.class).name());
    }

    @Test
    public void ShouldGetReportData() {
        long msisdn = 1234L;
        ArrayList<Long> emptyMsisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(null, null)).thenReturn(emptyMsisdnList);
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, "operator1", "circle1", "name1", "designation1", "status1"));
        when(frontLineWorkerDimensionService.getFilteredFLW(emptyMsisdnList, null, null, null, null, null, null)).thenReturn(frontLineWorkerDimensions);

        HashMap<String, String> criteria = new HashMap<String, String>();
        List<FlwReportData> flwReportDatas = flwDataSource.queryReport(criteria);
        assertEquals(1, flwReportDatas.size());
        assertEquals("1234", flwReportDatas.get(0).getMsisdn());
        assertEquals("name1", flwReportDatas.get(0).getName());
        assertEquals("designation1", flwReportDatas.get(0).getDesignation());
        assertEquals("operator1", flwReportDatas.get(0).getOperator());
        assertEquals("circle1", flwReportDatas.get(0).getCircle());
        assertEquals("status1", flwReportDatas.get(0).getStatus());
    }

    @Test
    public void ShouldGetReportDataIfThereIsNoCriteria() {
        long msisdn = 1234L;
        ArrayList<Long> emptyMsisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(null, null)).thenReturn(emptyMsisdnList);
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, "operator1", "circle1", "name1", "designation1", "status1"));
        when(frontLineWorkerDimensionService.getFilteredFLW(emptyMsisdnList, null, null, null, null, null, null)).thenReturn(frontLineWorkerDimensions);

        HashMap<String, String> criteria = null;
        List<FlwReportData> flwReportDatas = flwDataSource.queryReport(criteria);
        assertEquals(1, flwReportDatas.size());
        assertEquals("1234", flwReportDatas.get(0).getMsisdn());
        assertEquals("name1", flwReportDatas.get(0).getName());
        assertEquals("designation1", flwReportDatas.get(0).getDesignation());
        assertEquals("operator1", flwReportDatas.get(0).getOperator());
        assertEquals("circle1", flwReportDatas.get(0).getCircle());
        assertEquals("status1", flwReportDatas.get(0).getStatus());
    }

    @Test
    public void shouldGetFilteredFLWs() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String circle = "bihar";

        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("msisdn", msisdn.toString());
        criteria.put("name", name);
        criteria.put("status", status);
        criteria.put("designation", designation);
        criteria.put("operator", operator);
        criteria.put("circle", circle);

        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FlwReportData> filteredFLW = flwDataSource.queryReport(criteria);

        assertEquals(1, filteredFLW.size());
        assertEquals(msisdn.toString(), filteredFLW.get(0).getMsisdn());
    }

    @Test
    public void shouldGetFilteredFLWsBetweenStartDateAndEndDate() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String circle = "bihar";
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);

        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("msisdn", msisdn.toString());
        criteria.put("name", name);
        criteria.put("status", status);
        criteria.put("designation", designation);
        criteria.put("operator", operator);
        criteria.put("circle", circle);
        criteria.put("activityStartDate", activityStartDate.toString());
        criteria.put("activityEndDate", activityEndDate.toString());


        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(Arrays.asList(msisdn));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FlwReportData> filteredFLW = flwDataSource.queryReport(criteria);

        assertEquals(1, filteredFLW.size());
        assertEquals(msisdn.toString(), filteredFLW.get(0).getMsisdn());
    }

    @Test
    public void shouldNotApplyGeneralFiltersIfDateFilterReturnsNothing() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String circle = "bihar";
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);

        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("msisdn", msisdn.toString());
        criteria.put("name", name);
        criteria.put("status", status);
        criteria.put("designation", designation);
        criteria.put("operator", operator);
        criteria.put("circle", circle);
        criteria.put("activity-start-date", activityStartDate.toString());
        criteria.put("activity-end-date", activityEndDate.toString());

        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        ArrayList<Long> emptyMsisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(emptyMsisdnList);

        List<FlwReportData> filteredFLW = flwDataSource.queryReport(criteria);

        verifyZeroInteractions(frontLineWorkerDimensionService);
        assertEquals(0, filteredFLW.size());
    }

    @Test
    public void shouldNotApplyDateFiltersIfEitherOfTheDateFilterIsNotGiven() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.ANM.name();
        String operator = "airtel";
        String circle = "bihar";
        DateTime activityEndDate = DateTime.now().plusDays(1);

        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("msisdn", msisdn.toString());
        criteria.put("name", name);
        criteria.put("status", status);
        criteria.put("designation", designation);
        criteria.put("operator", operator);
        criteria.put("circle", circle);
        criteria.put("activity-end-date", activityEndDate.toString());

        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FlwReportData> filteredFLW = flwDataSource.queryReport(criteria);

        verifyZeroInteractions(courseItemMeasureService);
        assertEquals(1, filteredFLW.size());
    }
}