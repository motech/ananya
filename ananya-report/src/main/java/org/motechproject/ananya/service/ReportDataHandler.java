package org.motechproject.ananya.service;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.motechproject.ananya.domain.ReportData;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportDataHandler {

    private AllCallLogs allCallLogs;

    @Autowired
    public ReportDataHandler(AllCallLogs allCallLogs) {
        this.allCallLogs = allCallLogs;
    }

    @MotechListener(subjects={ReportDataPublisher.SEND_REPORT_DATA_KEY})
	public void execute(MotechEvent event) throws Exception {
        ReportData reportData = (ReportData) event.getParameters().get("1");
        Object dataBean = Class.forName(reportData.bean()).newInstance();
        new BeanUtilsBean().populate(dataBean, reportData.record());
        allCallLogs.add(dataBean);
    }
}
