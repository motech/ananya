package org.motechproject.ananya.service.helpers;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;

public class CourseItemMeasureServiceHelper {
    private CertificationCourseLog courseLog;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private LocationDimension locationDimension;
    private AudioTrackerLog audioTrackerLog;

    public CourseItemMeasureServiceHelper(CertificationCourseLog courseLog, FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension, AudioTrackerLog audioTrackerLog) {
        this.courseLog = courseLog;
        this.frontLineWorkerDimension = frontLineWorkerDimension;
        this.locationDimension = locationDimension;
        this.audioTrackerLog = audioTrackerLog;
    }

    public CourseItemMeasureServiceHelper() {
    }

    public CertificationCourseLog getCourseLog() {
        return courseLog;
    }

    public FrontLineWorkerDimension getFrontLineWorkerDimension() {
        return frontLineWorkerDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public AudioTrackerLog getAudioTrackerLog() {
        return audioTrackerLog;
    }
}
