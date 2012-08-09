package org.motechproject.ananya.support.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.classic.Session;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.OperatorService;
import org.motechproject.ananya.support.admin.domain.AdminQuery;
import org.motechproject.ananya.support.admin.domain.CallContent;
import org.motechproject.ananya.support.admin.domain.CallDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminInquiryService {
    public static final String ACADEMY_CALLS = "academyCalls";
    public static final String KUNJI_CALLS = "kunjiCalls";
    public static final String CALL_DETAILS = "callDetails";
    public static final String CALLER_DATA_JS = "callerDataJs";
    
    private FrontLineWorkerService frontLineWorkerService;
    private OperatorService operatorService;
    private DataAccessTemplate dataAccessTemplate;
    private Session session;

    @Autowired
    public AdminInquiryService(FrontLineWorkerService frontLineWorkerService, OperatorService operatorService, DataAccessTemplate dataAccessTemplate) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public Map<String, Object> getInquiryData(String msisdn) {
        try {
            openSession();

            Map<String, Object> result = new HashMap<String, Object>();
            result.put(ACADEMY_CALLS, getAcademyCallsContent(msisdn));
            result.put(KUNJI_CALLS, getKunjiCallsContent(msisdn));
            result.put(CALL_DETAILS, getCallDetails(msisdn));
            result.put(CALLER_DATA_JS, getCallerDataJs(msisdn));

            return result;
        } finally {
            closeSession();
        }
    }

    private void openSession() {
        session = dataAccessTemplate.getSessionFactory().openSession();
    }

    private void closeSession() {
        if (session != null)
            session.close();

        session = null;
    }

    private List<CallContent> getAcademyCallsContent(String callerId) {
        return callContentQueryResult(callerId, AdminQuery.ACADEMY_CALLS);
    }

    private List<CallContent> getKunjiCallsContent(String callerId) {
        return callContentQueryResult(callerId, AdminQuery.KUNJI_CALLS);
    }

    private List<CallDetail> getCallDetails(String callerId) {
        return callDetailQueryResult(callerId, AdminQuery.CALL_DETAILS);
    }

    private String getCallerDataJs(String msisdn) {
        String jsonJobAidCallerData = "{}";
        String jsonCertificateCourseCallerData = "{}";

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(msisdn);
        if (frontLineWorker != null) {
            JobAidCallerDataResponse jobAidCallerData = new JobAidCallerDataResponse(
                    frontLineWorker,
                    StringUtils.isEmpty(frontLineWorker.getOperator())
                            ? 0
                            : operatorService.findMaximumUsageFor(frontLineWorker.getOperator())
            );

            CertificateCourseCallerDataResponse certificateCourseCallerData = new CertificateCourseCallerDataResponse(frontLineWorker);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            jsonJobAidCallerData = gson.toJson(jobAidCallerData);
            jsonCertificateCourseCallerData = gson.toJson(certificateCourseCallerData);
        }


        return String.format(
                "var jobAidCallerData = %s;" +
                        "\n" +
                        "var certificateCourseCallerData = %s;",
                jsonJobAidCallerData,
                jsonCertificateCourseCallerData
        );
    }

    private List<CallContent> callContentQueryResult(String callerId, AdminQuery queryType) {
        List<CallContent> result = new ArrayList<CallContent>();

        callerId = StringUtils.isEmpty(callerId) ? "0" : callerId;

        String query = queryType.getQuery(callerId);
        List<Object[]> list = this.session.createQuery(query).list();
        for (Object[] row : list) {
            String name = String.valueOf(row[0]);
            String msisdn = String.valueOf(row[1]);
            String callId = String.valueOf(row[2]);
            String timeStamp = String.valueOf(row[3]);
            String contentName = String.valueOf(row[4]);
            String contentFileName = String.valueOf(row[5]);

            result.add(new CallContent(name, msisdn, callId, timeStamp, contentName, contentFileName));
        }

        return result;
    }

    private List<CallDetail> callDetailQueryResult(String callerId, AdminQuery queryType) {
        List<CallDetail> result = new ArrayList<CallDetail>();

        callerId = StringUtils.isEmpty(callerId) ? "0" : callerId;

        String query = queryType.getQuery(callerId);
        List<Object[]> list = this.session.createQuery(query).list();
        for (Object[] row : list) {
            String name = String.valueOf(row[0]);
            String msisdn = String.valueOf(row[1]);
            String callId = String.valueOf(row[2]);
            String startTime = String.valueOf(row[3]);
            String endTime = String.valueOf(row[4]);
            String duration = String.valueOf(row[5]);
            String calledNumber = String.valueOf(row[6]);
            String type = String.valueOf(row[7]);

            result.add(new CallDetail(name, msisdn, callId, startTime, endTime, duration, calledNumber, type));
        }

        return result;
    }
}
