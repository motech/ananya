package org.motechproject.ananya.service.publish;

import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbPublishService implements PublishService {

    @Autowired
    private RegistrationMeasureService registrationMeasureService;
    @Autowired
    private CourseItemMeasureService courseItemMeasureService;
    @Autowired
    private CallDurationMeasureService callDurationMeasureService;
    @Autowired
    private SMSSentMeasureService smsSentMeasureService;

    @Override
    public void publishSMSSent(LogData logData) {
        String callerId = logData.getDataId();
        this.smsSentMeasureService.createSMSSentMeasure(callerId);
    }

    @Override
    public void publishCallDisconnectEvent(String callId) {
        this.courseItemMeasureService.createCourseItemMeasure(callId);
        this.callDurationMeasureService.createCallDurationMeasure(callId);
    }

    @Override
    public void publishNewRegistration(String callerId) {
        registrationMeasureService.createRegistrationMeasure(new LogData(LogType.REGISTRATION, callerId));
    }
}
