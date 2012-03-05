package org.motechproject.ananya.functional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestUtils.TestUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class CertificationCourseLogFlowTest extends SpringIntegrationTest {

    private MyWebClient myWebClient;

    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;
    
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllCallLogCounters allCallLogCounters;

    @Autowired
    private AllLocations allLocations;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @Test
    public void shouldLogCertificationCourseAndSaveBookmarkAndScores() throws IOException {

        FrontLineWorker frontLineWorker = TestUtils.getSampleFLW();
        allFrontLineWorkers.add(frontLineWorker);

        String packet1 = "{" +
                "    \"chapterIndex\" : 1,                                     " +
                "    \"lessonOrQuestionIndex\" : 2,                            " +
                "    \"questionResponse\" : 1,                                 " +
                "    \"result\" : true,                                        " +
                "    \"interactionKey\" : \"playAnswerExplanation\",                " +

                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"contentType\" : \"lesson\",                             " +
                "    \"courseItemState\" : \"START\",                          " +
                "    \"contentData\" : 6,                                      " +
                "    \"certificateCourseId\" : \"e79139b5540bf3fc8d96635bc2926f90\"  " +
                "}";
        String packet2 = "{" +
                "   \"event\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";
        MyWebClient.PostParam callerId = param("callerId", "9986574000");
        String callId = "99865740001234567890";
        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam dataToPost = param("dataToPost",
                "[{\"token\":\"0\",\"type\":\"ccState\",\"data\":" + packet1 + "}]");

        myWebClient.post("http://localhost:9979/ananya/transferdata",callIdParam, callerId, dataToPost);

        CertificationCourseLog byCallId = allCertificateCourseLogs.findByCallId(callId);
        assertEquals(byCallId.getCourseLogItems().size(), 1);

        frontLineWorker = allFrontLineWorkers.get(frontLineWorker.getId());

        BookMark bookMark = frontLineWorker.bookMark();
        assertEquals((int)bookMark.getChapterIndex(), 1);
        assertEquals((int)bookMark.getLessonIndex(), 2);
        assertEquals(bookMark.getType(), "playAnswerExplanation");

        assertEquals(frontLineWorker.reportCard().scores().size(), 1);
        assertEquals(frontLineWorker.reportCard().scores().get(0).result(), true);


        CallLogCounter callLogCounter = allCallLogCounters.findByCallId(callId);

        markForDeletion(frontLineWorker);
        markForDeletion(byCallId);
        markForDeletion(callLogCounter);
    }

}
