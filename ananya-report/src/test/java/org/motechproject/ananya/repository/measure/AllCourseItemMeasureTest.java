package org.motechproject.ananya.repository.measure;

import org.joda.time.DateTime;
import org.junit.After;
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

import static junit.framework.Assert.assertEquals;

public class AllCourseItemMeasureTest extends SpringIntegrationTest {

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

    @After
    public void tearDown(){
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenFLWDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), new CourseItemDimension(), null, null, 2, CourseItemState.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenCourseItemDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(null, new CourseItemDimension(), new FrontLineWorkerDimension(), null, 2, CourseItemState.START));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotInsertCourseItemMeasureWhenTimeDimensionIsNull() {
        allCourseItemMeasures.save(new CourseItemMeasure(new TimeDimension(), null, new FrontLineWorkerDimension(),null, 2, CourseItemState.START));
    }

    @Test
    public void shouldFetchBasedOnCourseItemDimensionAndFLWAndEvent(){
        CourseItemType chapter = CourseItemType.CHAPTER;
        Long msisdn = Long.valueOf("987654");
        CourseItemState event = CourseItemState.END;
        String courseItemDimensionName = "name" + DateTime.now();

        LocationDimension locationDimension = new LocationDimension("locationId", "", "", "");
        CourseItemDimension courseItemDimension = new CourseItemDimension(courseItemDimensionName, "contentId", chapter, null);
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(msisdn, "operator", "name", "ASHA", "REGISTERED");

        TimeDimension timeDimension = allTimeDimensions.makeFor(DateTime.now());
        allCourseItemDimensions.add(courseItemDimension);
        allLocationDimensions.add(locationDimension);


        CourseItemMeasure courseItemMeasure = new CourseItemMeasure(timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension, 0, event);
        allCourseItemMeasures.save(courseItemMeasure);

        CourseItemMeasure measure = allCourseItemMeasures.fetchFor(frontLineWorkerDimension.getId(), courseItemDimension, String.valueOf(event));
        assertEquals(event,measure.getEvent());
        assertEquals(courseItemDimensionName,measure.getCourseItemDimension().getName());
        assertEquals(chapter,measure.getCourseItemDimension().getType());
        assertEquals(msisdn,measure.getFrontLineWorkerDimension().getMsisdn());
    }

}
