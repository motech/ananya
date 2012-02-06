package org.motechproject.ananya.service;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallDetails;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class ReportDataHandler {

    private AllCallDetails allCallDetails;

    @Autowired
    public ReportDataHandler(AllCallDetails allCallDetails) {
        this.allCallDetails = allCallDetails;
    }

    @MotechListener(subjects={ReportDataPublisher.SEND_REPORT_DATA_KEY})
	public void execute(MotechEvent event) throws Exception {
        ReportData reportData = (ReportData) event.getParameters().get("1");
        CallDetail callDetail = new CallDetail();
        new BeanUtilsBean().populate(callDetail, reportData.record());
        allCallDetails.add(callDetail);
    }
}
