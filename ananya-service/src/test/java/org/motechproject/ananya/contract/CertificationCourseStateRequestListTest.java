package org.motechproject.ananya.contract;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CertificationCourseStateRequestListTest {

    private CertificateCourseStateRequestList courseStateRequestList;

    @Before
    public void setUp() {
        courseStateRequestList = new CertificateCourseStateRequestList("123", "12345");
    }

    @Test
    public void shouldConvertJsonToCertificationCourseStateRequestAndAddToList() {

    	String language= "language";
        
    	String token1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String token2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a807e\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lesson\",\"courseItemState\":\"start\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";


        courseStateRequestList.add(token1, "1", language);
        courseStateRequestList.add(token2, "2", language);

        assertThat(courseStateRequestList.all().size(), is(2));
        assertTrue(courseStateRequestList.isNotEmpty());

        CertificateCourseStateRequest recentRequest = courseStateRequestList.lastRequest();
        assertThat(recentRequest.getContentName(), is("Chapter 1 Lesson 2"));
    }


    @Test
    public void shouldReturnTrueIfCourseCompletionKeyIfPresent() {

        String token1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String token2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a807e\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"start\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";

        String language= "language";
        
        courseStateRequestList.add(token1, "1", language);
        courseStateRequestList.add(token2, "2", language);

        assertTrue(courseStateRequestList.hasCourseCompletionInteraction());

    }
}
