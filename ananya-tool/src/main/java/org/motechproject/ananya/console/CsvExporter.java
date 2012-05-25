package org.motechproject.ananya.console;

import org.apache.commons.io.IOUtils;
import org.motechproject.export.builder.csv.CsvReportBuilder;
import org.motechproject.export.model.AllReportDataSources;
import org.motechproject.export.model.ReportDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvExporter {

    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tool.xml";

    public static void main(String args[]) throws IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllReportDataSources allReportDataSources = (AllReportDataSources) applicationContext.getBean("allReportDataSources");
        ReportDataSource reportDataSource = allReportDataSources.get(args[0].toUpperCase());
        if (reportDataSource == null)
            System.out.println("Report Data Source not found: enter a valid value for the first argument..eg: FLW, LOCATION");
        Map<String, String> criteria = new HashMap<String, String>();

        FileInputStream fileInputStream = new FileInputStream(args[1]);
        List<String> strings = IOUtils.readLines(fileInputStream);
        for (String line : strings) {
            String[] keyValue = line.split("=");
            criteria.put(keyValue[0].toLowerCase(), keyValue[1]);
        }

        new CsvReportBuilder(args[2], "queryReport", reportDataSource, criteria).build();
        System.out.println("Report file generated successfully.");
        applicationContext.close();
    }
}
