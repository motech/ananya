package org.motechproject.ananya.request;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CertificationCourseStateRequestListTest {

    private CertificationCourseStateRequestList courseStateRequestList;

    @Before
    public void setUp() {
        courseStateRequestList = new CertificationCourseStateRequestList();
    }

    @Test
    public void shouldConvertJsonToCertificationCourseStateRequestAndAddToList() {

        String token1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String token2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a807e\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lesson\",\"courseItemState\":\"start\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"2012-03-08T12:55:18Z\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";


        courseStateRequestList.add("123456", "123", token1, "1");
        courseStateRequestList.add("123456", "123", token2, "2");

        assertThat(courseStateRequestList.all().size(), is(2));
        assertFalse(courseStateRequestList.isEmpty());

        CertificationCourseStateRequest recentRequest = courseStateRequestList.lastRequest();
        assertThat(recentRequest.getContentName(),is("Chapter 1 Lesson 2"));
    }
}
