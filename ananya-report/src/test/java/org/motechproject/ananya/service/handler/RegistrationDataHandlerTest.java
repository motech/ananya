package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationDataHandlerTest {

    private RegistrationDataHandler handler;
    @Mock
    private RegistrationMeasureService registrationMeasureService;



    @Before
    public void setUp() {
        initMocks(this);
        handler = new RegistrationDataHandler(registrationMeasureService);
    }

    @Test
    public void shouldFetchLogDataFromMotechEventAndPassToCallMapper() {
        LogData logData = new LogData(LogType.REGISTRATION, "1234");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistration(event);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(registrationMeasureService).createRegistrationMeasure(captor.capture());
        assertEquals(logData, captor.getValue());
    }

    @Test
    public void shouldFetchLogDataFromMotechAndPassToMapper() {
        LogData logData = new LogData(LogType.REGISTRATION_SAVE_NAME, "1234");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistrationCompletion(event);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(registrationMeasureService).updateRegistrationStatusAndName(captor.capture());
        assertEquals(logData, captor.getValue());
    }

}
