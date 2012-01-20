package org.motechproject.bbcwt.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.service.FrontLineWorkerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class BookmarkControllerTest {
    @Mock
    private FrontLineWorkerService flwService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    
    private BookmarkController bookmarkController;

    @Before
    public void setUp() {
        initMocks(this);
        bookmarkController = new BookmarkController(flwService);
    }

    @Test
    public void shouldAddBookmark() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session.connection.remote.uri")).thenReturn("123");
        when(request.getParameter("bookmark.type")).thenReturn("lesson");
        when(request.getParameter("bookmark.chapterIndex")).thenReturn("0");
        when(request.getParameter("bookmark.lessonIndex")).thenReturn("1");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        when(flwService.getFrontLineWorker("123")).thenReturn(frontLineWorker);

        bookmarkController.addBookMark(request);

        assertEquals(frontLineWorker.getBookmark(), new BookMark("lesson", "0", "1"));
    }

    @Test
    public void shouldRetrieveBookmarkWhenItExists() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session.connection.remote.uri")).thenReturn("123");

        FrontLineWorker workerWithBookmark = new FrontLineWorker();
        workerWithBookmark.addBookMark(new BookMark("lesson", "0", "1"));

        when(flwService.getFrontLineWorker("123")).thenReturn(workerWithBookmark);

        String expectedBookmark = "<bookmark><type>lesson</type><chapterIndex>0</chapterIndex><lessonIndex>1</lessonIndex></bookmark>";
        String actualBookmark = bookmarkController.getBookmark(request);

        assertEquals(expectedBookmark, actualBookmark);
    }

    @Test
    public void shouldReturnAnEmptyBookmarkTagWhenThereIsNoBookmarkForAValidUser() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session.connection.remote.uri")).thenReturn("123");

        FrontLineWorker workerWithoutBookmark = new FrontLineWorker();
        when(flwService.getFrontLineWorker("123")).thenReturn(workerWithoutBookmark);

        String expectedBookmark = "<bookmark/>";
        String actualBookmark = bookmarkController.getBookmark(request);

        assertEquals(expectedBookmark, actualBookmark);
    }

    @Test
    public void shouldReturnAnEmptyBookmarkTagForAnInvalidUser() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session.connection.remote.uri")).thenReturn("123");

        when(flwService.getFrontLineWorker("123")).thenReturn(null);

        String expectedBookmark = "<bookmark/>";
        String actualBookmark = bookmarkController.getBookmark(request);

        assertEquals(expectedBookmark, actualBookmark);
    }
}
