package org.motechproject.ananya.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'SendSMSLog'")
public class SendSMSLog extends BaseLog {

    public SendSMSLog(){
    }

    public SendSMSLog(String callerId) {
        super(callerId, "", "", "");
    }
}
