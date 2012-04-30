package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioTrackerLogService {
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Autowired
    public AudioTrackerLogService(AllAudioTrackerLogs allAudioTrackerLogs) {
        this.allAudioTrackerLogs = allAudioTrackerLogs;
    }

    public void createNew(AudioTrackerLog audioTrackerLog) {
        allAudioTrackerLogs.add(audioTrackerLog);
    }

    public AudioTrackerLog getLogFor(String callId) {
        return allAudioTrackerLogs.findByCallId(callId);
    }

    public void deleteLogsFor(String callId) {
        allAudioTrackerLogs.deleteFor(callId);
    }
}
