package org.motechproject.ananya.domain;

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
    public static final String DEFAULT_LOCATION = "C00";
    public static final String DEFAULT_LOCATION_ID = "S01D000B000V000";


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
    private String locationId = DEFAULT_LOCATION_ID;

    @JsonProperty
    private DateTime registeredDate = DateUtil.now();

    @JsonProperty
    private Integer certificateCourseAttempts;

    @JsonProperty
    private Integer currentJobAidUsage;

    @JsonProperty
    private DateTime lastJobAidAccessTime;

    @JsonProperty
    private Map<String, Integer> promptsHeard = new HashMap<String, Integer>();

    public FrontLineWorker() {
    }

    public FrontLineWorker(String msisdn, String operator) {
        this.msisdn = msisdn;
        this.operator = (operator == null ? this.operator : operator);
        this.certificateCourseAttempts = 0;
        this.currentJobAidUsage = 0;
    }

    public FrontLineWorker(String msisdn, String name, Designation designation, Location location) {
        this(msisdn, null);
        this.name = name;
        this.designation = designation;
        this.locationId = location.getExternalId();
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
                '}';
    }

    public void setCurrentJobAidUsage(Integer currentJobAidUsage) {
        this.currentJobAidUsage = currentJobAidUsage;
    }

    public void setLocation(Location location) {
        locationId = location.getExternalId();
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = Designation.valueOf(designation);
    }

    public String getOperator() {
        return operator;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public Designation getDesignation() {
        return designation;
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

    public Integer currentCourseAttempt() {
        return certificateCourseAttempts;
    }

    public Integer getCurrentJobAidUsage() {
        return this.currentJobAidUsage != null ? this.currentJobAidUsage : 0;
    }

    public void markPromptHeard(String promptKey) {
        this.promptsHeard.put(promptKey,
                this.promptsHeard.containsKey(promptKey) ? this.promptsHeard.get(promptKey) + 1 : 1);
    }

    public Map<String, Integer> getPromptsHeard() {
        return this.promptsHeard;
    }

    public void setLastJobAidAccessTime(DateTime lastJobAidAccessTime) {
        this.lastJobAidAccessTime = lastJobAidAccessTime;
    }

    public DateTime getLastJobAidAccessTime() {
        return lastJobAidAccessTime;
    }
}
