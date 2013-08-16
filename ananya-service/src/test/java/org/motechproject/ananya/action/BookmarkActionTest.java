package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookmarkActionTest {

    private BookmarkAction bookmarkAction;

    @Before
    public void setUp() {
        bookmarkAction = new BookmarkAction();
    }

    @Test
    public void shouldUpdateBookmarkForFrontLineWorker() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");
        String json1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";
        String json2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";
        String language= "language";
        
        stateRequestList.add(json1, "1", language);
        stateRequestList.add(json2, "2", language);

        bookmarkAction.process(frontLineWorker, stateRequestList);

        BookMark bookMark = frontLineWorker.bookMark();
        assertThat(bookMark.getChapterIndex(), is(0));
        assertThat(bookMark.getLessonIndex(), is(1));
    }
}
