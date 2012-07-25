package org.motechproject.ananya.endtoend;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.CertificateCourseRequest;
import org.motechproject.ananya.framework.domain.CertificateCourseResponse;
import org.motechproject.ananya.framework.domain.CertificateCourseWebservice;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificateCourseEndToEndIT extends SpringIntegrationTest {

    @Autowired
    private CertificateCourseWebservice certificateCourseWebService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    private String callerId = "987654";
    private String operator = "airtel";
    private String callId = "2345678";
    private String circle = "circle";


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void after() {
        clearFLWData();
    }

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    @Test
    public void shouldReturnADefaultResponseForANewCaller() throws IOException {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, circle, callId);
        CertificateCourseResponse certificateCourseResponse = certificateCourseWebService.requestForCallerData(request);

        certificateCourseResponse.confirmPartiallyRegistered().confirmEmptyBookMark().confirmEmptyScores();

        couchDb.confirmFlwDoesNotExist(callerId);
        reportDb.confirmFlwDoesNotExist(callerId);
    }

    @Test
    public void shouldReturnValidResponseForExistingCaller() throws IOException {
        List<Score> scores = new ArrayList<Score>();
        scores.add(new Score("0", "0", true));
        scores.add(new Score("0", "1", true));

        Map<String, Integer> scoresMap = new HashMap<String, Integer>();
        scoresMap.put("0", 2);

        couchDb.createPartiallyRegisteredFlwFor(callerId, operator, circle).updateBookMark(callerId, 3, 4).updateScores(callerId, scores);
        reportDb.createMeasuresAndDimensionsForFlw(callerId, callId, operator, circle);

        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, circle, callId);
        CertificateCourseResponse certificateCourseResponse = certificateCourseWebService.requestForCallerData(request);
        certificateCourseResponse.confirmPartiallyRegistered().confirmBookMarkAt(3, 4).confirmScores(scoresMap);
    }


}
