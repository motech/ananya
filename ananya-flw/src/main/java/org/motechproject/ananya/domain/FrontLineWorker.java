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

    @JsonProperty
    private String name;

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String operator;

    @JsonProperty
    private String circle;

    @JsonProperty
    private BookMark bookmark;

    @JsonProperty
    private ReportCard reportCard = new ReportCard();

    @JsonProperty
    private RegistrationStatus status = RegistrationStatus.UNREGISTERED;

    @JsonProperty
    private Designation designation;

    @JsonProperty
    private String locationId = Location.getDefaultLocation().getExternalId();

    @JsonProperty
    private DateTime registeredDate = DateUtil.now();

    @JsonProperty
    private Integer certificateCourseAttempts;

    @JsonProperty
    private Integer currentJobAidUsage;

    @JsonProperty
    private DateTime lastJobAidAccessTime;

    @JsonProperty
    private DateTime lastModified;

    @JsonProperty
    private Map<String, Integer> promptsHeard = new HashMap<String, Integer>();

    @JsonIgnore
    private boolean modified;

    public FrontLineWorker() {
        this.certificateCourseAttempts = 0;
        this.currentJobAidUsage = 0;
    }

    public FrontLineWorker(String msisdn, String operator) {
        this();
        this.msisdn = prefixMsisdnWith91(msisdn);
        this.operator = operator == null ? this.operator : operator;
    }

    private String prefixMsisdnWith91(String msisdn) {
        return msisdn != null && msisdn.length() == 10 ? "91" + msisdn : msisdn;
    }

    public FrontLineWorker(String msisdn, String name, Designation designation, Location location, RegistrationStatus registrationStatus) {
        this(msisdn, null);
        this.name = name;
        this.designation = designation;
        this.locationId = location == null ? null : location.getExternalId();
        this.status = registrationStatus;
    }

    public FrontLineWorker(String msisdn, String name, Designation designation, String operator, String circle, Location location, RegistrationStatus registrationStatus, DateTime lastModified) {
        this(msisdn, name, designation, location, registrationStatus);
        this.operator = operator;
        this.circle = circle;
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "FrontLineWorker{" + msisdn + "}";
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

    public void setDesignation(Designation designation) {
        this.designation = designation;
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

    public String designationName() {
        return designation != null ? designation.name() : null;
    }

    public Long msisdn() {
        return Long.valueOf(msisdn);
    }

    public BookMark bookMark() {
        return bookmark != null ? bookmark : new EmptyBookmark();
    }

    public RegistrationStatus getStatus() {
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
        return this.designation.equals(Designation.AWW);
    }

    public DateTime getRegisteredDate() {
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

    public DateTime getLastModified() {
        return lastModified;
    }

    public boolean operatorIs(String operator) {
        return StringUtils.equalsIgnoreCase(this.operator, operator);
    }

    public void setRegistrationStatus(RegistrationStatus status) {
        this.status = status;
    }

    public void update(String name, Designation designation, Location location, RegistrationStatus registrationStatus, String circle, String operator, DateTime lastModified) {
        this.name = name;
        this.lastModified = lastModified;
        this.locationId = location.getExternalId();
        this.designation = designation;
        this.circle = circle;
        this.operator = operator;
        if (this.status.weight < registrationStatus.weight)
            this.status = registrationStatus;
    }

    public boolean hasPassedTheCourse() {
        return reportCard().totalScore() >= CERTIFICATE_COURSE_PASSING_SCORE;
    }

    public void resetJobAidUsageAndPrompts() {
        this.currentJobAidUsage = 0;
        this.promptsHeard.remove("Max_Usage");
    }

    public boolean circleIs(String circle) {
        return StringUtils.equalsIgnoreCase(this.circle, circle);
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getCircle() {
        return circle;
    }

    @JsonIgnore
    public boolean jobAidLastAccessedPreviousMonth() {
        DateTime now = DateTime.now();
        return lastJobAidAccessTime != null &&
                (lastJobAidAccessTime.getMonthOfYear() != now.getMonthOfYear() ||
                        lastJobAidAccessTime.getYear() != now.getYear());
    }

    @JsonIgnore
    public void setModified() {
        this.modified = true;
    }

    @JsonIgnore
    public boolean isModified() {
        return modified;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void merge(FrontLineWorker frontLineWorker) {
        if (this.status.equals(RegistrationStatus.UNREGISTERED)) {
            this.status = frontLineWorker.getStatus();
            this.locationId = frontLineWorker.getLocationId();
            this.designation = frontLineWorker.getDesignation();
            this.name = frontLineWorker.name();
            this.registeredDate = frontLineWorker.getRegisteredDate();
        }
        if (this.bookmark == null) {
            this.bookmark = frontLineWorker.bookMark();
            this.reportCard = frontLineWorker.reportCard();
            this.certificateCourseAttempts = frontLineWorker.certificateCourseAttempts;
        }
        if (this.currentJobAidUsage == 0) {
            this.currentJobAidUsage = frontLineWorker.currentJobAidUsage;
            this.lastJobAidAccessTime = frontLineWorker.lastJobAidAccessTime;
            this.promptsHeard = frontLineWorker.promptsHeard;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrontLineWorker that = (FrontLineWorker) o;
        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return msisdn != null ? msisdn.hashCode() : 0;
    }
}
