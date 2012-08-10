package org.motechproject.ananya.framework;

import org.joda.time.DateTime;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.*;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Repository
public class ReportDb {

    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    @Autowired
    private AllCourseItemMeasures allCourseItemMeasures;
    @Autowired
    private AllSMSSentMeasures allSMSSentMeasures;
    @Autowired
    private TestDataAccessTemplate template;
    @Autowired
    private AllJobAidContentMeasures allJobAidContentMeasures;
    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;
    @Autowired
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllCallDurationMeasures allCallDurationMeasures;


    public ReportDb confirmFLWDimensionForPartiallyRegistered(String callerId, String operator) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));

        assertNotNull(frontLineWorkerDimension);
        assertThat(frontLineWorkerDimension.getOperator(), is(operator));
        assertFalse(RegistrationStatus.valueOf(frontLineWorkerDimension.getStatus()).isRegistered());
        return this;
    }

    public ReportDb confirmRegistrationMeasureForPartiallyRegistered(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        Location defaultLocation = Location.getDefaultLocation();

        assertTrue(registrationMeasure.getTimeDimension().matches(new DateTime()));
        assertTrue(locationDimension.getBlock().equals(defaultLocation.getBlock()));
        assertTrue(locationDimension.getDistrict().equals(defaultLocation.getDistrict()));
        return this;
    }

    public ReportDb confirmCallDurationMeasure(String callId, String callerId, String calledNumber) {
        List<CallDurationMeasure> callDurationMeasures = allCallDurationMeasures.findByCallId(callId);
        for (CallDurationMeasure callDurationMeasure : callDurationMeasures) {
            assertEquals(Long.valueOf(callerId), callDurationMeasure.getFrontLineWorkerDimension().getMsisdn());
            assertEquals(Long.valueOf(calledNumber), callDurationMeasure.getCalledNumber());
        }
        return this;
    }

    public ReportDb confirmJobAidContentMeasure(String callId, String callerId, List<String> nodeNames) {
        List<JobAidContentMeasure> jobAidContentMeasures = allJobAidContentMeasures.findByCallId(callId);
        assertNotNull(jobAidContentMeasures);
        assertEquals(nodeNames.size(), jobAidContentMeasures.size());
        for (JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasures) {
            assertEquals(Long.valueOf(callerId), jobAidContentMeasure.getFrontLineWorkerDimension().getMsisdn());
            assertEquals((Integer) 100, jobAidContentMeasure.getPercentage());
            assertTrue(nodeNames.contains(jobAidContentMeasure.getJobAidContentDimension().getParent().getName()));
        }
        return this;
    }

    public ReportDb confirmCourseItemMeasure(String callId, String callerId) {
        List<CourseItemMeasure> courseItemMeasures = allCourseItemMeasures.fetchFor(callId);
        for (CourseItemMeasure courseItemMeasure : courseItemMeasures)
            assertEquals(Long.valueOf(callerId), courseItemMeasure.getFrontLineWorkerDimension().getMsisdn());
        return this;
    }

    public ReportDb confirmFlwDoesNotExist(String callerId) {
        assertNull(allFrontLineWorkerDimensions.fetchFor(new Long(callerId)));
        return this;
    }

    public ReportDb confirmSMSSent(String callerId, String smsReferenceNumber) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorkerDimension.getId());
        assertNotNull(smsSentMeasure);
        assertEquals(smsReferenceNumber, smsSentMeasure.getSmsReferenceNumber());
        return this;
    }

    public ReportDb clearFLWDimensionAndMeasures(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        if (frontLineWorkerDimension != null) {
            RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
            template.delete(registrationMeasure);
            template.delete(frontLineWorkerDimension);
        }
        return this;
    }

    public ReportDb clearCallDurationMeasure(String callId) {
        List<CallDurationMeasure> callDurationMeasures = allCallDurationMeasures.findByCallId(callId);
        for (CallDurationMeasure callDurationMeasure : callDurationMeasures)
            template.delete(callDurationMeasure);
        return this;
    }

    public ReportDb clearCourseItemMeasure(String callId) {
        List<CourseItemMeasure> courseItemMeasures = allCourseItemMeasures.fetchFor(callId);
        for (CourseItemMeasure courseItemMeasure : courseItemMeasures)
            template.delete(courseItemMeasure);
        return this;
    }

    public ReportDb clearJobAidMeasure(String callId) {
        List<JobAidContentMeasure> jobAidContentMeasures = allJobAidContentMeasures.findByCallId(callId);
        for (JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasures)
            template.delete(jobAidContentMeasure);
        return this;
    }

    public ReportDb clearSMSSentMeasure(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        SMSSentMeasure smsSentMeasure = allSMSSentMeasures.fetchFor(frontLineWorkerDimension.getId());
        template.delete(smsSentMeasure);
        return this;
    }

    public ReportDb createMeasuresAndDimensionsForFlw(String callerId, String callId, String operator, String circle) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(
                new Long(callerId), operator, circle, "", "ANM", "PARTIALLY_REGISTERED");
        LocationDimension locationDimension = allLocationDimensions.getFor(Location.getDefaultLocation().getExternalId());
        TimeDimension timeDimension = allTimeDimensions.getFor(DateTime.now());
        allRegistrationMeasures.createOrUpdate(new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId));
        return this;
    }
}
