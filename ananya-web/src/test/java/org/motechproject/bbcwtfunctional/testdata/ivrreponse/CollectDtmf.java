package org.motechproject.bbcwtfunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectDtmf {
    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();

    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    @XStreamAlias("o")
    @XStreamAsAttribute
    private Integer timeoutInMillis;

    @XStreamAlias("l")
    @XStreamAsAttribute
    private Integer length;

    @XStreamAlias("t")
    @XStreamAsAttribute
    private Character terminatingChar;

    public boolean hasAudio(String... audioResourceNames) {
        for (String audioResource : audioResourceNames) {
            boolean found = false;
            for (String audioUrl : playaudios) {
                if (audioUrl.endsWith(String.format("%s.wav", audioResource))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public String playAudio() {
        return playaudios.get(0);
    }

    public CollectDtmf playAudios(String... playAudios) {
        playaudios.addAll(Arrays.asList(playAudios));
        return this;
    }

    public boolean hasTimeOut(int timeOutInMillis) {
        return this.timeoutInMillis != null && this.timeoutInMillis == timeOutInMillis;
    }
}
