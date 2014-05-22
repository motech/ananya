package org.motechproject.ananya.framework;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestJsonData {

    private String tokenTemplate = "{\"token\":%s, \"type\":%s, \"data\":%s}";
    private String callStartPacketTemplate = "{\"callEvent\" : \"CALL_START\", \"time\" : \"%s\"}";
    private String audioTrackerPacketTemplate = "{\"contentId\":\"%s\", \"duration\":\"%s\", \"time\":\"%s\"}";
    private String disconnectPacketTemplate = "{\"callEvent\" : \"DISCONNECT\", \"time\" : \"%s\"}";
    private String coursePacketTemplate =
            "{\"result\":%s," +
                    "\"questionResponse\":%s," +
                    "\"contentId\":%s," +
                    "\"contentType\":%s," +
                    "\"certificateCourseId\":\"\"," +
                    "\"contentData\":%s," +
                    "\"interactionKey\":%s," +
                    "\"courseItemState\":%s," +
                    "\"contentName\":%s," +
                    "\"time\":%s," +
                    "\"chapterIndex\":%s," +
                    "\"lessonOrQuestionIndex\":%s}";


    @Autowired
    private AllNodes allNodes;

    @Autowired
    private AllStringContents allStringContents;

    public String forJobAidDisconnect(List<String> nodeNames) {
        Integer tokenNo = 0;
        DateTime callTimer = DateTime.now();
        List<String> tokens = new ArrayList<String>();

        tokens.add(String.format(tokenTemplate, tokenNo, "callDuration", String.format(callStartPacketTemplate, callTimer.getMillis())));
        tokenNo++;
        for (String nodeName : nodeNames) {
            Node node = allNodes.findByName(nodeName);
            List<StringContent> contents = node.contents();
            StringContent stringContent = contents.get(0);
            String duration = stringContent.getMetadata().get("duration");

            String packet = String.format(audioTrackerPacketTemplate, stringContent.getId(), duration, callTimer.getMillis());
            tokens.add(String.format(tokenTemplate, tokenNo, "audioTracker", packet));
            callTimer = callTimer.plusSeconds(Integer.parseInt(duration));
            tokenNo++;
        }
        tokens.add(String.format(tokenTemplate, tokenNo, "callDuration", String.format(disconnectPacketTemplate, callTimer.getMillis())));
        return "[" + StringUtils.join(tokens, ",") + "]";
    }


    public String forCourseDisconnect() {

        DateTime callTimer = DateTime.now();
        List<String> tokens = new ArrayList<String>();

        String chapter9ContentId = allNodes.findByName("Chapter 9").contents().get(0).getId();

        tokens.add(String.format(tokenTemplate, 0, "callDuration", String.format(callStartPacketTemplate, callTimer.getMillis())));
        tokens.add(String.format(tokenTemplate, 1, "ccState", coursePacket(null, null, chapter9ContentId, "quiz", null, "startQuiz", "start", "Chapter 9", callTimer.plusSeconds(10).getMillis() + "", "8", "3")));
        tokens.add(String.format(tokenTemplate, 2, "ccState", coursePacket(null, null, null, null, null, "poseQuestion", null, null, callTimer.plusSeconds(20).getMillis() + "", "8", "4")));
        tokens.add(String.format(tokenTemplate, 3, "ccState", coursePacket(false, "2", null, null, null, "playAnswerExplanation", "start", null, callTimer.plusSeconds(30).getMillis() + "", "8", "4")));
        tokens.add(String.format(tokenTemplate, 4, "ccState", coursePacket(false, "2", null, null, null, "poseQuestion", null, null, callTimer.plusSeconds(40).getMillis() + "", "8", "5")));
        tokens.add(String.format(tokenTemplate, 5, "ccState", coursePacket(true, "2", null, null, null, "playAnswerExplanation", "start", null, callTimer.plusSeconds(50).getMillis() + "", "8", "5")));
        tokens.add(String.format(tokenTemplate, 6, "ccState", coursePacket(true, "2", null, null, null, "poseQuestion", null, null, callTimer.plusSeconds(60).getMillis() + "", "8", "6")));
        tokens.add(String.format(tokenTemplate, 7, "ccState", coursePacket(false, "2", null, null, null, "playAnswerExplanation", "start", null, callTimer.plusSeconds(70).getMillis() + "", "8", "6")));
        tokens.add(String.format(tokenTemplate, 8, "ccState", coursePacket(false, "2", null, null, null, "poseQuestion", null, null, callTimer.plusSeconds(70).getMillis() + "", "8", "7")));
        tokens.add(String.format(tokenTemplate, 9, "ccState", coursePacket(true, "2", null, null, null, "playAnswerExplanation", "start", null, callTimer.plusSeconds(70).getMillis() + "", "8", "7")));
        tokens.add(String.format(tokenTemplate, 10, "ccState", coursePacket(null, null, chapter9ContentId, "quiz", "2", "reportChapterScore", "end", "Chapter 9", callTimer.plusSeconds(80).getMillis() + "", "8", "7")));
        tokens.add(String.format(tokenTemplate, 11, "ccState", coursePacket(null, null, chapter9ContentId, "chapter", null, "endOfChapterMenu", "end", "Chapter 9", callTimer.plusSeconds(90).getMillis() + "", "8", "7")));
        tokens.add(String.format(tokenTemplate, 12, "ccState", coursePacket(null, null, null, null, null, "playThanks", null, null, callTimer.plusSeconds(100).getMillis() + "", "9", "0")));
        tokens.add(String.format(tokenTemplate, 13, "ccState", coursePacket(null, null, null, null, null, "playFinalScore", null, null, callTimer.plusSeconds(110).getMillis() + "", "9", "0")));
        tokens.add(String.format(tokenTemplate, 14, "ccState", coursePacket(null, null, null, null, null, "playCourseResult", null, null, callTimer.plusSeconds(120).getMillis() + "", "9", "0")));
        tokens.add(String.format(tokenTemplate, 15, "ccState", coursePacket(null, null, null, "course", null, null, "end", null, callTimer.plusSeconds(130).getMillis() + "", null, null)));
        tokens.add(String.format(tokenTemplate, 16, "callDuration", String.format(disconnectPacketTemplate, callTimer.plusSeconds(100).getMillis())));

        return "[" + StringUtils.join(tokens, ",") + "]";
    }

    public String coursePacket(Boolean result, String questionResponse, String contentId, String contentType, String contentData,
                               String interactionKey, String courseItemState, String contentName, String time, String chapterIndex, String lessonOrQuestionIndex) {
        return String.format(coursePacketTemplate, result, quote(questionResponse), quote(contentId), quote(contentType), quote(contentData),
                quote(interactionKey), quote(courseItemState), quote(contentName), quote(time), chapterIndex, lessonOrQuestionIndex);
    }

    private String quote(String input) {
        return StringUtils.isBlank(input) ? null : "\"" + input + "\"";
    }
}