package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallDurationData;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Collection;

@Controller
public class CallDetailController {
    private CallDetailLoggerService callDetailLoggerService;
    private static Logger log = LoggerFactory.getLogger(CallDetailController.class);

    @Autowired
    public CallDetailController(CallDetailLoggerService callDetailLoggerService) {
        this.callDetailLoggerService = callDetailLoggerService;
    }

    @RequestMapping(method = RequestMethod.POST, value="calldurationdata/add")
    @ResponseBody
    public String addCallDurationData(HttpServletRequest request){
        String callerId = request.getParameter("callerId");
        String callId = request.getParameter("callId");
        String stringifiedData = request.getParameter("dataToPost");

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TransferData<CallDurationData>>>(){}.getType();
        Collection<TransferData<CallDurationData>> callDurationDatas = gson.fromJson(stringifiedData, collectionType);

        for(TransferData<CallDurationData> data : callDurationDatas)
        {
            CallDurationData callData = data.data();
            CallEvent callEvent =  callData.getEvent();
            String time = callData.getTime();
            CallDetailLog callDetailLog = new CallDetailLog(callId, callerId, callEvent, time, "operator");
            log.info("callerID : "+ callerId);
            log.info("event : "+ callEvent);
            log.info("time : "+ time);
            callDetailLoggerService.Save(callDetailLog);
        }
        return "Post Done";
    }
}
