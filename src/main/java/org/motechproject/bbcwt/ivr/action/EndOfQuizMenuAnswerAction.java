package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/endOfQuizMenuAnswer")
public class EndOfQuizMenuAnswerAction extends BaseAction {
    private static final Map<String, String> INPUT_FORWARD_MAP;

    public static final String INVALID_INPUT_KEY = "INVALID_INPUT";

    static {
        INPUT_FORWARD_MAP = new HashMap<String, String>();
        INPUT_FORWARD_MAP.put("1", "forward:/startQuiz");
        INPUT_FORWARD_MAP.put("2", "forward:/startNextChapter");
        INPUT_FORWARD_MAP.put("3", "forward:/repeatLastChapter");
        INPUT_FORWARD_MAP.put(INVALID_INPUT_KEY, "forward:/endOfQuizMenu");
    }

    @Autowired
    public EndOfQuizMenuAnswerAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String ivrInput = ivrInput(ivrRequest);
        if(inputInvalid(ivrInput)) {
            ivrResponseBuilder(request).addPlayText(messages.get(IVRMessage.INVALID_INPUT));
            ivrInput = INVALID_INPUT_KEY;
        }
        return INPUT_FORWARD_MAP.get(ivrInput);
    }

    private boolean inputInvalid(String chosenOption) {
        return !INPUT_FORWARD_MAP.containsKey(chosenOption);
    }

    private String ivrInput(IVRRequest ivrRequest) {
        return ivrRequest.getData().substring(0);
    }
}
