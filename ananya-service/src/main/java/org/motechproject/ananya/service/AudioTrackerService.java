package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.mapper.AudioTrackerLogItemMapper;
import org.motechproject.ananya.request.AudioTrackerRequest;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioTrackerService {
    private static Logger log = LoggerFactory.getLogger(AudioTrackerService.class);

    private AudioTrackerLogService audioTrackerLogService;

    @Autowired
    public AudioTrackerService(AudioTrackerLogService audioTrackerLogService) {
        this.audioTrackerLogService = audioTrackerLogService;
    }

    public void saveAllForCourse(AudioTrackerRequestList audioTrackerRequestList) {
        saveAudioTrackerState(audioTrackerRequestList, ServiceType.CERTIFICATE_COURSE);
    }

    public void saveAllForJobAid(AudioTrackerRequestList audioTrackerRequestList) {
        saveAudioTrackerState(audioTrackerRequestList, ServiceType.JOB_AID);
    }

    public void saveAudioTrackerState(AudioTrackerRequestList audioTrackerRequestList, ServiceType serviceType) {
        String callId = audioTrackerRequestList.getCallId();
        String callerId = audioTrackerRequestList.getCallerId();

        if (audioTrackerRequestList.isEmpty()) {
            log.info(callId + "- audioTrackerLog empty");
            return;
        }
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, serviceType);
        for (AudioTrackerRequest audioTrackerRequest : audioTrackerRequestList.all()) {
            AudioTrackerLogItem audioTrackerLogItem = AudioTrackerLogItemMapper.mapFrom(audioTrackerRequest);
            audioTrackerLog.addItem(audioTrackerLogItem);
        }
        audioTrackerLogService.createNew(audioTrackerLog);
        log.info(callId + "- audioTrackerLog saved");
    }

}
