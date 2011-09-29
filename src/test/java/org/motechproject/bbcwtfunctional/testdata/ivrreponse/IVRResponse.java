package org.motechproject.bbcwtfunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class IVRResponse {
    @XStreamAsAttribute
    private String sid;
    private Hangup hangup;
    private CollectDtmf collectdtmf;

    public String sid() {
        return sid;
    }

    public boolean isHangedUp() {
        return hangup != null;
    }

    public boolean collectDtmf() {
        return collectdtmf != null;
    }

    public boolean audioPlayed(String ... name) {
        return collectDtmf() && collectdtmf.hasAudio(name);
    }

    public String audioPlayed() {
        if (collectDtmf()) {
            return collectdtmf.playAudio();
        }
        return "";
    }
}
