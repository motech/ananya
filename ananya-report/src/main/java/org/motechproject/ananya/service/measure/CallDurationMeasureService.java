package org.motechproject.ananya.service.measure;

import org.ektorp.DocumentNotFoundException;
import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.JobAidCallDetails;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCallDurationMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.CallLogService;
import org.motechproject.ananya.service.OperatorService;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CallDurationMeasureService extends TransferableMeasureService{
	private static Logger log = LoggerFactory.getLogger(CallDurationMeasureService.class);

	private CallLogService callLoggerService;
	private AllCallDurationMeasures allCallDurationMeasures;
	private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
	private AllRegistrationMeasures allRegistrationMeasures;
	private AllTimeDimensions allTimeDimensions;
	private LocationDimensionService locationDimensionService;
	private OperatorService operatorService;

	public CallDurationMeasureService() {
	}

	@Autowired
	public CallDurationMeasureService(CallLogService callLoggerService,
			AllCallDurationMeasures allCallDurationMeasures,
			AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
			AllRegistrationMeasures allRegistrationMeasures,
			AllTimeDimensions allTimeDimensions,
			LocationDimensionService locationDimensionService,
			OperatorService operatorService) {
		this.callLoggerService = callLoggerService;
		this.allCallDurationMeasures = allCallDurationMeasures;
		this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
		this.allRegistrationMeasures = allRegistrationMeasures;
		this.allTimeDimensions = allTimeDimensions;
		this.locationDimensionService = locationDimensionService;
		this.operatorService = operatorService;
	}


	@Transactional
	public void createFor(String callId) {
		CallLog callLog = callLoggerService.getCallLogFor(callId);
		log.info(callId + "got call log");
		if (callLog == null) {
			log.info(callId + "- callLog not present");
			return;
		}
		if (callLog.hasNoItems()) {
			log.info(callId + "- callLog has no items");
			removeLog(callId, callLog);
			return;
		}

		Long callerId = callLog.callerIdAsLong();
		Long calledNumber = callLog.calledNumberAsLong();
		FrontLineWorkerDimension flwDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
		RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(flwDimension.getId());
		TimeDimension timeDimension = allTimeDimensions.getFor(callLog.startTime());
		LocationDimension locationDimension = registrationMeasure.getLocationDimension();

		for (CallLogItem callLogItem : callLog.getCallLogItems()) {
			if (callLogItem.hasNoTimeLimits()) continue;

			Integer durationInPulse = operatorService.usageInPulse(flwDimension.getOperator(), callLogItem.durationInMilliSec());

			CallDurationMeasure callDurationMeasure = new CallDurationMeasure(
					flwDimension, locationDimension, timeDimension,
					callId, calledNumber,
					callLogItem.duration(),
					callLogItem.getStartTime(),
					callLogItem.getEndTime(),
					callLogItem.getCallFlowType().name(),
					durationInPulse);
			addFlwHistory(callDurationMeasure);
			allCallDurationMeasures.add(callDurationMeasure);
		}
		log.info(callId + "- callLog callDurationMeasures added");
		removeLog(callId, callLog);
	}

	public void updateLocation(Long callerId, String locationId) {
		List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByCallerId(callerId);
		LocationDimension locationDimension = locationDimensionService.getFor(locationId);

		for (CallDurationMeasure callDurationMeasure : callDurationMeasureList) {
			callDurationMeasure.setLocationDimension(locationDimension);
		}
		allCallDurationMeasures.updateAll(callDurationMeasureList);
	}

	public void updateLocation(String oldLocationId, String newLocationId) {
		log.info(String.format("Updated call duration measures with old location id :%s to new location id : %s", oldLocationId, newLocationId));
		LocationDimension newLocation = locationDimensionService.getFor(newLocationId);
		List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByLocationId(oldLocationId);
		for (CallDurationMeasure callDurationMeasure : callDurationMeasureList) {
			callDurationMeasure.setLocationDimension(newLocation);
		}
		allCallDurationMeasures.updateAll(callDurationMeasureList);
	}

	public CallDetailsResponse getCallDetails(String msisdn) {
		Long msisdnInLong = Long.valueOf(msisdn);
		List<CallUsageDetails> callUsageDetailsList = allCallDurationMeasures.getCallUsageDetailsByMonthAndYear(msisdnInLong);
		List<CallDurationMeasure> recentJobAidCallDetailsList = allCallDurationMeasures.getRecentJobAidCallDetails(msisdnInLong);
		List<CallDurationMeasure> recentCertificateCourseCallDetailsList = allCallDurationMeasures.getRecentCertificateCourseCallDetails(msisdnInLong);

		return new CallDetailsResponse(callUsageDetailsList, recentJobAidCallDetailsList, recentCertificateCourseCallDetailsList);
	}

	private void removeLog(String callId, CallLog callLog) {
		try{
			callLoggerService.delete(callLog);
			log.info(callId + "- callLog removed");
		}catch(DocumentNotFoundException e){
			log.info("caught DocumentNotFoundException.Nothing to delete. error message is: "+e.getMessage());
			log.info(callId + "- callLog removed");
		}
	}

	public List<JobAidCallDetails> getJobAidCallDurations(String msisdn, LocalDate startDate, LocalDate endDate) {
		List<JobAidCallDetails> jobAidCallDetails = allCallDurationMeasures.getJobAidNighttimeCallDetails(Long.valueOf(msisdn), startDate, endDate);
		return jobAidCallDetails;
	}

	@Transactional
	public void transfer(FrontLineWorkerDimension fromFlw, FrontLineWorkerDimension toFlw) {
		allCallDurationMeasures.transfer(CallDurationMeasure.class, fromFlw.getId(), toFlw.getId());
	}
}
