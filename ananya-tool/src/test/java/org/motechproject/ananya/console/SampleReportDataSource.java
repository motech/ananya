package org.motechproject.ananya.console;

import org.motechproject.export.annotation.Report;
import org.motechproject.export.annotation.ReportGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Component
@ReportGroup(name="SampleFLW")
public class SampleReportDataSource {

    private List<SampleData> sampleDataList = new ArrayList<SampleData>();

    public SampleReportDataSource() {
        this.sampleDataList = asList(new SampleData("1234"), new SampleData("id2"));
    }

    @Report
    public List<SampleData> queryReport(Map<String, String> criteria) {
        String msisdn = criteria.get("msisdn");
        ArrayList<SampleData> filteredSampleData = new ArrayList<SampleData>();
        for(SampleData sampleData : sampleDataList) {
            if(sampleData.getmsisdn().equals(msisdn))
                filteredSampleData.add(sampleData);
        }
        return filteredSampleData;
    }
}
