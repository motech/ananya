package org.motechproject.ananya.support.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.motechproject.ananya.support.admin.domain.CallerDetail;
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
    public static final String CALLER_DETAIL = "callerDetail";
    public static final String CALLER_DATA_JSON = "var jobAidCallerData = %s; \n var certificateCourseCallerData = %s;";
    public static final String COUCHDB_ERROR = "couchdbError";
    public static final String POSTGRES_ERROR = "postgresError";

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
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            openSession();
            result.put(ACADEMY_CALLS, callContentQueryResult(msisdn, AdminQuery.ACADEMY_CALLS));
            result.put(KUNJI_CALLS, callContentQueryResult(msisdn, AdminQuery.KUNJI_CALLS));
            result.put(CALL_DETAILS, callDetailQueryResult(msisdn, AdminQuery.CALL_DETAILS));
            result.put(CALLER_DETAIL, callerDetailQueryResult(msisdn, AdminQuery.CALLER_DETAIL));
        } catch (Exception e) {
            result.put(POSTGRES_ERROR, "Postgres connection failed: "+ ExceptionUtils.getFullStackTrace(e));
        } finally {
            closeSession();
        }
        try {
            result.put(CALLER_DATA_JS, getCallerDataJs(msisdn));
        } catch (Exception e) {
            result.put(COUCHDB_ERROR, "Couchdb connection failed: " + ExceptionUtils.getFullStackTrace(e));
        }
        return result;
    }

    private void openSession() {
        session = dataAccessTemplate.getSessionFactory().openSession();
    }

    private void closeSession() {
        if (session != null) session.close();
        session = null;
    }

    private CallerDetail callerDetailQueryResult(String callerId, AdminQuery queryType) {
        String query = queryType.getQuery(correctIfEmpty(callerId));
        Object queryResult = this.session.createQuery(query).uniqueResult();
        if (queryResult != null) {
            Object[] row = (Object[]) queryResult;
            String msisdn = toString(row[0]);
            String name = toString(row[1]);
            return new CallerDetail(msisdn, name);
        }
        return new CallerDetail();
    }

    private String getCallerDataJs(String msisdn) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(msisdn);
        if (frontLineWorker == null)
            return String.format(CALLER_DATA_JSON, "{}", "{}");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int maxUsage = operatorService.findMaximumUsageFor(frontLineWorker.getOperator());

        return String.format(CALLER_DATA_JSON,
                gson.toJson(new JobAidCallerDataResponse(frontLineWorker, maxUsage)),
                gson.toJson(new CertificateCourseCallerDataResponse(frontLineWorker)));
    }

    private List<CallContent> callContentQueryResult(String callerId, AdminQuery queryType) {
        List<CallContent> callContents = new ArrayList<CallContent>();
        String query = queryType.getQuery(correctIfEmpty(callerId));
        List<Object[]> queryObjects = this.session.createQuery(query).list();

        for (Object[] row : queryObjects) {
            String callId = toString(row[0]);
            String timeStamp = toString(row[1]);
            String contentName = toString(row[2]);
            String contentFileName = toString(row[3]);
            callContents.add(new CallContent(callId, timeStamp, contentName, contentFileName));
        }
        return callContents;
    }

    private List<CallDetail> callDetailQueryResult(String callerId, AdminQuery queryType) {
        List<CallDetail> callDetails = new ArrayList<CallDetail>();
        String query = queryType.getQuery(correctIfEmpty(callerId));
        List<Object[]> queryObjects = this.session.createQuery(query).list();

        for (Object[] row : queryObjects) {
            String callId = toString(row[0]);
            String startTime = toString(row[1]);
            String endTime = toString(row[2]);
            String duration = toString(row[3]);
            String calledNumber = toString(row[4]);
            String type = toString(row[5]);
            callDetails.add(new CallDetail(callId, startTime, endTime, duration, calledNumber, type));
        }
        return callDetails;
    }

    private String toString(Object o) {
        return o == null ? "N/A" : String.valueOf(o);
    }

    private String correctIfEmpty(String callerId) {
        return StringUtils.isEmpty(callerId) ? "0" : callerId;
    }
}
