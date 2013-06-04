package org.motechproject.ananya.seed.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
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
        
    }

    @Transactional
    public List fetchFlwCourseHistory() {
        return template.find(
                "select " +
                        "cim.frontLineWorkerDimension.msisdn, cim.courseItemDimension.name, " +
                        "cim.courseItemDimension.type, cim.event, cim.score, cim.timestamp " +
                "from " +
                        "CourseItemMeasure cim " +
                "where " +
                        "cim.courseItemDimension.type = 'QUIZ' " +
                "order by " +
                        "cim.frontLineWorkerDimension.msisdn, cim.timeDimension.id, cim.timestamp"
        );
    }

    @Transactional
    public CallDurationMeasure fetchCallDurationMeasureForCallId(String callId) {
        return (CallDurationMeasure)template.find(
                "select cdm from CallDurationMeasure cdm where cdm.callId = '" + callId + "' and cdm.type = 'CALL'");
    }

}
