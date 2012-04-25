package org.motechproject.ananya.domain.dimension;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "time_dimension")
@NamedQuery(name = TimeDimension.FIND_BY_DAY_MONTH_YEAR, query = "select t from TimeDimension t where t.year=:year and t.month=:month and t.day=:day")
public class TimeDimension {

    public static final String FIND_BY_DAY_MONTH_YEAR = "find.by.day.month.year";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "day")
    private Integer day;

    @Column(name = "week")
    private Integer week;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "date")
    private Date date;

    public TimeDimension() {
    }

    public TimeDimension(DateTime time) {
        this(time.getDayOfYear(), time.getWeekOfWeekyear(), time.getMonthOfYear(), time.getYear(), time.toDate());
    }

    public TimeDimension(Integer day, Integer week, Integer month, Integer year, Date date) {
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean matches(DateTime time) {
        return this.day.equals(time.getDayOfYear())
                && this.week.equals(time.getWeekOfWeekyear())
                && this.month.equals(time.getMonthOfYear())
                && this.year.equals(time.getYear());
    }
}
