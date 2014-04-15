package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FailedRecordsProcessingState'")
public class FailedRecordsProcessingState extends MotechBaseDataObject {
    @JsonProperty
    private DateTime lastProcessedDate;

    FailedRecordsProcessingState(){
    }

    public FailedRecordsProcessingState(DateTime lastProcessedDate){
        this.lastProcessedDate = lastProcessedDate;
    }

    public void update(DateTime lastProcessedDate){
        this.lastProcessedDate = lastProcessedDate;
    }

    public DateTime getLastProcessedDate() {
        return lastProcessedDate == null ? null : lastProcessedDate.withZone(DateTimeZone.getDefault());
    }
}
