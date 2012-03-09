package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'FrontLineWorker'")
public class FrontLineWorker extends MotechBaseDataObject {

    public static final int CERTIFICATE_COURSE_PASSING_SCORE = 18;
    public static final int DUMMY_MAX_JOB_AID_USAGE = 50;

    @JsonProperty
    private String name;

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String operator;

    @JsonProperty
    private BookMark bookmark;

    @JsonProperty
    private ReportCard reportCard = new ReportCard();

    @JsonProperty
    private RegistrationStatus status = RegistrationStatus.PARTIALLY_REGISTERED;

    @JsonProperty
    private Designation designation;

    @JsonProperty
    private String locationId;

    @JsonProperty
    private DateTime registeredDate = DateUtil.now();

    @JsonProperty
    private Integer certificateCourseAttempts;

    @JsonProperty
    private Map<Integer, String> smsReferenceNumbers;

    @JsonProperty
    private Integer currentJobAidUsage;

    public FrontLineWorker() {
    }

    public FrontLineWorker(String msisdn , String operator) {
        this.msisdn = msisdn;
        this.operator = operator;
        this.currentJobAidUsage = DUMMY_MAX_JOB_AID_USAGE; //TODO This is just set to satisfy story #1344, and needs to be determined and set[Imdad/Sush]
    }

    @Override
    public String toString() {
        return "FrontLineWorker{" +
                "name='" + name + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", operator='" + operator + '\'' +
                ", bookmark=" + bookmark +
                ", reportCard=" + reportCard +
                ", status=" + status +
                ", designation=" + designation +
                ", locationId='" + locationId + '\'' +
                ", registeredDate=" + registeredDate +
                ", certificateCourseAttempts=" + certificateCourseAttempts +
                ", smsReferenceNumbers=" + smsReferenceNumbers +
                '}';
    }


    //Used only in tests
    public FrontLineWorker(String msisdn, Designation designation, String locationId, String operator) {
        this.msisdn = msisdn;
        this.designation = designation;
        this.locationId = locationId;
        this.operator = operator;
        this.certificateCourseAttempts = 0;
        this.smsReferenceNumbers = new HashMap<Integer, String>();
        this.currentJobAidUsage = DUMMY_MAX_JOB_AID_USAGE; //TODO This is just set to satisfy story #1344, and needs to be determined and set[Imdad/Sush]
    }

    public String getOperator() {
        //TODO: only for #1344, assuming the operator information is already present [Imdad/Sush]
        return StringUtils.isEmpty(operator) ? "airtel" : operator;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public Long msisdn() {
        return Long.valueOf(msisdn);
    }

    public BookMark bookMark() {
        return bookmark != null ? bookmark : new EmptyBookmark();
    }

    public FrontLineWorker status(RegistrationStatus status) {
        this.status = status;
        return this;
    }

    public RegistrationStatus status() {
        return status;
    }

    public ReportCard reportCard() {
        return reportCard;
    }

    public FrontLineWorker name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return this.name;
    }

    public void addBookMark(BookMark bookMark) {
        this.bookmark = bookMark;
    }

    @JsonIgnore
    public boolean isAnganwadi() {
        return this.designation.equals(Designation.ANGANWADI);
    }

    public DateTime registeredDate() {
       return registeredDate;
    }

    public void setRegisteredDate(DateTime registeredDate) {
       this.registeredDate = registeredDate;
    }

    public Integer incrementCertificateCourseAttempts() {
        return ++certificateCourseAttempts;
    }

    public void addSMSReferenceNumber(String smsReferenceNumber) {
        this.smsReferenceNumbers.put(this.certificateCourseAttempts, smsReferenceNumber);
    }

    public Integer currentCourseAttempt() {
        return certificateCourseAttempts;
    }

    public String smsReferenceNumber(int courseAttempt) {
        return this.smsReferenceNumbers.get(courseAttempt);
    }

    public Integer getCurrentJobAidUsage() {
        return this.currentJobAidUsage;
    }
}
