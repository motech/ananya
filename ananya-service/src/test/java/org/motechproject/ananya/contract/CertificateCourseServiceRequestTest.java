package org.motechproject.ananya.contract;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CertificateCourseServiceRequestTest {

    @Test
    public void shouldReturnStateRequestListFromTransferDataList() {
        CertificateCourseServiceRequest courseServiceRequest = new CertificateCourseServiceRequest("callId", "callerId", "calledNumber").withJson(postedData());
        CertificateCourseStateRequestList stateRequestList = courseServiceRequest.getCertificateCourseStateRequestList();
        assertThat(stateRequestList.getCallId(), is("callId"));
        assertThat(stateRequestList.getCallerId(), is("callerId"));
        assertThat(stateRequestList.all().size(), is(2));
    }

    private String postedData() {
        String packet1 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";
        String packet2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String packet3 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a807e\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lesson\",\"courseItemState\":\"start\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";

        String packet4 = "{" +
                "   \"callEvent\" : \"DISCONNECT\"," +
                "   \"time\"  : 1231413" +
                "}";

        return "[" +
                "   {" +
                "       \"token\" : 0," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet1 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 1," +
                "       \"type\"  : \"ccState\", " +
                "       \"data\"  : " + packet2 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 2," +
                "       \"type\"  : \"ccState\", " +
                "       \"data\"  : " + packet3 +
                "   }," +
                "   {" +
                "       \"token\" : 3," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet4 +
                "   }]";
    }
}
