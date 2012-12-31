package org.motechproject.ananya.utils.log4j.rolling;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.rolling.*;
import org.apache.log4j.rolling.helper.*;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.utils.log4j.rolling.helper.FileDeleteAction;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.*;

public class TimeBasedFixedBackupCountRollingPolicy extends RollingPolicyBase implements TriggeringPolicy {
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private TimeBasedRollingPolicy timeBasedRollingPolicy;
    private int maxBackupCount;
    private List<File> allLogFiles = new ArrayList<>();

    private static final String START_PATTERN_DELIMITER = "%d{";
    private static final String END_PATTERN_DELIMITER = "}";
    private static final String DEFAULT_PATTERN = "%d";

    public TimeBasedFixedBackupCountRollingPolicy() {
        timeBasedRollingPolicy = new TimeBasedRollingPolicy();
    }

    public void activateOptions() {
        super.activateOptions();

        updateFileNamePattern();

        timeBasedRollingPolicy.setActiveFileName(this.getActiveFileName());
        timeBasedRollingPolicy.setFileNamePattern(this.getFileNamePattern());
        timeBasedRollingPolicy.activateOptions();
    }

    private void updateFileNamePattern() {
        String fileNamePattern = this.getFileNamePattern();
        if (StringUtils.contains(fileNamePattern, DEFAULT_PATTERN) && !StringUtils.contains(fileNamePattern, START_PATTERN_DELIMITER)) {
            this.setFileNamePattern(StringUtils.replace(fileNamePattern, DEFAULT_PATTERN, START_PATTERN_DELIMITER + DEFAULT_DATE_PATTERN + END_PATTERN_DELIMITER));
        }
    }

    @Override
    public RolloverDescription initialize(String file, boolean append) throws SecurityException {
        RolloverDescription rolloverDescription = timeBasedRollingPolicy.initialize(file, append);

        if (maxBackupCount > 0 && rolloverDescription != null) {
            updateLogFiles(nextRolledFile(rolloverDescription));
            updateLogFiles(identifyAllLogFiles(getLogDir(rolloverDescription.getActiveFileName())));

            rolloverDescription = updateRolloverDescription(rolloverDescription, deleteObsoleteFilesAction());
        }

        return rolloverDescription;
    }

    @Override
    public RolloverDescription rollover(String activeFileName) throws SecurityException {
        RolloverDescription rolloverDescription = timeBasedRollingPolicy.rollover(activeFileName);

        if (maxBackupCount > 0 && rolloverDescription != null) {
            updateLogFiles(nextRolledFile(rolloverDescription));

            rolloverDescription = updateRolloverDescription(rolloverDescription, deleteObsoleteFilesAction());
        }

        return rolloverDescription;
    }

    private RolloverDescription updateRolloverDescription(RolloverDescription rolloverDescription, FileDeleteAction fileDeleteAction) {
        if (fileDeleteAction == null)
            return rolloverDescription;

        if (rolloverDescription.getAsynchronous() == null)
            return new RolloverDescriptionImpl(rolloverDescription.getActiveFileName(),
                    rolloverDescription.getAppend(),
                    rolloverDescription.getSynchronous(),
                    fileDeleteAction);

        List<Action> actions = new ArrayList<>();
        actions.add(rolloverDescription.getAsynchronous());
        actions.add(fileDeleteAction);
        return new RolloverDescriptionImpl(rolloverDescription.getActiveFileName(),
                rolloverDescription.getAppend(),
                rolloverDescription.getSynchronous(),
                new CompositeAction(actions, false));
    }

