package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AllCourseItemMeasuresIT extends SpringIntegrationTest {

    @Autowired
    AllCourseItemMeasures allCourseItemMeasures;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllCourseItemDimensions allCourseItemDimensions;

    @Autowired
    AllLocationDimensions allLocationDimensions;

    private UUID flwId = UUID.randomUUID();

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), null, null, DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenCourseItemDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(null, new CourseItemDimension(), new FrontLineWorkerDimension(), null, DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenTimeDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), null, new FrontLineWorkerDimension(), null, DateTime.now(), 2, CourseItemState.START, "callId"));
    }

    @Test
    public void shouldFetchBasedOnCourseItemDimensionAndFLWAndEvent() {
        CourseItemType chapter = CourseItemType.CHAPTER;
        Long msisdn = Long.valueOf("987654");
        String callId = "callId";
        CourseItemState event = CourseItemState.END;
        String courseItemDimensionName = "name" + DateTime.now();

        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "", "VALID");
        CourseItemDimension courseItemDimension = new CourseItemDimension(courseItemDimensionName, "contentId", chapter, null);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(msisdn, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);

        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        allCourseItemDimensions.add(courseItemDimension);
        allLocationDimensions.saveOrUpdate(locationDimension);


        CourseItemMeasure courseItemMeasure = new CourseItemMeasure(timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, DateTime.now(), 0, event,callId);
        allCourseItemMeasures.save(courseItemMeasure);

        List<CourseItemMeasure> measures = allCourseItemMeasures.fetchFor(callId);
        CourseItemMeasure measure = measures.get(0);
        assertEquals(event, measure.getEvent());
        assertEquals(courseItemDimensionName, measure.getCourseItemDimension().getName());
        assertEquals(chapter, measure.getCourseItemDimension().getType());
        assertEquals(msisdn, measure.getFrontLineWorkerDimension().getMsisdn());
        assertEquals(flwId, measure.getFrontLineWorkerDimension().getFlwId());
    }

    @Test
    public void shouldFetchAllFrontLineWorkerIdsBetweenADateRange() {
        CourseItemType chapter = CourseItemType.CHAPTER;
        Long msisdn1 = Long.valueOf("987654");
        Long msisdn2 = Long.valueOf("123456");
        String callId = "callId";
        CourseItemState event = CourseItemState.END;
        DateTime today = DateTime.now();
        DateTime tomorrow = DateTime.now().plusDays(1);
        String courseItemDimensionName = "name" + DateTime.now();

        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "", "VALID");
        CourseItemDimension courseItemDimension = new CourseItemDimension(courseItemDimensionName, "contentId", chapter, null);
        FrontLineWorkerDimension frontLineWorkerDimension1 = allFrontLineWorkerDimensions.createOrUpdate(msisdn1, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        TimeDimension timeDimensionForYesterday = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allCourseItemDimensions.add(courseItemDimension);
        allLocationDimensions.saveOrUpdate(locationDimension);
        CourseItemMeasure courseItemMeasureForYesterday = new CourseItemMeasure(timeDimensionForYesterday, courseItemDimension, frontLineWorkerDimension1, locationDimension, DateTime.now(), 0, event,callId);

        FrontLineWorkerDimension frontLineWorkerDimension2 = allFrontLineWorkerDimensions.createOrUpdate(msisdn2, "operator", "circle", "name", "ASHA", "REGISTERED", UUID.randomUUID(), null);
        TimeDimension timeDimensionForToday = allTimeDimensions.makeFor(today);
        allLocationDimensions.saveOrUpdate(locationDimension);
        CourseItemMeasure courseItemMeasureForToday = new CourseItemMeasure(timeDimensionForToday, courseItemDimension, frontLineWorkerDimension2, locationDimension, DateTime.now(), 0, event,callId);

        TimeDimension timeDimensionForTomorrow = allTimeDimensions.makeFor(tomorrow);
        CourseItemMeasure courseItemMeasureForTomorrow = new CourseItemMeasure(timeDimensionForTomorrow, courseItemDimension, frontLineWorkerDimension2, locationDimension, DateTime.now(), 0, event,callId);

        allCourseItemMeasures.save(courseItemMeasureForYesterday);
        allCourseItemMeasures.save(courseItemMeasureForToday);
        allCourseItemMeasures.save(courseItemMeasureForTomorrow);

        List<Long> filteredFrontLineWorkerIds = allCourseItemMeasures.getFilteredFrontLineWorkerMsisdns(today.toDate(), DateTime.now().plusDays(1).toDate());

        assertEquals(1, filteredFrontLineWorkerIds.size());
        assertEquals(msisdn2, filteredFrontLineWorkerIds.get(0));
    }

    @Test
    public void shouldFetchAllCourseItemMeasuresForACallerId() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId, null);
        CourseItemDimension courseItemDimension = new CourseItemDimension("name", "contentId", CourseItemType.CHAPTER, null);
        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "", "VALID");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCourseItemDimensions.add(courseItemDimension);
        CourseItemMeasure actualCourseItemMeasure = new CourseItemMeasure("callId", timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, DateTime.now(), 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure);

        List<CourseItemMeasure> courseItemMeasureFromDb = allCourseItemMeasures.findByCallerId(callerId);

        assertEquals(callerId, courseItemMeasureFromDb.get(0).getFrontLineWorkerDimension().getMsisdn());
    }

    @Test
    public void shouldFetchForAGivenLocation() {
        Long callerId = 1234L;
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(callerId, "operator", "circle", "name", "ASHA", "REGISTERED", flwId,
                null);
        CourseItemDimension courseItemDimension = new CourseItemDimension("name", "contentId", CourseItemType.CHAPTER, null);
        String locationId = "locationId";
        LocationDimension locationDimension = new LocationDimension(locationId, "", "", "", "VALID");
        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now().minusDays(1));
        allLocationDimensions.saveOrUpdate(locationDimension);
        allCourseItemDimensions.add(courseItemDimension);
        CourseItemMeasure actualCourseItemMeasure = new CourseItemMeasure("callId", timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, DateTime.now(), 20, 20);
        allCourseItemMeasures.save(actualCourseItemMeasure);

        List<CourseItemMeasure> courseItemMeasureList = allCourseItemMeasures.findByLocationId(locationId);

        assertEquals(1,courseItemMeasureList.size());
        assertEquals(callerId, courseItemMeasureList.get(0).getFrontLineWorkerDimension().getMsisdn());
    }
}
