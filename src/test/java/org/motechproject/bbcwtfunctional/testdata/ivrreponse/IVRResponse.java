package org.motechproject.bbcwtfunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XStreamAlias("response")
public class IVRResponse {
    @XStreamAsAttribute
    private String sid;
    private Hangup hangup;
    private CollectDtmf collectdtmf;
    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playAudios = new ArrayList<String>(10);
    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playText = new ArrayList<String>(10);

    public String sid() {
        return sid;
    }

    public boolean isHangedUp() {
        return hangup != null;
    }

    public boolean collectDtmf() {
        return collectdtmf != null;
    }

    public boolean audioPlayed(String... audios) {
        for (String audioResource : audios) {
            boolean found = false;
            for (String audioUrl : playAudios) {
                if (audioUrl.endsWith(String.format("%s.wav", audioResource))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public boolean promptPlayed(String... audios) {
        return collectDtmf() && collectdtmf.hasAudio(audios);
    }

    public String promptPlayed() {
        if (collectDtmf()) {
            return collectdtmf.playAudio();
        }
        return "";
    }
}
