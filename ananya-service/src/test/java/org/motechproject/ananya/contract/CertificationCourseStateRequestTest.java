package org.motechproject.ananya.contract;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CertificationCourseStateRequestTest {

    @Test
    public void shouldMakeNewCertificationCourseRequestObjectFromJson() {

        String jsonString =
               "{" +
               "    \"chapterIndex\" : 1,                                     " +
               "    \"lessonOrQuestionIndex\" : 2,                            " +
               "    \"questionResponse\" : 1,                                 " +
               "    \"result\" : true,                                        " +
               "    \"interactionKey\" : \"startNextChapter\",                " +

               "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
               "    \"contentType\" : \"lesson\",                             " +
               "    \"courseItemState\" : \"start\",                          " +
               "    \"contentData\" : 6,                                      " +
               "    \"certificateCourseId\" : \"e79139b5540bf3fc8d96635bc2926f90\"  " +
               "}";

        String callerId = "555";
        String callId = "555:123";
        String language= "language";
        String dataToken = "1";

        CertificateCourseStateRequest certificationCourseStateRequest =
                CertificateCourseStateRequest.createFrom(callerId, callId, dataToken, jsonString, language);

        assertEquals(certificationCourseStateRequest.callerId, callerId);
        assertEquals(certificationCourseStateRequest.callId, callId);
        assertEquals(certificationCourseStateRequest.token, dataToken);
        assertEquals((int)certificationCourseStateRequest.getChapterIndex(), 1);
        assertEquals((int)certificationCourseStateRequest.getLessonOrQuestionIndex(), 2);
        assertEquals((boolean)certificationCourseStateRequest.result(), true);
        assertEquals(certificationCourseStateRequest.getContentId(), "e79139b5540bf3fc8d96635bc2926f90");
        assertEquals(certificationCourseStateRequest.getContentType(), "lesson");
        assertEquals(certificationCourseStateRequest.getCourseItemState(), "start");
        assertEquals(certificationCourseStateRequest.getContentData(), "6");
        assertEquals(certificationCourseStateRequest.getCertificateCourseId(), "e79139b5540bf3fc8d96635bc2926f90");
    }
}
