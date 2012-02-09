package org.motechproject.ananya.domain.dimension;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import javax.persistence.*;

@Entity
@Table(name = "time_dimension")
@NamedQuery(name = TimeDimension.FIND_BY_DAY_MONTH_YEAR, query = "select t from TimeDimension t where t.dateTime=:dateTime")
public class TimeDimension {

    public static final String FIND_BY_DAY_MONTH_YEAR = "find.by.day.month.year";

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "day")
    private Integer day;

    @Column(name = "week")
    private Integer week;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "datetime")
    private DateTime dateTime;


    public TimeDimension(DateTime time) {
        this(time.get(DateTimeFieldType.dayOfYear()), time.get(DateTimeFieldType.weekOfWeekyear()),
                time.get(DateTimeFieldType.monthOfYear()), time.get(DateTimeFieldType.year()), time);
    }

    public TimeDimension(Integer day, Integer week, Integer month, Integer year, DateTime dateTime) {
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
