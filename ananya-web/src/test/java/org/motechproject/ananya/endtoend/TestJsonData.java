package org.motechproject.ananya.endtoend;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestJsonData {

    @Autowired
    private AllNodes allNodes;

    public String forJobAidDisconnect(List<String> nodeNames) {
        String tokenTemplate = "{\"token\":%s, \"type\":%s, \"data\":%s}";
        String callStartPacketTemplate = "{\"callEvent\" : \"CALL_START\", \"time\" : \"%s\"}";
        String audioTrackerPacketTemplate = "{\"contentId\":\"%s\", \"duration\":\"%s\", \"time\":\"%s\"}";
        String disconnectPacketTemplate = "{\"callEvent\" : \"DISCONNECT\", \"time\" : \"%s\"}";

        Integer tokenNumber = 0;
        DateTime callTimer = DateTime.now();
        List<String> tokens = new ArrayList<String>();

        String callStartPacket = String.format(callStartPacketTemplate, callTimer.getMillis());
        tokens.add(String.format(tokenTemplate, tokenNumber, "callDuration", callStartPacket));
        tokenNumber++;
        
        for (String nodeName : nodeNames) {
            Node node = allNodes.findByName(nodeName);
            List<StringContent> contents = node.contents();
            StringContent stringContent = contents.get(0);
            String duration = stringContent.getMetadata().get("duration");

            String packet = String.format(audioTrackerPacketTemplate, stringContent.getId(), duration, callTimer.getMillis());
            tokens.add(String.format(tokenTemplate, tokenNumber, "audioTracker", packet));
            callTimer = callTimer.plusSeconds(Integer.parseInt(duration));
            tokenNumber++;
        }
        String callEndPacket = String.format(disconnectPacketTemplate, callTimer.getMillis());
        tokens.add(String.format(tokenTemplate, tokenNumber, "callDuration", callEndPacket));
        
        return "["+StringUtils.join(tokens,",")+"]";
    }


}
