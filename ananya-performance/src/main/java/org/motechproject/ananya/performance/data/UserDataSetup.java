package org.motechproject.ananya.performance.data;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataSetup {

    public static final int usersPerOperator = 25000;
    public static final String msisdnPrefix = "99";
    public static final String noOfOperators = "6";

    private RegistrationMeasureService registrationMeasureService;
    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationLogService registrationLogService;

    @Autowired
    public UserDataSetup(RegistrationMeasureService registrationMeasureService,
                         FrontLineWorkerService frontLineWorkerService,
                         RegistrationLogService registrationLogService) {
        this.registrationMeasureService = registrationMeasureService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.registrationLogService = registrationLogService;
    }

    @PerformanceData(testName = "user-setup", description = "create airtel subscribers")
    public void loadAirtelSubscribers() {
        loadUsers("airtel", 1);
    }

    @PerformanceData(testName = "user-setup", description = "create reliance subscribers")
    public void loadRelianceSubscribers() {
        loadUsers("reliance", 2);
    }

    @PerformanceData(testName = "user-setup", description = "create tata subscribers")
    public void loadTataSubscribers() {
        loadUsers("tata", 3);
    }

    @PerformanceData(testName = "user-setup", description = "create idea subscribers")
    public void loadIdeaSubscribers() {
        loadUsers("idea", 4);
    }

    @PerformanceData(testName = "user-setup", description = "create bsnl subscribers")
    public void loadBsnlSubscribers() {
        loadUsers("bsnl", 5);
    }

    @PerformanceData(testName = "user-setup", description = "create vodafone subscribers")
    public void loadVodafoneSubscribers() {
        loadUsers("vodafone", 6);
    }

    private void loadUsers(String operatorName, int prefix) {
        for (int j = 0; j < usersPerOperator; j++) {
            String callerId = msisdnPrefix + prefix + "" + j;
            String callId = callerId + "-" + DateTime.now().getMillisOfDay();
            String circle = "bihar";
            String language = "hindi";

            frontLineWorkerService.createOrUpdateForCall(callerId, operatorName, circle, language);
            registrationLogService.add(new RegistrationLog(callId, callerId, operatorName, circle));
            registrationMeasureService.createFor(callId);

            System.out.println("loaded [callerid=" + callerId + "|thread=" + Thread.currentThread().getId() + "|count=" + j + "|operator=" + operatorName + "]");
        }
    }
}
