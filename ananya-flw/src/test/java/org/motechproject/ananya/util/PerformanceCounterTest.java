package org.motechproject.ananya.util;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class PerformanceCounterTest {

   @Test
   public void shouldRecordLapTimes() throws InterruptedException {
       PerformanceCounter counter = PerformanceCounter.get("my_counter");

       counter.start(); Thread.sleep(1000); counter.stop();
       counter.start(); Thread.sleep(1000); counter.stop();
       counter.start(); Thread.sleep(1000); counter.stop();
       counter.start(); Thread.sleep(1000); counter.stop();
       counter.start(); Thread.sleep(1000); counter.stop();

       assertTrue((counter.getLastRunTime() - 5000) < 100); // 100 millisecond error gap.
       assertTrue((counter.getAverageTime() - 1000) < 100);
   }

}
