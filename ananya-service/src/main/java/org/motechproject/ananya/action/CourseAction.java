package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;

public interface CourseAction {
    void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList courseStateRequestList);
}
