package org.motechproject.bbcwt.web;

import com.thoughtworks.selenium.SeleneseTestBase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.service.FrontLineWorkerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

        Assert.assertEquals(frontLineWorker.getBookmark(),new BookMark("lesson","0","1"));
    }
}
