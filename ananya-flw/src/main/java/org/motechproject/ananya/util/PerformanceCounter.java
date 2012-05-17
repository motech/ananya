package org.motechproject.ananya.util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceCounter {

    private static Logger log = LoggerFactory.getLogger(PerformanceCounter.class);

    private int numberRuns;
    
    private double totalTime;
    
    private double averageTime;
    
    private double lastRunTime;
    
    private DateTime lastStartTime;
    
    private boolean isRunning;

    private static Map<String, PerformanceCounter> performanceCounterMap =
            new ConcurrentHashMap<String, PerformanceCounter> ();

    private PerformanceCounter() {
        numberRuns = 0;
        totalTime = 0;
        averageTime = 0;
        lastRunTime = 0;
        isRunning = false;
    }

    public static PerformanceCounter get(String key) {
        if (performanceCounterMap.containsKey(key)) {
            return performanceCounterMap.get(key);
        }

        PerformanceCounter performanceCounter = new PerformanceCounter();
        performanceCounterMap.put(key, performanceCounter);
        return performanceCounter;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public double getLastRunTime() {
        return lastRunTime;
    }

    public void start() {
        isRunning = true;
        lastStartTime = DateTime.now();
    }
    
    public void stop() {
        DateTime now = DateTime.now();
        if (!isRunning) return; // Silent return

        lastRunTime = now.getMillis() - lastStartTime.getMillis();
        isRunning = false;
        totalTime += lastRunTime;
        numberRuns ++;
        averageTime = totalTime / numberRuns;
    }
    
    @Override
    public String toString() {
        return "PerformanceCounter{" +
                " lastStartTime=" + lastStartTime +
                ", numberRuns=" + numberRuns +
                ", totalTime=" + totalTime +
                ", lastRunTime=" + lastRunTime +
                ", averageTime=" + averageTime +
                '}';
    }

    public static void logAllCounters() {
        for(String key : performanceCounterMap.keySet()) {
            log.info(performanceCounterMap.get(key).toString());
        }
    }
}
