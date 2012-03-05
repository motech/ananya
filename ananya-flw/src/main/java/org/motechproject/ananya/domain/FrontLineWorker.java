package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type === 'FrontLineWorker'")
public class FrontLineWorker extends MotechBaseDataObject {

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
    private RegistrationStatus status = RegistrationStatus.UNREGISTERED;

    @JsonProperty
    private Designation designation;

    @JsonProperty
    private String locationId;

    @JsonProperty
    private DateTime registeredDate = DateUtil.now();

    @JsonProperty
    private Integer certificateCourseAttempts;

    public FrontLineWorker() {
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



    public FrontLineWorker(String msisdn, Designation designation, String locationId, String operator) {
        this.msisdn = msisdn;
        this.designation = designation;
        this.locationId = locationId;
        this.operator = operator;
        this.certificateCourseAttempts = 0;
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

    public boolean hasStartedCertificationCourse() {
        return status().isRegistered() && bookMark().getType() != null;
    }

    public Integer incrementCertificateCourseAttempts() {
        return ++certificateCourseAttempts;
    }

}
