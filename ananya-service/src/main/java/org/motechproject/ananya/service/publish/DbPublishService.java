package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbPublishService implements PublishService {
    private RegistrationMeasureService registrationMeasureService;
    private CourseItemMeasureService courseItemMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private SMSSentMeasureService smsSentMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;

    @Autowired
    public DbPublishService(RegistrationMeasureService registrationMeasureService, CourseItemMeasureService courseItemMeasureService,
                            CallDurationMeasureService callDurationMeasureService, SMSSentMeasureService smsSentMeasureService,
                            JobAidContentMeasureService jobAidContentMeasureService) {
        this.registrationMeasureService = registrationMeasureService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.smsSentMeasureService = smsSentMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
    }

    @Override
    public void publishCallDisconnectEvent(String callId, String callerId, ServiceType serviceType) {
        registrationMeasureService.createRegistrationMeasure(callerId);
        if (serviceType.equals(ServiceType.JOB_AID))
            this.jobAidContentMeasureService.createJobAidContentMeasure(callId);
        else
            this.courseItemMeasureService.createCourseItemMeasure(callId);

        this.callDurationMeasureService.createCallDurationMeasure(callId);
    }

}
