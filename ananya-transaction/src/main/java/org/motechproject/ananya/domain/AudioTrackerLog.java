package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type == 'AudioTrackerLog'")
public class AudioTrackerLog extends BaseLog {

    @JsonProperty
    private List<AudioTrackerLogItem> audioTrackerLogItems = new ArrayList<AudioTrackerLogItem>();

    @JsonProperty
    private ServiceType serviceType;

    public AudioTrackerLog() {
    }

    public AudioTrackerLog(String callId, String callerId, ServiceType serviceType) {
        super(callId, callerId);
        this.serviceType = serviceType;
    }

    public void addItem(AudioTrackerLogItem audioTrackerLogItem) {
        audioTrackerLogItems.add(audioTrackerLogItem);
    }

    public List<AudioTrackerLogItem> getAudioTrackerLogItems() {
        return audioTrackerLogItems;
    }

    public void setAudioTrackerLogItems(List<AudioTrackerLogItem> audioTrackerLogItems) {
        this.audioTrackerLogItems = audioTrackerLogItems;
    }

    public boolean typeIsCertificateCourse() {
        return this.serviceType.equals(ServiceType.CERTIFICATE_COURSE);
    }
}
