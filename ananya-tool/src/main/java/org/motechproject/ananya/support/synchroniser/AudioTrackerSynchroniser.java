package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.service.AudioTrackerLogService;
import org.motechproject.ananya.service.measure.CourseAudioTrackerMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Component
public class AudioTrackerSynchroniser extends BaseSynchronizer implements Synchroniser {

    private AudioTrackerLogService audioTrackerLogService;
    private CourseAudioTrackerMeasureService courseItemMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;

    @Autowired
    public AudioTrackerSynchroniser(AudioTrackerLogService audioTrackerLogService,
                                    CourseAudioTrackerMeasureService courseItemMeasureService,
                                    JobAidContentMeasureService jobAidContentMeasureService,
                                    @Qualifier("ananyaProperties") Properties properties) {
        this.audioTrackerLogService = audioTrackerLogService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.properties = properties;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("AudioTracker");
        List<AudioTrackerLog> audioTrackerLogs = audioTrackerLogService.getAll();
        correctContentIds(audioTrackerLogs);
        for (AudioTrackerLog audioTrackerLog : audioTrackerLogs) {
            try {
                if(!shouldProcessLog(audioTrackerLog)) continue;
                if (audioTrackerLog.typeIsCertificateCourse()) {
                    courseItemMeasureService.createFor(audioTrackerLog.getCallId());
                } else
                    jobAidContentMeasureService.createFor(audioTrackerLog.getCallId());
                synchroniserLog.add(audioTrackerLog.getCallId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(audioTrackerLog.getCallId(), "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    private void correctContentIds(List<AudioTrackerLog> audioTrackerLogs) {
        HashMap<String, String> oldContentIdToNewContentId = initContentIdMap();
        for (AudioTrackerLog log : audioTrackerLogs) {
            List<AudioTrackerLogItem> audioTrackerLogItems = log.items();
            List<AudioTrackerLogItem> newAudioTrackerLogItems = new ArrayList<AudioTrackerLogItem>();
            for (AudioTrackerLogItem audioTrackerLogItem : audioTrackerLogItems) {
                String newContentId = oldContentIdToNewContentId.get(audioTrackerLogItem.getContentId());
                if (newContentId != null) {
                    audioTrackerLogItem.setContentId(newContentId);
                }
                newAudioTrackerLogItems.add(audioTrackerLogItem);
            }
            log.setAudioTrackerLogItems(newAudioTrackerLogItems);
            audioTrackerLogService.update(log);
        }
    }

    private HashMap<String, String> initContentIdMap() {

        HashMap<String, String> oldContentIdToNewContentId = new HashMap<String, String>();
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6704a63", "7a823ae22badc42018c6542c597c9520");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6705a47", "7a823ae22badc42018c6542c597ca259");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67069d4", "7a823ae22badc42018c6542c597ca530");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67077bd", "7a823ae22badc42018c6542c597cb156");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6708d60", "7a823ae22badc42018c6542c597cbb0e");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6709031", "7a823ae22badc42018c6542c597cc1d2");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6709cc4", "7a823ae22badc42018c6542c597cced8");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670a7f1", "7a823ae22badc42018c6542c597ccf1c");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670c558", "7a823ae22badc42018c6542c597cdcdd");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670d149", "7a823ae22badc42018c6542c597cdea4");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670d9f7", "7a823ae22badc42018c6542c597ce60b");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670e8ab", "7a823ae22badc42018c6542c597ce839");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670ec30", "7a823ae22badc42018c6542c597cf360");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670f017", "7a823ae22badc42018c6542c597d01aa");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af670fb93", "7a823ae22badc42018c6542c597d0ca2");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67100b5", "7a823ae22badc42018c6542c597d13db");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6710374", "7a823ae22badc42018c6542c597d1984");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6711aa6", "7a823ae22badc42018c6542c597d1d84");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67124e8", "7a823ae22badc42018c6542c597d2971");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6712c81", "7a823ae22badc42018c6542c597d3209");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67006ce", "7a823ae22badc42018c6542c597c46ef");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6701203", "7a823ae22badc42018c6542c597c4d3b");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6701eed", "7a823ae22badc42018c6542c597c5d02");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af67023c7", "7a823ae22badc42018c6542c597c6a4d");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6702a95", "7a823ae22badc42018c6542c597c79a0");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6703a23", "7a823ae22badc42018c6542c597c8673");
        oldContentIdToNewContentId.put("5fc654d8ec2bac6c906be72af6704173", "7a823ae22badc42018c6542c597c8dd9");

        return oldContentIdToNewContentId;
    }

    @Override
    public Priority runPriority() {
        return Priority.low;
    }
}
