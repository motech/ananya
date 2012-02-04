package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.service.FrontLineWorkerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CallerDataControllerTest {
    @Mock
    private FrontLineWorkerService flwService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    private CallerDataController callerDataController;

    @Before
    public void setUp() {
        initMocks(this);
        callerDataController = new CallerDataController(flwService);
    }

    @Test
    public void shouldAddBookmark() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("callerId")).thenReturn("123");
        when(request.getParameter("bookmark.type")).thenReturn("lesson");
        when(request.getParameter("bookmark.chapterIndex")).thenReturn("0");
        when(request.getParameter("bookmark.lessonIndex")).thenReturn("1");

        callerDataController.addBookMark(request);
        verify(flwService).addBookMark(argThat(is("123")), argThat(is(new BookMark("lesson", "0", "1"))));
    }

    @Test
    public void shouldAddScore() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("callerId")).thenReturn("123");
        when(request.getParameter("quizResponse.chapterIndex")).thenReturn("0");
        when(request.getParameter("quizResponse.questionIndex")).thenReturn("5");
        when(request.getParameter("quizResponse.result")).thenReturn("true");

        ReportCard.Score expectedScore = new ReportCard.Score("0","5",true);
        callerDataController.addScore(request);
        
        verify(flwService).addScore(argThat(is("123")),argThat(is(expectedScore)));
    }
}
