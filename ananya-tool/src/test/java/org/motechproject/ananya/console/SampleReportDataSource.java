package org.motechproject.ananya.console;

import org.motechproject.export.annotation.Report;
import org.motechproject.export.annotation.ReportGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
@ReportGroup(name="SampleFLW")
public class SampleReportDataSource {

    private List<SampleData> sampleData1 = new ArrayList<SampleData>();

    public SampleReportDataSource() {
        this.sampleData1 = asList(new SampleData("id1"), new SampleData("id2"));
    }

    @Report
    public List<SampleData> queryReport() {
        return sampleData1;

    }
}
