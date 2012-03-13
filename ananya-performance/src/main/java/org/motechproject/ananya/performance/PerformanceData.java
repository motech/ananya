package org.motechproject.ananya.performance;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceData {
    String testName();
    String description();
}
