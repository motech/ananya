package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@TypeDiscriminator("doc.type === 'FrontLineWorker'")
public class FrontLineWorker extends MotechBaseDataObject {

    private final static UUID DUMMY_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static Logger log = LoggerFactory.getLogger(FrontLineWorker.class);

    public static final int CERTIFICATE_COURSE_PASSING_SCORE = 18;

    @JsonProperty
    private String name;

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String alternateContactNumber;

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
    private String language;

    @JsonProperty
    private String locationId = Location.getDefaultLocation().getExternalId();

    @JsonProperty
    private DateTime registeredDate = DateUtil.now();

    @JsonProperty
    private Integer certificateCourseAttempts;

    @JsonProperty
    private Integer currentJobAidUsage;

    @JsonProperty
    private Integer currentCourseUsage;

    @JsonProperty
    private DateTime lastJobAidAccessTime;
    
    @JsonProperty
    private DateTime lastCourseAccessTime;

    @JsonProperty
    private DateTime lastModified;

    @JsonProperty
    private Map<String, Integer> promptsHeard = new HashMap<String, Integer>();

    @JsonProperty
    private Map<String, Integer> promptsHeardForMA = new HashMap<String, Integer>();

    
    @JsonIgnore
    private boolean modified;
    
    @JsonIgnore
    private boolean cappingEnabledMA=false;
    
    @JsonIgnore
    private boolean cappingEnabledMK=true;

    @JsonProperty
    private UUID flwId = DUMMY_UUID;

    @JsonProperty
    private VerificationStatus verificationStatus;

    public FrontLineWorker() {
        this.certificateCourseAttempts = 0;
        this.currentJobAidUsage = 0;
        this.currentCourseUsage = 0;
    }

    public FrontLineWorker(String msisdn, String operator, String circle, String language) {
        this();
        this.msisdn = prefixMsisdnWith91(msisdn);
        this.circle = circle;
        this.operator = operator;
        this.language = language;
    }

