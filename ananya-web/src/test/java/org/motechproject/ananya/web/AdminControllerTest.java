package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.page.InquiryPage;
import org.motechproject.ananya.domain.page.LoginPage;
import org.motechproject.ananya.domain.page.LogsPage;
import org.motechproject.ananya.domain.page.MonitorPage;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdminControllerTest {

    private AdminController controller;
    @Mock
    private MonitorPage monitorPage;
    @Mock
    private LoginPage loginPage;
    @Mock
    private InquiryPage inquiryPage;
    @Mock
    private LogsPage logsPage;
    @Mock
    private FileInputStream mockFileInputStream;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new AdminController(monitorPage, loginPage, inquiryPage, logsPage);
    }

    @Test
    public void shouldCallMonitorPageToDisplayResults() throws Exception {
        ModelAndView modelAndView = mock(ModelAndView.class);
        when(monitorPage.display()).thenReturn(modelAndView);

        ModelAndView actualView = controller.showMonitorPage();
        assertEquals(modelAndView, actualView);
        verify(monitorPage).display();
    }

    @Test
    public void shouldCallInquiryPageToDisplay() throws Exception {
        ModelAndView modelAndView = mock(ModelAndView.class);
        when(inquiryPage.display()).thenReturn(modelAndView);

        ModelAndView actualView = controller.showInquiryPage();
        assertEquals(modelAndView, actualView);
        verify(inquiryPage).display();
    }

    @Test
    public void shouldCallLoginPageToDisplay() throws Exception {
        String errorMsg = "error message";
        HttpServletRequest request = mock(HttpServletRequest.class);
        ModelAndView modelAndView = mock(ModelAndView.class);

        when(request.getParameter("login_error")).thenReturn(errorMsg);
        when(loginPage.display(errorMsg)).thenReturn(modelAndView);

        ModelAndView actualView = controller.login(request);

        assertEquals(modelAndView, actualView);
        verify(loginPage).display(errorMsg);
    }

    @Test
    public void shouldCallDisplayPageToDisplayResultsForACallerId() throws Exception {
        String msisdn = "9986574410";
        Map<String, Object> map = new HashMap<String, Object>();
        when(inquiryPage.display(msisdn)).thenReturn(map);

        Map<String, Object> resultMap = controller.showInquiryPage(msisdn);

        verify(inquiryPage).display(msisdn);
        assertSame(map, resultMap);
    }

    @Test
    public void shouldCallLogsPageToDisplay() throws Exception {
        ModelAndView expectedModelAndView = new ModelAndView();
        when(logsPage.display()).thenReturn(expectedModelAndView);

        ModelAndView modelAndView = controller.showLogs();

        verify(logsPage).display();
        assertSame(expectedModelAndView, modelAndView);
    }
}
