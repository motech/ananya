package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "operator_dimension")
public class OperatorDimension {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "circle")
    private String circle;

    @Column(name = "allowed_usage_per_month")
    private Integer allowedUsagePerMonth;

    @Column(name = "start_of_pulse_in_milli_sec")
    private Integer startOfPulseInMilliSec;

    @Column(name = "end_of_pulse_in_milli_sec")
    private Integer endOfPulseInMilliSec;

    public OperatorDimension(String name, Integer allowedUsagePerMonth, Integer startOfPulseInMilliSec, Integer endOfPulseInMilliSec) {
        this.name = name;
        this.allowedUsagePerMonth = allowedUsagePerMonth;
        this.startOfPulseInMilliSec = startOfPulseInMilliSec;
        this.endOfPulseInMilliSec = endOfPulseInMilliSec;
        this.circle = null;
    }
    
    public OperatorDimension(String name, Integer allowedUsagePerMonth, Integer startOfPulseInMilliSec, Integer endOfPulseInMilliSec, String circle) {
        this.name = name;
        this.allowedUsagePerMonth = allowedUsagePerMonth;
        this.startOfPulseInMilliSec = startOfPulseInMilliSec;
        this.endOfPulseInMilliSec = endOfPulseInMilliSec;
        this.circle = circle;
    }

    public OperatorDimension() {
    }

    public String getName() {
        return name;
    }

    public Integer getStartOfPulseInMilliSec() {
        return startOfPulseInMilliSec;
    }

    public Integer getEndOfPulseInMilliSec() {
        return endOfPulseInMilliSec;
    }

    public Integer getAllowedUsagePerMonth() {
        return allowedUsagePerMonth;
    }

	public String getCircle() {
		return circle;
	}
    
    
}
