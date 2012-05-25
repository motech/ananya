package org.motechproject.ananya.console;

import org.apache.commons.io.IOUtils;
import org.motechproject.export.builder.csv.CsvReportBuilder;
import org.motechproject.export.model.AllReportDataSources;
import org.motechproject.export.model.ReportDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvExporter {

    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tool.xml";

    public static void main(String args[]) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllReportDataSources allReportDataSources = (AllReportDataSources) applicationContext.getBean("allReportDataSources");
        ReportDataSource reportDataSource = allReportDataSources.get(args[0].toUpperCase());
        if (reportDataSource == null)
            System.out.println("Report Data Source not found: enter a valid value for the first argument..eg: FLW, LOCATION");
        Map<String, String> criteria = new HashMap<String, String>();

        FileInputStream fileInputStream = null;
        List<String> strings = new ArrayList<String>();
        try {
            fileInputStream = new FileInputStream(args[1]);
            strings = IOUtils.readLines(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String line : strings) {
            String[] keyValue = line.split("=");
            criteria.put(keyValue[0].toLowerCase(), keyValue[1]);
        }

        String outputFileName = null;
        if(args.length == 3)
            outputFileName = args[2];

        new CsvReportBuilder(outputFileName, "queryReport", reportDataSource, criteria).build();
        System.out.println("Report file generated successfully.");
        applicationContext.close();
    }
}
