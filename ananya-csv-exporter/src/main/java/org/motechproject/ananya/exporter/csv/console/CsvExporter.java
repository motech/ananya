package org.motechproject.ananya.exporter.csv.console;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.export.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class CsvExporter {
    private ExportService exportService;

    @Autowired
    public CsvExporter(ExportService exportService) {
        this.exportService = exportService;
    }

    public void buildReport(String entityName, String outputFilePath, String filterFilePath) throws Exception {
        File file = new File(outputFilePath);
        file.createNewFile();
        exportService.exportAsCSV(entityName, new FileWriter(outputFilePath), getCriteria(filterFilePath));
    }

    private Map<String, String> getCriteria(String filterFilePath) throws IOException {
        Map<String, String> criteria = new HashMap<String, String>();
        if (filterFilePath != null) {
            List<String> strings = IOUtils.readLines(new FileInputStream(filterFilePath));
            for (String line : strings) {
                if (StringUtils.isBlank(line))
                    continue;

                String[] keyValue = line.split("=");
                int criteriaArrayLength = keyValue.length;
                if (criteriaArrayLength < 1 || criteriaArrayLength > 2)
                    continue;

                String criteriaKey = keyValue[0].trim().toLowerCase();
                String criteriaValue = (criteriaArrayLength > 1) ? keyValue[1].trim() : StringUtils.EMPTY;

                criteria.put(criteriaKey, criteriaValue);
            }
        }
        return criteria;
    }
}
