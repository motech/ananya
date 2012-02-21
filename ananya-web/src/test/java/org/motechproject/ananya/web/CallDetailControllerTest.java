package org.motechproject.ananya.web;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallDurationData;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.motechproject.ananya.domain.CallEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        String callerId = "callerId";
        String callId = "callId";

        String dataToPost = "[{\"token\":\"0\",\"data\":{\"event\":\"CALL_START\",\"time\":\"Tue Feb 21 2012 11:13:44 GMT+0530 (IST)\"}}]";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn(dataToPost);

        controller.addCallDurationData(request);
        ArgumentCaptor<CallDetailLog> captor = ArgumentCaptor.forClass(CallDetailLog.class);
        verify(callDetailLoggerService).Save(captor.capture());
        CallDetailLog captured = captor.getValue();

        assertEquals(callerId, getFieldValue(captured,"callerId"));
        assertEquals(callId, getFieldValue(captured,"callId"));
        assertEquals("Tue Feb 21 2012 11:13:44 GMT+0530 (IST)", getFieldValue(captured,"time"));
        assertEquals(CallEvent.CALL_START, getFieldValue(captured,"callEvent"));
    }
}
