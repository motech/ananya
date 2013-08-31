package org.motechproject.ananya.repository.dimension;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@Transactional
public class AllFrontLineWorkerHistoryIT extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkerHistory allFrontLineWorkerHistory;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Test
    public void shouldConstructFlwHistory() {
        FrontLineWorkerDimension flwDimension = getFlwDimension();

        RegistrationMeasure registrationMeasure = getRegistrationMeasure(flwDimension);
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory(registrationMeasure);
        ReflectionToStringBuilder.setDefaultStyle(SHORT_PREFIX_STYLE);
        String flwHistory = ReflectionToStringBuilder.toStringExclude(frontLineWorkerHistory,
                asList("flwId", "guid", "timestamp", "locationId", "timeId"));
        assertEquals("FrontLineWorkerHistory[id=<null>,msisdn=911234567890,operator=operator," +
                "circle=circle,name=name,designation=AWW,status=status,verificationStatus=OTHER," +
                "alternateContactNumber=911234567899,callId=123222,isCurrent=true]", flwHistory);
        assertEquals(flwDimension.getId(), getField(frontLineWorkerHistory, "flwId"));
        assertEquals(flwDimension.getFlwId(), getField(frontLineWorkerHistory, "guid"));
        assertEquals(registrationMeasure.getLocationDimension().getId(), getField(frontLineWorkerHistory, "locationId"));
        assertEquals(registrationMeasure.getTimeDimension().getId(), getField(frontLineWorkerHistory, "timeId"));
        assertNotNull(getField(frontLineWorkerHistory, "timestamp"));
    }

    @Test
    public void shouldGetCurrentHistory() {
        FrontLineWorkerDimension flwDimension = getFlwDimension();
        FrontLineWorkerHistory oldFrontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(flwDimension));
        oldFrontLineWorkerHistory.markOld();
        allFrontLineWorkerHistory.createOrUpdate(oldFrontLineWorkerHistory);
        FrontLineWorkerHistory currentFrontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(flwDimension));
        allFrontLineWorkerHistory.createOrUpdate(currentFrontLineWorkerHistory);

        FrontLineWorkerHistory current = allFrontLineWorkerHistory.getCurrent(flwDimension.getId());

        assertTrue(reflectionEquals(current, currentFrontLineWorkerHistory));
    }

    @Test
    public void shouldCreateNewFrontLineWorkerHistory() {
        FrontLineWorkerDimension flwDimension = getFlwDimension();
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory(getRegistrationMeasure(flwDimension));

        allFrontLineWorkerHistory.createOrUpdate(frontLineWorkerHistory);

        FrontLineWorkerHistory current = allFrontLineWorkerHistory.getCurrent(flwDimension.getId());
        assertTrue(reflectionEquals(current, frontLineWorkerHistory));
    }

    private FrontLineWorkerDimension getFlwDimension() {
        return allFrontLineWorkerDimensions.createOrUpdate(911234567890L, 911234567899L, "operator", "circle", "name", Designation.AWW.name(), "status", UUID.randomUUID(), VerificationStatus.OTHER);
    }

    private RegistrationMeasure getRegistrationMeasure(FrontLineWorkerDimension frontLineWorkerDimension) {
        LocationDimension locationDimension = new LocationDimension("ZZZ999", "bihar", "Mandwa", "Algarh", "Gujarat", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(DateTime.now());
        return new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "123222");
    }

}
