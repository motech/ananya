package org.motechproject.ananya.service.integrationtests;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.handler.SMSSentDataHandler;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.annotations.MotechListenerAbstractProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class SMSSentDataHandlerIT extends SpringIntegrationTest {
    @Autowired
    SMSSentDataHandler handler;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    AllSMSReferences allSMSReferences;

    @Autowired
    AllLocationDimensions allLocationDimensions;

    @Autowired
    AllRegistrationMeasures allRegistrationMeasures;

    @After
    public void tearDown(){
        allFrontLineWorkers.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.flush();
    }

    @Test
    public void shouldBindToTheCorrectListener() throws NoSuchFieldException, IllegalAccessException {
        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY);

        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];

        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
        declaredField.setAccessible(true);
        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);
        assertEquals(1, listeners.size());
        assertEquals(SMSSentDataHandler.class,handler.getDeclaringClass());
        assertEquals("handleSMSSent",handler.getName());
    }

    @Test
    public void shouldLogSMSSentMeasure() {
        String msisdn = "9" + System.currentTimeMillis();
        FrontLineWorker flw = new FrontLineWorker(msisdn, "Rani",Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);

        flw.incrementCertificateCourseAttempts();
        allFrontLineWorkers.add(flw);

        DateTime now = DateTime.now();
        TimeDimension timeDimension= allTimeDimensions.addOrUpdate(now);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(msisdn), "airtel", "Rani", "ANGANWADI", "REGISTERED");

        LocationDimension locationDimension = new LocationDimension("", "", "", "");
        allLocationDimensions.add(locationDimension);
        allRegistrationMeasures.add(new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension));

        LogData logData = new LogData(LogType.SMS_SENT, msisdn);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleSMSSent(event);

        List<SMSSentMeasure> smsSentMeasures = template.loadAll(SMSSentMeasure.class);

        assertEquals(1, smsSentMeasures.size());
    }
}
