package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CourseItemMeasureService {
    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private AllCourseItemMeasures allCourseItemMeasures;
    private LocationDimensionService locationDimensionService;

    public CourseItemMeasureService() {
    }

    @Autowired
    public CourseItemMeasureService(AllCourseItemMeasures allCourseItemMeasures, LocationDimensionService locationDimensionService) {
        this.allCourseItemMeasures = allCourseItemMeasures;
        this.locationDimensionService = locationDimensionService;
    }

    public List<Long> getAllFrontLineWorkerMsisdnsBetween(Date startDate, Date endDate) {
        return allCourseItemMeasures.getFilteredFrontLineWorkerMsisdns(startDate, endDate);
    }

    public void updateLocation(Long callerId, String locationId) {
        List<CourseItemMeasure> courseItemMeasures = allCourseItemMeasures.findByCallerId(callerId);
        LocationDimension locationDimension = locationDimensionService.getFor(locationId);
        for (CourseItemMeasure courseItemMeasure : courseItemMeasures) {
            courseItemMeasure.setLocationDimension(locationDimension);
        }
        allCourseItemMeasures.updateAll(courseItemMeasures);
    }

    public void updateLocation(String oldLocationId, String newLocationId) {
        log.info(String.format("Updated course item measures with old location id :%s to new location id : %s", oldLocationId, newLocationId));
        LocationDimension newLocation = locationDimensionService.getFor(newLocationId);
        List<CourseItemMeasure> courseItemMeasureList = allCourseItemMeasures.findByLocationId(oldLocationId);
        for (CourseItemMeasure courseItemMeasure : courseItemMeasureList) {
            courseItemMeasure.setLocationDimension(newLocation);
        }
        allCourseItemMeasures.updateAll(courseItemMeasureList);
    }

    @Transactional
    public void transfer(FrontLineWorkerDimension fromFlw, FrontLineWorkerDimension toFlw) {
        allCourseItemMeasures.transfer(CourseItemMeasure.class, fromFlw.getId(), toFlw.getId());
    }
}
