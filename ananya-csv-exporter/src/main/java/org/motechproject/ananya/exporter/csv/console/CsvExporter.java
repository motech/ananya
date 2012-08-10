package org.motechproject.ananya.exporter.csv.console;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.export.builder.csv.CsvReportBuilder;
import org.motechproject.export.model.ReportDataSource;

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
                if(StringUtils.isBlank(line))
                    continue;

                String[] keyValue = line.split("=");
                int criteriaArrayLength = keyValue.length;
                if(criteriaArrayLength < 1 || criteriaArrayLength > 2)
                    continue;

                String criteriaKey = keyValue[0].trim().toLowerCase();
                String criteriaValue = (criteriaArrayLength > 1) ? keyValue[1].trim() : StringUtils.EMPTY;

                criteria.put(criteriaKey, criteriaValue);
            }
        }
        return criteria;
    }
}
