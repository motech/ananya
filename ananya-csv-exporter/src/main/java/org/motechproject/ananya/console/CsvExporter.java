package org.motechproject.ananya.console;

import org.apache.commons.io.IOUtils;
import org.motechproject.export.builder.csv.CsvReportBuilder;
import org.motechproject.export.model.ReportDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvExporter {
    private final ReportDataSource reportDataSource;
    private final String filterFilePath;
    private final String outputFilePath;

    public CsvExporter(ReportDataSource reportDataSource, String filterFilePath, String outputFilePath) {
        this.reportDataSource = reportDataSource;
        this.filterFilePath = filterFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void buildReport() throws Exception {
        if (reportDataSource == null)
            throw new RuntimeException("Entity to be exported not found");
        new CsvReportBuilder(outputFilePath, "queryReport", reportDataSource, getCriteria()).build();

        System.out.println(String.format("Report file generated successfully"));
    }

    private Map<String, String> getCriteria() throws IOException {
        Map<String, String> criteria = new HashMap<String, String>();
        if (filterFilePath != null) {
            List<String> strings = IOUtils.readLines(new FileInputStream(filterFilePath));
            for (String line : strings) {
                String[] keyValue = line.split("=");
                criteria.put(keyValue[0].toLowerCase(), keyValue[1]);
            }
        }
        return criteria;
    }
}
