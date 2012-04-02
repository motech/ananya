package org.motechproject.ananya.framework;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    private DataAccessTemplate template;


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
        assertTrue(registrationMeasure.getTimeDimension().matches(new DateTime()));

        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        Location defaultLocation = Location.getDefaultLocation();
        assertTrue(locationDimension.getBlock().equals(defaultLocation.getBlock()));
        assertTrue(locationDimension.getDistrict().equals(defaultLocation.getDistrict()));
        assertTrue(locationDimension.getPanchayat().equals(defaultLocation.getPanchayat()));
        return this;
    }

    public void clearDimensionAndMeasures(String callerId) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        template.delete(registrationMeasure);
        template.delete(frontLineWorkerDimension);
    }
    
    public ReportDb confirmCourseItemMeasureForDisconnectEvent(String callerId, BookMark bookMark, String eventType){
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        int questionIndex = bookMark.getLessonIndex()+1;
        int chapterIndex = bookMark.getChapterIndex() + 1;
        String courseItemDimensionName = "Chapter " + chapterIndex + " Lesson " + questionIndex;
        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(courseItemDimensionName, CourseItemType.LESSON);

        CourseItemMeasure courseItemMeasure = allCourseItemMeasures.fetchFor(frontLineWorkerDimension.getId(), courseItemDimension, eventType);

        assertNotNull(courseItemMeasure);
        return this;
    }
}
