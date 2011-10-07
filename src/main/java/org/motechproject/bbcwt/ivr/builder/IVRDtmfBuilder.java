package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IVRDtmfBuilder {
    private String playText;
    private String playAudio;
    private Integer timeOutInMillis;
    private Integer maxLengthOfResponse;
    private List<Prompt> prompts = new ArrayList<Prompt>(3);

    public IVRDtmfBuilder addPlayText(String... playTexts) {
        for (String playText : playTexts)
            this.prompts.add(new TextPrompt(playText));
        return this;
    }

    public IVRDtmfBuilder addPlayAudio(String... playAudios) {
        for (String playAudio : playAudios)
            this.prompts.add(new AudioPrompt(playAudio));
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
        for (Prompt prompt : prompts) prompt.appendPrompt(collectDtmf);
        if (timeOutInMillis != null) collectDtmf.setTimeOut(timeOutInMillis);
        if (maxLengthOfResponse != null) collectDtmf.setMaxDigits(maxLengthOfResponse);
        return collectDtmf;
    }

}
