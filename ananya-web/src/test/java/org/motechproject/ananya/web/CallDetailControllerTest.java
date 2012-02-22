package org.motechproject.ananya.web;

import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        DateTime dateTime = new DateTime(2012, 2, 22, 14, 53, 13);
        String dataToPost = "[{\"token\":\"0\",\"data\":{\"event\":\"CALL_START\",\"time\":1329902593000}}]";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn(dataToPost);

        controller.addCallDurationData(request);
        ArgumentCaptor<CallDetailLog> captor = ArgumentCaptor.forClass(CallDetailLog.class);
        verify(callDetailLoggerService).Save(captor.capture());
        CallDetailLog captured = captor.getValue();

        
        assertEquals(callerId, getFieldValue(captured,"callerId"));
        assertEquals(callId, getFieldValue(captured,"callId"));
        assertEquals(dateTime, getFieldValue(captured, "time"));
        assertEquals(CallEvent.CALL_START, getFieldValue(captured,"callEvent"));
    }
}
