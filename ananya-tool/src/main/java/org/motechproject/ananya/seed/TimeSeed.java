package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCallDurationMeasures;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class TimeSeed {

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private DataAccessTemplate template;

    @Seed(priority = 2, version = "1.0", comment = "load dimensions for 2 years from 1-1-2012 to 1-1-2014")
    public void createDimensionsInPostgres() {
        LocalDate startDate = DateUtil.newDate(2012, 1, 1);
        LocalDate endDate = DateUtil.newDate(2014, 1, 1);

        while (DateUtil.isOnOrBefore(startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay())) {
            allTimeDimensions.addOrUpdate(startDate.toDateTimeAtCurrentTime());
            startDate = startDate.plusDays(1);
        }
    }

    @Seed(priority = 2, version = "1.2", comment = "update the newly added column date for all dimensions")
    public void updateNewlyAddedDateFieldForAllDimensions() {
        LocalDate startDate = DateUtil.newDate(2012, 1, 1);
        LocalDate endDate = DateUtil.newDate(2014, 1, 1);

        while (DateUtil.isOnOrBefore(startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay())) {
            TimeDimension timeDimension = allTimeDimensions.getFor(startDate.toDateTimeAtCurrentTime());
            timeDimension.setDate(startDate.toDate());
            allTimeDimensions.update(timeDimension);
            startDate = startDate.plusDays(1);
        }
    }

    @Seed(priority = 0, version = "2.0", comment = "update time_id column of callDurationMeasure, courseItemMeasure and jobaidContentMeasure, according to local time zone")
    public  void updateTimeId() {
        List<CallDurationMeasure> callDurationMeasures = template.loadAll(CallDurationMeasure.class);
        for(CallDurationMeasure callDurationMeasure : callDurationMeasures) {
            DateTime timestamp = new DateTime(
                    callDurationMeasure.getStartTime().getTime()
            ).toDateTime(DateTimeZone.getDefault());

            if(timestamp.getDayOfYear() != callDurationMeasure.getTimeDimension().getDay()) {
                callDurationMeasure.setTimeDimension(allTimeDimensions.getFor(timestamp));
                template.update(callDurationMeasure);
            }
        }

        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);
        for(CourseItemMeasure courseItemMeasure : courseItemMeasures) {
            DateTime timestamp = new DateTime(
                    courseItemMeasure.getTimestamp().getTime()
            ).toDateTime(DateTimeZone.getDefault());

            if(timestamp.getDayOfYear() != courseItemMeasure.getTimeDimension().getDay()) {
                courseItemMeasure.setTimeDimension(allTimeDimensions.getFor(timestamp));
                template.update(courseItemMeasure);
            }
        }

        List<JobAidContentMeasure> jobAidContentMeasures = template.loadAll(JobAidContentMeasure.class);
        for(JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasures) {
            DateTime startTime = new DateTime(
                    jobAidContentMeasure.getTimestamp().getTime()
            ).toDateTime(DateTimeZone.getDefault());

            if(startTime.getDayOfYear() != jobAidContentMeasure.getTimeDimension().getDay()) {
                jobAidContentMeasure.setTimeDimension(allTimeDimensions.getFor(startTime));
                template.update(jobAidContentMeasure);
            }
        }
    }

}
