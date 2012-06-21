package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
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

    public void saveAudioTrackerState(AudioTrackerRequestList audioTrackerRequestList, ServiceType serviceType) {
        log.info("Audio Tracker Request List " + audioTrackerRequestList);
        if (audioTrackerRequestList.isEmpty()) return;

        AudioTrackerLog audioTrackerLog = createAudioTrackerLog(audioTrackerRequestList, serviceType);
        audioTrackerLogService.createNew(audioTrackerLog);
    }

    private AudioTrackerLog createAudioTrackerLog(AudioTrackerRequestList audioTrackerRequestList, ServiceType serviceType) {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(
                audioTrackerRequestList.getCallId(),
                audioTrackerRequestList.getCallerId(),
                serviceType);

        for (AudioTrackerRequest audioTrackerRequest : audioTrackerRequestList.all())
            audioTrackerLog.addItem(AudioTrackerLogItemMapper.mapFrom(audioTrackerRequest));

        return audioTrackerLog;
    }
}
