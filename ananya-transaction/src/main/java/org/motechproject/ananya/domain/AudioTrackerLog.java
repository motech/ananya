package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

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

    public List<AudioTrackerLogItem> items() {
        return audioTrackerLogItems;
    }

    public void setAudioTrackerLogItems(List<AudioTrackerLogItem> audioTrackerLogItems) {
        this.audioTrackerLogItems = audioTrackerLogItems;
    }

    public boolean typeIsCertificateCourse() {
        return this.serviceType.equals(ServiceType.CERTIFICATE_COURSE);
    }

    public boolean hasNoItems() {
        return audioTrackerLogItems == null || audioTrackerLogItems.isEmpty();
    }

    public DateTime time() {
        return hasNoItems() ? null : audioTrackerLogItems.get(0).getTime();
    }

}