    public FrontLineWorker(String msisdn, String alternateContactNumber, String name, Designation designation, Location location, String language, DateTime lastModified, UUID flwId) {
        this();
        this.msisdn = prefixMsisdnWith91(msisdn);
        this.alternateContactNumber = prefixMsisdnWith91(alternateContactNumber);
        this.name = name;
        this.designation = designation;
        this.locationId = location == null ? Location.getDefaultLocation().getExternalId() : location.getExternalId();
        this.lastModified = lastModified;
        this.flwId = flwId;
        this.language = language;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setCurrentJobAidUsage(Integer currentJobAidUsage) {
        this.currentJobAidUsage = currentJobAidUsage;
    }

    public void setCertificateCourseAttempts(Integer certificateCourseAttempts) {
        this.certificateCourseAttempts = certificateCourseAttempts;
    }

    public void setReportCard(ReportCard reportCard) {
        this.reportCard = reportCard;
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

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }

    public String getName() {
        return name;
    }

    public Designation getDesignation() {
        return designation;
    }

    public BookMark getBookmark() {
        return bookmark;
    }

    public String designationName() {
        return designation != null ? designation.name() : null;
    }

    public Long msisdn() {
        return Long.valueOf(msisdn);
    }

    public Long alternateContactNumber() {
        return alternateContactNumber == null ? null : Long.valueOf(alternateContactNumber);
    }

    public BookMark bookMark() {
        return bookmark != null ? bookmark : new EmptyBookmark();
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

    public void setBookMark(BookMark bookMark) {
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

    public Integer currentCourseAttempts() {
        return certificateCourseAttempts;
    }

    public Integer getCurrentJobAidUsage() {
        return this.currentJobAidUsage != null ? this.currentJobAidUsage : 0;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void markPromptHeard(String promptKey) {
        this.promptsHeard.put(promptKey,
                this.promptsHeard.containsKey(promptKey) ? this.promptsHeard.get(promptKey) + 1 : 1);
    }

    public Map<String, Integer> getPromptsHeard() {
        return this.promptsHeard;
    }
    
    public void markPromptHeardForMA(String promptKey) {
        this.promptsHeardForMA.put(promptKey,
                this.promptsHeardForMA.containsKey(promptKey) ? this.promptsHeardForMA.get(promptKey) + 1 : 1);
    }


    public Map<String, Integer> getPromptsHeardForMA() {
		return promptsHeardForMA;
	}

	public void setLastJobAidAccessTime(DateTime lastJobAidAccessTime) {
        this.lastJobAidAccessTime = lastJobAidAccessTime;
    }

    public DateTime getLastJobAidAccessTime() {
        return lastJobAidAccessTime;
    }
    
    public DateTime getLastCourseAccessTime() {
		return lastCourseAccessTime;
	}

	public void setLastCourseAccessTime(DateTime lastCourseAccessTime) {
		this.lastCourseAccessTime = lastCourseAccessTime;
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

    public boolean update(String name, Designation designation, Location location, DateTime lastModified, UUID flwId, VerificationStatus verificationStatus, String alternateContactNumber) {
        if (!canBeUpdated(lastModified)) {
            return false;
        }
        this.alternateContactNumber = alternateContactNumber;
        this.name = name;
        this.designation = designation;
        this.lastModified = lastModified == null ? this.lastModified : lastModified;
        this.verificationStatus = verificationStatus;
        updateFlwId(flwId);

        if (location == null) {
            location = Location.getDefaultLocation();
        }
        this.locationId = location.getExternalId();

        if (isAlreadyRegistered()) {
            decideRegistrationStatus(location);
        }

        return true;
    }

    public boolean hasPassedTheCourse() {
        return reportCard().totalScore() >= CERTIFICATE_COURSE_PASSING_SCORE;
    }

    public void resetJobAidUsageAndPrompts() {
        this.currentJobAidUsage = 0;
        this.promptsHeard.remove("Max_Usage");
    }
    
    public void resetCourseUsageAndPrompts() {
        this.currentCourseUsage = 0;
        this.promptsHeardForMA.remove("Max_Usage");
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
    public boolean courseLastAccessedPreviousMonth() {
        DateTime now = DateTime.now();
        return lastCourseAccessTime != null &&
                (lastCourseAccessTime.getMonthOfYear() != now.getMonthOfYear() ||
                		lastCourseAccessTime.getYear() != now.getYear());
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

    public UUID getFlwId() {
        return flwId;
    }

    public void setFlwId(UUID flwId) {
        this.flwId = flwId;
    }

    public void merge(FrontLineWorker frontLineWorker) {
        if (this.status.equals(RegistrationStatus.UNREGISTERED)) {
            this.status = frontLineWorker.getStatus();
            this.locationId = frontLineWorker.getLocationId();
            this.designation = frontLineWorker.getDesignation();
            this.name = frontLineWorker.name();
            this.registeredDate = frontLineWorker.getRegisteredDate();
            this.language = frontLineWorker.getLanguage();
            this.alternateContactNumber = frontLineWorker.getAlternateContactNumber();
        }
        if (this.bookmark == null) {
            this.bookmark = frontLineWorker.bookMark();
            this.reportCard = frontLineWorker.reportCard();
            this.certificateCourseAttempts = frontLineWorker.certificateCourseAttempts;
            this.currentCourseUsage = frontLineWorker.currentCourseUsage;
            this.promptsHeardForMA = frontLineWorker.promptsHeardForMA;
            this.lastCourseAccessTime = frontLineWorker.lastCourseAccessTime;
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

    @JsonIgnore
    public boolean isUnRegistered() {
        return status.equals(RegistrationStatus.UNREGISTERED);
    }

    @JsonIgnore
    public boolean isAlreadyRegistered() {
        return !isUnRegistered();
    }

    public void decideRegistrationStatus(Location location) {
        boolean locationAbsent = (Location.getDefaultLocation().equals(location));
        boolean locationIncomplete = location.isMissingDetails();
        boolean designationInvalid = Designation.isInValid(designationName());
        boolean nameInvalid = StringUtils.isBlank(name);
        boolean isLocationStatusNotValid = location.getLocationStatusAsEnum() != LocationStatus.VALID;

        if (locationAbsent || locationIncomplete || isLocationStatusNotValid || designationInvalid || nameInvalid) {
            status = RegistrationStatus.PARTIALLY_REGISTERED;
            return;
        }
        status = RegistrationStatus.REGISTERED;
    }

    public FrontLineWorker updateWith(FrontLineWorker frontLineWorker) {
        operator = frontLineWorker.getOperator();
        circle = frontLineWorker.getCircle();
        locationId = frontLineWorker.getLocationId();
        return this;
    }

    public boolean courseInProgress() {
        return bookMark().notAtPlayThanks();
    }

    private String prefixMsisdnWith91(String msisdn) {
        return msisdn != null && msisdn.length() == 10 ? "91" + msisdn : msisdn;
    }

    private void updateFlwId(UUID flwId) {
        if (this.flwId != null && !this.flwId.equals(flwId)) {
            log.warn(String.format("Changing FLW ID for msisdn[%s]", this.msisdn));
        }

        this.flwId = flwId;
    }

    public void updateJobAidUsage(Integer durationInMilliSec) {
        this.currentJobAidUsage += durationInMilliSec;
    }
    
    public void updateCourseUsage(Integer durationInMilliSec) {
        this.currentCourseUsage += durationInMilliSec;
    }

    public void updateLocation(Location location) {
        locationId = location.getExternalId();
        if (isAlreadyRegistered())
            decideRegistrationStatus(location);
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    private boolean canBeUpdated(DateTime updatedOn) {
        if (lastModified == null || updatedOn == null) {
            return true;
        }
        return (DateUtil.isOnOrBefore(lastModified, updatedOn));
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    public ReportCard getReportCard() {
        return reportCard;
    }

    public void setPromptsHeard(Map<String, Integer> promptsHeard) {
        this.promptsHeard = promptsHeard;
    }
    

	public void setPromptsHeardForMA(Map<String, Integer> promptsHeardForMA) {
		this.promptsHeardForMA = promptsHeardForMA;
	}

	public Integer getCurrentCourseUsage() {
		return this.currentCourseUsage != null ? this.currentCourseUsage : 0;
	}

	public void setCurrentCourseUsage(Integer currentCourseUsage) {
		this.currentCourseUsage = currentCourseUsage;
	}

	public boolean isCappingEnabledMA() {
		return cappingEnabledMA;
	}

	public void setCappingEnabledMA(boolean cappingEnabledMA) {
		this.cappingEnabledMA = cappingEnabledMA;
	}

	public boolean isCappingEnabledMK() {
		return cappingEnabledMK;
	}

	public void setCappingEnabledMK(boolean cappingEnabledMK) {
		this.cappingEnabledMK = cappingEnabledMK;
	}
    
	
    
}
