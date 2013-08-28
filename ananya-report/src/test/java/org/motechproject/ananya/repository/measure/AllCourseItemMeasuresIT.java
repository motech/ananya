package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.*;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.dimension.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AllCourseItemMeasuresIT extends SpringIntegrationTest {

    @Autowired
    private AllCourseItemMeasures allCourseItemMeasures;

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private AllLanguageDimension allLanguageDimension;


    private UUID flwId = UUID.randomUUID();
    private TimeDimension timeDimensionForYesterday;
    private LocationDimension locationDimension;
    private LanguageDimension languageDimension;
    private String locationId;
    private TimeDimension timeDimensionForTomorrow;
    private TimeDimension timeDimensionForToday;
    private DateTime now;
    private CourseItemDimension courseItemDimension;

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(LanguageDimension.class));
    }

    @Before
    public void setUp() {
        tearDown();
        locationId = "locationId";
        now = DateTime.now();
        locationDimension = new LocationDimension(locationId, "", "", "", "", "VALID");
        languageDimension = new LanguageDimension("bhojpuri", "bho", "badhai ho..");
        timeDimensionForYesterday = allTimeDimensions.makeFor(now.minusDays(1));
        timeDimensionForToday = allTimeDimensions.makeFor(now);
        timeDimensionForTomorrow = allTimeDimensions.makeFor(now.plusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allLanguageDimension.addOrUpdate(languageDimension);
        courseItemDimension = new CourseItemDimension("name", "contentId", CourseItemType.CHAPTER, null);
        allCourseItemDimensions.add(courseItemDimension);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), null, null, new LanguageDimension(), DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenCourseItemDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), null, new FrontLineWorkerDimension(), null, new LanguageDimension(), DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenTimeDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(null, new CourseItemDimension(), new FrontLineWorkerDimension(), null, new LanguageDimension(), DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenLanguageDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), new FrontLineWorkerDimension(), null, null, DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test
    public void shouldFetchBasedOnCourseItemDimensionAndFLWAndEvent() {
        CourseItemType chapter = CourseItemType.CHAPTER;
        Long msisdn = Long.valueOf("987654");
        String callId = "callId";
        CourseItemState event = CourseItemState.END;
        FrontLineWorkerDimension frontLineWorkerDimension = createFLW(msisdn);

        CourseItemMeasure courseItemMeasure = new CourseItemMeasure(timeDimensionForYesterday, courseItemDimension, frontLineWorkerDimension, locationDimension, languageDimension, now, 0, event, callId);
        allCourseItemMeasures.save(courseItemMeasure);

        List<CourseItemMeasure> measures = allCourseItemMeasures.fetchFor(callId);
        CourseItemMeasure measure = measures.get(0);
        assertEquals(event, measure.getEvent());
        assertEquals(courseItemDimension.getName(), measure.getCourseItemDimension().getName());
        assertEquals(chapter, measure.getCourseItemDimension().getType());
        assertEquals(msisdn, measure.getFrontLineWorkerDimension().getMsisdn());
        assertEquals(flwId, measure.getFrontLineWorkerDimension().getFlwId());
    }

    @Test
    public void shouldFetchAllFrontLineWorkerIdsBetweenADateRange() {
        Long msisdn1 = Long.valueOf("987654");
        Long msisdn2 = Long.valueOf("123456");
        String callId = "callId";
        CourseItemState event = CourseItemState.END;

        FrontLineWorkerDimension frontLineWorkerDimension1 = createFLW(msisdn1);
        CourseItemMeasure courseItemMeasureForYesterday = new CourseItemMeasure(timeDimensionForYesterday, courseItemDimension, frontLineWorkerDimension1, locationDimension, languageDimension, now, 0, event, callId);

        FrontLineWorkerDimension frontLineWorkerDimension2 = allFrontLineWorkerDimensions.createOrUpdate(msisdn2, null, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        allLocationDimensions.saveOrUpdate(locationDimension);
        CourseItemMeasure courseItemMeasureForToday = new CourseItemMeasure(timeDimensionForToday, courseItemDimension, frontLineWorkerDimension2, locationDimension, languageDimension, now, 0, event, callId);

        CourseItemMeasure courseItemMeasureForTomorrow = new CourseItemMeasure(timeDimensionForTomorrow, courseItemDimension, frontLineWorkerDimension2, locationDimension, languageDimension, now, 0, event, callId);

        allCourseItemMeasures.save(courseItemMeasureForYesterday);
        allCourseItemMeasures.save(courseItemMeasureForToday);
        allCourseItemMeasures.save(courseItemMeasureForTomorrow);

        List<Long> filteredFrontLineWorkerIds = allCourseItemMeasures.getFilteredFrontLineWorkerMsisdns(now.toDate(), now.plusDays(1).toDate());

        assertEquals(1, filteredFrontLineWorkerIds.size());
        assertEquals(msisdn2, filteredFrontLineWorkerIds.get(0));
    }

    @Test
    public void shouldFetchAllCourseItemMeasuresForACallerId() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = createFLW(callerId);
        CourseItemMeasure actualCourseItemMeasure = new CourseItemMeasure("callId", timeDimensionForYesterday, courseItemDimension, frontLineWorkerDimension, locationDimension, languageDimension, now, 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure);

        List<CourseItemMeasure> courseItemMeasureFromDb = allCourseItemMeasures.findByCallerId(callerId);

        assertEquals(callerId, courseItemMeasureFromDb.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldFetchForAGivenLocation() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = createFLW(callerId);

        CourseItemMeasure actualCourseItemMeasure = new CourseItemMeasure("callId", timeDimensionForYesterday, courseItemDimension, frontLineWorkerDimension, locationDimension, languageDimension, now, 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure);

        List<CourseItemMeasure> courseItemMeasureList = allCourseItemMeasures.findByLocationId(locationId);

        assertEquals(1, courseItemMeasureList.size());
        assertEquals(callerId, courseItemMeasureList.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    private FrontLineWorkerDimension createFLW(Long callerId) {
        return allFrontLineWorkerDimensions.createOrUpdate(callerId, null, "operator", "circle", "name", "ASHA", "REGISTERED", flwId,
                null);
    }

    @Test
    public void shouldTransferRecords() {
        FrontLineWorkerDimension flw1 = createFLW(123L);
        FrontLineWorkerDimension flw2 = createFLW(1233L);
        CourseItemMeasure actualCourseItemMeasure1 = new CourseItemMeasure("callId1", timeDimensionForYesterday, courseItemDimension, flw1, locationDimension, languageDimension, now, 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure1);
        CourseItemMeasure actualCourseItemMeasure2 = new CourseItemMeasure("callId2", timeDimensionForYesterday, courseItemDimension, flw2, locationDimension, languageDimension, now, 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure2);

        allCourseItemMeasures.transfer(CourseItemMeasure.class, flw1.getId(), flw2.getId());
        List<CourseItemMeasure> courseItemMeasuresByCallId2 = allCourseItemMeasures.fetchFor("callId2");
        List<CourseItemMeasure> courseItemMeasuresByCallId1 = allCourseItemMeasures.fetchFor("callId1");
        assertEquals(1, courseItemMeasuresByCallId1.size());
        assertEquals(1, courseItemMeasuresByCallId2.size());
        assertEquals(courseItemMeasuresByCallId1.get(0).getFlwId(), flw2.getId());
        assertEquals(courseItemMeasuresByCallId2.get(0).getFlwId(), flw2.getId());
    }
}
