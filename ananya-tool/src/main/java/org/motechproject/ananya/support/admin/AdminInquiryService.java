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
import java.util.Collections;
import java.util.List;

@Service
public class AdminInquiryService {
    private FrontLineWorkerService frontLineWorkerService;
    private OperatorService operatorService;
    private Session session;

    @Autowired
    public AdminInquiryService(FrontLineWorkerService frontLineWorkerService, OperatorService operatorService, DataAccessTemplate dataAccessTemplate) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.session = dataAccessTemplate.getSessionFactory().openSession();
    }

    public List<CallContent> getAcademyCallsContent(String callerId) {
        AdminQuery academyCalls = AdminQuery.ACADEMY_CALLS;

        return callContentQueryResult(callerId, academyCalls);
    }

    public List<CallContent> getKunjiCallsContent(String callerId) {
        AdminQuery kunjiCalls = AdminQuery.KUNJI_CALLS;

        return callContentQueryResult(callerId, kunjiCalls);
    }

    public List<CallDetail> getCallDetails(String msisdn) {
        return Collections.EMPTY_LIST;
    }

    public String getCallerDataJs(String msisdn) {
        JobAidCallerDataResponse jobAidCallerData = JobAidCallerDataResponse.forNewUser(0);
        CertificateCourseCallerDataResponse certificateCourseCallerData = CertificateCourseCallerDataResponse.forNewUser();

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(msisdn);
        if (frontLineWorker != null) {
            jobAidCallerData = new JobAidCallerDataResponse(
                    frontLineWorker,
                    StringUtils.isEmpty(frontLineWorker.getOperator())
                            ? 0
                            : operatorService.findMaximumUsageFor(frontLineWorker.getOperator())
            );

            certificateCourseCallerData = new CertificateCourseCallerDataResponse(frontLineWorker);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonJobAidCallerData = gson.toJson(jobAidCallerData);
        String jsonCertificateCourseCallerData = gson.toJson(certificateCourseCallerData);

        return String.format(
                "var jobAidCallerData = %s;" +
                        "\n" +
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

            result.add(new CallContent(
                    name,
                    msisdn,
                    callId,
                    timeStamp,
                    contentName,
                    contentFileName
            ));
        }

        return result;
    }
}
