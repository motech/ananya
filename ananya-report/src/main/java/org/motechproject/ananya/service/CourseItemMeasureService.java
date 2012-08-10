package org.motechproject.ananya.service;

import org.motechproject.ananya.repository.measure.AllCourseItemMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CourseItemMeasureService {
    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private AllCourseItemMeasures allCourseItemMeasures;

    public CourseItemMeasureService() {
    }

    @Autowired
    public CourseItemMeasureService(AllCourseItemMeasures allCourseItemMeasures) {
        this.allCourseItemMeasures = allCourseItemMeasures;
    }

    public List<Long> getAllFrontLineWorkerMsisdnsBetween(Date startDate, Date endDate) {
        return allCourseItemMeasures.getFilteredFrontLineWorkerMsisdns(startDate, endDate);
    }
}
