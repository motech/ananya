package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import org.apache.commons.lang.StringUtils;

public class IVRDtmfBuilder {
    private String playText;
    private String playAudio;

    public IVRDtmfBuilder withPlayText(String playText) {
        this.playText = playText;
        return this;
    }

    public IVRDtmfBuilder withPlayAudio(String playAudio) {
        this.playAudio = playAudio;
        return this;
    }

    public CollectDtmf create() {
        CollectDtmf collectDtmf = new CollectDtmf();
        if (StringUtils.isNotBlank(playText)) collectDtmf.addPlayText(playText);
        if (StringUtils.isNotBlank(playAudio)) collectDtmf.addPlayAudio(playAudio);
        return collectDtmf;
    }

}
