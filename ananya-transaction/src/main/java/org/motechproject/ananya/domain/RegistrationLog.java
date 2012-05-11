package org.motechproject.ananya.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'RegistrationLog'")
public class RegistrationLog extends BaseLog {

    public RegistrationLog() {
    }

    public RegistrationLog(String callerId, String operator) {

        super(callerId, "", operator, "");
    }
}