    private void updateLogFiles(final File file) {
        if (file == null || CollectionUtils.exists(allLogFiles, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                File existingFile = (File) object;

                return file.getPath().equals(existingFile.getPath());
            }
        }))
            return;

        allLogFiles.add(file);
    }

    private void updateLogFiles(List<File> files) {
        for (File file : files) {
            updateLogFiles(file);
        }
    }

    private File nextRolledFile(RolloverDescription rolloverDescription) {
        try {
            if (rolloverDescription.getAsynchronous() != null) {
                if (rolloverDescription.getAsynchronous() instanceof ZipCompressAction) {
                    ZipCompressAction zipCompressAction = (ZipCompressAction) rolloverDescription.getAsynchronous();

                    Field fieldDestination = ZipCompressAction.class.getDeclaredField("destination");
                    fieldDestination.setAccessible(true);

                    return (File) fieldDestination.get(zipCompressAction);
                } else if (rolloverDescription.getAsynchronous() instanceof GZCompressAction) {
                    GZCompressAction gzCompressAction = (GZCompressAction) rolloverDescription.getAsynchronous();

                    Field fieldDestination = GZCompressAction.class.getDeclaredField("destination");
                    fieldDestination.setAccessible(true);

                    return (File) fieldDestination.get(gzCompressAction);
                }
            }

            if (rolloverDescription.getSynchronous() != null) {
                FileRenameAction fileRenameAction = (FileRenameAction) rolloverDescription.getSynchronous();

                Field fieldDestination = FileRenameAction.class.getDeclaredField("destination");
                fieldDestination.setAccessible(true);

                return (File) fieldDestination.get(fileRenameAction);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return rolloverDescription.getActiveFileName() != null ? new File(rolloverDescription.getActiveFileName()) : null;
    }

    private List<File> identifyAllLogFiles(File logsDir) {
        File[] logFiles = logsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getPath().equals(getActiveFileName()))
                    return true;

                try {
                    dateOf(file);
                } catch (Exception e) {
                    return false;
                }

                return true;
            }
        });

        return logFiles == null ? new ArrayList<File>() : Arrays.asList(logFiles);
    }

    private FileDeleteAction deleteObsoleteFilesAction() {
        FileDeleteAction action = null;

        if (allLogFiles.size() > maxBackupCount) {
            arrangeLogFilesInNewerToOlderOrder(allLogFiles);
            List<File> obsoleteFiles = allLogFiles.subList(maxBackupCount, allLogFiles.size());

            File[] obsoleteFilesArray = new File[obsoleteFiles.size()];
            action = new FileDeleteAction(obsoleteFiles.toArray(obsoleteFilesArray));

            obsoleteFiles.clear();
        }

        return action;
    }

    private void arrangeLogFilesInNewerToOlderOrder(List<File> allLogFiles) {
        Collections.sort(allLogFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String f1Name = f1.getPath();
                if (f1Name.equals(getActiveFileName()))
                    return -1;
                String f2Name = f2.getPath();
                if (f2Name.equals((getActiveFileName())))
                    return 1;

                return dateOf(f1).isAfter(dateOf(f2)) ? -1 : 1;
            }
        });
    }

    private DateTime dateOf(File logFile) {
        return DateTimeFormat.forPattern(getPattern()).parseDateTime(getLogFileDatePart(logFile.getPath()));
    }

    private String getLogFileDatePart(String logFileName) {
        return StringUtils.substringBetween(logFileName, getBeforePattern(), getAfterPattern());
    }

    private String getPattern() {
        return StringUtils.substringBetween(getFileNamePattern(), START_PATTERN_DELIMITER, END_PATTERN_DELIMITER);
    }

    private String getBeforePattern() {
        return StringUtils.substringBefore(getFileNamePattern(), START_PATTERN_DELIMITER);
    }

    private String getAfterPattern() {
        return StringUtils.substringAfter(getFileNamePattern(), END_PATTERN_DELIMITER);
    }

    private File getLogDir(String file) {
        File parentDir = new File(file).getParentFile();
        return parentDir == null ? new File(".") : parentDir;
    }

    @Override
    public boolean isTriggeringEvent(Appender appender, LoggingEvent event, String filename, long fileLength) {
        return timeBasedRollingPolicy.isTriggeringEvent(appender, event, filename, fileLength);
    }

    public int getMaxBackupCount() {
        return maxBackupCount;
    }

    public void setMaxBackupCount(int maxBackupCount) {
        this.maxBackupCount = maxBackupCount;
    }
}
