package org.motechproject.ananya.functional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.motechproject.ananya.repository.AllCertificationCourseLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class CertificationCourseLogFlowTest extends SpringIntegrationTest {

    private MyWebClient myWebClient;

    @Autowired
    private AllCertificationCourseLogs allCertificationCourseLogs;
    
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllCallLogCounters allCallLogCounters;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }
    @Test
    public void shouldLog() throws IOException {

        FrontLineWorker flw = new FrontLineWorker("9986574000", Designation.ANGANWADI, "S001B003", null);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);
        
        String packet1 = "{" +
                "    \"chapterIndex\" : 1,                                     " +
                "    \"lessonOrQuestionIndex\" : 2,                            " +
                "    \"questionResponse\" : 1,                                 " +
                "    \"result\" : true,                                        " +
                "    \"interactionKey\" : \"startNextChapter\",                " +

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
        MyWebClient.PostParam callId = param("callId", "99865740001234567890");
        MyWebClient.PostParam dataToPost = param("dataToPost",
                "[{\"token\":\"0\",\"type\":\"ccState\",\"data\":" + packet1 + "}]");

        myWebClient.post("http://localhost:9979/ananya/transferdata",callId, callerId, dataToPost);

        CertificationCourseLog byCallId = allCertificationCourseLogs.findByCallId("99865740001234567890");
        assertEquals(byCallId.getCourseLogItems().size(), 1);

    }

}
