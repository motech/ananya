package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.motechproject.ananya.domain.CallEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.security.util.FieldUtils.getFieldValue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class CallDetailControllerTest {
    private CallDetailController controller;
    @Mock
    private CallDetailLoggerService callDetailLoggerService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CallDetailController(callDetailLoggerService);
    }

    @Test
    public void shouldSaveCallDetail() throws IllegalAccessException {
        String callerNo = "callerNo";
        String callNo = "callNo";
        String dataToPost = "data";
        when(request.getParameter("session.connection.remote.uri")).thenReturn(callerNo);
        when(request.getParameter("dataToPost")).thenReturn(dataToPost);

        controller.addCallDetailData(request);
        ArgumentCaptor<CallDetailLog> captor = ArgumentCaptor.forClass(CallDetailLog.class);
        verify(callDetailLoggerService).Save(captor.capture());
        CallDetailLog captured = captor.getValue();

        assertEquals(callerNo, getFieldValue(captured,"callerId"));
    }
}
