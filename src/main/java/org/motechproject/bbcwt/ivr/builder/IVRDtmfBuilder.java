package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import org.apache.commons.lang.StringUtils;

public class IVRDtmfBuilder {
    private String playText;
    private String playAudio;
    private Integer timeOutInMillis;
    private Integer maxLengthOfResponse;

    public IVRDtmfBuilder withPlayText(String playText) {
        this.playText = playText;
        return this;
    }

    public IVRDtmfBuilder withPlayAudio(String playAudio) {
        this.playAudio = playAudio;
        return this;
    }

    public IVRDtmfBuilder withTimeOutInMillis(Integer timeout) {
        this.timeOutInMillis = timeout;
        return this;
    }

    public IVRDtmfBuilder withMaximumLengthOfResponse(Integer maxLengthOfResponse) {
        this.maxLengthOfResponse = maxLengthOfResponse;
        return this;
    }

    public CollectDtmf create() {
        CollectDtmf collectDtmf = new CollectDtmf();
        if (StringUtils.isNotBlank(playText)) collectDtmf.addPlayText(playText);
        if (StringUtils.isNotBlank(playAudio)) collectDtmf.addPlayAudio(playAudio);
        if (timeOutInMillis != null) collectDtmf.setTimeOut(timeOutInMillis);
        if (maxLengthOfResponse != null) collectDtmf.setMaxDigits(maxLengthOfResponse);
        return collectDtmf;
    }

}
