package org.motechproject.ananya.endtoend;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.EmptyBookmark;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.TestJsonData;
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
    @Autowired
    private TestJsonData testJsonData;

    private String callerId = "919987345645";
    private String callId = "919987345645-123456789";
    private String operator = "airtel";
    private String circle = "circle";
    private String langauge = "bhojpuri";
    private String calledNumber = "5771122";

    @Before
    @After
    public void setUp() {

        reportDb.clearFLWDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
        couchDb.clearAllLogs();
    }

    @Test
    public void shouldReturnADefaultResponseForANewCaller() throws IOException {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, circle, callId, calledNumber, langauge);
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

        couchDb.createPartiallyRegisteredFlwFor(callerId, operator, circle, langauge).updateBookMark(callerId, 3, 4).updateScores(callerId, scores);
        reportDb.createMeasuresAndDimensionsForFlw(callerId, callId, operator, circle);

        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, circle, callId, calledNumber, langauge);
        CertificateCourseResponse certificateCourseResponse = certificateCourseWebService.requestForCallerData(request);
        certificateCourseResponse.confirmPartiallyRegistered().confirmBookMarkAt(3, 4).confirmScores(scoresMap);
    }

    @Test
    public void shouldCreateLogsDimensionsMeasuresForDisconnectOfACallerFinishingCourse() throws IOException, InterruptedException {
        couchDb.createOperator(operator, 39 * 60 * 1000, 0, 60000);
        couchDb.createPartiallyRegisteredFlwFor(callerId, operator, circle, langauge).updateBookMark(callerId, 8, 3).updateScores(callerId, presetScores());
        couchDb.addANode("Chapter 9");
        reportDb.createMeasuresAndDimensionsForFlw(callerId, callId, operator, circle);

        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, circle, callId, calledNumber, langauge);
        request.setJsonPostData(testJsonData.forCourseDisconnect());
        certificateCourseWebService.requestForDisconnect(request);


        couchDb.confirmBookmarkUpdated(callerId, new EmptyBookmark());

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId)
                .confirmCallDurationMeasure(callId, callerId, "5771102")
                .confirmCourseItemMeasure(callId, callerId);

        couchDb.confirmNoRegistrationLogFor(callId)
                .confirmNoAudioTrackerLogFor(callId)
                .confirmNoCallLogFor(callId)
                .confirmNoCourseLogFor(callId);

        reportDb.clearCallDurationMeasure(callId)
                .clearCourseItemMeasure(callId);
    }

    private List<Score> presetScores() {
        List<Score> scores = new ArrayList<Score>();
        for (int chapIndex = 0; chapIndex < 9; chapIndex++)
            for (int lessonIndex = 0; lessonIndex < 4; lessonIndex++)
                scores.add(new Score(String.valueOf(chapIndex), String.valueOf(lessonIndex), true));
        return scores;
    }
}
