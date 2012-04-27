package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type == 'AudioTrackerLog'")
public class AudioTrackerLog extends BaseLog {

    @JsonProperty
    private List<AudioTrackerLogItem> audioTrackerLogItems = new ArrayList<AudioTrackerLogItem>();

    public AudioTrackerLog() {
    }

    public AudioTrackerLog(String callId, String callerId) {
        super(callId, callerId);
    }

    public void addItem(AudioTrackerLogItem audioTrackerLogItem) {
        audioTrackerLogItems.add(audioTrackerLogItem);
    }
}
