package org.motechproject.ananya.domain.log;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'CertificationCourseLog'")
public class CertificationCourseLog extends BaseLog {
    public CertificationCourseLog() {

    }
    
    public CertificationCourseLog(String callerId, String callId, String token) {
        super(callerId, null, null, null, null, token, callId);
    }
}