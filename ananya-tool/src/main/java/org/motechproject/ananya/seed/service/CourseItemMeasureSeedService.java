package org.motechproject.ananya.seed.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseItemMeasureSeedService {

    @Autowired
    private DataAccessTemplate template;

    @Transactional
    public List<CourseItemMeasure> fetchQuizStartMeasuresBetweenDates(DateTime startDate, DateTime endDate) {

        return template.find(
                "select " +
                    "c " +
                "from " +
                    "CourseItemMeasure c " +
                "where " +
                    "c.courseItemDimension.type = 'QUIZ' and " +
                    "c.event = 'START' and " +
                    "c.timestamp >= ? and " +
                    "c.timestamp <= ?",
                startDate.toDate(), endDate.toDate()
        );

//        return
//                (List<CourseItemMeasure>) template.findByNamedQueryAndNamedParam(
//                        "select " +
//                            "c " +
//                        "from " +
//                            "CourseItemMeasure c " +
//                        "where " +
//                            "c.courseItemDimension.type = 'QUIZ' and " +
//                            "c.event = 'START' and " +
//                            "c.timestamp >= start_date and " +
//                            "c.timestamp <= end_date",
//                        new String[] { "start_date", "end_date"},
//                        new Object[] { startDate, endDate });
    }

    @Transactional
    public List<CourseItemMeasure> fetchQuizEndMeasuresBetweenDates(DateTime startDate, DateTime endDate) {


        return template.find(
                "select " +
                    "c " +
                "from " +
                    "CourseItemMeasure c " +
                "where " +
                    "c.courseItemDimension.type = 'QUIZ' and " +
                    "c.event = 'END' and " +
                    "c.timestamp >= ? and " +
                    "c.timestamp <= ?",
                startDate.toDate(), endDate.toDate()
        );

//        return
//                (List<CourseItemMeasure>) template.findByNamedQueryAndNamedParam(
//                        "select " +
//                            "c " +
//                        "from " +
//                            "CourseItemMeasure c " +
//                        "where " +
//                            "c.courseItemDimension.type = 'QUIZ' and " +
//                            "c.event = 'END' and " +
//                            "c.timestamp >= start_date and " +
//                            "c.timestamp <= end_date",
//                        new String[] { "start_date", "end_date"},
//                        new Object[] { startDate, endDate });
        
    }

}
