package org.motechproject.ananya.utils.log4j.rolling;

import org.apache.log4j.rolling.RolloverDescription;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.rolling.helper.CompositeAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.utils.log4j.rolling.helper.FileDeleteAction;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TimeBasedRollingPolicy.class})
public class TimeBasedFixedBackupCountRollingPolicyTest {

    private String logBasePath;

    @Before
    public void setUp() {
        logBasePath = this.getClass().getResource("/log4j_test_log_dir/").getPath();
    }

    @Test
    public void shouldUpdateFileNamePatternToFullWhenDefaultIsUsed_ActivateOptions() {
        TimeBasedFixedBackupCountRollingPolicy timeBasedFixedBackupCountRollingPolicy = new TimeBasedFixedBackupCountRollingPolicy();
        timeBasedFixedBackupCountRollingPolicy.setFileNamePattern("log-name.%d.log");

        timeBasedFixedBackupCountRollingPolicy.activateOptions();

        assertEquals("log-name.%d{yyyy-MM-dd}.log", timeBasedFixedBackupCountRollingPolicy.getFileNamePattern());
    }

    @Test
    public void shouldDeleteObsoleteLogsUponInitialize() {
        TimeBasedFixedBackupCountRollingPolicy timeBasedFixedBackupCountRollingPolicy = new TimeBasedFixedBackupCountRollingPolicy();
        timeBasedFixedBackupCountRollingPolicy.setActiveFileName(logFilePath("dummy-app.log"));
        timeBasedFixedBackupCountRollingPolicy.setFileNamePattern(logFilePath("dummy-app.%d.log"));
        timeBasedFixedBackupCountRollingPolicy.setMaxBackupCount(4);
        timeBasedFixedBackupCountRollingPolicy.activateOptions();

        RolloverDescription rolloverDescription = timeBasedFixedBackupCountRollingPolicy.initialize(null, false);

        Action action = rolloverDescription.getAsynchronous();
        assertNotNull(action);
        assertTrue(action instanceof FileDeleteAction);
        FileDeleteAction fileDeleteAction = (FileDeleteAction) action;
        assertEquals(3, fileDeleteAction.getFilesToBeDeleted().length);
        assertEquals("dummy-app.2012-12-03.log", fileDeleteAction.getFilesToBeDeleted()[0].getName());
        assertEquals("dummy-app.2012-12-02.log", fileDeleteAction.getFilesToBeDeleted()[1].getName());
        assertEquals("dummy-app.2012-12-01.log", fileDeleteAction.getFilesToBeDeleted()[2].getName());
    }

    @Test
    public void shouldDeleteObsoleteLogsUponRollover() {
        TimeBasedFixedBackupCountRollingPolicy timeBasedFixedBackupCountRollingPolicy = new TimeBasedFixedBackupCountRollingPolicy();
        timeBasedFixedBackupCountRollingPolicy.setActiveFileName(logFilePath("dummy-app.log"));
        timeBasedFixedBackupCountRollingPolicy.setFileNamePattern(logFilePath("dummy-app.%d.log"));
        timeBasedFixedBackupCountRollingPolicy.setMaxBackupCount(4);
        timeBasedFixedBackupCountRollingPolicy.activateOptions();
        timeBasedFixedBackupCountRollingPolicy.initialize(null, false); //This will delete the files 2012-12-03,02,01

        long advanceTimeByADay = System.currentTimeMillis() + 86400000;
        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(advanceTimeByADay);

        RolloverDescription rolloverDescription = timeBasedFixedBackupCountRollingPolicy.rollover(logFilePath("dummy-app.log"));

        Action action = rolloverDescription.getAsynchronous();
        assertNotNull(action);
        assertTrue(action instanceof FileDeleteAction);
        FileDeleteAction fileDeleteAction = (FileDeleteAction) action;
        assertEquals(1, fileDeleteAction.getFilesToBeDeleted().length);
        assertEquals("dummy-app.2012-12-04.log", fileDeleteAction.getFilesToBeDeleted()[0].getName());
    }

    @Test
    public void shouldDeleteObsoleteLogsUponRollover_whenGZippedToo() throws NoSuchFieldException, IllegalAccessException {
        TimeBasedFixedBackupCountRollingPolicy timeBasedFixedBackupCountRollingPolicy = new TimeBasedFixedBackupCountRollingPolicy();
        timeBasedFixedBackupCountRollingPolicy.setActiveFileName(logFilePath("foo-app.log"));
        timeBasedFixedBackupCountRollingPolicy.setFileNamePattern(logFilePath("foo-app.%d.log.gz"));
        timeBasedFixedBackupCountRollingPolicy.setMaxBackupCount(4);
        timeBasedFixedBackupCountRollingPolicy.activateOptions();
        timeBasedFixedBackupCountRollingPolicy.initialize(null, false); //This will delete the files 2012-12-03,02,01

        long advanceTimeByADay = System.currentTimeMillis() + 86400000;
        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(advanceTimeByADay);

        RolloverDescription rolloverDescription = timeBasedFixedBackupCountRollingPolicy.rollover(logFilePath("dummy-app.log"));

        Action action = rolloverDescription.getAsynchronous();
        assertNotNull(action);
        assertTrue(action instanceof CompositeAction);
        Field actionsField = CompositeAction.class.getDeclaredField("actions");
        actionsField.setAccessible(true);
        Action[] actions = (Action[]) actionsField.get(action);
        FileDeleteAction fileDeleteAction = (FileDeleteAction) actions[1];
        assertEquals(1, fileDeleteAction.getFilesToBeDeleted().length);
        assertEquals("foo-app.2012-12-04.log.gz", fileDeleteAction.getFilesToBeDeleted()[0].getName());
    }

    private String logFilePath(String s1) {
        return logBasePath + s1;
    }
}
