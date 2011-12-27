package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IVRResponseBuilder {
    private String sid;
    private CollectDtmf collectDtmf;
    private boolean isHangUp;
    private List<Prompt> prompts = new ArrayList<Prompt>();
    private String nextUrl;

    public IVRResponseBuilder withSid(String sid) {
        this.sid = sid;
        return this;
    }

    public IVRResponseBuilder addPlayText(String... playTexts) {
        for (String playText : playTexts)
            this.prompts.add(new TextPrompt(playText));
        return this;
    }

    public IVRResponseBuilder addPlayAudio(String... playAudios) {
        for (String playAudio : playAudios)
            this.prompts.add(new AudioPrompt(playAudio));
        return this;
    }

    public IVRResponseBuilder withCollectDtmf(CollectDtmf collectDtmf) {
        this.collectDtmf = collectDtmf;
        return this;
    }

    public IVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    public IVRResponseBuilder withNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
        return this;
    }

    public Response create() {
        Response response = new Response();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);
        for (Prompt prompt : prompts) prompt.appendMessage(response);
        if (StringUtils.isNotBlank(nextUrl)) response.addGotoNEXTURL(this.nextUrl);
        if (collectDtmf != null) response.addCollectDtmf(collectDtmf);
        if (isHangUp) response.addHangup();
        return response;
    }
}

interface Prompt {
    void appendMessage(Response ivrResponse);
    void appendPrompt(CollectDtmf collectDtmf);
}

class TextPrompt implements Prompt {
    private String playText;
    public TextPrompt(String playText) {
        this.playText = playText;
    }

    @Override
    public void appendMessage(Response ivrResponse) {
        ivrResponse.addPlayText(playText);
    }

    @Override
    public void appendPrompt(CollectDtmf collectDtmf) {
        collectDtmf.addPlayText(playText);
    }
}

class AudioPrompt implements Prompt {
    private String audioLocation;
    public AudioPrompt(String audioLocation) {
        this.audioLocation = audioLocation;
    }


    @Override
    public void appendMessage(Response ivrResponse) {
        ivrResponse.addPlayAudio(audioLocation);
    }

    @Override
    public void appendPrompt(CollectDtmf collectDtmf) {
        collectDtmf.addPlayAudio(audioLocation);
    }
}
