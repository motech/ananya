package org.motechproject.ananya.console;

import org.motechproject.export.builder.csv.CsvReportBuilder;
import org.motechproject.export.model.AllReportDataSources;
import org.motechproject.export.model.ReportDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CsvExporter {

    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tool.xml";

    public static void main(String args[]){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllReportDataSources allReportDataSources = (AllReportDataSources) applicationContext.getBean("allReportDataSources");
        ReportDataSource reportDataSource = allReportDataSources.get(args[0]);
        if(reportDataSource == null)
            System.out.println("Report Data Source not found: enter a valid value for the first argument..eg: FLW, LOCATION");
        new CsvReportBuilder(args[1], "queryReport", reportDataSource).build();
    }
}
