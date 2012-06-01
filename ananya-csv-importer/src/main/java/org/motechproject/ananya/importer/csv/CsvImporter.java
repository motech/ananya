package org.motechproject.whp.importer.csv;

import org.motechproject.importer.CSVDataImporter;
import org.motechproject.whp.importer.csv.exceptions.ExceptionMessages;
import org.motechproject.whp.importer.csv.exceptions.WHPImportException;
import org.motechproject.whp.importer.csv.logger.ImporterLogger;
import org.motechproject.whp.mapping.StringToEnumeration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class CsvImporter {

    private static final String APPLICATION_CONTEXT_XML = "applicationDataImporterContext.xml";

    public static void main(String argvs[]) throws Exception {
        try {
            validateAndSetUpLogger(argvs);
            ImportType importType = validateAndSetImportType(argvs[0]);
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
            CSVDataImporter csvDataImporter = (CSVDataImporter) context.getBean("csvDataImporter");
            importType.performAction(argvs[1], csvDataImporter);
        } catch (Exception exception) {
            ImporterLogger.error(exception);
            throw exception;
        }
    }

    private static void validateAndSetUpLogger(String[] argvs) throws Exception {
        validateArgCount(argvs);
        setLogger(argvs[2]);
        validateImportFile(argvs[1]);
    }

    private static ImportType validateAndSetImportType(String mode) throws WHPImportException {
        ImportType importType = (ImportType) new StringToEnumeration().convert(mode, ImportType.class);
        if (importType == null) {
            throw new WHPImportException(ExceptionMessages.ILLEGAL_ARGUMENTS);
        }
        return importType;
    }

    private static void validateImportFile(String importFile) throws WHPImportException {
        try {
            if (!new File(importFile).canRead()) {
                throw new WHPImportException("invalid file");
            }
        } catch (Exception exception) {
            throw new WHPImportException("Unable to read file - " + importFile + " Either file does not exist or the file does not have read permission");
        }
    }

    private static void setLogger(String logFile) throws WHPImportException {
        try {
            new File(logFile).createNewFile();
            ImporterLogger.loadAppender(logFile);
        } catch (Exception exception) {
            throw new WHPImportException("Unable to create/access the log file -" + logFile);
        }
    }

    public static void validateArgCount(String args[]) throws Exception {
        if (args.length < 3) {
            throw new WHPImportException(ExceptionMessages.ILLEGAL_ARGUMENTS);
        }
    }
}

enum ImportType {
    Provider() {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            ImporterLogger.info("Importing provider records from file : " + importFile);
            csvDataImporter.importData("providerRecordImporter", importFile);
        }
    }, Patient {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            ImporterLogger.info("Importing patient records from file : " + importFile);
            csvDataImporter.importData("patientRecordImporter", importFile);
        }
    }, ProviderTest {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            ImporterLogger.info("Testing import of provider records from file : " + importFile);
            csvDataImporter.importData("providerRecordValidator", importFile);
        }
    }, PatientTest {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            ImporterLogger.info("Testing import of patient records from file : " + importFile);
            csvDataImporter.importData("patientRecordValidator", importFile);
        }
    };

    abstract void performAction(String importFile, CSVDataImporter csvDataImporter);
};

